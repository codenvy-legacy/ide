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
package org.exoplatform.ide.client.operation.rename;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ComboBoxField;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * View for renaming files and for changing mime-type of file.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameFileView extends ViewImpl implements RenameFilePresenter.Display {

    /*
     * Form constants.
     */
    private static final int WIDTH = 440;

    private static final int HEIGHT = 220;

    private static final String ID = "ideRenameItemForm";

    private static final String RENAME_BUTTON_ID = "ideRenameItemFormRenameButton";

    private static final String CANCEL_BUTTON_ID = "ideRenameItemFormCancelButton";

    private static final String RENAME_FIELD = "ideRenameItemFormRenameField";

    private static final String MIME_TYPE_FIELD = "ideRenameItemFormMimeTypeField";

    private static final String TITLE = IDE.NAVIGATION_CONSTANT.renameItemTitle();

    private static final String WARNING_MSG_STYLE = "exo-rename-warning-msg";

    @UiField
    TextInput nameField;

    @UiField
    ComboBoxField mimeTypesField;

    @UiField
    ImageButton renameButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    Label warningLabel;

    interface RenameFileViewUiBinder extends UiBinder<Widget, RenameFileView> {
    }

    private static RenameFileViewUiBinder uiBinder = GWT.create(RenameFileViewUiBinder.class);

    public RenameFileView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        nameField.setName(RENAME_FIELD);
        mimeTypesField.setName(MIME_TYPE_FIELD);
        renameButton.setButtonId(RENAME_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        warningLabel.addStyleName(WARNING_MSG_STYLE);
    }

    public HasValue<String> getItemNameField() {
        return nameField;
    }

    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    public HasClickHandlers getRenameButton() {
        return renameButton;
    }

    public HasKeyPressHandlers getNameFieldKeyPressHandler() {
        return (HasKeyPressHandlers)nameField;
    }

    public void setMimeTypes(String[] mimeTypes) {
        mimeTypesField.setValueMap(mimeTypes);
    }

    public HasValue<String> getMimeType() {
        return mimeTypesField;
    }

    public void disableMimeTypeSelect() {
        mimeTypesField.setEnabled(false);
    }

    public void enableMimeTypeSelect() {
        mimeTypesField.setEnabled(true);
    }

    public void setDefaultMimeType(String mimeType) {
        mimeTypesField.setValue(mimeType);
    }

    public void addLabel(String text) {
        if (text == null) {
            warningLabel.setText("");
            warningLabel.setHeight("0px");
        } else {
            warningLabel.setHeight("12px");
            warningLabel.setText(text);
        }
    }

    /** @see org.exoplatform.ide.client.navigation.RenameFilePresenter.Display#enableRenameButton(boolean) */
    @Override
    public void enableRenameButton(boolean enable) {
        renameButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.client.navigation.RenameFilePresenter.Display#focusInNameField() */
    @Override
    public void focusInNameField() {
        nameField.focus();
    }

    /** @see org.exoplatform.ide.client.operation.rename.RenameFilePresenter.Display#selectAllText() */
    @Override
    public void selectAllText() {
        nameField.selectAll();
    }

}