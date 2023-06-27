package com.mycompany.model.user;

import com.mycompany.model.category.Category;
import com.mycompany.model.fundraising.Fundraising;
import com.mycompany.model.fundraising.FundraisingRepository;
import com.mycompany.model.transaction.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.json.*;


import java.util.Collection;
import java.util.List;

@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173", "https://frontend-tossacoin.vercel.app"})
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
    @Autowired
    FundraisingRepository fundraisingRepository;
    @Autowired
    TransactionRepository transactionRepository;


    @GetMapping("/loginBasic")
    @ResponseBody
    public com.nimbusds.jose.shaded.json.JSONObject tryToLogin(Authentication authentication){
        com.nimbusds.jose.shaded.json.JSONObject jsonObject = new com.nimbusds.jose.shaded.json.JSONObject();
        Collection<? extends GrantedAuthority> auth = authentication.getAuthorities();
        jsonObject.put("user_role", auth);
        return jsonObject;
    }

    @PostMapping("/register")
    @ResponseBody
    com.nimbusds.jose.shaded.json.JSONObject registerUser(@RequestBody String data){
        com.nimbusds.jose.shaded.json.JSONObject status = new com.nimbusds.jose.shaded.json.JSONObject();
        //System.out.println("register");
        //System.out.println(data);
        JSONObject jsonObject = new JSONObject(data);
        JSONObject value =  jsonObject.getJSONObject("value");
        String name = value.getString("name");
        String email = value.getString("email");
        String password = value.getString("password");
        User user = userService.registerUser(name, email, password);
        status.put("User", user!=null);
        return status;
    }


    @GetMapping("/myAccount")
    @ResponseBody
    com.nimbusds.jose.shaded.json.JSONObject getMe(Authentication authentication){
        User user = userRepository.getUserByEmail(authentication.getName());
        if(user==null)
        {
            JSONObject jsonObject = new JSONObject(authentication.getPrincipal());
            String email = jsonObject.getJSONObject("attributes").getString("email");
            user = userRepository.getUserByEmail(email);
        }
        com.nimbusds.jose.shaded.json.JSONObject jsonObject = new com.nimbusds.jose.shaded.json.JSONObject();
        jsonObject.put("role", user.getRole());
        jsonObject.put("login_type", user.getLoginType());
        jsonObject.put("name", user.getName());
        jsonObject.put("bank_number", user.getBankNumber());
        jsonObject.put("pesel", user.getPesel());
        jsonObject.put("phone_number", user.getPhoneNumber());
        jsonObject.put("email", user.getEmail());
        return jsonObject;

    }


    @GetMapping("/myFundraising")
    @ResponseBody
    com.nimbusds.jose.shaded.json.JSONObject getMyFund(Authentication authentication){
        User user = userRepository.getUserByEmail(authentication.getName());
        List<Fundraising> funds = fundraisingRepository.findAllByOwnerAndAvailableIsTrueOrderByCollectedMoney(user);
        com.nimbusds.jose.shaded.json.JSONArray jsonArray = new com.nimbusds.jose.shaded.json.JSONArray();
        for(Fundraising fund: funds)
        {
            jsonArray.add(fundraisingToJSON(fund));
        }

        com.nimbusds.jose.shaded.json.JSONObject jsonObject = new com.nimbusds.jose.shaded.json.JSONObject();
        jsonObject.put("array", jsonArray);

        return jsonObject;

    }

    public com.nimbusds.jose.shaded.json.JSONArray getTransactionCount(long id){
        com.nimbusds.jose.shaded.json.JSONArray jsonArray = new com.nimbusds.jose.shaded.json.JSONArray();
        List<String> result = transactionRepository.selectAmountCount(id);
        for(String res: result){
            String[] split = res.split(",");
            com.nimbusds.jose.shaded.json.JSONObject jsonObject = new com.nimbusds.jose.shaded.json.JSONObject();
            jsonObject.put("name", split[0]);
            jsonObject.put("numberOfPayments", split[1]);
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }
    public com.nimbusds.jose.shaded.json.JSONObject fundraisingToJSON(Fundraising fund){
        com.nimbusds.jose.shaded.json.JSONObject jsonObj = new com.nimbusds.jose.shaded.json.JSONObject();


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

        com.nimbusds.jose.shaded.json.JSONArray jsonArray = new com.nimbusds.jose.shaded.json.JSONArray();
        for(Category category : fund.getCategory()){
            jsonArray.add(category.getName());
        }

        jsonObj.put("categories", jsonArray);

        com.nimbusds.jose.shaded.json.JSONArray transactions = getTransactionCount(fund.getId());
        jsonObj.put("transactions", transactions);

        System.out.println(jsonObj);

        return jsonObj;
    }

}
