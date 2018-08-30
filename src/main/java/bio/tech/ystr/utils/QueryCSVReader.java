package bio.tech.ystr.utils;

import bio.tech.ystr.forms.BiospecimenForm;
import bio.tech.ystr.persistence.dao.StudyRepository;
import bio.tech.ystr.persistence.model.*;
import org.apache.commons.collections4.IteratorUtils;
import org.neo4j.ogm.model.Result;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import java.io.*;
import java.util.*;

@Component
public class QueryCSVReader {

    private File csvFile;
    private String separator;
    private NeoRequest neoRequest;

    @Autowired
    private StudyQuery studyQuery;

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private Session session;

    public QueryCSVReader() {
        this.csvFile = null;
        this.separator = ",";
        this.neoRequest = null;
    }

    public void setNeoRequest(NeoRequest neoRequest) {
        this.neoRequest = neoRequest;
        File folder = new File(neoRequest.getPathDocuments());
        for (File file: folder.listFiles()) {
            if (file.getName().endsWith(".csv")) {
                this.csvFile = file;
                break;
            }
        }
    }

    public Collection<BiospecimenForm> biospecimensCSV() {

        Collection<BiospecimenForm> biospecimens = new ArrayList<>();
        String[] acronyms = this.neoRequest.getAcronyms().split(", ");
        LinkedHashMap<String, String> parameters = queryParameters();

        int i = 0;
        for (String acronym: acronyms) {

            parameters.put("studyName", acronym);
            // if acronym is knows the parameters below are not necessary. No need to complicate the query,
            // thus, we set them at empty.
            parameters.put("description", "");
            parameters.put("disease", "");
            parameters.put("design", "");
            if (i == 0) {
                parameters = studyQuery.removeNullParameters(parameters);
                i++;
            }
            String query = studyQuery.constructQuery(parameters);
            String level = studyQuery.getQueryLevel();
            switch(level) {
                case "study":
                    biospecimens.addAll(studyLevelQuery(parameters));
                    break;
                case "participant":
                case "specimen":
                    biospecimens.addAll(otherLevelQuery(parameters, query, level));
                    break;
            }
        }
        return biospecimens;
    }

    private LinkedHashMap<String, String> queryParameters () {
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
        BufferedReader br = null;
        String line = "";
        try {
            br = new BufferedReader(new FileReader(this.csvFile));

            String[] header = br.readLine().split(this.separator);

            while ((line = br.readLine()) != null) {
                String[] row = line.split(this.separator, -1);
                for (int i = 0; i < header.length; i++) {
                    parameters.put(header[i], row[i]);
                }
            }
        }catch (FileNotFoundException ex) {

        }catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return parameters;
    }

    private Collection<BiospecimenForm> studyLevelQuery(LinkedHashMap<String, String> parameters) {

        Collection<BiospecimenForm> biospecimens = new ArrayList<>();
        NeoStudy study = studyRepository.findByAcronym(parameters.get("studyName"));
        study = session.load(study.getClass(), study.getId());
        for (NeoParticipant participant: study.getParticipants()) {
            participant = session.load(participant.getClass(), participant.getId());
            for (NeoSpecimen specimen: participant.getSpecimens()) {
                BiospecimenForm bioForm = setBioFormFields(study, participant, specimen);
                biospecimens.add(bioForm);
            }
        }
        return biospecimens;
    }

    private Collection<BiospecimenForm> otherLevelQuery(LinkedHashMap<String, String> parameters, String query, String level) {
        Collection<BiospecimenForm> biospecimens = new ArrayList<>();
        NeoStudy study = studyRepository.findByAcronym(parameters.get("studyName"));
        Result result = session.query(query, parameters);
        List<Map<String, Object>> content = IteratorUtils.toList(result.iterator());
        for (Map<String, Object> c : content) {
            NeoParticipant participant = null;
            for (Map.Entry<String, Object> entry : c.entrySet()) {
                if (entry.getKey().equals("p")) {
                    participant = (NeoParticipant) entry.getValue();
                    participant = session.load(NeoParticipant.class, participant.getId());
                    if (level.equals("participant")) {
                        for (NeoSpecimen specimen: participant.getSpecimens()) {
                            BiospecimenForm bioForm = setBioFormFields(study, participant, specimen);
                            biospecimens.add(bioForm);
                        }
                    }
                } else if (entry.getKey().equals("sp")) {
                    NeoSpecimen specimen = (NeoSpecimen) entry.getValue();
                    BiospecimenForm bioForm = setBioFormFields(study, participant, specimen);
                    biospecimens.add(bioForm);
                }
            }
        }

        return biospecimens;
    }

    private BiospecimenForm setBioFormFields(NeoStudy study, NeoParticipant participant, NeoSpecimen specimen) {

        BiospecimenForm bioForm = new BiospecimenForm();
        bioForm.setSpecimenId(specimen.getSampleId());
        bioForm.setNoAliquots(specimen.getNoAliquots());
        bioForm.setBiorepository(specimen.getBiobankName());
        bioForm.setParticipant(participant);
        bioForm.setStudy(study);

        return bioForm;
    }
}
