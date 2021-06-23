package com.company.quarium.roles;

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

    @EntityAccess(entityName = "*", operations = {EntityOp.READ})
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityName = "*", view = "*")
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
            "quarium_Module.edit",
            "ext_quarium_Checklist.edit",
            "quarium_RegressChecklist.edit",
            "quarium_TestCase.edit",
            "diffFrame",
            "help",
            "aboutWindow",
            "settings"})
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }
}
