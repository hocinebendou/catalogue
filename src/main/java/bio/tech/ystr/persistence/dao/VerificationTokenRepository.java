package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.User;
import bio.tech.ystr.persistence.model.VerificationToken;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

public interface VerificationTokenRepository extends PagingAndSortingRepository<VerificationToken, Long> {

    VerificationToken findByToken(String token);

    VerificationToken findByUser(Collection<User> user);

    Stream<VerificationToken> findAllByExpiryDateLessThan(Date now);

    void deleteByExpiryDateLessThan(Date now);

    @Query( "MATCH (t:VerificationToken) " +
            "WHERE t.expiryDate <= {date} " +
            "detach delete t")
    void deleteAllExpiredSince(@Param("date") Date now);
}
