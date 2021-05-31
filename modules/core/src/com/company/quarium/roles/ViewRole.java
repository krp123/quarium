package com.company.quarium.roles;

import com.company.quarium.entity.checklist.*;
import com.company.quarium.entity.project.ConfigurationProjectRelationship;
import com.company.quarium.entity.project.Module;
import com.company.quarium.entity.project.QaProjectRelationship;
import com.company.quarium.entity.project.SimpleProject;
import com.company.quarium.entity.references.Configuration;
import com.company.quarium.entity.references.Dbms;
import com.company.quarium.entity.references.Qa;
import com.company.quarium.entity.references.ThesisVersion;
import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.EntityAccess;
import com.haulmont.cuba.security.app.role.annotation.EntityAttributeAccess;
import com.haulmont.cuba.security.app.role.annotation.Role;
import com.haulmont.cuba.security.app.role.annotation.ScreenAccess;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;

@Role(name = ViewRole.NAME)
public class ViewRole extends AnnotatedRoleDefinition {

    public static final String NAME = "View";

    @EntityAccess(entityClass = SimpleProject.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = SimpleChecklist.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = Checklist.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = RegressChecklist.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = TestCase.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = Step.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = Module.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = Configuration.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = ConfigurationProjectRelationship.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = Qa.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = QaProjectRelationship.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = ThesisVersion.class,
            operations = {EntityOp.READ})
    @EntityAccess(entityClass = Dbms.class,
            operations = {EntityOp.READ})
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityClass = SimpleProject.class, view = "*")
    @EntityAttributeAccess(entityClass = SimpleChecklist.class, view = "*")
    @EntityAttributeAccess(entityClass = RegressChecklist.class, view = "*")
    @EntityAttributeAccess(entityClass = TestCase.class, view = "*")
    @EntityAttributeAccess(entityClass = Step.class, view = "*")
    @EntityAttributeAccess(entityClass = Module.class, view = "*")
    @EntityAttributeAccess(entityClass = Configuration.class, view = "*")
    @EntityAttributeAccess(entityClass = ConfigurationProjectRelationship.class, view = "*")
    @EntityAttributeAccess(entityClass = Qa.class, view = "*")
    @EntityAttributeAccess(entityClass = QaProjectRelationship.class, view = "*")
    @EntityAttributeAccess(entityClass = ThesisVersion.class, view = "*")
    @EntityAttributeAccess(entityClass = Dbms.class, view = "*")
    @Override
    public EntityAttributePermissionsContainer entityAttributePermissions() {
        return super.entityAttributePermissions();
    }

    @ScreenAccess(screenIds = {
            "projects",
            "checklists",
            "quarium_SimpleProject.edit",
            "quarium_Project.browse",
            "quarium_SimpleChecklist.edit",
            "quarium_Checklist.browse",
            "quarium_ProjectVersion.edit",
            "quarium_VersionExtChecklist.edit",
            "quarium_VersionRegressChecklist.edit",
            "help",
            "aboutWindow",
            "settings"})
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }
}
