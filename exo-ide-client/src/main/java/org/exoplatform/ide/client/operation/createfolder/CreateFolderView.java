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
package org.exoplatform.ide.client.operation.createfolder;

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
 * Created by The eXo Platform SAS .
 * <p/>
 * View for create folder form.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class CreateFolderView extends ViewImpl implements CreateFolderPresenter.Display {

    public static final String ID = "ideCreateFolderForm";

    public static final int WIDTH = 410;

    public static final int HEIGHT = 175;

    public final String ID_CREATE_BUTTON = "ideCreateFolderFormCreateButton";

    public final String ID_CANCEL_BUTTON = "ideCreateFolderFormCancelButton";

    public final String ID_DYNAMIC_FORM = "ideCreateFolderFormDynamicForm";

    public final String NAME_FIELD = "ideCreateFolderFormNameField";

    public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.createFolderFormTitle();

    @UiField
    TextInput folderNameField;

    @UiField
    ImageButton createButton;

    @UiField
    ImageButton cancelButton;

    interface CreateFolderViewUiBinder extends UiBinder<Widget, CreateFolderView> {
    }

    private static CreateFolderViewUiBinder uiBinder = GWT.create(CreateFolderViewUiBinder.class);

    /**
     * @param eventBus
     * @param selectedItem
     * @param href
     */
    public CreateFolderView() {
        super(ID, "popup", TITLE, new Image(IDEImageBundle.INSTANCE.newFolder()), WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        folderNameField.setName(NAME_FIELD);
        createButton.setButtonId(ID_CREATE_BUTTON);
        cancelButton.setButtonId(ID_CANCEL_BUTTON);
    }

    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    public HasClickHandlers getCreateButton() {
        return createButton;
    }

    public HasValue<String> getFolderNameField() {
        return folderNameField;
    }

    public HasKeyPressHandlers getFolderNameFiledKeyPressed() {
        return (HasKeyPressHandlers)folderNameField;
    }

    /** @see org.exoplatform.ide.client.navigation.CreateFolderPresenter.Display#setFocusInNameField() */
    @Override
    public void setFocusInNameField() {
        folderNameField.focus();
    }

    /** @see org.exoplatform.ide.client.operation.createfolder.CreateFolderPresenter.Display#selectFolderName() */
    @Override
    public void selectFolderName() {
        folderNameField.selectAll();
    }

}
