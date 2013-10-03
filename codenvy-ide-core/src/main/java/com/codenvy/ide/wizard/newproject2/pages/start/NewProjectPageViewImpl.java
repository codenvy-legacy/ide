/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.wizard.newproject2.pages.start;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.paas.PaaSAgentImpl;
import com.codenvy.ide.wizard.WizardResource;
import com.codenvy.ide.wizard.newproject.ProjectTypeAgentImpl;
import com.codenvy.ide.wizard.newproject.ProjectTypeData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;


/**
 * NewProjectPageViewImpl is the view of new project page wizard. Provides selecting type of technology for creating new project.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewProjectPageViewImpl extends Composite implements NewProjectPageView {
    interface NewProjectViewImplUiBinder extends UiBinder<Widget, NewProjectPageViewImpl> {
    }

    private static NewProjectViewImplUiBinder uiBinder = GWT.create(NewProjectViewImplUiBinder.class);

    @UiField
    TextBox projectName;
    @UiField(provided = true)
    Grid    technologies;
    @UiField(provided = true)
    Grid    paases;
    @UiField
    Image   chooseTechnologyTooltip;
    @UiField
    Image   choosePaaSTooltip;
    @UiField(provided = true)
    final   WizardResource           res;
    @UiField(provided = true)
    final   CoreLocalizationConstant locale;
    private ActionDelegate           delegate;
    private ToggleButton             selectedProjectType;
    private ToggleButton             selectedPaaS;
    private JsonArray<ToggleButton> paasButton = JsonCollections.createArray();
    private JsonArray<PaaS> availablePaaS;

    /**
     * Create view.
     *
     * @param resource
     * @param locale
     * @param projectTypeAgent
     * @param paaSAgent
     */
    @Inject
    protected NewProjectPageViewImpl(WizardResource resource, CoreLocalizationConstant locale, ProjectTypeAgentImpl projectTypeAgent,
                                     PaaSAgentImpl paaSAgent) {
        this.res = resource;
        this.locale = locale;


        // TODO remove agents
        JsonArray<ProjectTypeData> projectTypes = projectTypeAgent.getProjectTypes();
        createTechnologiesTable(projectTypes);

        JsonArray<PaaS> paases = paaSAgent.getPaaSes();
        createPaasTable(paases);
        availablePaaS = paases;

        initWidget(uiBinder.createAndBindUi(this));
    }

    /**
     * Create table with available technologies.
     *
     * @param projectTypes
     *         available technologies
     */
    private void createTechnologiesTable(JsonArray<ProjectTypeData> projectTypes) {
        //create table where contains kind of technology
        technologies = new Grid(2, projectTypes.size());
        HTMLTable.CellFormatter formatter = technologies.getCellFormatter();

        //create button for each available wizard
        for (int i = 0; i < projectTypes.size(); i++) {
            final ProjectTypeData projectTypeData = projectTypes.get(i);

            Image icon = new Image(projectTypeData.getIcon());
            final ToggleButton btn;
            if (icon != null) {
                btn = new ToggleButton(icon);
            } else {
                btn = new ToggleButton();
            }
            btn.setSize("48px", "48px");

            final int id = i;
            btn.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    //if user click on other button (the button isn't selected) then new button changes to selected.
                    //otherwise the button must be selected.
                    if (selectedProjectType != btn) {
                        if (selectedProjectType != null) {
                            selectedProjectType.setDown(false);
                        }
                        selectedProjectType = btn;

// TODO need to improve
//                        String nature = projectTypeData.getTypeName();
//                        for (int i = 0; i < availablePaaS.size(); i++) {
//                            PaaS paas = availablePaaS.get(i);
//                            JsonArray<String> paases = paas.getRequiredProjectTypes();
//                            ToggleButton button = paasButton.get(i);
//                            button.setEnabled(false);
//                            button.setDown(false);
//
//                            // TODO constant
//                            if (!paas.getId().equals("None")) {
//                                if (paases.contains(nature)) {
//                                    button.setEnabled(true);
//                                }
//                            } else {
//                                button.setEnabled(true);
//                            }
//                        }

                        selectedPaaS = null;

                        delegate.onProjectTypeSelected(id);
                    } else {
                        selectedProjectType.setDown(true);
                    }
                }
            });
            technologies.setWidget(0, i, btn);
            formatter.setHorizontalAlignment(0, i, HasHorizontalAlignment.ALIGN_CENTER);

            Label title = new Label(projectTypeData.getTitle());
            technologies.setWidget(1, i, title);
            formatter.setHorizontalAlignment(1, i, HasHorizontalAlignment.ALIGN_CENTER);
        }
    }

    /**
     * Create table with available paases.
     *
     * @param paases
     *         available paases
     */
    private void createPaasTable(JsonArray<PaaS> paases) {
        this.paases = new Grid(2, paases.size());
        HTMLTable.CellFormatter formatter = this.paases.getCellFormatter();

        //create button for each paas
        for (int i = 0; i < paases.size(); i++) {
            PaaS paas = paases.get(i);

            ImageResource icon = paas.getImage();
            final ToggleButton btn;
            if (icon != null) {
                btn = new ToggleButton(new Image(icon));
            } else {
                btn = new ToggleButton();
            }
            btn.setSize("48px", "48px");
            btn.setEnabled(false);

            final int id = i;
            btn.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    //if user click on other button (the button isn't selected) then new button changes to selected.
                    //otherwise the button must be selected.
                    if (selectedPaaS != btn) {
                        if (selectedPaaS != null) {
                            selectedPaaS.setDown(false);
                        }
                        selectedPaaS = btn;

                        delegate.onPaaSSelected(id);
                    } else {
                        selectedPaaS.setDown(true);
                    }
                }
            });
            this.paases.setWidget(0, i, btn);
            formatter.setHorizontalAlignment(0, i, HasHorizontalAlignment.ALIGN_CENTER);

            Label title = new Label(paas.getTitle());
            this.paases.setWidget(1, i, title);
            formatter.setHorizontalAlignment(1, i, HasHorizontalAlignment.ALIGN_CENTER);

            paasButton.add(btn);
        }
    }

    /** {@inheritDoc} */
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public String getProjectName() {
        return projectName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void showPopup(String message, int left, int top) {
        HTML htmlText = new HTML(message);

        PopupPanel popup = new PopupPanel();
        popup.setAutoHideEnabled(true);
        popup.setWidth("auto");
        popup.getElement().getStyle().setPadding(5.0, Style.Unit.PX);
        popup.setPopupPosition(left, top);
        popup.setWidget(htmlText);
        popup.show();
    }

    @UiHandler("projectName")
    public void handleKeyUp(KeyUpEvent event) {
        delegate.checkProjectName();
    }

    @UiHandler("chooseTechnologyTooltip")
    public void onTechnologyIconClicked(ClickEvent event) {
        delegate.onTechnologyIconClicked(event.getClientX() + 10, event.getClientY() + 10);
    }

    @UiHandler("choosePaaSTooltip")
    public void onPaaSIconClicked(ClickEvent event) {
        delegate.onPaaSIconClicked(event.getClientX() + 10, event.getClientY() + 10);
    }
}