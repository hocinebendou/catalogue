package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.NeoCart;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CartRepository extends PagingAndSortingRepository<NeoCart, Long> {

    NeoCart findNeoCartByCartId(@Param("cartId") String cartId);

    List<NeoCart> findNeoCartByStatus(@Param("status") String status);

    @Query( "MATCH (p:NeoProject) " +
            "WHERE ID(p) = {id} " +
            "MATCH (p)-[]->(c:NeoCart) " +
            "RETURN c")
    List<NeoCart> projectCarts(@Param("id") Long id);

    @Query( "MATCH (p:NeoProject) " +
            "WHERE ID(p) = {projectId} " +
            "MATCH (p)-[]->(c:NeoCart {cartId: {cartId}}) " +
            "RETURN c")
    NeoCart findNeoCartByProjectIdAndCartId(@Param("projectId") Long projectId,
                                            @Param("cartId") String cartId);

}
