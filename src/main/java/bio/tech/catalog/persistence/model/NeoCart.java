package bio.tech.catalog.persistence.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.validator.constraints.NotEmpty;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@NodeEntity
public class NeoCart {

    @GraphId
    private Long id;

    @NotEmpty
    private String cartId;
    private String username;
    private String projectId;
    private String status;

    @Relationship(type = "HAS_QUERY")
    private List<NeoQuery> queries = new ArrayList<>();

    @Relationship(type = "HAS_DATA_QUERY")
    private List<NeoDataQuery> dataQueries = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public void setCardId(String cartId) {
        this.cartId = cartId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public List<NeoQuery> getQueries() {
        return queries;
    }

    public void setQueries(List<NeoQuery> queries) { this.queries = queries; }

    public List<NeoDataQuery> getDataQueries() {
        return dataQueries;
    }

    public void setDataQueries(List<NeoDataQuery> dataQueries) {
        this.dataQueries = dataQueries;
    }
}
