package com.company.quarium.web.gui.components;

import com.haulmont.cuba.gui.components.TextField;
import com.haulmont.cuba.gui.meta.*;

@StudioComponent(category = "Components",
        xmlns = "http://schemas.company.com/demo/0.1/ui-component.xsd",
        xmlnsAlias = "app",
        canvasBehaviour = CanvasBehaviour.INPUT_FIELD)
@StudioProperties(properties = {
        @StudioProperty(name = "dataContainer", type = PropertyType.DATACONTAINER_REF),
        @StudioProperty(name = "property", type = PropertyType.PROPERTY_PATH_REF, options = "string"),
}, groups = @PropertiesGroup(
        properties = {"dataContainer", "property"}, constraint = PropertiesConstraint.ALL_OR_NOTHING
))
public interface LinkField extends TextField {
    String NAME = "linkField";


    @StudioProperty(type = PropertyType.INTEGER)
    void setMaxTextLength(int maxLength);
    int getMaxTextLength();

    void focusLinkField();
}
