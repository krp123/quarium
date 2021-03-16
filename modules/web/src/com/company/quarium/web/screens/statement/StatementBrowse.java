package com.company.quarium.web.screens.statement;

import com.company.quarium.entity.Statement;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Statement.browse")
@UiDescriptor("statement-browse.xml")
@LookupComponent("statementsTable")
@LoadDataBeforeShow
public class StatementBrowse extends StandardLookup<Statement> {
}