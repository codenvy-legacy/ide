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
package org.exoplatform.ide.git.client.remove;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View for removing changes in git index.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 30, 2011 9:25:02 AM anya $
 */
public class RemoveFromIndexView extends ViewImpl implements RemoveFromIndexPresenter.Display {
    public static final int     HEIGHT           = 95;

    public static final int     WIDTH            = 335;

    public static final String  ID               = "ideRemoveFromIndexView";

    private static final String REMOVE_BUTTON_ID = "ideRemoveFromIndexViewRemoveButton";

    private static final String CANCEL_BUTTON_ID = "ideRemoveFromIndexViewCancelButton";

    private static final String MESSAGE_FIELD_ID = "ideRemoveFromIndexViewMessageField";

    private static final String FROM_INDEX_ID    = "ideRemoveFromIndexOnlyBox";

    /* Elements titles */
    @UiField
    ImageButton                 removeButton;

    @UiField
    ImageButton                 cancelButton;

    @UiField
    Label                       messageField;

    @UiField
    CheckBox                    fromIndexBox;

    interface RemoveFromIndexViewUiBinder extends UiBinder<Widget, RemoveFromIndexView> {
    }

    private static RemoveFromIndexViewUiBinder uiBinder = GWT.create(RemoveFromIndexViewUiBinder.class);

    public RemoveFromIndexView() {
        super(ID, ViewType.MODAL, GitExtension.MESSAGES.removeFromIndexTitle(), null, WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));
        messageField.getElement().setId(MESSAGE_FIELD_ID);
        removeButton.setButtonId(REMOVE_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        fromIndexBox.getElement().setId(FROM_INDEX_ID);
    }

    /** @see org.exoplatform.ide.git.client.remove.RemoveFromIndexPresenter.Display#getRemoveButton() */
    @Override
    public HasClickHandlers getRemoveButton() {
        return removeButton;
    }

    /** @see org.exoplatform.ide.git.client.remove.RemoveFromIndexPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.git.client.remove.RemoveFromIndexPresenter.Display#getMessage() */
    @Override
    public HasValue<String> getMessage() {
        return messageField;
    }

    /** @see org.exoplatform.ide.git.client.remove.RemoveFromIndexPresenter.Display#getFromIndexValue() */
    @Override
    public HasValue<Boolean> getFromIndexValue() {
        return fromIndexBox;
    }
}
