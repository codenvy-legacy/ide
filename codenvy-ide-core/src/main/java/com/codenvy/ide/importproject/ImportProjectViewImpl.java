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
package com.codenvy.ide.importproject;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.ui.window.Window;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import javax.annotation.Nonnull;

import java.util.List;


/**
 * The implementation of {@link com.codenvy.ide.importproject.ImportProjectView}.
 *
 * @author Roman Nikitenko
 */
public class ImportProjectViewImpl extends Window implements ImportProjectView{

    public interface ImportProjectViewBinder extends UiBinder<Widget, ImportProjectViewImpl> {
    }

    Button btnCancel;

    Button btnImport;

    @UiField
    TextBox projectName;

    @UiField
    Label description;

    @UiField
    TextBox uri;

    @UiField
    ListBox importersList;

    ImportProjectView.ActionDelegate delegate;
    CoreLocalizationConstant         locale;

    /** Create view. */
    @Inject
    public ImportProjectViewImpl(ImportProjectViewBinder importProjectViewBinder, CoreLocalizationConstant locale) {
        this.locale = locale;
        this.setTitle(locale.importProjectViewTitle());
        setWidget(importProjectViewBinder.createAndBindUi(this));

        btnCancel = createButton(locale.cancel(), "file-importProject-cancel", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onCancelClicked();
            }
        });
        getFooter().add(btnCancel);

        btnImport = createButton(locale.importProjectButton(), "file-importProject-import", new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                delegate.onImportClicked();
            }
        });
        getFooter().add(btnImport);

        importersList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                delegate.onImporterSelected();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.show();
    }

    @Override
    public void showWarning() {
        Info infoWindow = new Info(locale.importProjectWarningTitle(), locale.importProjectEnteredWrongUri());
        infoWindow.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    @UiHandler("uri")
    public void onUriChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    @UiHandler("projectName")
    public void onProjectNameChanged(KeyUpEvent event) {
        delegate.onValueChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ImportProjectView.ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getProjectName() {
        return projectName.getText();
    }

    /** {@inheritDoc} */
    @Override
    public void setProjectName(@Nonnull String projectName) {
        this.projectName.setText(projectName);
    }

    /** {@inheritDoc} */
    @Override
    public void setUri(@Nonnull String uri) {
        this.uri.setText(uri);
    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getUri() {
        return uri.getText();
    }


    @Override
    public void setDescription(@Nonnull String description) {
        this.description.setText(description);

    }

    /** {@inheritDoc} */
    @Nonnull
    @Override
    public String getImporter() {
        int index = importersList.getSelectedIndex();
        return index != -1 ? importersList.getItemText(index) : "";
    }

    /** {@inheritDoc} */
    @Override
    public void setImporters(@Nonnull List<String> importers) {
        importersList.clear();
        for (String importer : importers) {
            importersList.addItem(importer);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabledImportButton(boolean enabled) {
        btnImport.setEnabled(enabled);
    }

    /** {@inheritDoc} */
    @Override
    protected void onClose() {
    }
}
