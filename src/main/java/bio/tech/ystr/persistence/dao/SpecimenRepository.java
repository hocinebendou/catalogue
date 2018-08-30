package bio.tech.ystr.persistence.dao;

import bio.tech.ystr.persistence.model.NeoSpecimen;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Created by hocine on 2017/10/10.
 */
public interface SpecimenRepository extends PagingAndSortingRepository<NeoSpecimen, Long> {

    NeoSpecimen findNeoSpecimenBySampleId(@Param("sampleId") String sampleId);

    @Query( "MATCH (s:NeoSpecimen)-[rc:HAS_COUNTRY]->(c:NeoCountry) MATCH (s)-[rt:HAS_SPECTYPE]->(t:NeoSpecType) " +
            "RETURN s,rc,c,rt,t LIMIT {limit}")
    Collection<NeoSpecimen> graph(@Param("limit") int limit);

    Collection<NeoSpecimen> findAll();

    /* ------------------------------------------------ */

    // query by specimen types
    @Query( "MATCH (s:NeoStudy)-[]->(p:NeoParticipant)-[]->(m:NeoSpecimen) " +
            "-[]->(t:NeoSpecType) " +
            "WHERE t.name IN {names} " +
            "RETURN m")
    List<NeoSpecimen> findBySpecimenTypes(@Param("names") List<String> names);

    // query by countries
    @Query( "MATCH (s:NeoStudy)-[]->(p:NeoParticipant)-[]->(m:NeoSpecimen) " +
            "-[]->(c:NeoCountry) " +
            "WHERE c.name IN {names} " +
            "RETURN m")
    List<NeoSpecimen> findByCountries(@Param("names") List<String> names);

    // query by acronyms
    @Query( "MATCH (s:NeoStudy) " +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant)-[]->(m:NeoSpecimen) " +
            "RETURN m")
    List<NeoSpecimen> findByAcronyms(@Param("acronyms") List<String> acronyms);

    // query by participant ids
    @Query( "MATCH (s:NeoStudy)-[]->(p:NeoParticipant) " +
            "WHERE p.participantId IN {ids} " +
            "MATCH (s)-[]->(p)-[]->(m:NeoSpecimen) " +
            "RETURN m")
    List<NeoSpecimen> findByParticipantIds(@Param("ids") List<String> ids);

    // query by acronyms and participant ids
    @Query( "MATCH (s:NeoStudy) " +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant) " +
            "WHERE p.participantId IN {ids} " +
            "MATCH (s)-[]-(p)-[]->(m:NeoSpecimen) " +
            "RETURN m")
    List<NeoSpecimen> findByAcronymsAndParticipantIds(@Param("acronyms") List<String> acronyms,
                                                      @Param("ids") List<String> ids);

    // query by acronyms and specimen types
    @Query( "MATCH (s:NeoStudy) " +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant)-[]->(m:NeoSpecimen)-[]->(t:NeoSpecType) " +
            "WHERE t.name IN {types} " +
            "RETURN m")
    List<NeoSpecimen> findByAcronymsAndTypes(@Param("acronyms") List<String> acronyms,
                                             @Param("types") List<String> types);

    // query by acronyms and countries
    @Query( "MATCH (s:NeoStudy) " +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoCountry)-[]->(m:NeoSpecimen)-[]->(c:NeoCountry) " +
            "WHERE c.name IN {countries} " +
            "RETURN m")
    List<NeoSpecimen> findByAcronymsAndCountries(@Param("acronyms") List<String> acronyms,
                                                 @Param("countries") List<String> countries);

    // query by participant ids and specimen types
    @Query( "MATCH (s:NeoStudy)-[]->(p:NeoParticipant) " +
            "WHERE p.participantId IN {ids} " +
            "MATCH (s)-[]->(p)-[]->(m:NeoSpecimen)-[]->(t:NeoSpecType) " +
            "WHERE t.name IN {types} " +
            "RETURN m")
    List<NeoSpecimen> findByParticipantIdsAndTypes(@Param("ids") List<String> ids,
                                                   @Param("types") List<String> types);

    // query by participant ids and countries
    @Query( "MATCH (s:NeoStudy)-[]->(p:NeoParticipant) " +
            "WHERE p.participantId IN {ids} " +
            "MATCH (s)-[]->(p)-[]->(m:NeoSpecimen)-[]->(c:NeoCountry) " +
            "WHERE c.name IN {countries} " +
            "RETURN m")
    List<NeoSpecimen> findByParticipantIdsAndCountries(@Param("ids") List<String> ids,
                                                       @Param("countries") List<String> countries);

    // query by types and countries
    @Query( "MATCH(s:NeoStudy)-[]->(p:NeoParticipant)-[]->(m:NeoSpecimen)-[]->(t:NeoSpecType) " +
            "WHERE t.name IN {types} " +
            "MATCH (s)-[]->(p)-[]->(m)-[]->(c:NeoCountry) " +
            "WHERE c.name IN {countries} " +
            "RETURN m")
    List<NeoSpecimen> findByTypesAndCountries(@Param("types") List<String> types,
                                              @Param("countries") List<String> countries);

    // query by acronyms, participant ids and specimen types
    @Query( "MATCH (s:NeoStudy) " +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant) " +
            "WHERE p.participantId IN {ids} " +
            "MATCH (s)-[]->(p)-[]->(m:NeoSpecimen)-[]->(t:NeoSpecType) " +
            "WHERE t.name IN {types} " +
            "RETURN m")
    List<NeoSpecimen> findByAcronymsAndParticipantIdsAndTypes(@Param("acronyms") List<String> acronyms,
                                                              @Param("ids") List<String> ids,
                                                              @Param("types") List<String> types);

    // query by acronyms, participant ids and countries
    @Query( "MATCH (s:NeoStudy) " +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant) " +
            "WHERE p.participantId IN {ids} " +
            "MATCH (s)-[]->(p)-[]->(m:NeoSpecimen)-[]->(c:NeoCountry) " +
            "WHERE c.name IN {countries} " +
            "RETURN m")
    List<NeoSpecimen> findByAcronymsAndParticipantIdsAndCountries(@Param("acronyms") List<String> acronyms,
                                                                  @Param("ids") List<String> ids,
                                                                  @Param("countries") List<String> countries);

    // query by acronyms, participant ids, types and countries
    @Query( "MATCH(s:NeoStudy) " +
            "WHERE s.acronym IN {acronyms} " +
            "MATCH (s)-[]->(p:NeoParticipant) " +
            "WHERE p.participantId IN {ids} " +
            "MATCH (s)-[]->(p)-[]->(m:NeoSpecimen)-[]->(t:NeoSpecType) " +
            "WHERE t.name IN {types} " +
            "MATCH (s)-[]->(p)-[]->(m)-[]->(c:NeoCountry) " +
            "WHERE c.name IN {countries} " +
            "RETURN m")
    List<NeoSpecimen> findByAcronymsAndParticipantIdsAndTypesAndCountries(@Param("acronyms") List<String> acronyms,
                                                                          @Param("ids") List<String> ids,
                                                                          @Param("types") List<String> types,
                                                                          @Param("countries") List<String> countries);
}
