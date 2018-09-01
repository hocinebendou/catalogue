package bio.tech.ystr.service;

import bio.tech.ystr.persistence.dao.PasswordResetTokenRepository;
import bio.tech.ystr.persistence.dao.RoleRepository;
import bio.tech.ystr.persistence.dao.UserRepository;
import bio.tech.ystr.persistence.dao.VerificationTokenRepository;
import bio.tech.ystr.persistence.model.PasswordResetToken;
import bio.tech.ystr.persistence.model.Role;
import bio.tech.ystr.persistence.model.User;
import bio.tech.ystr.persistence.model.VerificationToken;
import bio.tech.ystr.web.dto.UserDto;
import bio.tech.ystr.web.error.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService implements IUserService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    private static final String TOKEN_INVALID = "invalidToken";
    private static final String TOKEN_EXPIRED = "expired";
    private static final String TOKEN_VALID = "valid";

    @Override
    public User registerNewUserAccount(UserDto accountDto) {
        if (emailExist(accountDto.getEmail())) {
            throw new UserAlreadyExistException("There is an account with that email adress: " + accountDto.getEmail());
        } else if (usernameExist(accountDto.getUsername())) {
            throw new UserAlreadyExistException("There is an account with that username: " + accountDto.getUsername());
        }

        final User user = new User();

        user.setFirstName(accountDto.getFirstName());
        user.setLastName(accountDto.getLastName());
        user.setInstitutionName(accountDto.getInstitutionName());
        user.setUsername(accountDto.getUsername());
        user.setPassword(passwordEncoder.encode(accountDto.getPassword()));
        user.setEmail(accountDto.getEmail());
        Collection<Role> roles = new ArrayList<>();
        roles.add(roleRepository.findByName("ROLE_USER"));
        user.setRoles(roles);

        return repository.save(user);
    }

    @Override
    public User getUser(final String verificationToken) {
        final VerificationToken token = tokenRepository.findByToken(verificationToken);
        if (token != null) {
            return token.getUser().stream().findFirst().orElse(null);
        }
        return null;
    }

    @Override
    public VerificationToken getVerificationToken(String verificationToken) {
        return tokenRepository.findByToken(verificationToken);
    }

    @Override
    public void saveRegisteredUser(final User user) {
        repository.save(user);
    }

    @Override
    public void deleteUser(final Collection<User> user) {
        final VerificationToken verificationToken = tokenRepository.findByUser(user);
        if (verificationToken != null) {
            tokenRepository.delete(verificationToken);
        }

        final PasswordResetToken passwordToken = passwordResetTokenRepository.findByUser(user);
        if (passwordToken != null) {
            passwordResetTokenRepository.delete(passwordToken);
        }

        user.stream().findFirst().ifPresent(u -> repository.delete(u));
    }

    @Override
    public void createVerificationTokenForUser(final Collection<User> user, final String token){
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        final VerificationToken verificationToken = tokenRepository.findByToken(token);
        if (verificationToken == null) {
            return TOKEN_INVALID;
        }

        final Collection<User> collection = verificationToken.getUser();
        final User user = collection.stream().findFirst().orElse(null);
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <=0) {
            tokenRepository.delete(verificationToken);
            return TOKEN_EXPIRED;
        }

        if (user == null) {
            return null;
        }

//        user.setEnabled(true);
        user.setEmailEnabled(true);
        repository.save(user);

        return TOKEN_VALID;
    }

    @Override
    public User findUserByEmail(final String email) {

        return repository.findByEmail(email);
    }

    @Override
    public User findUserByUsername(final String username) {

        return repository.findByUsername(username);
    }

    @Override
    public User getUserByID(final long id) {
        return repository.findOne(id);
    }

    @Override
    public void changeUserPassword(final User user, final String password) {
        user.setPassword(passwordEncoder.encode(password));
        repository.save(user);
    }

    @Override
    public boolean checkIfValidOldPassword(final User user, final String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public List<String> getUsersFromSessionRegistry() {
        return sessionRegistry.getAllPrincipals().stream().filter((u) -> !sessionRegistry.getAllSessions(u, false).isEmpty()).map(Object::toString).collect(Collectors.toList());
    }

    @Override
    public void createPasswordResetTokenForUser(final Collection<User> user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);

    }

    @Override
    public Collection<User> findUsersByRole(String role) {

        return repository.findAllByRole(role);
    }

    @Override
    public User findUserByCartId(Long id) {

        return repository.findUserByCartId(id);
    }

    @Override
    public Collection<User> findAllUsers() {

        return repository.findAll();
    }

    private boolean emailExist(final String email) {
        return repository.findByEmail(email) != null;
    }

    private boolean usernameExist(final String username) {
        return repository.findByUsername(username) != null;
    }


}
