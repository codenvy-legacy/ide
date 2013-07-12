/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link RemoteView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class RemoteViewImpl extends DialogBox implements RemoteView {
    interface RemoteViewImplUiBinder extends UiBinder<Widget, RemoteViewImpl> {
    }

    private static RemoteViewImplUiBinder ourUiBinder = GWT.create(RemoteViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnClose;
    @UiField
    com.codenvy.ide.ui.Button btnAdd;
    @UiField
    com.codenvy.ide.ui.Button btnDelete;
    @UiField(provided = true)
    CellTable<Remote>         repositories;
    @UiField(provided = true)
    final   GitResources            res;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private ActionDelegate          delegate;
    private boolean                 isShown;

    /**
     * Create view.
     *
     * @param resources
     * @param locale
     */
    @Inject
    protected RemoteViewImpl(GitResources resources, GitLocalizationConstant locale) {
        this.res = resources;
        this.locale = locale;

        initRepositoriesTable();

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setText(locale.remotesViewTitle());
        this.setWidget(widget);
    }

    /** Initialize the columns of the grid. */
    private void initRepositoriesTable() {
        repositories = new CellTable<Remote>();

        Column<Remote, String> nameColumn = new Column<Remote, String>(new TextCell()) {
            @Override
            public String getValue(Remote remote) {
                return remote.getName();
            }
        };
        Column<Remote, String> urlColumn = new Column<Remote, String>(new TextCell()) {
            @Override
            public String getValue(Remote remote) {
                return remote.getUrl();
            }
        };

        repositories.addColumn(nameColumn, locale.remoteGridNameField());
        repositories.setColumnWidth(nameColumn, "20%");
        repositories.addColumn(urlColumn, locale.remoteGridLocationField());
        repositories.setColumnWidth(urlColumn, "80%");

        final SingleSelectionModel<Remote> selectionModel = new SingleSelectionModel<Remote>();
        selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
            @Override
            public void onSelectionChange(SelectionChangeEvent event) {
                Remote selectedObject = selectionModel.getSelectedObject();
                delegate.onRemoteSelected(selectedObject);
            }
        });
        repositories.setSelectionModel(selectionModel);
    }

    /** {@inheritDoc} */
    @Override
    public void setRemotes(@NotNull JsonArray<Remote> remotes) {
        // Wraps JsonArray in java.util.List
        List<Remote> list = new ArrayList<Remote>();
        for (int i = 0; i < remotes.size(); i++) {
            list.add(remotes.get(i));
        }
        repositories.setRowData(list);
    }

    /** {@inheritDoc} */
    @Override
    public void setEnableDeleteButton(boolean enabled) {
        btnDelete.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isShown() {
        return isShown;
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.isShown = false;
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.isShown = true;
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnClose")
    public void onCloseClicked(ClickEvent event) {
        delegate.onCloseClicked();
    }

    @UiHandler("btnAdd")
    public void onAddClicked(ClickEvent event) {
        delegate.onAddClicked();
    }

    @UiHandler("btnDelete")
    public void onDeleteClicked(ClickEvent event) {
        delegate.onDeleteClicked();
    }
}