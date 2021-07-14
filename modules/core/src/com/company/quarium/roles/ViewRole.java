package com.company.quarium.roles;

import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.*;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;
import com.haulmont.cuba.security.role.SpecificPermissionsContainer;

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
            "quarium_TestRun.edit",
            "quarium_TestRunTestSuitBrowse.browse",
            "quarium_TestSuit.edit",
            "quarium_Milestone.edit",
            "diffFrame",
            "help",
            "aboutWindow",
            "settings",
            "thirdpartyLicenseWindow",
            "runtimePropertiesFrame",
            "layoutAnalyzer",
            "fileUploadDialog",
            "multiuploadDialog",
            "inputDialog",
            "backgroundWorkProgressWindow",
            "backgroundWorkWindow",
            "addCondition",
            "date-interval-editor",
            "customConditionEditor",
            "customConditionFrame",
            "dynamicAttributesConditionEditor",
            "dynamicAttributesConditionFrame",
            "filterEditor",
            "ftsConditionFrame",
            "groupConditionFrame",
            "propertyConditionFrame",
            "filterSelect",
            "saveFilter",
            "list-editor-popup",
            "editWindowActions",
            "extendedEditWindowActions",
            "saveSetInFolder",
            "login",
            "loginWindow",
            "main",
            "mainWindow",
            "notFoundScreen"})
    @Override
    public ScreenPermissionsContainer screenPermissions() {
        return super.screenPermissions();
    }

    @SpecificAccess(permissions = {"cuba.gui.loginToClient"})
    @Override
    public SpecificPermissionsContainer specificPermissions() {
        return super.specificPermissions();
    }
}
