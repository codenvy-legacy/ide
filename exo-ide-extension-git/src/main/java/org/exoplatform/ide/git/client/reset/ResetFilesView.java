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
package org.exoplatform.ide.git.client.reset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.git.client.GitExtension;

/**
 * View for reseting files in index. Must be added to Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 12, 2011 5:57:31 PM anya $
 */
public class ResetFilesView extends ViewImpl implements ResetFilesPresenter.Display {
    private static final int    HEIGHT           = 290;

    private static final int    WIDTH            = 480;

    public static final String  ID               = "ideResetFilesView";

    /* Elements IDs */
    private static final String RESET_BUTTON_ID  = "ideResetFilesViewResetButton";

    private static final String CANCEL_BUTTON_ID = "ideResetFilesViewCancelButton";

    /** Reset files button. */
    @UiField
    ImageButton                 resetButton;

    /** Cancel button. */
    @UiField
    ImageButton                 cancelButton;

    /** The grid to view files in index. */
    @UiField
    IndexFilesGrid              indexFilesGrid;

    interface ResetFilesViewUiBinder extends UiBinder<Widget, ResetFilesView> {
    }

    private static ResetFilesViewUiBinder uiBinder = GWT.create(ResetFilesViewUiBinder.class);

    public ResetFilesView() {
        super(ID, ViewType.MODAL, GitExtension.MESSAGES.resetFilesViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        resetButton.setButtonId(RESET_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.git.client.reset.ResetFilesPresenter.Display#getResetButton() */
    @Override
    public HasClickHandlers getResetButton() {
        return resetButton;
    }

    /** @see org.exoplatform.ide.git.client.reset.ResetFilesPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.git.client.reset.ResetFilesPresenter.Display#getIndexFilesGrid() */
    @Override
    public ListGrid<IndexFile> getIndexFilesGrid() {
        return indexFilesGrid;
    }
}
