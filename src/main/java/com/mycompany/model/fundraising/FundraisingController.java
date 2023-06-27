package com.mycompany.model.fundraising;

import com.mycompany.model.category.Category;
import com.mycompany.model.category.CategoryRepository;
import com.mycompany.model.transaction.TransactionRepository;
import com.mycompany.model.user.UserRepository;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173", "https://frontend-tossacoin.vercel.app"})
@RestController
public class FundraisingController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FundraisingRepository fundraisingRepository;
    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    TransactionRepository transactionRepository;

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
    public JSONObject searchByFunds(@RequestParam(name = "phrase")String phrase, @RequestParam(name = "page")int page){
        List<Fundraising> funds;
        funds = fundraisingRepository.findAllByTitleContainsOrDescriptionContains(phrase, phrase, PageRequest.of(page, 10));
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
        if(optionalFundraising.isPresent()){
            return fundraisingToJSON(optionalFundraising.get());
        }
        return new JSONObject();
    }


    @ResponseBody
    @PostMapping("/createFundraising")
    public void createFundraising(Authentication authentication, @RequestBody String data)  {
//        data = data.replace("createFundraising?", "");
//
//        Fundraising fundraising = jsonToFund(new org.json.JSONObject(data), authentication.getName());
//        fundraisingRepository.save(fundraising);
//
//        return fundraisingToJSON(fundraising);

        Collection<? extends GrantedAuthority> auth = authentication.getAuthorities();
        System.out.println(auth);

        System.out.println(data);

    }

    public JSONArray getTransactionCount(long id){
        JSONArray jsonArray = new JSONArray();
        List<String> result = transactionRepository.selectAmountCount(id);
        for(String res: result){
            String[] split = res.split(",");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("name", split[0]);
            jsonObject.put("numberOfPayments", split[1]);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
    public JSONObject fundraisingToJSON(Fundraising fund){
        JSONObject jsonObj = new JSONObject();


        jsonObj.put("id", fund.getId());
        jsonObj.put("fundraising_start",fund.getFundraisingStart());
        jsonObj.put("fundraising_end", fund.getFundraisingEnd());
        jsonObj.put("title", fund.getTitle());
        jsonObj.put("collected_money",fund.getCollectedMoney());
        jsonObj.put("goal", fund.getGoal());
        jsonObj.put("image", fund.getPictures());
        jsonObj.put("owner_name", fund.getOwner().getName());
        jsonObj.put("owner_surname", fund.getOwner().getSurname());
        jsonObj.put("description", fund.getDescription());
        jsonObj.put("available", fund.isAvailable());

        JSONArray jsonArray = new JSONArray();
        for(Category category : fund.getCategory()){
            jsonArray.add(category.getName());
        }

        jsonObj.put("categories", jsonArray);

        JSONArray transactions = getTransactionCount(fund.getId());
        jsonObj.put("transactions", transactions);

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



}

