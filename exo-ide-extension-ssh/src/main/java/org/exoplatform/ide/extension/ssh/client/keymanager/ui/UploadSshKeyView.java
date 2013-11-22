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
package org.exoplatform.ide.extension.ssh.client.keymanager.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.client.framework.ui.upload.FileUploadInput;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.UploadSshKeyPresenter.Display;

/**
 * This view represent upload private ssh key. It's contains two fields:
 * <ul>
 * <li>Host name
 * <li>File name(which fills by "File upload dialog")
 * </ul>
 * And two buttons:
 * <ul>
 * <li>Upload
 * <li>Cancel
 * </ul>
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */

public class UploadSshKeyView extends ViewImpl implements Display {

    /** ID of View */
    private static final String ID = "ideUploadSshKeyView";

    private static UploadSshKeyUiBinder uiBinder = GWT.create(UploadSshKeyUiBinder.class);

    interface UploadSshKeyUiBinder extends UiBinder<Widget, UploadSshKeyView> {
    }

    @UiField
    TextInput hostField;

    @UiField
    FileUploadInput fileField;

    @UiField
    ImageButton cancelButton;

    @UiField
    ImageButton uploadButton;

    @UiField
    TextInput fileNameField;

    @UiField
    Label messageLabel;

    @UiField
    FormPanel formPanel;

    public UploadSshKeyView() {
        super(ID, ViewType.MODAL, "Upload private SSH key", null, 350, 130, false);
        add(uiBinder.createAndBindUi(this));
    }

    /** {@inheritDoc} */
    @Override
    public HasValue<String> getHostField() {
        return hostField;
    }

    /** {@inheritDoc} */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** {@inheritDoc} */
    @Override
    public HasValue<String> getFileNameField() {
        return fileNameField;
    }

    /** {@inheritDoc} */
    @Override
    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /** {@inheritDoc} */
    @Override
    public HasClickHandlers getUploadButton() {
        return uploadButton;
    }

    /** {@inheritDoc} */
    @Override
    public FormPanel getFormPanel() {
        return formPanel;
    }

    /** {@inheritDoc} */
    @Override
    public void setUploadButtonEnabled() {
        uploadButton.setEnabled(true);
    }

    /** {@inheritDoc} */
    @Override
    public HasFileSelectedHandler getFileUploadInput() {
        return fileField;
    }

}
