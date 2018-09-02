package bio.tech.catalog.forms;


import bio.tech.catalog.persistence.model.NeoParticipant;
import bio.tech.catalog.persistence.model.NeoStudy;

public class BiospecimenForm {

    private String specimenId;
    private int noAliquots;
    private String biorepository;
    private NeoParticipant participant;
    private NeoStudy study;

    // getters and setters

    public String getSpecimenId() {
        return specimenId;
    }

    public void setSpecimenId(String specimenId) {
        this.specimenId = specimenId;
    }

    public int getNoAliquots() {
        return noAliquots;
    }

    public void setNoAliquots(int noAliquots) {
        this.noAliquots = noAliquots;
    }

    public String getBiorepository() {
        return biorepository;
    }

    public void setBiorepository(String biorepository) {
        this.biorepository = biorepository;
    }

    public NeoParticipant getParticipant() {
        return participant;
    }

    public void setParticipant(NeoParticipant participant) {
        this.participant = participant;
    }

    public NeoStudy getStudy() {
        return study;
    }

    public void setStudy(NeoStudy study) {
        this.study = study;
    }
}
