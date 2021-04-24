package com.company.quarium.web.gui.components;

import com.haulmont.bali.events.Subscription;
import com.haulmont.chile.core.datatypes.Datatype;
import com.haulmont.cuba.core.global.AppBeans;
import com.haulmont.cuba.core.global.Messages;
import com.haulmont.cuba.gui.components.*;
import com.haulmont.cuba.gui.components.data.ValueSource;
import com.haulmont.cuba.gui.xml.layout.ComponentsFactory;
import com.haulmont.cuba.web.gui.components.WebAbstractField;
import com.haulmont.cuba.web.gui.components.WebComponentsHelper;
import com.haulmont.cuba.web.widgets.CubaTextField;
import com.vaadin.event.FieldEvents;
import org.springframework.util.StringUtils;

import java.util.function.Consumer;
import java.util.function.Function;

public class WebLinkField extends WebAbstractField implements LinkField {

    private int maxTextLength = 0;
    private TextField<String> inputField;
    private Link link;
    private HBoxLayout hBox;
    private LinkButton linkButton;
    protected ComponentsFactory componentsFactory;


    public WebLinkField() {
        componentsFactory = AppBeans.get(ComponentsFactory.NAME);
        hBox = componentsFactory.createComponent(HBoxLayout.class);
        hBox.setSpacing(true);
        inputField = componentsFactory.createComponent(TextField.class);
        hBox.add(inputField);
        initLinkFields();
        inputField.addValueChangeListener(e -> setLinkValue(e.getValue()));
        CubaTextField vTextField = (CubaTextField) WebComponentsHelper.unwrap(inputField);
        vTextField.addBlurListener((FieldEvents.BlurListener) event -> setVisibleLinkField(false));
        setVisibleLinkField(false);
        component = WebComponentsHelper.unwrap(hBox);
    }

    @Override
    public boolean isRequired() {
        return inputField.isRequired();
    }

    @Override
    public void setRequired(boolean required) {
        inputField.setRequired(required);
    }

    @Override
    public void setRequiredMessage(String msg) {
        inputField.setRequiredMessage(msg);
    }

    @Override
    public String getRequiredMessage() {
        return inputField.getRequiredMessage();
    }

    @Override
    public boolean isEditable() {
        return inputField.isEditable();
    }

    @Override
    public void setEditable(boolean editable) {
        linkButton.setVisible(editable);
        inputField.setEditable(editable);
    }

    @Override
    public String getCaption() {
        return inputField.getCaption();
    }

    @Override
    public void setCaption(String caption) {
        inputField.setCaption(caption);
    }

    @Override
    public String getDescription() {
        return inputField.getDescription();
    }

    @Override
    public void setDescription(String description) {
        inputField.setDescription(description);
    }

    @Override
    public void validate() throws ValidationException {
        inputField.validate();
    }

    @Override
    public boolean isValid() {
        return inputField.isValid();
    }

    @Override
    public ValueSource getValueSource() {
        return inputField.getValueSource();
    }

    @Override
    public void setValueSource(ValueSource valueSource) {
        inputField.setValueSource(valueSource);
    }

    @Override
    public void setValue(Object value) {
        inputField.setValue((String) value);
        setLinkValue((String) value);
    }

    @Override
    public void setWidth(String width) {
        inputField.setWidth(width);
        hBox.setWidth(width);

    }

    @Override
    public float getWidth() {
        return inputField.getWidth();
    }

    @Override
    public int getWidthUnits() {
        return inputField.getWidthUnits();
    }


    @Override
    public Datatype getDatatype() {
        return inputField.getDatatype();
    }

    @Override
    public void setDatatype(Datatype datatype) {
        inputField.setDatatype(datatype);
    }

    @Override
    public String getRawValue() {
        return inputField.getRawValue();
    }


    @Override
    public Function getFormatter() {
        return inputField.getFormatter();
    }

    @Override
    public void setFormatter(Function formatter) {
        inputField.setFormatter(formatter);
    }

    @Override
    public String getInputPrompt() {
        return inputField.getInputPrompt();
    }

    @Override
    public void setInputPrompt(String inputPrompt) {
        inputField.setInputPrompt(inputPrompt);
    }

    @Override
    public void setCursorPosition(int position) {
        inputField.setCursorPosition(position);
    }

    @Override
    public int getMaxLength() {
        return inputField.getMaxLength();
    }

    @Override
    public void setMaxLength(int value) {
        inputField.setMaxLength(value);
    }

    @Override
    public boolean isTrimming() {
        return inputField.isTrimming();
    }

