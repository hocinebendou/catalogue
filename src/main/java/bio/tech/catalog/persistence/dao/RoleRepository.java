package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.Role;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface RoleRepository extends PagingAndSortingRepository<Role, Long> {

    Role findByName(@Param("name") String name);

    @Query ("MATCH (u:User) WHERE ID(u)={id} " +
            "MATCH (u)-[]->(r:Role) " +
            "RETURN r")
    Collection<Role> findByUserId(@Param("id") Long id);

    @Override
    void delete(Role role);
}
