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
package org.exoplatform.ide.extension.aws.client.s3;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

public class CreateBucketView extends ViewImpl implements CreateBucketPresenter.Display {
    private static final String ID = "ideLoginView";

    private static final int WIDTH = 410;

    private static final int HEIGHT = 213;

    private static final String CREATE_BUTTON_ID = "ideCreateBucketButton";

    private static final String CANCEL_BUTTON_ID = "ideCreateBucketCancelButton";

    private static final String NAME_FIELD_ID = "ideCreateBucketNameField";

    private static final String REGION_FIELD_ID = "ideCreateBucketRegionField";

    /** UI binder for this view. */
    private static LoginViewUiBinder uiBinder = GWT.create(LoginViewUiBinder.class);

    interface LoginViewUiBinder extends UiBinder<Widget, CreateBucketView> {
    }

    /** Email field. */
    @UiField
    TextInput bucketNameField;

    /** Password field. */
    @UiField
    ListBox regionField;

    /** Login button. */
    @UiField
    ImageButton createButton;

    /** Cancel button. */
    @UiField
    ImageButton cancelButton;

    public CreateBucketView() {
        super(ID, ViewType.MODAL, "Create bucket", null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        bucketNameField.setName(NAME_FIELD_ID);
        regionField.setName(REGION_FIELD_ID);
        createButton.setButtonId(CREATE_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getBucketName() */
    @Override
    public TextFieldItem getBucketName() {
        return bucketNameField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getRegion() */
    @Override
    public ListBox getRegion() {
        return regionField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getDeployButton() */
    @Override
    public HasClickHandlers getCreateButton() {
        return createButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }


    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#enableLaunchButton(boolean) */
    @Override
    public void enableCreateButton(boolean enable) {
        createButton.setEnabled(enable);
    }

    /** @see org.exoplatform.ide.extension.aws.client.login.LoginPresenter.Display#focusInName() */
    @Override
    public void focusInName() {
        bucketNameField.setFocus(true);
    }

}
