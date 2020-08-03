package com.mvnikitin.issuetracker.issuelifecycle;

import java.util.List;

/**
 * Интерфейс управления жизненным циклом из набора состояний State
 */
public interface Stateable {

    String changeState(String currentState, String nextState);

    List<String> getAvailableNextStates(String currentState);

    String getInitialState();
}
