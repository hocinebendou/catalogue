package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoQuery;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QueryRepository extends PagingAndSortingRepository<NeoQuery, Long> {

    @Query( "MATCH (c:NeoCart) " +
            "WHERE ID(c) = {id} " +
            "MATCH (c)-[]->(q:NeoQuery) " +
            "RETURN q " +
            "ORDER BY ID(q) DESC")
    List<NeoQuery> cartQueries(@Param("id") Long id);
}
