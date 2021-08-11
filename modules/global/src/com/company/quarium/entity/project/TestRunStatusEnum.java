package com.company.quarium.entity.project;

import com.haulmont.chile.core.datatypes.impl.EnumClass;

import javax.annotation.Nullable;


public enum TestRunStatusEnum implements EnumClass<String> {

    ACTIVE("Active"),
    COMPLETED("Completed"),
    PAUSE("Pause"),
    NOT_STARTED("Not started");

    private String id;

    TestRunStatusEnum(String value) {
        this.id = value;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static TestRunStatusEnum fromId(String id) {
        for (TestRunStatusEnum at : TestRunStatusEnum.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}