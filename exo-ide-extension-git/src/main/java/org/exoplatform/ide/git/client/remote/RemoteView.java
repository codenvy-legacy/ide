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
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.shared.Remote;

/**
 * View for remote repositories list with possibility to add and delete remote repository. Must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 18, 2011 12:31:05 PM anya $
 */
public class RemoteView extends ViewImpl implements RemotePresenter.Display {
    private static final int    HEIGHT           = 280;

    private static final int    WIDTH            = 480;

    public static final String  ID               = "ideRemoteView";

    /* Elements IDs */

    private static final String ADD_BUTTON_ID    = "ideRemoteViewAddButton";

    private static final String DELETE_BUTTON_ID = "ideRemoteViewDeleteButton";

    private static final String CLOSE_BUTTON_ID  = "ideRemoteViewCloseButton";

    /** Create remote repository button. */
    @UiField
    ImageButton                 addButton;

    /** Delete remote repository button. */
    @UiField
    ImageButton                 deleteButton;

    /** Close button. */
    @UiField
    ImageButton                 closeButton;

    /** Grid with remote repositories. */
    @UiField
    RemoteGrid                  remoteGrid;

    interface RemoteViewUiBinder extends UiBinder<Widget, RemoteView> {
    }

    private static RemoteViewUiBinder uiBinder = GWT.create(RemoteViewUiBinder.class);

    public RemoteView() {
        super(ID, ViewType.MODAL, GitExtension.MESSAGES.remotesViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        addButton.setButtonId(ADD_BUTTON_ID);
        deleteButton.setButtonId(DELETE_BUTTON_ID);
        closeButton.setButtonId(CLOSE_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getAddButton() */
    @Override
    public HasClickHandlers getAddButton() {
        return addButton;
    }

    /** @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getRemoteGrid() */
    @Override
    public ListGridItem<Remote> getRemoteGrid() {
        return remoteGrid;
    }

    /** @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getSelectedRemote() */
    @Override
    public Remote getSelectedRemote() {
        return remoteGrid.getSelectedRemote();
    }

    /** @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#getDeleteButton() */
    @Override
    public HasClickHandlers getDeleteButton() {
        return deleteButton;
    }

    /** @see org.exoplatform.ide.git.client.remote.RemotePresenter.Display#enableDeleteButton(boolean) */
    @Override
    public void enableDeleteButton(boolean enable) {
        deleteButton.setEnabled(enable);
    }
}
