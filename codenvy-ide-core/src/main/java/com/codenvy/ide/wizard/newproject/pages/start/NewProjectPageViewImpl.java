/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.wizard.newproject.pages.start;

import com.codenvy.api.project.shared.dto.ProjectTypeDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.IconRegistry;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ToggleButton;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


/**
 * NewProjectPageViewImpl is the view of new project page wizard. Provides selecting type of technology for creating new project.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class NewProjectPageViewImpl extends Composite implements NewProjectPageView {
    interface NewProjectViewImplUiBinder extends UiBinder<Widget, NewProjectPageViewImpl> {
    }

    @UiField
    TextBox     projectName;
    @UiField
    SimplePanel techPanel;
    @UiField(provided = true)
    final   Resources                res;
    @UiField(provided = true)
    final   CoreLocalizationConstant locale;
    private IconRegistry             iconRegistry;
    private ActionDelegate           delegate;
    private Array<ToggleButton>      projectTypeButtons;

    /**
     * Create view.
     *
     * @param uiBinder
     * @param resource
     * @param locale
     * @param iconRegistry
     */
    @Inject
    protected NewProjectPageViewImpl(NewProjectViewImplUiBinder uiBinder,
                                     Resources resource,
                                     CoreLocalizationConstant locale,
                                     IconRegistry iconRegistry) {
        this.res = resource;
        this.locale = locale;
        this.iconRegistry = iconRegistry;

        initWidget(uiBinder.createAndBindUi(this));
        this.ensureDebugId("file-newProject-newProjectPage");
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
    public void setProjectTypes(Array<ProjectTypeDescriptor> projectTypes) {
        projectTypeButtons = Collections.createArray();
        //create table where contains kind of technology
        Grid grid = new Grid(2, projectTypes.size());
        techPanel.setWidget(grid);
        HTMLTable.CellFormatter formatter = grid.getCellFormatter();

        //create button for each available wizard
        for (int i = 0; i < projectTypes.size(); i++) {
            final ProjectTypeDescriptor projectTypeData = projectTypes.get(i);
            Image icon = iconRegistry.getIcon(projectTypeData.getProjectTypeId() + ".projecttype.big.icon").getImage();
            icon.setSize("92px", "92px");
            final ToggleButton btn = new ToggleButton(icon);
            btn.setSize("92px", "92px");

            final int id = i;
            btn.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    delegate.onProjectTypeSelected(id);
                }
            });
            grid.setWidget(0, i, btn);
            formatter.setHorizontalAlignment(0, i, HasHorizontalAlignment.ALIGN_CENTER);

            Label title = new Label(projectTypeData.getProjectTypeName());
            grid.setWidget(1, i, title);
            formatter.setHorizontalAlignment(1, i, HasHorizontalAlignment.ALIGN_CENTER);

            btn.ensureDebugId("file-newProject-" + title.getText());
            projectTypeButtons.add(btn);
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
    public void focusProjectName() {
        projectName.setFocus(true);
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

}