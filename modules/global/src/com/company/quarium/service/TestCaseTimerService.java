package com.company.quarium.service;

import java.util.Map;

public interface TestCaseTimerService {
    String NAME = "quarium_TestCaseTimerService";

    Map<String, String> getTimeUnitsValues(int seconds);
}