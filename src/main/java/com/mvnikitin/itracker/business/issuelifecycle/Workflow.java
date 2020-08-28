package com.mvnikitin.itracker.business.issuelifecycle;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс Workflow - определяет таблицу возможных переходов.
 * Настраивается пользователем для определённого типа Issue
 * (одного из четырёх).
 */
public class Workflow implements WorkflowManager, Stateable {

    private int id;
    private String type;
    private String first;
    private String name;
    private String description;

    private Map<String, Set<String>> stateTransitions = new HashMap<>();

    public Workflow(String type, String first) {
        this.type = type;
        this.first = first;
        stateTransitions.put(first, new HashSet<>());
    }

    //--------------         Stateable        -------------------------
    //
    @Override
    public String changeState(String currentState, String nextState) {
        return stateTransitions.get(currentState).contains(nextState) ?
                nextState : currentState;
    }

    @Override
    public List<String> getAvailableNextStates(String currentStateCode) {
        return stateTransitions.get(currentStateCode).stream()
                .collect(Collectors.toList());
    }

    @Override
    public String getInitialState() {
        return first;
    }

    //--------------      WorkflowManager     -------------------------
    //
    @Override
    public void addNextState(String currentState, String nextState) {
        Set<String> nextStates = stateTransitions.get(currentState);

        if (nextStates == null) {
            nextStates = new HashSet<>();
            stateTransitions.put(currentState, nextStates);
        }
        nextStates.add(nextState);
    }

    @Override
    public boolean removeNextState(String currentState, String nextState) {
        Set<String> nextStates = stateTransitions.get(currentState);

        if (nextStates != null) {
            return nextStates.remove(nextState);
        }

        return false;
    }

    @Override
    public Map<String, Set<String>> getStateTransitions() {
        return stateTransitions;
    }

    @Override
    public String getType() {
        return type;
    }


    @Override
    public String getFirst() {
        return first;
    }

    @Override
    public void clean() {
        stateTransitions.clear();
        first = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    @Override
    public String toString() {
        return "Workflow{" +
                "type=" + type +
                ", first=" + first +
                ", stateTransitions=" + stateTransitions +
                '}';
    }
}
