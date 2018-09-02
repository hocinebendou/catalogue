package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.PasswordResetToken;
import bio.tech.catalog.persistence.model.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

public interface PasswordResetTokenRepository extends PagingAndSortingRepository<PasswordResetToken, Long> {

    PasswordResetToken findByToken(String token);
    PasswordResetToken findByUser(Collection<User> user);
    Stream<PasswordResetToken> findAllByExpiryDateLessThan(Date now);
    void deleteByExpiryDateLessThan(Date now);

    @Query( "MATCH (t:PasswordResetToken) " +
            "WHERE t.expiryDate <= {date} " +
            "detach delete t")
    void deleteAllExpiredSince(@Param("date") Date now);
}
