package com.mycompany.model.fundraising;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class FundraisingController {

    @Autowired
    FundraisingRepository fundraisingRepository;

    @GetMapping("/home")
    @ResponseBody
    JSONObject getAllFunds(){
        List<Fundraising> funds = fundraisingRepository.findAllByAvailableIsTrueOrderByFundraisingStart();
        System.out.println("/home");
        JSONArray jsonArray = new JSONArray();
        for(Fundraising fund: funds)
        {
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

            jsonArray.add(jsonObj);

        }


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array", jsonArray);

        System.out.println(jsonObject);
        return jsonObject;
    }




}
