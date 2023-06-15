package com.mycompany.model.fundraising;

import com.mycompany.ParameterStringBuilder;
import com.mycompany.model.category.Category;
import com.mycompany.model.category.CategoryRepository;
import com.mycompany.model.donation.Donation;
import com.mycompany.model.user.UserRepository;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.json.*;
import org.springframework.web.multipart.MultipartFile;


import javax.mail.Multipart;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173"})
@RestController
public class FundraisingController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FundraisingRepository fundraisingRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("/home")
    @ResponseBody
    JSONObject getAllFunds(){
        List<Fundraising> funds = fundraisingRepository.findAllByAvailableIsTrueOrderByFundraisingStart();
        JSONArray jsonArray = new JSONArray();
        for(Fundraising fund: funds) {
            jsonArray.add(fundraisingToJSON(fund));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array", jsonArray);

        return jsonObject;
    }

    @GetMapping("/search")
    public JSONObject searchByFunds(@RequestParam(name = "phrase")String phrase){
        List<Fundraising> funds = fundraisingRepository.findAllByTitleContainsOrDescriptionContains(phrase, phrase);
        JSONArray jsonArray = new JSONArray();
        for(Fundraising fund: funds) {
            jsonArray.add(fundraisingToJSON(fund));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array", jsonArray);

        return jsonObject;
    }


    @GetMapping("/fundraising")
    @ResponseBody
    public JSONObject getFundraising(@RequestParam(name = "id")Long id){
        Optional<Fundraising> optionalFundraising = fundraisingRepository.findById(id);
        return optionalFundraising.map(FundraisingController::fundraisingToJSON).orElseGet(JSONObject::new);
    }


    @ResponseBody
    @PostMapping("/createFundraising")
    public JSONObject createFundraising(Authentication authentication, @RequestBody String data) throws ParseException {
        data = data.replace("createFundraising?", "");

        Fundraising fundraising = jsonToFund(new org.json.JSONObject(data), authentication.getName());
        fundraisingRepository.save(fundraising);

        return fundraisingToJSON(fundraising);

    }

    public static JSONObject fundraisingToJSON(Fundraising fund){
        JSONObject jsonObj = new JSONObject();


        jsonObj.put("id", fund.getId());
        jsonObj.put("fundraising_start",fund.getFundraisingStart());
        jsonObj.put("fundraising_end", fund.getFundraisingEnd());
        jsonObj.put("title", fund.getTitle());
        jsonObj.put("collected_money",fund.getCollectedMoney());
        jsonObj.put("goal", fund.getGoal());
        jsonObj.put("image", fund.getImage());
        jsonObj.put("owner_name", fund.getOwner().getName());
        jsonObj.put("owner_surname", fund.getOwner().getSurname());
        jsonObj.put("description", fund.getDescription());
        jsonObj.put("available", fund.isAvailable());

        JSONArray jsonArray = new JSONArray();
        for(Category category : fund.getCategory()){
            jsonArray.add(category.getName());
        }

        jsonObj.put("categories", jsonArray);

        jsonArray = new JSONArray();

        for(Donation donation : fund.getDonations()){
            JSONObject donationJSON = new JSONObject();
            donationJSON.put("fund_id", donation.getFundraisingID().getId());
            donationJSON.put("amount", donation.getAmount());
            jsonArray.add(donationJSON);
        }
        jsonObj.put("donations", jsonArray);

        System.out.println(jsonObj);

        return jsonObj;
    }

    public Fundraising jsonToFund(org.json.JSONObject jsonObject, String ownerName) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Fundraising fundraising = new Fundraising();
        fundraising.setTitle(jsonObject.getString("title"));
        fundraising.setFundraisingStart(format.parse(jsonObject.getString("fundraising_start")));
        fundraising.setFundraisingEnd(format.parse(jsonObject.getString("fundraising_end")));
        fundraising.setGoal(Integer.parseInt(jsonObject.getString("goal")));
        fundraising.setDescription(jsonObject.getString("description"));
        fundraising.setCollectedMoney(0);
        fundraising.setAvailable(false);
        fundraising.setOwner(userRepository.getUserByEmail(ownerName));
        //fundraising.setImage(jsonObject.getString("image").getBytes());

        return fundraising;
    }

    @GetMapping("/accessToken")
    @ResponseBody
    public JSONObject getAccessTokenToPayment(){
        JSONObject token = new JSONObject();

        try{
            URL url = new URL("https://secure.snd.payu.com/pl/standard/user/oauth/authorize");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");

            Map<String, String> parameters = new HashMap<>();

            parameters.put("grant_type", "client_credentials");
            parameters.put("client_id", "467060");
            parameters.put("client_secret", "f0120ec367af90950345c878fdb823f2");

            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.writeBytes(ParameterStringBuilder.getParamsString(parameters));
            out.flush();
            out.close();

            con.connect();

            int status = con.getResponseCode();
            System.out.println("Status: " + status);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            System.out.println("Response: " + content);

            org.json.JSONObject response = new org.json.JSONObject(content.toString());
            token.put("token",response.get("access_token"));

            con.disconnect();

        }catch (Exception e){
            e.printStackTrace();
        }
        token.put("token", "dupa");

        return token;
    }

}

