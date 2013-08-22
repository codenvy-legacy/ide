/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.git.client.github.githubimport;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.github.load.ProjectData;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.Button;
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
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * * The implementation of {@link com.codenvy.ide.ext.git.client.init.InitRepositoryView}.
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
    final   GitResources            res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private ActionDelegate          delegate;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected ImportViewImpl(GitResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;
        createRepositoriesTable();

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.importFromGithubTitle());
        this.setWidget(widget);
    }

    /** Creates table what contains list of available repositories. */
    private void createRepositoriesTable() {
        repositories = new CellTable<ProjectData>();

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
    public void setRepositories(@NotNull JsonArray<ProjectData> repositories) {
        // Wraps JsonArray in java.util.List
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
    public void setAccountNames(@NotNull JsonArray<String> names) {
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