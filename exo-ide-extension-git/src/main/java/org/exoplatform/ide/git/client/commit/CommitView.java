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
package org.exoplatform.ide.git.client.commit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View for commiting from index to repository. Must be added to <b>View.gwt.xml file</b>.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 31, 2011 10:38:47 AM anya $
 */
public class CommitView extends ViewImpl implements CommitPresenter.Display {
    private static final int    HEIGHT           = 240;

    private static final int    WIDTH            = 460;

    public static final String  ID               = "ideCommitView";

    /* Elements IDs */

    private static final String COMMIT_BUTTON_ID = "ideCommitViewCommitButton";

    private static final String CANCEL_BUTTON_ID = "ideCommitViewCancelButton";

    private static final String MESSAGE_FIELD_ID = "ideCommitViewMessageField";

    private static final String ALL_FIELD_ID     = "ideCommitViewAllField";

    private static final String AMEND_FIELD_ID   = "ideCommitViewAmendField";

    /* Elements titles */

    @UiField
    ImageButton                 commitButton;

    @UiField
    ImageButton                 cancelButton;

    @UiField
    TextAreaInput               messageField;

    @UiField
    CheckBox                    allField;

    @UiField
    CheckBox                    amendField;

    interface CommitViewUiBinder extends UiBinder<Widget, CommitView> {
    }

    private static CommitViewUiBinder uiBinder = GWT.create(CommitViewUiBinder.class);

    public CommitView() {
        super(ID, ViewType.MODAL, GitExtension.MESSAGES.commitTitle(), null, WIDTH, HEIGHT);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        allField.setName(ALL_FIELD_ID);
        amendField.setName(AMEND_FIELD_ID);

        messageField.setName(MESSAGE_FIELD_ID);
        commitButton.setButtonId(COMMIT_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getCommitButton() */
    @Override
    public HasClickHandlers getCommitButton() {
        return commitButton;
    }

    /** @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getMessage() */
    @Override
    public HasValue<String> getMessage() {
        return messageField;
    }

    /** @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#enableCommitButton(boolean) */
    @Override
    public void enableCommitButton(boolean enable) {
        commitButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#focusInMessageField() */
    @Override
    public void focusInMessageField() {
        messageField.focus();
    }

    /** @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getAllField() */
    @Override
    public HasValue<Boolean> getAllField() {
        return allField;
    }

    /** @see org.exoplatform.ide.git.client.commit.CommitPresenter.Display#getAmendField() */
    @Override
    public HasValue<Boolean> getAmendField() {
        return amendField;
    }
}
