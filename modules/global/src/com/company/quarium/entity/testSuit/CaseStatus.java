package com.company.quarium.entity.testSuit;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CaseStatus implements EnumClass<String> {

    PASSED("Passed"),
    FAILED("Failed"),
    BLOCKED("Blocked"),
    SKIPPED("Skipped");

    private String id;

    CaseStatus(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CaseStatus fromId(String id) {
        for (CaseStatus at : CaseStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}