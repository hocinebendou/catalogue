package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoParticipant;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface ParticipantRepository extends PagingAndSortingRepository<NeoParticipant, Long> {

    NeoParticipant findNeoParticipantByParticipantId(@Param("participantId") String participantId);

    NeoParticipant findNeoParticipantByAcronym(@Param("acronym") String study);

    Collection<NeoParticipant> findNeoParticipantByAcronymLike(@Param("acronym") String acronym);

    // query participant ids by gender
    @Query( "MATCH (p:NeoParticipant) -[]->(g:NeoGender) " +
            "WHERE g.name IN  {names} " +
            "RETURN p.participantId")
    List<String> findBySex(@Param("names") List<String> names);

    // query participant ids by ethnicity
    @Query( "MATCH (p:NeoParticipant)-[]->(e:NeoEthnicity) " +
            "WHERE e.name IN {names} " +
            "RETURN p.participantId")
    List<String> findByEthnicity(@Param("names") List<String> names);

    // query participant ids by acronyms
    @Query( "MATCH (s:NeoStudy)-[]->(p:NeoParticipant) " +
            "WHERE s.acronym IN {acronyms} " +
            "RETURN p.participantId")
    List<String> findByAcronyms(@Param("acronyms") List<String> acronyms);

    // query participant ids by acronyms and gender
    @Query( "MATCH (s: NeoStudy)" +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant)-[]->(g:NeoGender) " +
            "WHERE g.name IN {names} " +
            "RETURN p.participantId")
    List<String> findByAcronymsAndSex(@Param("acronyms") List<String> acronyms,
                                      @Param("names") List<String> names);

    // query participant ids by acronyms and ethnicity
    @Query( "MATCH (s: NeoStudy)" +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant)-[]->(e:NeoEthnicity) " +
            "WHERE e.name IN {names} " +
            "RETURN p.participantId")
    List<String> findByAcronymsAndEthnicity(@Param("acronyms") List<String> acronyms,
                                            @Param("names") List<String> names);

    // query participant ids by gender and ethnicity
    @Query( "MATCH (s: NeoStudy)-[]->(p:NeoParticipant)-[]->(g:NeoGender)" +
            "WHERE g.name IN {sex} " +
            "MATCH (s)-[]->(p)-[]->(e:NeoEthnicity) " +
            "WHERE e.name IN {ethnicity} " +
            "RETURN p.participantId")
    List<String> findBySexAndEthnicity(@Param("sex") List<String> sex,
                                       @Param("ethnicity") List<String> ethnicity);

    // query participant ids by gender and ethnicity
    @Query( "MATCH (s: NeoStudy) " +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant)-[]->(g:NeoGender) " +
            "WHERE g.name IN {sex} " +
            "MATCH (s)-[]->(p)-[]->(e:NeoEthnicity) " +
            "WHERE e.name IN {ethnicity}" +
            "RETURN p.participantId")
    List<String> findByAcronymsAndSexAndEthnicity(@Param("acronyms") List<String> acronyms,
                                                  @Param("sex") List<String> sex,
                                                  @Param("ethnicity") List<String> ethnicity);
}
