package bio.tech.catalog.persistence.dao;

import bio.tech.catalog.persistence.model.NeoSpecQuery;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpecQueryRepository extends PagingAndSortingRepository<NeoSpecQuery, Long> {

    NeoSpecQuery findNeoSpecQueryBySpecimenIdAndQueryId(@Param("specimenId") String specimenId,
                                                        @Param("queryId") Long queryId);

    List<NeoSpecQuery> findNeoSpecQueriesBySpecimenIdAndQueryId(@Param("specimenId") String specimenId,
                                                                @Param("queryId") Long queryId);
}
