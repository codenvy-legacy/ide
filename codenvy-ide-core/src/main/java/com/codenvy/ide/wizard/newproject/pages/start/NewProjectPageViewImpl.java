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
package com.codenvy.ide.wizard.newproject.pages.start;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.paas.PaaS;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.ProjectTypeData;
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
    TextBox     projectName;
    @UiField
    Image       chooseTechnologyTooltip;
    @UiField
    Image       choosePaaSTooltip;
    @UiField
    SimplePanel paasPanel;
    @UiField
    SimplePanel techPanel;
    @UiField(provided = true)
    final   Resources                res;
    @UiField(provided = true)
    final   CoreLocalizationConstant locale;
    private ActionDelegate           delegate;
    private JsonArray<ToggleButton>  projectTypeButtons;
    private JsonArray<ToggleButton>  paasButtons;

    /**
     * Create view.
     *
     * @param resource
     * @param locale
     */
    @Inject
    protected NewProjectPageViewImpl(Resources resource, CoreLocalizationConstant locale) {
        this.res = resource;
        this.locale = locale;

        initWidget(uiBinder.createAndBindUi(this));
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
    public void setProjectTypes(JsonArray<ProjectTypeData> projectTypes) {
        projectTypeButtons = JsonCollections.createArray();
        //create table where contains kind of technology
        Grid grid = new Grid(2, projectTypes.size());
        techPanel.setWidget(grid);
        HTMLTable.CellFormatter formatter = grid.getCellFormatter();

        //create button for each available wizard
        for (int i = 0; i < projectTypes.size(); i++) {
            final ProjectTypeData projectTypeData = projectTypes.get(i);

            ImageResource icon = projectTypeData.getIcon();
            final ToggleButton btn;
            if (icon != null) {
                btn = new ToggleButton(new Image(icon));
            } else {
                btn = new ToggleButton();
            }
            btn.setSize("48px", "48px");

            final int id = i;
            btn.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    delegate.onProjectTypeSelected(id);
                }
            });
            grid.setWidget(0, i, btn);
            formatter.setHorizontalAlignment(0, i, HasHorizontalAlignment.ALIGN_CENTER);

            Label title = new Label(projectTypeData.getTitle());
            grid.setWidget(1, i, title);
            formatter.setHorizontalAlignment(1, i, HasHorizontalAlignment.ALIGN_CENTER);

            projectTypeButtons.add(btn);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setPaases(JsonArray<PaaS> paases) {
        paasButtons = JsonCollections.createArray();

        Grid grid = new Grid(2, paases.size());
        paasPanel.setWidget(grid);
        HTMLTable.CellFormatter formatter = grid.getCellFormatter();

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
                    delegate.onPaaSSelected(id);
                }
            });
            grid.setWidget(0, i, btn);
            formatter.setHorizontalAlignment(0, i, HasHorizontalAlignment.ALIGN_CENTER);

            Label title = new Label(paas.getTitle());
            grid.setWidget(1, i, title);
            formatter.setHorizontalAlignment(1, i, HasHorizontalAlignment.ALIGN_CENTER);

            paasButtons.add(btn);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectProjectType(int id) {
        for (int i = 0; i < projectTypeButtons.size(); i++) {
            ToggleButton button = projectTypeButtons.get(i);
            button.setDown(i == id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void selectPaas(int id) {
        for (int i = 0; i < paasButtons.size(); i++) {
            ToggleButton button = paasButtons.get(i);
            button.setDown(i == id);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void focusProjectName() {
        projectName.setFocus(true);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnablePaas(int id, boolean isEnabled) {
        ToggleButton button = paasButtons.get(id);
        button.setEnabled(isEnabled);
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