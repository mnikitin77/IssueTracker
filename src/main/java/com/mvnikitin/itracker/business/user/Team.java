package com.mvnikitin.itracker.business.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Team {

    private Map<Integer, User> users = new HashMap<>();
    private int id;
    private String name;

    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    public void add(User user) {
        if (user != null) {
            users.put(user.getId(), user);
        }
    }

    public boolean remove(int id) {
        return users.remove(id) != null;
    }

    public User getUserById(int id) {
        return users.get(id);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
