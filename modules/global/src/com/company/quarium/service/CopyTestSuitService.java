package com.company.quarium.service;

import com.company.quarium.entity.testsuit.RunTestSuit;
import com.company.quarium.entity.testsuit.SharedTestSuit;
import com.company.quarium.entity.testsuit.TestSuit;

public interface CopyTestSuitService {
    String NAME = "quarium_CopyTestSuitService";

    SharedTestSuit copyTestSuit(TestSuit testSuit);

    RunTestSuit copyRunTestSuit(TestSuit testSuit);
}