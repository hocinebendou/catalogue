package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.NeoDataQuery;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DataQueryRepository extends PagingAndSortingRepository<NeoDataQuery, Long> {

    @Query( "MATCH (c:NeoCart) " +
            "WHERE ID(c) = {id} " +
            "MATCH (c)-[]->(q:NeoDataQuery) " +
            "RETURN q")
    List<NeoDataQuery> cartQueries(@Param("id") Long id);

}
