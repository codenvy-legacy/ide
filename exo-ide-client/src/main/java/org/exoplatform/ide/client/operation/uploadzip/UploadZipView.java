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
package org.exoplatform.ide.client.operation.uploadzip;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.upload.FileUploadInput;
import org.exoplatform.ide.client.framework.ui.upload.FormFields;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UploadZipView extends ViewImpl implements
                                            org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter.Display {

    private static final String ID = "ideUploadForm";

    private static final String OVERWRITE_HIDDEN_FIELD = "overwrite";

    /** Initial width of this view. */
    private static final int WIDTH = 450;

    /** Initial height of this view. */
    private static final int HEIGHT = 165;

    private static final String UPLOAD_FOLDER_TITLE = IDE.UPLOAD_CONSTANT.uploadFolderTitle();

    private static UploadZipViewUiBinder uiBinder = GWT.create(UploadZipViewUiBinder.class);

    interface UploadZipViewUiBinder extends UiBinder<Widget, UploadZipView> {
    }

    @UiField
    FormPanel uploadForm;

    @UiField
    TextInput fileNameField;

    @UiField
    HorizontalPanel postFieldsPanel;

    @UiField
    FileUploadInput fileUploadInput;

    @UiField
    ImageButton uploadButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    CheckBox overwriteField;

    private Hidden overwriteHiddenField;

    public UploadZipView() {
        super(ID, "modal", UPLOAD_FOLDER_TITLE, new Image(IDEImageBundle.INSTANCE.upload()), WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
        overwriteHiddenField = new Hidden(OVERWRITE_HIDDEN_FIELD);
    }

    @Override
    public HasClickHandlers getUploadButton() {
        return uploadButton;
    }

    @Override
    public void setUploadButtonEnabled(boolean enabled) {
        uploadButton.setEnabled(enabled);
    }

    @Override
    public HasClickHandlers getCancelButton() {
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
    public void setHiddenFields(String location, String mimeType, String nodeType, String jcrContentNodeType) {
        Hidden locationField = new Hidden(FormFields.LOCATION, location);
        postFieldsPanel.add(locationField);
    }

    @Override
    public HasFileSelectedHandler getFileUploadInput() {
        return fileUploadInput;
    }

    /** @see org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter.Display#setOverwriteHiddenField(java.lang.Boolean) */
    @Override
    public void setOverwriteHiddenField(Boolean overwrite) {
        overwriteHiddenField.setValue(String.valueOf(overwrite));
        if (postFieldsPanel.getWidgetIndex(overwriteHiddenField) == -1)
            postFieldsPanel.add(overwriteHiddenField);
    }

    /** @see org.exoplatform.ide.client.operation.uploadzip.UploadZipPresenter.Display#getOverwriteAllField() */
    @Override
    public HasValue<Boolean> getOverwriteAllField() {
        return overwriteField;
    }

}
