package com.company.quarium.web.gui.xml.layout.loaders;

import com.company.quarium.web.gui.components.LinkField;
import com.haulmont.cuba.gui.xml.layout.loaders.TextFieldLoader;
import org.apache.commons.lang3.StringUtils;

public class LinkFieldLoader extends TextFieldLoader {
    @Override
    public void createComponent() {
        resultComponent = factory.create(LinkField.class);
        loadId(resultComponent, element);
    }

    @Override
    public void loadComponent() {
        super.loadComponent();

        String maxTextLength = element.attributeValue("maxTextLength");
        if (StringUtils.isNotEmpty(maxTextLength)) {
            ((LinkField) resultComponent).setMaxTextLength(Integer.valueOf(maxTextLength));
        }
    }
}
