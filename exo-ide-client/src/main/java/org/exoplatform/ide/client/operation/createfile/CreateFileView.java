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
package org.exoplatform.ide.client.operation.createfile;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CreateFileView.java Feb 6, 2013 6:04:55 PM azatsarynnyy $
 */

public class CreateFileView extends ViewImpl implements CreateFilePresenter.Display {

    public static final String ID = "ideCreateFileForm";

    public static final int WIDTH = 410;

    public static final int HEIGHT = 175;

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createFileFormTitle();

    public final String NAME_FIELD = "ideCreateFileFormNameField";

    public final String ID_CREATE_BUTTON = "ideCreateFileFormCreateButton";

    public final String ID_CANCEL_BUTTON = "ideCreateFileFormCancelButton";

    @UiField
    TextInput fileNameField;

    @UiField
    ImageButton createButton;

    @UiField
    ImageButton cancelButton;

    interface CreateFolderViewUiBinder extends UiBinder<Widget, CreateFileView> {
    }

    private static CreateFolderViewUiBinder uiBinder = GWT.create(CreateFolderViewUiBinder.class);

    public CreateFileView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.newFile()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        fileNameField.setName(NAME_FIELD);
        createButton.setButtonId(ID_CREATE_BUTTON);
        cancelButton.setButtonId(ID_CANCEL_BUTTON);
    }

    /** @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#getCreateButton() */
    @Override
    public HasClickHandlers getCreateButton() {
        return createButton;
    }

    /** @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#getFileNameField() */
    @Override
    public HasValue<String> getFileNameField() {
        return fileNameField;
    }

    /** @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#getFileNameFiledKeyPressed() */
    @Override
    public HasKeyPressHandlers getFileNameFiledKeyPressed() {
        return (HasKeyPressHandlers)fileNameField;
    }

    /** @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#setFocusInNameField() */
    @Override
    public void setFocusInNameField() {
        fileNameField.focus();
    }

    /** @see org.exoplatform.ide.client.operation.createfile.CreateFilePresenter.Display#selectFileName(int) */
    @Override
    public void selectFileName(int extensionLength) {
        fileNameField.setSelectionRange(0, fileNameField.getValue().length() - extensionLength - 1);
    }

}