package bio.tech.catalog.security;

public interface ISecurityUserService {
    String validatePasswordResetToken(long id, String token);
}
