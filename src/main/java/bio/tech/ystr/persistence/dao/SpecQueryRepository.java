package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoSpecQuery;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpecQueryRepository extends PagingAndSortingRepository<NeoSpecQuery, Long> {

    NeoSpecQuery findNeoSpecQueryBySpecimenIdAndQueryId(@Param("specimenId") String specimenId,
                                                        @Param("queryId") Long queryId);

    List<NeoSpecQuery> findNeoSpecQueriesBySpecimenIdAndQueryId(@Param("specimenId") String specimenId,
                                                                @Param("queryId") Long queryId);
}
