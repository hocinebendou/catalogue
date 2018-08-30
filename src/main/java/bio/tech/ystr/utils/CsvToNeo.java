package bio.tech.ystr.utils;

import bio.tech.ystr.persistence.dao.*;
import bio.tech.ystr.persistence.model.*;
import bio.tech.ystr.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class CsvToNeo {

    private String csvFilename;
    private String biobankName;
    private String subject;
    private String text;
    private String toEmail;
    private Set<NeoCountry> countries;
    private Set<NeoSpecType> specimenTypes;
    private final static String COMMA = ",";
    private final static String DOT = "\\.";

    @Autowired
    private StudyRepository studyRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private SpecimenRepository specimenRepository;

    @Autowired
    private CountryRepository countryRepository;

    @Autowired
    private SpecTypeRepository specTypeRepository;

    @Autowired
    private PdfGeneratorUtil pdfGeneratorUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    public SimpleMailMessage template;

    public CsvToNeo () {
        this.csvFilename = "";
        this.biobankName = "";
        this.toEmail = "";
        this.subject = "H3Africa catalogue report";
        this.text = "";
        this.countries = new HashSet<>();
        this.specimenTypes = new HashSet<>();
    }

    @Transactional
    public String parseBiobankCSVFile() {
        try {
            HashMap<String, Integer> studyNbSpecimens = new HashMap<>();

            File file = new File(this.csvFilename);

            pdfGeneratorUtil.setPdfName(file.getName().split(DOT)[0]);

            InputStream inputStream = new FileInputStream(file);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            int i = 0, j=0;
            for (String line : br.lines().skip(1).collect(Collectors.toList())) {

                NeoStudy study;
                NeoParticipant participant;
                NeoSpecimen specimen;
                NeoCountry country;
                NeoSpecType specimenType;

                boolean newSpecimen = false;

                String[] p = line.split(COMMA);

                try {
                    study = studyRepository.findByAcronym(p[3]);
                } catch (NullPointerException ex) {
                    study = null;
                }
                if (study == null) {
                    pdfGeneratorUtil.addNotFoundStudy(p[3]);
                    pdfGeneratorUtil.incNotInsertedRows();
                    continue;
                }
                try {
                    participant = participantRepository.findNeoParticipantByParticipantId(p[2]);
                } catch (NullPointerException ex) {
                    participant = null;
                }
                if (participant == null) {
                    pdfGeneratorUtil.incNotInsertedRows();
                    continue;
                }
                try {
                    specimen = specimenRepository.findNeoSpecimenBySampleId(p[1]);
                } catch (NullPointerException ex) {
                    specimen = null;
                }
                try {
                    country = countryRepository.findNeoCountryByName(p[0]);
                } catch (NullPointerException ex) {
                    country = null;
                }
                try {
                    specimenType = specTypeRepository.findNeoSpecTypeByName(p[5]);
                } catch (NullPointerException ex) {
                    specimenType = null;
                }

                if (country == null) {
                    country = new NeoCountry(p[0]);
                    this.countries.add(country);
                }
                if (specimenType == null) {
                    specimenType = new NeoSpecType(p[5]);
                    this.specimenTypes.add(specimenType);
                }
                if (specimen == null) {
                    i++;
                    newSpecimen = true;
                    specimen = new NeoSpecimen(p[1], Integer.parseInt(p[4]), p[6], this.biobankName);
                    pdfGeneratorUtil.incInsertedRows();
                } else {
                    j++;
                    specimen.setSampleId(p[1]);
                    specimen.setNoAliquots(Integer.parseInt(p[4]));
                    specimen.setCollectionDate(p[6]);
                    specimen.setBiobankName(this.biobankName);
                    pdfGeneratorUtil.incUpdatedRows();
                }
                specimen.setCountry(country);
                specimen.setSpecType(specimenType);

                if (!studyNbSpecimens.containsKey(study.getAcronym()))
                    studyNbSpecimens.put(study.getAcronym(), study.getNoSpecimens());

                int sSpecimens = 0, pSpecimens = 0;
                if (newSpecimen) {
                    if (specimen.getNoAliquots() > 0) {
                        sSpecimens = studyNbSpecimens.get(study.getAcronym()) + 1;
                        pSpecimens = participant.getNoSpecimens() + 1;
                    }
                } else {
                    if (specimen.getNoAliquots() == 0) {
                        sSpecimens = studyNbSpecimens.get(study.getAcronym()) - 1;
                        pSpecimens = participant.getNoSpecimens() - 1;
                    }
                }

                studyNbSpecimens.put(study.getAcronym(), sSpecimens);
                participant.setNoSpecimens(pSpecimens);

                countryRepository.save(country);
                specTypeRepository.save(specimenType);

                specimen.setAcronym(study.getAcronym());
                specimen.setEthnicity(participant.getEthnicity().getName());
                specimen.setDisease(study.getDisease());
                specimen.setSex(participant.getGender().getName());
                specimenRepository.save(specimen);

                participant.addSpecimen(specimen);
                participant.setAcronym(study.getAcronym());
                participantRepository.save(participant);

            }

            for (Map.Entry<String, Integer> entry : studyNbSpecimens.entrySet()) {
                NeoStudy study = studyRepository.findByAcronym(entry.getKey());
                study.setNoSpecimens(entry.getValue());
                studyRepository.save(study);
            }

            String pdfFilePath = pdfGeneratorUtil.createPdf();
            if (!pdfFilePath.equals("")) {
                emailService.sendMessageUsingTemplate(this.toEmail, this.subject, template,
                        pdfFilePath, file.getName());
            }
        }catch (Exception e) {
            e.printStackTrace();
            return "File problem read: ";
        }
        return "File successfully read.";
    }

    public void setCsvFilename (String csvFilename) {
        this.csvFilename = csvFilename;
    }

    public void setBiobankName (String biobankName) {
        this.biobankName = biobankName;
    }

    public void setToEmail (String toEmail) { this.toEmail = toEmail; }
}
