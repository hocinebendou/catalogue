package bio.tech.ystr.service;

import bio.tech.ystr.persistence.model.User;
import bio.tech.ystr.web.dto.UserDto;
import bio.tech.ystr.web.error.UserAlreadyExistException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IUserService {

    User registerNewUserAccount(UserDto accountDto) throws UserAlreadyExistException;

    User getUser(String verificationToken);

    void saveRegisteredUser(User user);

    void deleteUser(Collection<User> user);

    void createVerificationTokenForUser(Collection<User> user, String token);

    User findUserByEmail(String email);

    User getUserByID(long id);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String password);

    List<String> getUsersFromSessionRegistry();

    String validateVerificationToken(String token);
}
