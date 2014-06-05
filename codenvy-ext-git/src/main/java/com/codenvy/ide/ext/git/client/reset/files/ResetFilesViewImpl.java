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
package com.codenvy.ide.ext.git.client.reset.files;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.IndexFile;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_LEFT;

/**
 * The implementation of {@link ResetFilesPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ResetFilesViewImpl extends Window implements ResetFilesView {
    interface ResetFilesViewImplUiBinder extends UiBinder<Widget, ResetFilesViewImpl> {
    }

    private static ResetFilesViewImplUiBinder ourUiBinder = GWT.create(ResetFilesViewImplUiBinder.class);

    Button               btnReset;
    Button               btnCancel;
    @UiField(provided = true)
    CellTable<IndexFile> indexFiles;
    @UiField(provided = true)
    final   GitLocalizationConstant locale;
    private ActionDelegate          delegate;

    /**
     * Create view.
     *
     * @param locale
     */
    @Inject
    protected ResetFilesViewImpl(GitLocalizationConstant locale) {
        this.locale = locale;

        initColumns();

        Widget widget = ourUiBinder.createAndBindUi(this);

        this.setTitle(locale.resetFilesViewTitle());
        this.setWidget(widget);
        
        btnCancel = createButton(locale.buttonCancel(), "", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnReset = createButton(locale.buttonReset(), "", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onResetClicked();
            }
        });
        getFooter().add(btnReset);
    }

    /** Initialize the columns of the grid. */
    private void initColumns() {
        indexFiles = new CellTable<IndexFile>();

        // Create files column:
        Column<IndexFile, String> filesColumn = new Column<IndexFile, String>(new TextCell()) {
            @Override
            public String getValue(IndexFile file) {
                return file.getPath();
            }
        };

        // Create column with checkboxes:
        Column<IndexFile, Boolean> checkColumn = new Column<IndexFile, Boolean>(new CheckboxCell(false, true)) {
            @Override
            public Boolean getValue(IndexFile file) {
                return !file.isIndexed();
            }
        };

        // Create bean value updater:
        FieldUpdater<IndexFile, Boolean> checkFieldUpdater = new FieldUpdater<IndexFile, Boolean>() {
            @Override
            public void update(int index, IndexFile file, Boolean value) {
                file.setIndexed(!value);
            }
        };

        checkColumn.setFieldUpdater(checkFieldUpdater);

        filesColumn.setHorizontalAlignment(ALIGN_LEFT);

        indexFiles.addColumn(checkColumn, new SafeHtml() {
            @Override
            public String asString() {
                return "&nbsp;";
            }
        });
        indexFiles.setColumnWidth(checkColumn, 1, Style.Unit.PCT);
        indexFiles.addColumn(filesColumn, FILES);
        indexFiles.setColumnWidth(filesColumn, 35, Style.Unit.PCT);
    }

    /** {@inheritDoc} */
    @Override
    public void setIndexedFiles(@NotNull Array<IndexFile> indexedFiles) {
        // Wraps Array in java.util.List
        List<IndexFile> appList = new ArrayList<IndexFile>();
        for (int i = 0; i < indexedFiles.size(); i++) {
            appList.add(indexedFiles.get(i));
        }
        indexFiles.setRowData(appList);
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
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