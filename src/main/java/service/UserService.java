package service;

import domain.User;
import java.util.List;
import java.util.UUID;

public interface UserService {

    List<User> getAllUsers() throws Exception;
    User getUserById(UUID userId) throws Exception;
    User createUser(User user) throws Exception;
    void updateUser(User user) throws Exception;
    void deleteUser(UUID userId) throws Exception;

    boolean authenticateUser(String email, String plainPassword) throws Exception;

}
