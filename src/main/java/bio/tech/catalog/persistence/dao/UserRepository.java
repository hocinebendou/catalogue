package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.User;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;

public interface UserRepository extends PagingAndSortingRepository<User, Long> {

    Collection<User> findAll();

    User findByEmail(String email);

    User findByUsername(String username);

    @Override
    void delete(User user);

    @Query ("MATCH (u:User)-[]->(r:Role {name:{role}}) " +
            "RETURN u")
    Collection<User> findAllByRole(@Param("role") String role);

    @Query( "MATCH (u:User)-[]->" +
            "(p:NeoProject)-[]->(c:NeoCart) " +
            "WHERE ID(c) = {id} " +
            "RETURN u")
    User findUserByCartId(@Param("id") Long id);
}