    @Override
    public void setTrimming(boolean trimming) {
        inputField.setTrimming(trimming);
    }

    public int getMaxTextLength() {
        return maxTextLength;
    }

    @Override
    public void setMaxTextLength(int maxTextLength) {
        this.maxTextLength = maxTextLength;
    }

    private String fixLink(String linkStr) {
        if (linkStr == null) return "";
        if (!linkStr.matches("http.*")) {
            linkStr = "http://" + linkStr;
        }
        return linkStr;
    }

    private void setLinkValue(String linkStr) {
        String localLink = linkStr;
        link.setDescription(linkStr);
        if (linkStr != null && maxTextLength > 3 && linkStr.length() > maxTextLength) {
            localLink = linkStr.substring(0, maxTextLength - 3).concat("...");
        }
        link.setEnabled(!StringUtils.isEmpty(localLink));
        link.setCaption(localLink);
        link.setUrl(fixLink(linkStr));
    }

    private void setVisibleLinkField(boolean visible) {
        inputField.setVisible(visible);
        inputField.setWidthFull();
        link.setVisible(!visible);
        linkButton.setVisible(!visible);
    }

    protected void initLinkFields() {
        link = componentsFactory.createComponent(Link.class);
        link.setTarget("_blank");
        com.vaadin.ui.Link vLink = (com.vaadin.ui.Link) WebComponentsHelper.unwrap(link);
        vLink.addStyleName("link-field");
        setLinkValue(inputField.getValue());
        linkButton = componentsFactory.createComponent(LinkButton.class);
        linkButton.setCaption(AppBeans.get(Messages.class).getMessage(this.getClass(), "editLinkButton.caption"));
        linkButton.setAction(new AbstractAction("edit") {
            @Override
            public void actionPerform(Component component) {
                focusLinkField();
            }
        });
        hBox.setWidthFull();
        hBox.add(link);
        hBox.add(linkButton);
        linkButton.setAlignment(Alignment.BOTTOM_RIGHT);
        hBox.expand(link);
        hBox.setAlignment(Alignment.MIDDLE_CENTER);
    }

    @Override
    public void focusLinkField() {
        setVisibleLinkField(true);
        inputField.requestFocus();
    }

    @Override
    public CaseConversion getCaseConversion() {
        return inputField.getCaseConversion();
    }

    @Override
    public void setCaseConversion(CaseConversion caseConversion) {
        inputField.setCaseConversion(caseConversion);
    }

    @Override
    public Subscription addTextChangeListener(Consumer<TextChangeEvent> listener) {
        return null;
    }

    @Override
    public void removeTextChangeListener(Consumer<TextChangeEvent> listener) {

    }

    @Override
    public int getTextChangeTimeout() {
        return inputField.getTextChangeTimeout();
    }

    @Override
    public void setTextChangeTimeout(int timeout) {
        inputField.setTextChangeTimeout(timeout);
    }

    @Override
    public TextChangeEventMode getTextChangeEventMode() {
        return inputField.getTextChangeEventMode();
    }

    @Override
    public void setTextChangeEventMode(TextChangeEventMode mode) {
        inputField.setTextChangeEventMode(mode);
    }

    @Override
    public void selectAll() {
        inputField.selectAll();
    }

    @Override
    public void setSelectionRange(int pos, int length) {
        inputField.setSelectionRange(pos, length);
    }

    @Override
    public void commit() {
        inputField.commit();
    }

    @Override
    public void discard() {
        inputField.discard();
    }

    @Override
    public boolean isBuffered() {
        return inputField.isBuffered();
    }

    @Override
    public void setBuffered(boolean buffered) {
        inputField.setBuffered(buffered);
    }

    @Override
    public boolean isModified() {
        return inputField.isModified();
    }

    @Override
    public void focus() {
        ((com.vaadin.ui.Component.Focusable) component).focus();
    }

    @Override
    public int getTabIndex() {
        return inputField.getTabIndex();
    }

    @Override
    public void setTabIndex(int tabIndex) {
        inputField.setTabIndex(tabIndex);
    }

    @Override
    public void setConversionErrorMessage(String conversionErrorMessage) {

    }

    @Override
    public String getConversionErrorMessage() {
        return null;
    }

    @Override
    public Subscription addEnterPressListener(Consumer<EnterPressEvent> listener) {
        return null;
    }

    @Override
    public void removeEnterPressListener(Consumer<EnterPressEvent> listener) {

    }

    @Override
    public void setHtmlName(String htmlName) {

    }

    @Override
    public String getHtmlName() {
        return null;
    }
}
