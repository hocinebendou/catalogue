package bio.tech.ystr.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

/**
 * Created by hocine on 2017/04/17.
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@NodeEntity
public class NeoDesign {

    @GraphId
    private Long id;

    private String name;

    public NeoDesign() {}

    public long getId() { return this.id; }
    public String getName() {
        return name;
    }
}
