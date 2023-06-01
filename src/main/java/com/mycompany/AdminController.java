package com.mycompany;

import com.mycompany.model.fundraising.FundraisingRepository;
import com.mycompany.model.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.nimbusds.jose.shaded.json.JSONObject;

@RestController
public class AdminController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FundraisingRepository fundraisingRepository;

    //uzytkownicy
    @GetMapping("/admin/users")
    public JSONObject getUsers(){
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    //zbiorki
    @GetMapping("/admin/funds")
    public JSONObject getFunds(){
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    //toggle zbiorek
    @PostMapping("/admin/fund")
    public JSONObject toggleFundAvailable(@RequestParam(name = "id")String id){
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    //toggle uzytkownikow
    @PostMapping("/admin/user")
    public JSONObject toggleUserAvailable(@RequestParam(name = "id")String id){
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }
}
