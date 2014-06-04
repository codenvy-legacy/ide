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
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitResources;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of {@link RemoteView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class RemoteViewImpl extends Window implements RemoteView {
    interface RemoteViewImplUiBinder extends UiBinder<Widget, RemoteViewImpl> {
    }

    private static RemoteViewImplUiBinder ourUiBinder = GWT.create(RemoteViewImplUiBinder.class);

    Button            btnClose;
    Button            btnAdd;
    Button            btnDelete;
    @UiField(provided = true)
    CellTable<Remote> repositories;

    private Remote selectedObject;
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
    protected RemoteViewImpl(GitResources resources,
                             final GitLocalizationConstant locale,
                             com.codenvy.ide.Resources ideResources) {
        this.res = resources;
        this.locale = locale;

        initRepositoriesTable(ideResources);

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.remotesViewTitle());
        this.setWidget(widget);
        
        btnClose = createButton(locale.buttonClose(), "git-remotes-remotes-close", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCloseClicked();
            }
        });
        getFooter().add(btnClose);

        btnAdd = createButton(locale.buttonAdd(), "git-remotes-remotes-add", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onAddClicked();
            }
        });
        getFooter().add(btnAdd);

        btnDelete = createButton(locale.buttonRemove(), "git-remotes-remotes-remove", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {

                Ask ask = new Ask(locale.deleteRemoteRepositoryTitle(), locale.deleteRemoteRepositoryQuestion(selectedObject.getName()), new AskHandler() {
                    @Override
                    public void onOk() {
                        delegate.onDeleteClicked();
                    }
                });
                ask.show();
            }
        });
        getFooter().add(btnDelete);
    }

    /** Initialize the columns of the grid.
     * @param ideResources*/
    private void initRepositoriesTable(com.codenvy.ide.Resources ideResources) {
        repositories = new CellTable<Remote>(15, ideResources);

        Column<Remote, String> nameColumn = new Column<Remote, String>(new TextCell()) {
            @Override
            public String getValue(Remote remote) {
                return remote.getName();
            }
            @Override
            public void render(Cell.Context context, Remote remote, SafeHtmlBuilder sb) {
                sb.appendHtmlConstant("<div id=\"" + UIObject.DEBUG_ID_PREFIX + "git-remotes-remotes-cellTable-" + context.getIndex() + "\">");
                super.render(context, remote, sb);
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
                selectedObject = selectionModel.getSelectedObject();
                delegate.onRemoteSelected(selectedObject);
            }
        });
        repositories.setSelectionModel(selectionModel);
    }

    /** {@inheritDoc} */
    @Override
    public void setRemotes(@NotNull Array<Remote> remotes) {
        // Wraps Array in java.util.List
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
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}