package bio.tech.catalog.utils;


import bio.tech.catalog.persistence.model.NeoParticipant;
import bio.tech.catalog.persistence.model.NeoSpecimen;
import bio.tech.catalog.persistence.model.NeoStudy;
import org.apache.commons.collections4.IteratorUtils;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StudyQuery {

    @Autowired
    private Session session;

    private boolean participateLevel;
    private boolean specimenLevel;

    public StudyQuery () {
        this.participateLevel = false;
        this.specimenLevel = false;
    }

    public LinkedHashMap<String, String> removeNullParameters(Map<String, String> params) {
        LinkedHashMap<String, String> paramsQuery = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : params.entrySet())
            if (entry.getValue() != null)
                if (!entry.getValue().isEmpty())
                    paramsQuery.put(entry.getKey(), entry.getValue());

        return paramsQuery;
    }

    public String constructQuery(Map<String, String> parameterValues) {
        String query = "";
        String queryStudyName = "";
        String queryDisease = "";
        String queryDescription = "";
        String queryDesign = "";
        String queryCountry = "";
        String queryGender = "";
        String querySpecType = "";
        String queryCharacter = "";
        String queryEthnicity = "";
        String queryBmi = "";
        String queryAge = "";

        this.participateLevel = false;
        this.specimenLevel = false;

        for (Map.Entry<String, String> entry : parameterValues.entrySet()) {

            if(entry.getValue() != "") {
                switch (entry.getKey()) {
                    case "description":
                        queryDescription = "REDUCE(res = [], w IN SPLIT({" + entry.getKey() + "}, \" \") | ";
                        queryDescription += "CASE WHEN w <> '' THEN res + (\"(?i).*\\\\b\" + w + \"\\\\b.*\") ELSE res END) AS res ";
                        break;
                    case "studyName":
                        queryStudyName = "MATCH (s:NeoStudy{acronym:'" + entry.getValue() + "'}) ";
                        break;
                    case "disease":
                        queryDisease = "MATCH (s:NeoStudy{disease:'" + entry.getValue() + "'}) ";
                        break;
                    case "country":
                        queryCountry = "MATCH(s:NeoStudy)-[:HAS_PARTICIPANT]->(p:NeoParticipant)" +
                                "-[:HAS_SPECIMEN]->(sp:NeoSpecimen)-[:HAS_COUNTRY]->(:NeoCountry {name:'"+ entry.getValue() +"'}) ";
                        break;
                    case "gender":
                        queryGender = "MATCH(s:NeoStudy)-[:HAS_PARTICIPANT]->(p:NeoParticipant)-[:HAS_GENDER]->(g:NeoGender{name:'"+ entry.getValue() +"'}) ";
                        break;
                    case "specType":
                        querySpecType = "MATCH (s:NeoStudy)-[:HAS_PARTICIPANT]->(p:NeoParticipant)-[:HAS_SPECIMEN]->(sp:NeoSpecimen)" +
                                "-[:HAS_SPECTYPE]->(st:NeoSpecType {name:{specType}}) ";
                        break;
                    case "character":
                        queryCharacter = "MATCH (s:NeoStudy)-[:HAS_PARTICIPANT]->(p:NeoParticipant)-[:HAS_CHARACTER]->(ct:NeoCharacter {name:{character}}) ";
                        break;
                    case "ethnicity":
                        queryEthnicity = "MATCH (s:NeoStudy)-[:HAS_PARTICIPANT]->(p:NeoParticipant)-[:HAS_ETHNICITY]->(ct:NeoEthnicity {name:{ethnicity}}) ";
                        break;
                    case "design":
                        queryDesign = "MATCH (s: NeoStudy)-[r:STUDY_DESIGN]->(d:NeoDesign {name: '"+ entry.getValue() +"'}) ";
                        break;
                    case "bmiOp":
                        queryBmi = "MATCH (s:NeoStudy)-[:HAS_PARTICIPANT]->(p:NeoParticipant) WHERE toFloat(p.bmi) "+ entry.getValue() +" toFloat({bmiVal}) ";
                        break;
                    case "ageOp":
                        queryAge = "MATCH (s:NeoStudy)-[:HAS_PARTICIPANT]->(p:NeoParticipant) WHERE toInt(p.age) "+ entry.getValue() +" toInt({ageVal}) ";
                    default:
                        break;
                }
            }
        }

        if (!queryStudyName.isEmpty()){
            query += queryStudyName;
            query += "WITH s ";
        }
        if (!queryDisease.isEmpty()){
            query += queryDisease;
            query += "WITH s ";
        }
        if (!queryDesign.isEmpty()) {
            query += queryDesign;
            query += "WITH s ";
        }
        if (!queryGender.isEmpty()) {
            query += queryGender;
            query += "WITH s, p ";
            this.participateLevel = true;
        }
        if (!queryCharacter.isEmpty()) {
            query += queryCharacter;
            query += "WITH s, p ";
            this.participateLevel = true;
        }
        if (!queryEthnicity.isEmpty()) {
            query += queryEthnicity;
            query += "WITH s, p ";
            this.participateLevel = true;
        }
        if (!queryBmi.isEmpty()) {
            query += queryBmi;
            query += "WITH s, p ";
            this.participateLevel = true;
        }
        if (!queryAge.isEmpty()) {
            query += queryAge;
            query += "WITH s, p ";
            this.participateLevel = true;
        }
        if (!queryCountry.isEmpty()) {
            query += queryCountry;
            query += "WITH s, p, sp ";
            this.specimenLevel = true;
        }
        if (!querySpecType.isEmpty()) {
            query += querySpecType;
            query += "WITH s, p, sp ";
            this.specimenLevel = true;
        }
        if (!queryDescription.isEmpty()) {
            if (query.isEmpty()){
                query += "MATCH (s:NeoStudy) with s";
            }
            query += ", " + queryDescription;
            query += "WHERE ALL (regexp IN res WHERE s.description =~ regexp) ";
        }
        if (!query.isEmpty()) {
            if (this.specimenLevel)
                query += "RETURN s, p, sp";
            else if (this.participateLevel)
                query += "RETURN s, p";
            else
                query += "RETURN s";
        }

        return query;
    }

    public Collection<NeoStudy> runNeoQuery(String query, Map<String, String> paramsQuery) {
        Collection<NeoStudy> studies = new HashSet<>();
        Result result = session.query(query, paramsQuery);
        List<Map<String, Object>> mapStudies = IteratorUtils.toList(result.iterator());

        for (Map<String, Object> i : mapStudies) {
            NeoStudy study = null;
            for (Map.Entry<String, Object> entry : i.entrySet()) {
                if (entry.getKey().equals("s")) {
                    study = (NeoStudy)entry.getValue();
                    studies.add(session.load(NeoStudy.class, study.getId()));
                    if (!this.participateLevel && !this.specimenLevel) {
                        study.setSearchNoSpecimens(study.getNoSpecimens());
                        study.setSearchNoParticipants(study.getParticipants().size());
                    }
                } else {
                    if (this.specimenLevel) {
                        if (entry.getKey().equals("sp")) {

                            NeoSpecimen specimen = (NeoSpecimen) entry.getValue();
                            if (specimen.getNoAliquots() > 0)
                                study.setSearchNoSpecimens(study.getSearchNoSpecimens() + 1);

                        } else if (entry.getKey().equals("p")) {

                            NeoParticipant participant = (NeoParticipant) entry.getValue();
                            if (participant.getNoSpecimens() > 0)
                                study.setSearchNoParticipants(study.getSearchNoParticipants() + 1);

                        }
                    } else if (this.participateLevel) {
                        if (entry.getKey().equals("p")) {
                            NeoParticipant participant = (NeoParticipant) entry.getValue();
                            int noSpecimens = study.getSearchNoSpecimens() + participant.getNoSpecimens();
                            study.setSearchNoSpecimens(noSpecimens);
                            if (participant.getNoSpecimens() > 0)
                                study.setSearchNoParticipants(study.getSearchNoParticipants() + 1);
                        }
                    }
                }
            }
        }

        return studies;
    }

    // getters
    public String getQueryLevel() {

        if (this.specimenLevel)
            return "specimen";

        else if (this.participateLevel)
            return "participant";

        return "study";
    }
}
