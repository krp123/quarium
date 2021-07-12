package com.company.quarium.entity.checklist;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum CaseResult implements EnumClass<String> {

    PASSED("Passed"),
    FAILED("Failed"),
    BLOCKED("Blocked"),
    SKIPPED("Skipped");

    private String id;

    CaseResult(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static CaseResult fromId(String id) {
        for (CaseResult at : CaseResult.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}