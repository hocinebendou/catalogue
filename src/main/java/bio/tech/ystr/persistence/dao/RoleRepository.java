package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.Role;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {

    Role findByName(@Param("name") String name);

    @Query ("MATCH (u:User) WHERE ID(u)={id} " +
            "MATCH (u)-[]->(r:Role) " +
            "RETURN r")
    List<Role> findByUserId(@Param("id") Long id);

    @Override
    void delete(Role role);
}
