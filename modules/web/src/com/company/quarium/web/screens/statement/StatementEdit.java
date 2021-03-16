package com.company.quarium.web.screens.statement;

import com.company.quarium.entity.Statement;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Statement.edit")
@UiDescriptor("statement-edit.xml")
@EditedEntityContainer("statementDc")
@LoadDataBeforeShow
public class StatementEdit extends StandardEditor<Statement> {
}