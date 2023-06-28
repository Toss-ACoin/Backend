package com.mycompany;

import com.mycompany.model.category.Category;
import com.mycompany.model.fundraising.Fundraising;
import com.mycompany.model.fundraising.FundraisingRepository;
import com.mycompany.model.image.Image;
import com.mycompany.model.transaction.TransactionRepository;
import com.mycompany.model.user.User;
import com.mycompany.model.user.UserRepository;
import com.mycompany.utilts.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.nimbusds.jose.shaded.json.JSONObject;

import java.util.List;
import com.nimbusds.jose.shaded.json.*;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173", "https://frontend-tossacoin.vercel.app"})
@RestController
@RequestMapping()
public class AdminController {

    @Autowired
    UserRepository userRepository;
    @Autowired
    FundraisingRepository fundraisingRepository;
    @Autowired
    TransactionRepository transactionRepository;

    //uzytkownicy
    @GetMapping("/admin/users")
    public JSONArray getUsers(){
        List<User> users = userRepository.findAll();
        return getUsersInfo(users);
    }

    //zbiorki
    @RequestMapping(value = "/admin/funds", method = GET)
    public JSONArray getFunds(){
        List<Fundraising> fundraisings = fundraisingRepository.findAll();
        return getFundraisings(fundraisings);
    }

    //toggle zbiorek
    @PatchMapping("/admin/fund")
    public JSONObject toggleFundAvailable(@RequestParam(name = "id")Long id){
        Fundraising fundraising = fundraisingRepository.getById(id);
        fundraising.setAvailable(!fundraising.isAvailable());
        fundraisingRepository.save(fundraising);
        return fundraisingToJSON(fundraising);
    }

    //toggle uzytkownikow
    @PatchMapping("/admin/user")
    public JSONObject toggleUserAvailable(@RequestParam(name = "id")Long id){
        User user = userRepository.getById(id);
        user.setBlocked(!user.getBlocked());
        userRepository.save(user);
        return getUserInfo(user);
    }


    JSONArray getUsersInfo(List<User> users){
        JSONArray jsonArray = new JSONArray();
        for(User user : users){
            jsonArray.add(getUserInfo(user));
        }

        return jsonArray;
    }

    JSONObject getUserInfo(User user){
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", user.getId());
        jsonObject.put("name", user.getName());
        jsonObject.put("surname", user.getSurname());
        jsonObject.put("email", user.getEmail());
        jsonObject.put("phone", user.getPhoneNumber());
        jsonObject.put("register_date", user.getRegisterDate());
        jsonObject.put("birth_date", user.getBirthDate());
        jsonObject.put("pesel", user.getPesel());
        jsonObject.put("bank_number", user.getBankNumber());
        jsonObject.put("role", user.getRole());
        jsonObject.put("login_type", user.getLoginType());
        jsonObject.put("blocked", user.getBlocked());
        JSONArray jsonArray = new JSONArray();
        for(Fundraising fund : user.getFundraisingIds()){
            jsonArray.add(fund.getId());
        }
        jsonObject.put("funds", jsonArray);

        return jsonObject;

    }

    public JSONArray getFundraisings(List<Fundraising> funds){
        JSONArray jsonArray = new JSONArray();
        for(Fundraising fundraising : funds){
            jsonArray.add(fundraisingToJSON(fundraising));
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

        JSONArray pictures = new JSONArray();
        List<Image> imageList = fund.getPictures();
        for (Image image: imageList) {
            pictures.add(ImageUtil.decompressImage(image.getPicture()));
        }

        jsonObj.put("pictures", pictures);

        return jsonObj;
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

}
