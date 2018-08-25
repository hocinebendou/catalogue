package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.Role;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {

    Role findByName(@Param("name") String name);

    @Override
    void delete(Role role);
}
