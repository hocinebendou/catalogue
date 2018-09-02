package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.NeoDataSet;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DatasetRepository extends PagingAndSortingRepository<NeoDataSet, Long> {
    List<NeoDataSet> findAll();

    @Query( "MATCH (s:NeoStudy{acronym: {acronym}})" +
            "-[]->(d:NeoDataSet) " +
            "RETURN d")
    List<NeoDataSet> findAllByStudyAcronym(@Param("acronym") String acronym);
}
