package com.mycompany.model.category;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173", "https://frontend-tossacoin.vercel.app"})
@RestController
public class CategoryController {

    @Autowired
    CategoryRepository repository;
    @GetMapping("/category")
    public JSONObject getAllCategory(){
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<Category> categoryList = repository.findAll();
        for (Category category: categoryList) {
            jsonArray.add(category.getName());
        }
        jsonObject.put("categories_array", jsonArray);
        return jsonObject;
    }
}
