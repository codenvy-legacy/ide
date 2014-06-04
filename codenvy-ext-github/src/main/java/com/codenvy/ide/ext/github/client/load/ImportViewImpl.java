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
package com.codenvy.ide.ext.github.client.load;

import com.codenvy.ide.Resources;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.github.client.GitHubLocalizationConstant;
import com.codenvy.ide.ext.github.client.GitHubResources;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.SafeHtmlCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * * The implementation of {@link com.codenvy.ide.ext.github.client.init.InitRepositoryView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ImportViewImpl extends DialogBox implements ImportView {
    interface ImportViewImplUiBinder extends UiBinder<Widget, ImportViewImpl> {
    }

    private static ImportViewImplUiBinder ourUiBinder = GWT.create(ImportViewImplUiBinder.class);

    @UiField
    Button                 btnCancel;
    @UiField
    Button                 btnFinish;
    @UiField
    TextBox                projectName;
    @UiField
    ListBox                accountName;
    @UiField(provided = true)
    CellTable<ProjectData> repositories;
    @UiField(provided = true)
    final   GitHubResources            res;
    @UiField(provided = true)
    final   GitHubLocalizationConstant locale;
    private ActionDelegate             delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected ImportViewImpl(GitHubResources resources, GitHubLocalizationConstant locale, Resources ideResources) {
        this.res = resources;
        this.locale = locale;
        createRepositoriesTable(ideResources);

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.importFromGithubTitle());
        this.setWidget(widget);
    }

    /** Creates table what contains list of available repositories.
     * @param ideResources*/
    private void createRepositoriesTable(Resources ideResources) {
        repositories = new CellTable<ProjectData>(15, ideResources);

        Column<ProjectData, ImageResource> iconColumn = new Column<ProjectData, ImageResource>(new ImageResourceCell()) {
            @Override
            public ImageResource getValue(ProjectData item) {
                return res.project();
            }
        };

        Column<ProjectData, SafeHtml> repositoryColumn = new Column<ProjectData, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final ProjectData item) {
                return new SafeHtml() {
                    public String asString() {
                        return item.getName();
                    }
                };
            }
        };

        Column<ProjectData, SafeHtml> descriptionColumn = new Column<ProjectData, SafeHtml>(new SafeHtmlCell()) {
            @Override
            public SafeHtml getValue(final ProjectData item) {
                return new SafeHtml() {
                    public String asString() {
                        return "<span>" + item.getDescription() + "</span>";
                    }
                };
            }
        };

        repositories.addColumn(iconColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
        repositories.setColumnWidth(iconColumn, 28, Style.Unit.PX);

        repositories.addColumn(repositoryColumn, locale.samplesListRepositoryColumn());
        repositories.addColumn(descriptionColumn, locale.samplesListDescriptionColumn());

        // don't show loading indicator
        repositories.setLoadingIndicator(null);

        final SingleSelectionModel<ProjectData> selectionModel = new SingleSelectionModel<ProjectData>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                ProjectData selectedObject = selectionModel.getSelectedObject();
                delegate.onRepositorySelected(selectedObject);
            }
        });
        repositories.setSelectionModel(selectionModel);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectName(@NotNull String projectName) {
        this.projectName.setText(projectName);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getProjectName() {
        return projectName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setRepositories(@NotNull Array<ProjectData> repositories) {
        // Wraps Array in java.util.List
        List<ProjectData> list = new ArrayList<ProjectData>();
        for (int i = 0; i < repositories.size(); i++) {
            list.add(repositories.get(i));
        }
        this.repositories.setRowData(list);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableFinishButton(boolean enabled) {
        btnFinish.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @NotNull
    @Override
    public String getAccountName() {
        int index = accountName.getSelectedIndex();
        return index != -1 ? accountName.getItemText(index) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setAccountNames(@NotNull Array<String> names) {
        this.accountName.clear();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            this.accountName.addItem(name);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnFinish")
    public void onFinishClicked(ClickEvent event) {
        delegate.onFinishClicked();
    }

    @UiHandler("accountName")
    public void onAccountChange(ChangeEvent event) {
        delegate.onAccountChanged();
    }
}