package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.Privilege;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PrivilegeRepository extends PagingAndSortingRepository<Privilege, Long> {

    Privilege findByName(String name);

    @Query ("MATCH (r:Role {name: {name}})" +
            "-[]->(p:Privilege) " +
            "RETURN p")
    List<Privilege> findByRoleName(@Param("name") String name);

    @Override
    void delete(Privilege privilege);
}
