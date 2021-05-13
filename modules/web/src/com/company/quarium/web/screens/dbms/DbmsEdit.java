package com.company.quarium.web.screens.dbms;

import com.company.quarium.entity.references.Dbms;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Dbms.edit")
@UiDescriptor("dbms-edit.xml")
@EditedEntityContainer("dbmsDc")
@LoadDataBeforeShow
public class DbmsEdit extends StandardEditor<Dbms> {
}