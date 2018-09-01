package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.Role;
import bio.tech.ystr.persistence.model.User;
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

    @Query( "MATCH (u:NeoUser)-[]->" +
            "(p:NeoProject)-[]->(c:NeoCart) " +
            "WHERE ID(c) = {id} " +
            "RETURN u")
    User findNeoUserByCartId(@Param("id") Long id);
}
