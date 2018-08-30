package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    User findByEmail(String email);

    User findByUsername(String username);

    @Override
    void delete(User user);

}
