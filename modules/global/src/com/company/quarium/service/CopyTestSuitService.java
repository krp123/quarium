package com.company.quarium.service;

import com.company.quarium.entity.testSuit.RunTestSuit;
import com.company.quarium.entity.testSuit.SharedTestSuit;
import com.company.quarium.entity.testSuit.TestSuit;

public interface CopyTestSuitService {
    String NAME = "quarium_CopyTestSuitService";

    SharedTestSuit copyTestSuit(TestSuit testSuit);

    RunTestSuit copyRunTestSuit(TestSuit testSuit);
}