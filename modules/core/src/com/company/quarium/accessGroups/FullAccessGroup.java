package com.company.quarium.accessGroups;

import com.company.quarium.entity.project.*;
import com.company.quarium.entity.references.Configuration;
import com.company.quarium.entity.references.Dbms;
import com.company.quarium.entity.references.Qa;
import com.company.quarium.entity.references.ThesisVersion;
import com.company.quarium.entity.testsuit.*;
import com.haulmont.cuba.core.entity.EntitySnapshot;
import com.haulmont.cuba.security.app.group.AnnotatedAccessGroupDefinition;
import com.haulmont.cuba.security.app.group.annotation.AccessGroup;
import com.haulmont.cuba.security.app.group.annotation.JpqlConstraint;
import com.haulmont.cuba.security.group.ConstraintsContainer;

@AccessGroup(name = FullAccessGroup.NAME)
public class FullAccessGroup extends AnnotatedAccessGroupDefinition {

    public static final String NAME = "Full Access";

    @JpqlConstraint(target = SimpleProject.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = Project.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = SharedTestSuit.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = RunTestSuit.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = TestSuit.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = TestCase.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = Step.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = Module.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = Configuration.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = ConfigurationProjectRelationship.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = Qa.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = QaProjectRelationship.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = ThesisVersion.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = Dbms.class, where = "{E}.createdBy is not null")
    @JpqlConstraint(target = EntitySnapshot.class, where = "{E}.createdBy is not null")
    @Override
    public ConstraintsContainer accessConstraints() {
        return super.accessConstraints();
    }
}
