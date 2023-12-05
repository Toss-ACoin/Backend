package com.mycompany.model.transaction;


import com.mycompany.model.fundraising.Fundraising;
import com.mycompany.model.fundraising.FundraisingRepository;
import com.mycompany.model.user.User;
import com.mycompany.model.user.UserRepository;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;


//@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173", "https://frontend-tossacoin.vercel.app"})
@RestController
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    FundraisingRepository fundraisingRepository;

    @PostMapping("/transaction")
    public JSONObject saveTransaction(Authentication authentication, @RequestBody String data){
        JSONObject jsonObject = new JSONObject();
        org.json.JSONObject tempObject = new org.json.JSONObject(data);
        long id = tempObject.getLong("id");
        int amount = tempObject.getInt("amount");
        Transaction transaction = new Transaction();
        transaction.setFundraisingId(id);
        transaction.setDate(Date.from(LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()));
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.PAYPAL);
        User user = userRepository.getUserByEmail(authentication.getName());
        transaction.setPayerId(user.getId());
        transactionRepository.save(transaction);
        updateFundraising(amount, id);
        boolean isFundDone = checkIfGoalIsAchived(id);

        jsonObject.put("complete", true);
        jsonObject.put("isDone", isFundDone);
        return jsonObject;
    }

    private boolean checkIfGoalIsAchived(long id){
        Optional<Fundraising> fundraisingOptional = fundraisingRepository.findById(id);
        if(fundraisingOptional.isPresent()){
            Fundraising temp = fundraisingOptional.get();
            return temp.getGoal() >= temp.getCollectedMoney();
        }
        return false;
    }

    private void updateFundraising(int amount, long id){
        Optional<Fundraising> fundraisingOptional = fundraisingRepository.findById(id);
        if(fundraisingOptional.isPresent()){
            Fundraising temp = fundraisingOptional.get();
            int collected = temp.getCollectedMoney() + amount;
            temp.setCollectedMoney(collected);
            fundraisingRepository.save(temp);
        }

    }
}
