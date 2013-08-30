/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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

package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.upload.FileUploadInput;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UploadFileView extends ViewImpl implements
                                             org.exoplatform.ide.extension.aws.client.s3.UploadFilePresenter.Display {

    private static final String MIME_TYPE_HIDDED_FIELD = "mimeType";

    private static final String NAME_HIDDED_FIELD = "name";

    private static final String OVERWRITE_HIDDED_FIELD = "overwrite";

    public static final int WIDTH = 460;

    public static final int HEIGHT = 200;

    private static final String ID = "ideUploadForm";

    private static final String TITLE = "Upload";

    private static UploadFileViewUiBinder uiBinder = GWT.create(UploadFileViewUiBinder.class);

    interface UploadFileViewUiBinder extends UiBinder<Widget, UploadFileView> {
    }

    @UiField
    ImageButton openButton, cancelButton;

    @UiField
    TextInput fileNameField;

    @UiField
    HorizontalPanel postFieldsPanel;

    @UiField
    FormPanel uploadForm;

    @UiField
    FileUploadInput fileUploadInput;

    @UiField
    ComboBoxField mimeTypesField;

    private Hidden nameHiddenField;

    private Hidden mimeTypeHiddenField;

    private Hidden overwriteHiddenField;

    public UploadFileView() {
        super(ID, "modal", TITLE, new Image(), WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        nameHiddenField = new Hidden(NAME_HIDDED_FIELD);
        mimeTypeHiddenField = new Hidden(MIME_TYPE_HIDDED_FIELD);
        overwriteHiddenField = new Hidden(OVERWRITE_HIDDED_FIELD);
    }

    @Override
    public HasValue<String> getMimeTypeField() {
        return mimeTypesField;
    }

    @Override
    public void setSelectedMimeType(String mimeType) {
        mimeTypesField.setValue(mimeType);
    }

    @Override
    public void setMimeTypes(String[] mimeTypes) {
        mimeTypesField.setValueMap(mimeTypes);
    }

    @Override
    public void setMimeTypeFieldEnabled(boolean enabled) {
        mimeTypesField.setEnabled(enabled);
    }

    @Override
    public HasClickHandlers getOpenButton() {
        return openButton;
    }

    @Override
    public void setOpenButtonEnabled(boolean enabled) {
        openButton.setEnabled(enabled);
    }

    @Override
    public HasClickHandlers getCloseButton() {
        return cancelButton;
    }

    @Override
    public FormPanel getUploadForm() {
        return uploadForm;
    }

    @Override
    public HasValue<String> getFileNameField() {
        return fileNameField;
    }

    @Override
    public HasFileSelectedHandler getFileUploadInput() {
        return fileUploadInput;
    }

    /** @see org.exoplatform.ide.extension.aws.client.s3.UploadFilePresenter.Display#setMimeTypeHiddedField(java.lang.String) */
    @Override
    public void setMimeTypeHiddedField(String mimeType) {
        mimeTypeHiddenField.setValue(mimeType);
        if (postFieldsPanel.getWidgetIndex(mimeTypeHiddenField) == -1)
            postFieldsPanel.add(mimeTypeHiddenField);

    }

    /** @see org.exoplatform.ide.extension.aws.client.s3.UploadFilePresenter.Display#setNameHiddedField(java.lang.String) */
    @Override
    public void setNameHiddedField(String name) {
        nameHiddenField.setValue(name);
        nameHiddenField.setName("name");
        if (postFieldsPanel.getWidgetIndex(nameHiddenField) == -1)
            postFieldsPanel.add(nameHiddenField);
    }

    /** @see org.exoplatform.ide.extension.aws.client.s3.UploadFilePresenter.Display#setOverwriteHiddedField(java.lang.Boolean) */
    @Override
    public void setOverwriteHiddedField(Boolean overwrite) {
        overwriteHiddenField.setValue(String.valueOf(overwrite));
        if (postFieldsPanel.getWidgetIndex(overwriteHiddenField) == -1)
            postFieldsPanel.add(overwriteHiddenField);
    }

}
