package com.mycompany.model.fundraising;

import com.mycompany.model.user.UserRepository;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.json.*;
import org.springframework.web.multipart.MultipartFile;


import javax.mail.Multipart;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class FundraisingController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FundraisingRepository fundraisingRepository;

    @GetMapping("/home")
    @ResponseBody
    JSONObject getAllFunds(){
        List<Fundraising> funds = fundraisingRepository.findAllByAvailableIsTrueOrderByFundraisingStart();
        System.out.println("/home");
        JSONArray jsonArray = new JSONArray();
        for(Fundraising fund: funds) {
            jsonArray.add(fundraisingToJSON(fund));
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array", jsonArray);

        System.out.println(jsonObject);
        return jsonObject;
    }


    @GetMapping("/fundraising")
    @ResponseBody
    public JSONObject getFundraising(@RequestParam(name = "id")Long id){
        Optional<Fundraising> optionalFundraising = fundraisingRepository.findById(id);
        if(optionalFundraising.isPresent()) {
            return fundraisingToJSON(optionalFundraising.get());
        }
        return new JSONObject();
    }

    @GetMapping("/createFundraising")
    @ResponseBody
    public JSONObject createFundraising(Authentication authentication){
        Fundraising fundraising = jsonToFund(new org.json.JSONObject(authentication.getPrincipal()), authentication.getName());
        fundraisingRepository.save(fundraising);

        return fundraisingToJSON(fundraising);
    }

    public JSONObject fundraisingToJSON(Fundraising fund){
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

        return jsonObj;
    }

    public Fundraising jsonToFund(org.json.JSONObject jsonObject, String ownerName){
        Fundraising fundraising = new Fundraising();
        fundraising.setFundraisingStart(new Date(jsonObject.getJSONObject("attributes").getString("fundraising_start")));
        fundraising.setFundraisingEnd(new Date(jsonObject.getJSONObject("attributes").getString("fundraising_end")));
        fundraising.setTitle(jsonObject.getJSONObject("attributes").getString("title"));
        fundraising.setGoal(Integer.parseInt(jsonObject.getJSONObject("attributes").getString("goal")));
        fundraising.setDescription(jsonObject.getJSONObject("attributes").getString("description"));
        fundraising.setCollectedMoney(0);
        fundraising.setAvailable(false);
        fundraising.setOwner(userRepository.getUserByEmail(ownerName));
        //fundraising.setImage(jsonObject.getJSONObject("attributes").getString("image").getBytes());

        return fundraising;
    }

}

