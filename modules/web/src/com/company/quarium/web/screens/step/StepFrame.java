package com.company.quarium.web.screens.step;

import com.haulmont.cuba.gui.screen.ScreenFragment;
import com.haulmont.cuba.gui.screen.UiController;
import com.haulmont.cuba.gui.screen.UiDescriptor;

@UiController("quarium_StepFrame")
@UiDescriptor("step-frame.xml")
public class StepFrame extends ScreenFragment {
    /*@Inject
    private DataManager dataManager;
    @Inject
    private CollectionContainer<Step> stepCollection;
    @Inject
    private InstanceContainer<TestCase> testCaseDc;
    @Inject
    protected WebComponentsFactory webComponentsFactory;
    @Inject
    protected GroupBoxLayout stepGroupBox;

    protected WebGridLayout stepGrid;
    protected int insertedRows;

    @Subscribe
    protected void onInit(Screen.InitEvent event) {
        initSteps();
    }

    private void initSteps() {
        if (testCaseDc.getItem().getCaseStep() == null)
            createStep();
         else
            repaintSteps();
    }

    public Step createStep() {
        Step step = dataManager.create(Step.class);
        step.setTestCase(testCaseDc.getItem());
        stepCollection.setItem(step);
        return step;
    }

    public void repaintSteps() {

    }

    public WebGridLayout createStepGrid() {
        WebGridLayout gridLayout = (WebGridLayout) webComponentsFactory.createComponent(GridLayout.class);
        gridLayout.setColumns(2);
        gridLayout.setSpacing(true);
        return gridLayout;
    }

    public Button createStepButton(AbstractAction action) {
        WebLinkButton linkButton = (WebLinkButton) webComponentsFactory.createComponent(LinkButton.class);
        linkButton.setAction(action);
        return linkButton;
    }

    public Label createStepLabel(String caption) {
        WebLabel label = (WebLabel) webComponentsFactory.createComponent(Label.class);
        label.setValue(caption);
        return label;
    }

    public void insertStepRow(TextArea textArea, Button button, int row) {
        ((com.vaadin.ui.GridLayout) WebComponentsHelper.unwrap(stepGrid)).insertRow(row);
        stepGrid.add(textArea, 0, row);
        stepGrid.add(button, 1, row);
        stepGrid.setColumnExpandRatio(0, 1.0f);
        insertedRows++;
    }

    protected int getRowIndexFromComponent(Component component) {
        com.vaadin.ui.Component vaadinComponent = component instanceof TextArea ?
                (com.vaadin.ui.Component) ((Component.Wrapper) component).getComposition() : WebComponentsHelper.unwrap(component);
        com.vaadin.ui.GridLayout.Area area = ((com.vaadin.ui.GridLayout) WebComponentsHelper.unwrap(stepGrid))
                .getComponentArea(vaadinComponent);
        return area.getRow1();
    }*/
}