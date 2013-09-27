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
package org.exoplatform.ide.git.client.branch;

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
import org.exoplatform.ide.git.shared.Branch;

/**
 * View for displaying branches and work with it. Must be pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 8, 2011 9:50:54 AM anya $
 */
public class BranchView extends ViewImpl implements BranchPresenter.Display {
    private static final int    HEIGHT             = 300;

    private static final int    WIDTH              = 522;

    public static final String  ID                 = "ideBranchView";

    /* Elements IDs */
    private static final String CREATE_BUTTON_ID   = "ideBranchViewCreateButton";

    private static final String CHECKOUT_BUTTON_ID = "ideBranchViewCheckoutButton";

    private static final String DELETE_BUTTON_ID   = "ideBranchViewDeleteButton";

    private static final String RENAME_BUTTON_ID   = "ideBranchViewRenameButton";

    private static final String CLOSE_BUTTON_ID    = "ideBranchViewCloseButton";

    /** Create branch button. */
    @UiField
    ImageButton                 createButton;

    /** Checkout branch button. */
    @UiField
    ImageButton                 checkoutButton;

    /** Delete branch button. */
    @UiField
    ImageButton                 deleteButton;

    /** Rename branch button. */
    @UiField
    ImageButton                 renameButton;

    /** Cancel button. */
    @UiField
    ImageButton                 closeButton;

    @UiField
    BranchGrid                  branchGrid;

    interface BranchViewUiBinder extends UiBinder<Widget, BranchView> {
    }

    private static BranchViewUiBinder uiBinder = GWT.create(BranchViewUiBinder.class);

    public BranchView() {
        super(ID, ViewType.MODAL, GitExtension.MESSAGES.branchTitle(), null, WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        checkoutButton.setButtonId(CHECKOUT_BUTTON_ID);
        createButton.setButtonId(CREATE_BUTTON_ID);
        deleteButton.setButtonId(DELETE_BUTTON_ID);
        renameButton.setButtonId(RENAME_BUTTON_ID);
        closeButton.setButtonId(CLOSE_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getCreateBranchButton() */
    @Override
    public HasClickHandlers getCreateBranchButton() {
        return createButton;
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getCheckoutBranchButton() */
    @Override
    public HasClickHandlers getCheckoutBranchButton() {
        return checkoutButton;
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getDeleteBranchButton() */
    @Override
    public HasClickHandlers getDeleteBranchButton() {
        return deleteButton;
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getRenameBranchButton() */
    @Override
    public HasClickHandlers getRenameBranchButton() {
        return renameButton;
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getCloseButton() */
    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getBranchesGrid() */
    @Override
    public ListGridItem<Branch> getBranchesGrid() {
        return branchGrid;
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#getSelectedBranch() */
    @Override
    public Branch getSelectedBranch() {
        return branchGrid.getSelectedBranch();
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#enableDeleteButton(boolean) */
    @Override
    public void enableDeleteButton(boolean enabled) {
        deleteButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#enableCheckoutButton(boolean) */
    @Override
    public void enableCheckoutButton(boolean enabled) {
        checkoutButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.git.client.branch.BranchPresenter.Display#enableRenameButton(boolean) */
    @Override
    public void enableRenameButton(boolean enabled) {
        renameButton.setEnabled(enabled);
    }

}
