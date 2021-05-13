package com.company.quarium.web.screens.dbms;

import com.company.quarium.entity.references.Dbms;
import com.haulmont.cuba.gui.screen.*;

@UiController("quarium_Dbms.browse")
@UiDescriptor("dbms-browse.xml")
@LookupComponent("dbmsesTable")
@LoadDataBeforeShow
public class DbmsBrowse extends StandardLookup<Dbms> {
}