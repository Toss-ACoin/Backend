package com.mycompany.utilts;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public String changePassword(String passwordToChange){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(passwordToChange);
    }
    public static void main(String[] args){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "test";
        String encodedPassword = encoder.encode(rawPassword);

        System.out.println(encodedPassword);
    }
}
