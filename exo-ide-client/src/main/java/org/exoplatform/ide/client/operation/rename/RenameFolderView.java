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

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * View for renaming folders.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class RenameFolderView extends ViewImpl implements RenameFolderPresenter.Display {

    /*
     * Form constants.
     */
    private static final int WIDTH = 410;

    private static final int HEIGHT = 175;

    private static final String ID = "ideRenameItemForm";

    private static final String RENAME_BUTTON_ID = "ideRenameItemFormRenameButton";

    private static final String CANCEL_BUTTON_ID = "ideRenameItemFormCancelButton";

    private static final String RENAME_FIELD = "ideRenameItemFormRenameField";

    @UiField
    TextInput nameField;

    @UiField
    ImageButton renameButton;

    @UiField
    ImageButton cancelButton;

    private static final String TITLE = IDE.NAVIGATION_CONSTANT.renameItemTitle();

    interface RenameFolderViewUiBinder extends UiBinder<Widget, RenameFolderView> {
    }

    private static RenameFolderViewUiBinder uiBinder = GWT.create(RenameFolderViewUiBinder.class);

    public RenameFolderView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT, true);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        nameField.setName(RENAME_FIELD);
        renameButton.setButtonId(RENAME_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    public HasValue<String> getNameField() {
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

    /** @see org.exoplatform.ide.client.navigation.RenameFolderPresenter.Display#enableRenameButton(boolean) */
    public void enableRenameButton(boolean enable) {
        renameButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.client.navigation.RenameFolderPresenter.Display#focusInNameField() */
    @Override
    public void focusInNameField() {
        nameField.focus();
    }

}
