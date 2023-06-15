package com.mycompany;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication

public class MyWebAppApplication {
    //public static Dotenv dotenv;
    public static void main(String[] args) {
        //dotenv = Dotenv.load();
        System.getenv("CLIENT_ID_PAYU");
        SpringApplication.run(MyWebAppApplication.class, args);
    }

}
