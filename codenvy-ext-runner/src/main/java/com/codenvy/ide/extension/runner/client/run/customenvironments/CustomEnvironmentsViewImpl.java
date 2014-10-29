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
package com.codenvy.ide.extension.runner.client.run.customenvironments;

import com.codenvy.ide.extension.runner.client.RunnerResources;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.vectomatic.dom.svg.ui.SVGImage;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link CustomEnvironmentsView}.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomEnvironmentsViewImpl extends Window implements CustomEnvironmentsView {

    @UiField
    ScrollPanel listPanel;
    private Button                        btnRemove;
    private Button                        btnEdit;
    private ActionDelegate                delegate;
    private RunnerLocalizationConstant    localizationConstants;
    private CellTable<CustomEnvironment>  grid;
    private RunnerResources               runnerResources;

    @Inject
    protected CustomEnvironmentsViewImpl(final com.codenvy.ide.Resources resources, final RunnerResources runnerResources, RunnerLocalizationConstant localizationConstants,
                                         EditImagesViewImplUiBinder uiBinder) {
        this.localizationConstants = localizationConstants;
        this.runnerResources = runnerResources;
        this.setTitle(localizationConstants.customEnvironmentsViewTitle());
        Widget widget = uiBinder.createAndBindUi(this);
        this.setWidget(widget);
        createButtons();

        grid = new CellTable<CustomEnvironment>(15, resources);

        // Create files column:
        Column<CustomEnvironment, String> nameColumn = new Column<CustomEnvironment, String>(new TextCell()) {
            @Override
            public String getValue(CustomEnvironment environment) {
                return environment.getName();
            }
        };

        nameColumn.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
        grid.addColumn(nameColumn);

        final SingleSelectionModel<CustomEnvironment> selectionModel = new SingleSelectionModel<CustomEnvironment>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                delegate.onEnvironmentSelected(selectionModel.getSelectedObject());
            }
        });
        grid.setSelectionModel(selectionModel);

        grid.addDomHandler(new DoubleClickHandler() {
            @Override
            public void onDoubleClick(DoubleClickEvent event) {
                if (selectionModel.getSelectedObject() != null) {
                    delegate.onEditClicked();
                }
            }
        }, DoubleClickEvent.getType());

        grid.addDomHandler(new KeyUpHandler() {
            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeKeyCode() && selectionModel.getSelectedObject() != null) {
                    delegate.onEditClicked();
                }
            }
        }, KeyUpEvent.getType());

        grid.setEmptyTableWidget(new Label(localizationConstants.customEnvironmentsEmptyGridMessage()));
        grid.setWidth("100%");
        listPanel.add(grid);
    }

    private void createButtons() {
        Button btnAdd = createButton(localizationConstants.buttonAdd(), new SVGImage(runnerResources.addEnvironment()), "customEnvironments-add", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onAddClicked();
            }
        });
        btnAdd.getElement().getStyle().setFloat(Style.Float.LEFT);
        btnAdd.getElement().getStyle().setMarginLeft(12, Style.Unit.PX);

        btnRemove = createButton(localizationConstants.buttonRemove(), new SVGImage(runnerResources.removeEnvironment()), "customEnvironments-remove", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onRemoveClicked();
            }
        });
        btnRemove.getElement().getStyle().setFloat(Style.Float.LEFT);

        btnEdit = createButton(localizationConstants.buttonEdit(), new SVGImage(runnerResources.editEnvironment()), "customEnvironments-edit", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onEditClicked();
            }
        });
        btnEdit.getElement().getStyle().setFloat(Style.Float.LEFT);

        final Button btnClose = createButton(localizationConstants.buttonClose(), "customEnvironments-close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        btnClose.addStyleName(resources.centerPanelCss().blueButton());
        btnClose.getElement().getStyle().setMarginRight(10, Style.Unit.PX);


        getFooter().add(btnAdd);
        getFooter().add(btnEdit);
        getFooter().add(btnRemove);
        getFooter().add(btnClose);
    }

    @Override
    protected void onClose() {
        delegate.onCloseClicked();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setRemoveButtonEnabled(boolean isEnabled) {
        btnRemove.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEditButtonEnabled(boolean isEnabled) {
        btnEdit.setEnabled(isEnabled);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnvironments(Array<CustomEnvironment> environments) {
        List<CustomEnvironment> environmentList = new ArrayList<CustomEnvironment>();
        for (CustomEnvironment environment : environments.asIterable()) {
            environmentList.add(environment);
        }
        grid.setRowData(environmentList);
    }

    @Override
    public void selectEnvironment(CustomEnvironment environment) {
        grid.getSelectionModel().setSelected(environment, true);
        delegate.onEnvironmentSelected(environment);
    }

    /** {@inheritDoc} */
    @Override
    public void closeDialog() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    interface EditImagesViewImplUiBinder extends UiBinder<Widget, CustomEnvironmentsViewImpl> {
    }
}
