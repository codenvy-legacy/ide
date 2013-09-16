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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 21, 2012 12:34:46 PM anya $
 */
public class CreateVersionView extends ViewImpl implements CreateVersionPresenter.Display {
    private static final String ID = "ideCreateVersionView";

    private static final int WIDTH = 580;

    private static final int HEIGHT = 310;

    private static final String VERSION_LABEL_FIELD_ID = "ideCreateApplicationViewVersionLabelField";

    private static final String DESCRIPTION_FIELD_ID = "ideCreateVersionViewDescriptionField";

    private static final String S3_BUCKET_FIELD_ID = "ideCreateVersionViewS3BucketField";

    private static final String S3_KEY_FIELD_ID = "ideCreateVersionViewS3KeyField";

    private static final String CREATE_BUTTON_ID = "ideCreateVersionViewCreateButton";

    private static final String CANCEL_BUTTON_ID = "ideCreateVersionViewCancelButton";

    private static CreateVersionViewUiBinder uiBinder = GWT.create(CreateVersionViewUiBinder.class);

    interface CreateVersionViewUiBinder extends UiBinder<Widget, CreateVersionView> {
    }

    @UiField
    TextInput versionLabelField;

    @UiField
    TextInput descriptionField;

    @UiField
    TextInput s3BucketField;

    @UiField
    TextInput s3KeyField;

    @UiField
    ImageButton createButton;

    @UiField
    ImageButton cancelButton;

    public CreateVersionView() {
        super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.createVersionViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        versionLabelField.setName(VERSION_LABEL_FIELD_ID);
        descriptionField.setName(DESCRIPTION_FIELD_ID);
        s3BucketField.setName(S3_BUCKET_FIELD_ID);
        s3KeyField.setName(S3_KEY_FIELD_ID);

        createButton.setButtonId(CREATE_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getVersionLabelField() */
    @Override
    public TextFieldItem getVersionLabelField() {
        return versionLabelField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getDescriptionField() */
    @Override
    public TextFieldItem getDescriptionField() {
        return descriptionField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getS3BucketField() */
    @Override
    public TextFieldItem getS3BucketField() {
        return s3BucketField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getS3KeyField() */
    @Override
    public TextFieldItem getS3KeyField() {
        return s3KeyField;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getCreateButton() */
    @Override
    public HasClickHandlers getCreateButton() {
        return createButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#enableCreateButton
     * (boolean) */
    @Override
    public void enableCreateButton(boolean enabled) {
        createButton.setEnabled(enabled);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.create.CreateVersionPresenter.Display#focusInVersionLabelField() */
    @Override
    public void focusInVersionLabelField() {
        versionLabelField.setFocus(true);
    }

}
