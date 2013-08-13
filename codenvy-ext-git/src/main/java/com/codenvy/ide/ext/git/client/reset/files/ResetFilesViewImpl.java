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
package com.codenvy.ide.ext.git.client.reset.files;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.git.shared.IndexFile;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

import static com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_LEFT;

/**
 * The implementation of {@link ResetFilesPresenter}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class ResetFilesViewImpl extends DialogBox implements ResetFilesView {
    interface ResetFilesViewImplUiBinder extends UiBinder<Widget, ResetFilesViewImpl> {
    }

    private static ResetFilesViewImplUiBinder ourUiBinder = GWT.create(ResetFilesViewImplUiBinder.class);

    @UiField
    com.codenvy.ide.ui.Button btnReset;
    @UiField
    com.codenvy.ide.ui.Button btnCancel;
    @UiField(provided = true)
    CellTable<IndexFile>      indexFiles;
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

        this.setText(locale.resetFilesViewTitle());
        this.setWidget(widget);
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
                return !file.indexed();
            }
        };

        // Create bean value updater:
        FieldUpdater<IndexFile, Boolean> checkFieldUpdater = new FieldUpdater<IndexFile, Boolean>() {
            @Override
            public void update(int index, IndexFile file, Boolean value) {
                ((DtoClientImpls.IndexFileImpl)file).setIndexed(!value);
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
    public void setIndexedFiles(@NotNull JsonArray<IndexFile> indexedFiles) {
        // Wraps JsonArray in java.util.List
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
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @UiHandler("btnReset")
    public void onResetClicked(ClickEvent event) {
        delegate.onResetClicked();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }
}