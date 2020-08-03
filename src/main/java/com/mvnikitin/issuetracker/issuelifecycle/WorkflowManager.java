package com.mvnikitin.issuetracker.issuelifecycle;

import java.util.Map;
import java.util.Set;

/**
 * Интерфейс гибкой настройки Workflow по набору состояний State
 */
public interface WorkflowManager {

    void addNextState(String currentState, String nextState);

    boolean removeNextState(String currentState, String nextState);

    Map<String, Set<String>> getStateTransitions();

    String getType();

    String getFirst();

    void clean();
}
