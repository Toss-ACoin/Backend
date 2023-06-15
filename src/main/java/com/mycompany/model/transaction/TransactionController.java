package com.mycompany.model.transaction;


import com.mycompany.ParameterStringBuilder;
import org.json.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping("/accessToken")
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

            JSONObject response = new JSONObject(content.toString());
            response.get("access_token");

            con.disconnect();

        }catch (Exception e){
            e.printStackTrace();
        }




        return token;
    }
}
