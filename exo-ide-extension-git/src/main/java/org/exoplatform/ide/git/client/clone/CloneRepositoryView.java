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
package org.exoplatform.ide.git.client.clone;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * UI for cloning repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 4:54:24 PM anya $
 */
public class CloneRepositoryView extends ViewImpl implements
                                                 org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display {
    public static final String  ID                    = "ideCloneRepositoryView";

    private static final String CLONE_BUTTON_ID       = "ideCloneRepositoryViewCloneButton";

    private static final String CANCEL_BUTTON_ID      = "ideCloneRepositoryViewCancelButton";

    private static final String PROJECT_NAME_FIELD_ID = "ideCloneRepositoryViewProjectNameField";

    private static final String REMOTE_URI_FIELD_ID   = "ideCloneRepositoryViewRemoteUriField";

    private static final String REMOTE_NAME_FIELD_ID  = "ideCloneRepositoryViewRemoteNameField";

    private static final String PROJECT_TYPE_FIELD_ID = "ideCloneRepositoryProjectTypeId";

    @UiField
    ImageButton                 cloneButton;

    @UiField
    ImageButton                 cancelButton;

    @UiField
    TextInput                   projectNameField;

    @UiField
    TextInput                   remoteUriField;

    @UiField
    TextInput                   remoteNameField;

    interface CloneRepositoryViewUiBinder extends UiBinder<Widget, CloneRepositoryView> {
    }

    private static CloneRepositoryViewUiBinder uiBinder = GWT.create(CloneRepositoryViewUiBinder.class);

    public CloneRepositoryView() {
        super(ID, ViewType.MODAL, GitExtension.MESSAGES.cloneTitle(), null, 480, 230, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
        remoteUriField.getElement().setPropertyString("placeholder", GitExtension.MESSAGES.cloneRemoteUriFieldExample());

        projectNameField.setName(PROJECT_NAME_FIELD_ID);
        remoteUriField.setName(REMOTE_URI_FIELD_ID);
        remoteNameField.setName(REMOTE_NAME_FIELD_ID);

        cloneButton.setButtonId(CLONE_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getProjectNameValue() */
    public HasValue<String> getProjectNameValue() {
        return projectNameField;
    }

    /** @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getRemoteUriValue() */
    public HasValue<String> getRemoteUriValue() {
        return remoteUriField;
    }

    /** @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getRemoteNameValue() */
    public HasValue<String> getRemoteNameValue() {
        return remoteNameField;
    }

    /** @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getCloneButton() */
    public HasClickHandlers getCloneButton() {
        return cloneButton;
    }

    /** @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#getCancelButton() */
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#enableCloneButton(boolean) */
    public void enableCloneButton(boolean enable) {
        cloneButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.git.client.clone.CloneRepositoryPresenter.Display#focusInRemoteUrlField() */
    @Override
    public void focusInRemoteUrlField() {
        remoteUriField.focus();
    }
}
