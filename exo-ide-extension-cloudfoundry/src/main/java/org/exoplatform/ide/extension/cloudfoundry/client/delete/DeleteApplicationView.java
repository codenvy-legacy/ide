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
package org.exoplatform.ide.extension.cloudfoundry.client.delete;

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
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;

/**
 * View for deleting CloudFoundry application.
 * View must be pointed in Views.gwt.xml.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 2, 2011 2:26:41 PM anya $
 */
public class DeleteApplicationView extends ViewImpl implements DeleteApplicationPresenter.Display {
    private static final String ID = "ideDeleteApplicationView";

    private static final int WIDTH = 420;

    private static final int HEIGHT = 160;

    private static final String RENAME_BUTTON_ID = "ideDeleteApplicationViewRenameButton";

    private static final String CANCEL_BUTTON_ID = "ideDeleteApplicationViewCancelButton";

    /** Checkbox, that indicates is delete services with application. */
    @UiField
    CheckBox deleteServicesField;

    /** Delete button. */
    @UiField
    ImageButton deleteButton;

    /** Cancel button. */
    @UiField
    ImageButton cancelButton;

    @UiField
    Label askLabel;

    @UiField
    Label askDeleteServicesLabel;

    private static DeleteApplicationViewUiBinder uiBinder = GWT.create(DeleteApplicationViewUiBinder.class);

    interface DeleteApplicationViewUiBinder extends UiBinder<Widget, DeleteApplicationView> {
    }

    public DeleteApplicationView() {
        super(ID, ViewType.MODAL, CloudFoundryExtension.LOCALIZATION_CONSTANT.deleteApplicationTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        deleteButton.setButtonId(RENAME_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationPresenter.Display#getDeleteServicesCheckbox() */
    @Override
    public HasValue<Boolean> getDeleteServicesCheckbox() {
        return deleteServicesField;
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationPresenter.Display#getDeleteButton() */
    @Override
    public HasClickHandlers getDeleteButton() {
        return deleteButton;
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationPresenter.Display#setAskMessage(java.lang.String) */
    @Override
    public void setAskMessage(String message) {
        askLabel.setIsHTML(true);
        askLabel.setValue(message);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationPresenter.Display#setAskDeleteServices(java.lang
     * .String) */
    @Override
    public void setAskDeleteServices(String text) {
        askDeleteServicesLabel.setIsHTML(true);
        askDeleteServicesLabel.setValue(text);
    }

}
