package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoEthnicity;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

/**
 * Created by hocine on 2017/11/27.
 */
public interface EthnicityRepository extends PagingAndSortingRepository<NeoEthnicity, Long> {

    List<NeoEthnicity> findAll();

    // query all ethnicity names
    @Query( "MATCH (e: NeoEthnicity) " +
            "RETURN e.name")
    List<String> findAllEthnicityNames();
}
