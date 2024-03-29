package com.mycompany.model.fundraising;

import com.mycompany.model.category.Category;
import com.mycompany.model.category.CategoryRepository;
import com.mycompany.model.image.Image;
import com.mycompany.model.transaction.Transaction;
import com.mycompany.model.transaction.TransactionRepository;
import com.mycompany.model.user.User;
import com.mycompany.model.user.UserRepository;
import com.mycompany.utilts.ImageUtil;
import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

//@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173", "https://frontend-tossacoin.vercel.app"})
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
        System.out.println("home");
        List<Fundraising> funds = fundraisingRepository.findAllByAvailableIsTrueOrderByFundraisingStart();
        JSONArray jsonArray = new JSONArray();
        for(Fundraising fund: funds) {
            jsonArray.add(fundraisingToJSON(fund));
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array", jsonArray);

        return jsonObject;
    }

    @GetMapping("/amounts")
    public JSONObject fundAmount(@RequestParam(name = "id")String id){
        Long fundId = Long.parseLong(id);
        Fundraising fundraising = fundraisingRepository.getById(fundId);
        List<Transaction> transactions = transactionRepository.findAllByFundraisingId(fundId);
        if(transactions==null){
            return new JSONObject();
        }
        JSONArray jsonArray = new JSONArray();
        Date previousDate = fundraising.getFundraisingStart();
        int dayAmount=0;
        for(Transaction transaction: transactions){
            if(previousDate.compareTo(transaction.getDate())!=0){
                JSONObject day = new JSONObject();
                day.put("date", previousDate);
                day.put("amount", dayAmount);
                jsonArray.add(day);
                dayAmount=0;
                previousDate = transaction.getDate();
            }
            dayAmount+=transaction.getAmount();

        }
        JSONObject day = new JSONObject();
        day.put("date", previousDate);
        day.put("amount", dayAmount);
        jsonArray.add(day);
        JSONObject res = new JSONObject();
        res.put("dates", jsonArray);
        return res;
    }

    @GetMapping("/search")
    public JSONArray searchByFunds(@RequestParam(name = "phrase")String phrase){
        System.out.println("search");
        //String phrase = "";
        int page = 0;
        Page<Fundraising> funds;
        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        funds = fundraisingRepository.findAllByAvailableIsTrueAndTitleContainsAndFundraisingEndAfter(phrase, today, PageRequest.of(page, 6));
        JSONArray jsonArray = new JSONArray();
        for(Fundraising fund: funds) {
            jsonArray.add(fundraisingToJSON(fund));

        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("array", jsonArray);
        jsonObject.put("pages", funds.getTotalPages());

        return jsonArray;
    }


    @GetMapping("/fundraising")
    @ResponseBody
    public JSONObject getFundraising(@RequestParam(name = "id")Long id){
        System.out.println("fund");
        Optional<Fundraising> optionalFundraising = fundraisingRepository.findById(id);
        if(optionalFundraising.isPresent()){
            return fundraisingToJSON(optionalFundraising.get());
        }
        return new JSONObject();
    }


    @ResponseBody
    @PostMapping("/createFundraising")
    public JSONObject createFundraising(Authentication authentication, @RequestBody String data)  {

        JSONObject jsonObject = new JSONObject();
        org.json.JSONObject request = new org.json.JSONObject(data);

        User owner = userRepository.getUserByEmail(authentication.getName());
        Category category = categoryRepository.findByName(request.getString("category"));
        List<Category> categoryList = new ArrayList<>();
        categoryList.add(category);
        Date date = new Date();
        try {
            LocalDate dateTemp = LocalDate.parse(request.getString("date"));
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MMM-dd", Locale.getDefault());
            date = Date.from(dateTemp.atStartOfDay(ZoneId.systemDefault()).toInstant());

        }catch (Exception e){
            e.printStackTrace();
        }

        Date today = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());

        Fundraising fundraising = new Fundraising();
        fundraising.setTitle(request.getString("title"));
        fundraising.setGoal(request.getInt("goal"));
        fundraising.setOwner(owner);
        fundraising.setCategory(categoryList);
        fundraising.setAvailable(true);
        fundraising.setDescription(request.getString("description"));
        fundraising.setFundraisingEnd(date);
        fundraising.setFundraisingStart(today);

        fundraisingRepository.save(fundraising);


        fundraising = fundraisingRepository.getFundraisingByTitle(request.getString("title"));
        jsonObject.put("id", fundraising.getId());

        return jsonObject;

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

