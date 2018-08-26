package bio.tech.ystr.security;

public interface ISecurityUserService {
    String validatePasswordResetToken(long id, String token);
}
