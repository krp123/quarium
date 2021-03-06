package com.company.quarium.roles;

import com.haulmont.cuba.security.app.role.AnnotatedRoleDefinition;
import com.haulmont.cuba.security.app.role.annotation.*;
import com.haulmont.cuba.security.entity.EntityOp;
import com.haulmont.cuba.security.role.EntityAttributePermissionsContainer;
import com.haulmont.cuba.security.role.EntityPermissionsContainer;
import com.haulmont.cuba.security.role.ScreenPermissionsContainer;
import com.haulmont.cuba.security.role.SpecificPermissionsContainer;

@Role(name = QaRole.NAME)
public class QaRole extends AnnotatedRoleDefinition {
    public final static String NAME = "Qa";

    @EntityAccess(entityName = "*", operations = {EntityOp.CREATE, EntityOp.READ, EntityOp.UPDATE, EntityOp.DELETE})
    @Override
    public EntityPermissionsContainer entityPermissions() {
        return super.entityPermissions();
    }

    @EntityAttributeAccess(entityName = "*", modify = "*")
    @Override
    public EntityAttributePermissionsContainer entityAttributePermissions() {
        return super.entityAttributePermissions();
    }

    @ScreenAccess(screenIds = {
            "projects",
            "checklists",
            "quarium_SimpleProject.edit",
            "quarium_Project.browse",
            "quarium_SharedTestSuitEdit.edit",
            "quarium_MyProjects.browse",
            "quarium_SharedTestSuit.browse",
            "quarium_VersionExtChecklist.edit",
            "quarium_VersionRegressChecklist.edit",
            "quarium_Module.edit",
            "quarium_SharedTestSuit.edit",
            "quarium_BaseTestSuit.edit",
            "quarium_RunTestSuit.edit",
            "quarium_TestCase.edit",
            "quarium_RunTestCase.edit",
            "quarium_CaseResult.edit",
            "quarium_Qa.browse",
            "quarium_Qa.edit",
            "quarium_Configuration.browse",
            "quarium_Configuration.edit",
            "quarium_Defect.edit",
            "quarium_Exceluploadwindow",
            "quarium_ProjectExceluploadwindow",
            "quarium_TestRun.edit",
            "quarium_TestRunTestSuitBrowse.browse",
            "quarium_ProjectTestSuit.edit",
            "quarium_Milestone.edit",
            "quarium_TestSuitCase.browse",
            "quarium_ProjectTestCaseBrowserFrame",
            "quarium_RunTestSuitCase.browse",
            "quarium_RunTestCaseBrowserFrame",
            "ftsSearchResults",
            "fts_HitInfoDetailsScreen",
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

    @SpecificAccess(permissions = {
            "cuba.gui.loginToClient",
            "cuba.gui.filter.customConditions",
            "cuba.gui.filter.edit"
    })
    @Override
    public SpecificPermissionsContainer specificPermissions() {
        return super.specificPermissions();
    }
}
