package com.mycompany.model.image;


import com.mycompany.model.fundraising.Fundraising;
import com.mycompany.model.fundraising.FundraisingRepository;
import com.mycompany.utilts.ImageUtil;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = {"https://frontend-eight-lime-76.vercel.app/", "http://localhost:5173", "https://frontend-tossacoin.vercel.app"})
@RestController
public class ImageController {

    @Autowired
    ImageRepository imageRepository;
    @Autowired
    FundraisingRepository fundraisingRepository;

    @PostMapping("/uploadImage")
    public JSONObject uploadImage(@RequestParam("image") MultipartFile file, @RequestParam("id")Long id){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("upload", HttpStatus.NOT_ACCEPTABLE);
        Image image = new Image();
        try {
            image.setPicture(ImageUtil.compressImage(file.getBytes()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        image.setFilename(file.getOriginalFilename());
        imageRepository.save(image);

        Optional<Image> imageOptional = imageRepository.findImageByFilename(file.getOriginalFilename());
        if(imageOptional.isPresent()){
            jsonObject.put("upload", HttpStatus.OK);
            image = imageOptional.get();
            Optional<Fundraising> optionalFundraising = fundraisingRepository.findById(id);
            if(optionalFundraising.isPresent()){
                Fundraising fundraising = optionalFundraising.get();
                List<Image> imageList = fundraising.getPictures();
                imageList.add(image);
                fundraising.setPictures(imageList);
                fundraisingRepository.save(fundraising);
            }
        }

        return jsonObject;
    }

}
