package bio.tech.catalog.security;

import java.util.ArrayList;
import java.util.List;

public class ActiveUserStore {

    private List<String> users;

    public ActiveUserStore() { users = new ArrayList<>(); }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
