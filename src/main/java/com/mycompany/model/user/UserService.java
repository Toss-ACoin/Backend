package com.mycompany.model.user;

import com.mycompany.utilts.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
       user.setRole(UserRole.ROLE_USER);

       userRepository.save(user);

        return userRepository.getUserByEmail(email);
    }
}
