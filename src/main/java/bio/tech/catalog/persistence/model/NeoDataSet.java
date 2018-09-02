package bio.tech.catalog.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hocine on 2017/08/30.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@NodeEntity
public class NeoDataSet {
    @GraphId
    private Long id;

    private String egaAccess;
    private String description;

    @Relationship(type = "HAS_DATAGEN")
    private List<NeoDataGen> dataGens = new ArrayList<>();

    @Relationship(type = "HAS_TECHNOLOGY")
    private List<NeoTechnology> technologies = new ArrayList<>();

    @Relationship(type = "HAS_FILETYPE")
    private List<NeoFileType> fileTypes = new ArrayList<>();

    public NeoDataSet() {}

    public NeoDataSet(String egaAccess, String description) {
        this.egaAccess = egaAccess;
        this.description = description;
    }

    public Long getId() {
        return id;
    }
    public String getEgaAccess() {
        return egaAccess;
    }
    public String getDescription() {
        return description;
    }
    public List<NeoDataGen> getDataGens() { return dataGens; }
    public List<NeoFileType> getFileTypes() { return fileTypes; }
    public List<NeoTechnology> getTechnologies() {return technologies; }

    // Setters
    public void addDataGen(NeoDataGen dataGen) { dataGens.add(dataGen); }
    public void addTechnology(NeoTechnology technology) { technologies.add(technology); }
    public void addFileType(NeoFileType fileType) { fileTypes.add(fileType); }
}
