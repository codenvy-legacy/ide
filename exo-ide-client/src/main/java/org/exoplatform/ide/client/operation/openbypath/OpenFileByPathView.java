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
package org.exoplatform.ide.client.operation.openbypath;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenFileByPathView extends ViewImpl implements
                                                 org.exoplatform.ide.client.operation.openbypath.OpenFileByPathPresenter.Display {

    public static final int WIDTH = 500;

    public static final int HEIGHT = 165;

    private static final String ID = "ideOpenFileByPathWindow";

    private static final String OPEN_BUTTON_ID = "ideOpenFileByPathFormOpenButton";

    private static final String CANCEL_BUTTON_ID = "ideOpenFileByPathFormCancelButton";

    private static final String FILE_PATH_FIELD_NAME = "ideOpenFileByPathFormFilePathField";

    private static final String TITLE = IDE.UPLOAD_CONSTANT.openFileByPathTitle();

    @UiField
    TextInput filePathField;

    @UiField
    ImageButton openButton;

    @UiField
    ImageButton cancelButton;

    interface OpenFileByPathViewUiBinder extends UiBinder<Widget, OpenFileByPathView> {
    }

    private static OpenFileByPathViewUiBinder uiBinder = GWT.create(OpenFileByPathViewUiBinder.class);

    public OpenFileByPathView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.ok()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        filePathField.setName(FILE_PATH_FIELD_NAME);
        openButton.setButtonId(OPEN_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    public HasClickHandlers getCancelButton() {
        return this.cancelButton;
    }

    public HasKeyPressHandlers getFilePathField() {
        return this.filePathField;
    }

    public void disableOpenButton() {
        openButton.setEnabled(false);
    }

    public void enableOpenButton() {
        openButton.setEnabled(true);
    }

    public HasClickHandlers getOpenButton() {
        return openButton;
    }

    public TextFieldItem getFilePathFieldOrigin() {
        return filePathField;
    }

    /** @see org.exoplatform.ide.client.upload.OpenFileByPathPresenter.Display#selectPathField() */
    @Override
    public void selectPathField() {
        filePathField.selectAll();
    }

    /** @see org.exoplatform.ide.client.upload.OpenFileByPathPresenter.Display#focusInPathField() */
    @Override
    public void focusInPathField() {
        filePathField.focus();
    }

}
