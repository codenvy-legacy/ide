package com.codenvy.ide.importproject;

import com.codenvy.ide.CoreLocalizationConstant;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
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
public class ImportProjectViewImpl extends DialogBox implements ImportProjectView{

    public interface ImportProjectViewBinder extends UiBinder<Widget, ImportProjectViewImpl> {
    }

    @UiField
    Button btnCancel;

    @UiField
    Button btnImport;

    @UiField
    TextBox projectName;

    @UiField
    TextBox uri;

    @UiField
    ListBox importersList;

    ImportProjectView.ActionDelegate delegate;

    /** Create view. */
    @Inject
    public ImportProjectViewImpl(ImportProjectViewBinder importProjectViewBinder, CoreLocalizationConstant locale) {
        this.setText(locale.importProjectViewTitle());
        setWidget(importProjectViewBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public void showDialog() {
        this.center();
        this.show();
    }

    /** {@inheritDoc} */
    @Override
    public void close() {
        this.hide();
    }

    @UiHandler("btnCancel")
    public void onCancelClicked(ClickEvent event) {
        delegate.onCancelClicked();
    }

    @UiHandler("btnImport")
    public void onImportClicked(ClickEvent event) {
        delegate.onImportClicked();
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
}
