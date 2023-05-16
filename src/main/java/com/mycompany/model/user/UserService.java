package com.mycompany.model.user;

import com.mycompany.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.swing.text.html.parser.Entity;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User registerUser(String name, String email, String password){
        User user = new User();
        user.setEmail(email);
        user.setName(name);

       PasswordGenerator passwordGenerator = new PasswordGenerator();
       String encodedPassword = passwordGenerator.changePassword(password);

       user.setPassword(encodedPassword);
       user.setBlocked(false);
       user.setLoginType(LoginType.BASIC);
       user.setRole(UserRole.USER);

       userRepository.save(user);

        return userRepository.getUserByEmail(email);
    }
}
