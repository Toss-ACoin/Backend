package com.mycompany.model.user;

import com.mycompany.model.fundraising.Fundraising;
import com.mycompany.model.fundraising.FundraisingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.json.*;


import java.util.List;

import static com.mycompany.model.fundraising.FundraisingController.fundraisingToJSON;

@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173"})
@RestController
public class UserController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;
    @Autowired
    FundraisingRepository fundraisingRepository;


    @GetMapping("/loginBasic")
    @ResponseBody
    public boolean tryToLogin(Authentication authentication){
        return authentication.isAuthenticated();
    }

    //@PostMapping("/register")
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    com.nimbusds.jose.shaded.json.JSONObject registerUser(@RequestBody String data){
        com.nimbusds.jose.shaded.json.JSONObject status = new com.nimbusds.jose.shaded.json.JSONObject();
        System.out.println("register");
        System.out.println(data); //czekamy na frontend
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
        jsonObject.put("loginType", user.getLoginType());
        jsonObject.put("name", user.getName());
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

}
