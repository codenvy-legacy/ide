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
package org.exoplatform.ide.git.client.remote;

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

/**
 * View for adding new remote repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 18, 2011 5:48:04 PM anya $
 */
public class AddRemoteRepositoryView extends ViewImpl implements AddRemoteRepositoryPresenter.Display {
    private static final int    HEIGHT           = 200;

    private static final int    WIDTH            = 480;

    public static final String  ID               = "ideAddRemoteRepositoryView";

    /* Elements IDs */

    private static final String OK_BUTTON_ID     = "ideAddRemoteRepositoryViewOkButton";

    private static final String CANCEL_BUTTON_ID = "ideAddRemoteRepositoryViewCancelButton";

    private static final String NAME_FIELD_ID    = "ideAddRemoteRepositoryViewNameField";

    private static final String URL_FIELD_ID     = "ideAddRemoteRepositoryViewUrlField";

    /** Ok button. */
    @UiField
    ImageButton                 okButton;

    /** Cancel button. */
    @UiField
    ImageButton                 cancelButton;

    /** Remote repository name field. */
    @UiField
    TextInput                   nameField;

    /** Remote repository URL field. */
    @UiField
    TextInput                   urlField;

    interface AddRemoteRepositoryViewUiBinder extends UiBinder<Widget, AddRemoteRepositoryView> {
    }

    private static AddRemoteRepositoryViewUiBinder uiBinder = GWT.create(AddRemoteRepositoryViewUiBinder.class);

    public AddRemoteRepositoryView(String title) {
        super(ID, ViewType.MODAL, title, null, WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        nameField.setName(NAME_FIELD_ID);
        urlField.setName(URL_FIELD_ID);
        okButton.setButtonId(OK_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#getOkButton() */
    @Override
    public HasClickHandlers getOkButton() {
        return okButton;
    }

    /** @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#enableOkButton(boolean) */
    @Override
    public void enableOkButton(boolean enable) {
        okButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#getName() */
    @Override
    public HasValue<String> getName() {
        return nameField;
    }

    /** @see org.exoplatform.ide.git.client.remote.AddRemoteRepositoryPresenter.Display#getUrl() */
    @Override
    public HasValue<String> getUrl() {
        return urlField;
    }

}
