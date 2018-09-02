package bio.tech.catalog.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by hocine on 2017/11/30.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@NodeEntity
public class NeoTechnology {
    @GraphId
    private Long id;

    private String name;

    public NeoTechnology() {}

    public NeoTechnology(String name) { this.name = name; }

    public String getName() {
        return name;
    }
}
