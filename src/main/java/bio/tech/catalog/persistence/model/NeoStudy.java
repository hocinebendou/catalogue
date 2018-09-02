package bio.tech.catalog.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.session.Session;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@NodeEntity
public class NeoStudy {
	
	@Autowired
    Session session;
	
    @GraphId
    private Long id;

    private String acronym;
    private String disease;
    private String title;
    private String description;
    private boolean hasSpecimens;
    private boolean hasDataSets;
    private int totalSpecimens;
    private int noCases;
    private int noControls;
    private String[] dataTypes;
    private int noSpecimens;
    private int searchNoSpecimens;
    private int searchNoParticipants;

    @Relationship(type = "STUDY_DESIGN")
    private List<NeoDesign> designs = new ArrayList<>();

    @Relationship(type = "HAS_PARTICIPANT")
    private List<NeoParticipant> participants = new ArrayList<>();

    @Relationship(type = "HAS_DATASET")
    private List<NeoDataSet> dataSets = new ArrayList<>();
    
    public NeoStudy() {}

    public NeoStudy(String acronym, String title, String description) {
        this.acronym = acronym;
        this.title = title;
        this.description = description;
        this.disease = "";
        this.hasDataSets = false;
        this.hasSpecimens = false;
        this.totalSpecimens = 0;
        this.noCases = 0;
        this.noControls = 0;
        this.dataTypes = null;
        this.noSpecimens = 0;
        this.searchNoSpecimens = 0;
        this.searchNoParticipants = 0;
    }

    // Getters
    public long getId() {
    	return id;
    }
    public String getAcronym() {
        return acronym;
    }
    public String getDisease() {
        return disease;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public List<NeoDesign> getDesigns() {
        return designs;
    }
    public List<NeoParticipant> getParticipants() {
        return participants;
    }
    public List<NeoDataSet> getDataSets() { return dataSets; }
    public boolean getHasSpecimens() {
    	return this.getTotalSpecimens() > 0;
    }
    public boolean getHasDataSets() { return dataSets.size() > 0; }
    public int getTotalSpecimens() { return totalSpecimens; }
    public int getNoCases() { return noCases; }
    public int getNoControls() { return  noControls; }
    public String[] getDataTypes() { return dataTypes; }
    public int getNoSpecimens() { return noSpecimens; }
    public int getSearchNoSpecimens() { return searchNoSpecimens; }
    public int getSearchNoParticipants() { return searchNoParticipants; }

    // Setters
    public void setTotalSpecimens(int total) { this.totalSpecimens = total; }
    public void setNoCases(int noCases) { this.noCases = noCases; }
    public void setNoControls(int noControls) { this.noControls = noControls; }
    public void setHasSpecimens(boolean bool) { this.hasSpecimens = bool; }
    public void setHasDataSets(boolean bool) { this.hasDataSets = bool; }
    public void setDataTypes(String types) { this.dataTypes = types.split(","); }
    public void setNoSpecimens(int noSpecimens) { this.noSpecimens = noSpecimens; }
    public void setSearchNoSpecimens(int searchNoSpecimens) { this.searchNoSpecimens = searchNoSpecimens; }
    public void setSearchNoParticipants(int searchNoParticipants) { this.searchNoParticipants = searchNoParticipants; }
}
