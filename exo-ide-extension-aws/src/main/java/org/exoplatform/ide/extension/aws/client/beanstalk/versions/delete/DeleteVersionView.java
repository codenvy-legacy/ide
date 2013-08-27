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
package org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete;

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
import org.exoplatform.ide.extension.aws.client.AWSExtension;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 5:59:53 PM anya $
 */
public class DeleteVersionView extends ViewImpl implements DeleteVersionPresenter.Display {
    private static final String ID = "ideDeleteVersionView";

    private static final int WIDTH = 460;

    private static final int HEIGHT = 170;

    private static final String DELETE_BUTTON_ID = "ideDeleteVersionViewDeleteButton";

    private static final String CANCEL_BUTTON_ID = "ideDeleteVersionViewCancelButton";

    private static final String DELETE_S3_BUNDLE_ID = "ideDeleteVersionViewDeleteS3Bundle";

    @UiField
    Label questionLabel;

    @UiField
    CheckBox deleteS3BundleField;

    @UiField
    ImageButton deleteButton;

    @UiField
    ImageButton cancelButton;

    private static DeleteVersionViewUiBinder uiBinder = GWT.create(DeleteVersionViewUiBinder.class);

    interface DeleteVersionViewUiBinder extends UiBinder<Widget, DeleteVersionView> {
    }

    public DeleteVersionView() {
        super(ID, ViewType.MODAL, AWSExtension.LOCALIZATION_CONSTANT.deleteVersionViewTitle(), null, WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));

        questionLabel.setIsHTML(true);
        deleteButton.setButtonId(DELETE_BUTTON_ID);
        cancelButton.setButtonId(CANCEL_BUTTON_ID);
        deleteS3BundleField.setName(DELETE_S3_BUNDLE_ID);
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.DeleteVersionPresenter.Display#getDeleteButton() */
    @Override
    public HasClickHandlers getDeleteButton() {
        return deleteButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.DeleteVersionPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.DeleteVersionPresenter.Display#getDeleteQuestion() */
    @Override
    public HasValue<String> getDeleteQuestion() {
        return questionLabel;
    }

    /** @see org.exoplatform.ide.extension.aws.client.beanstalk.versions.delete.DeleteVersionPresenter.Display#getDeleteS3Bundle() */
    @Override
    public HasValue<Boolean> getDeleteS3Bundle() {
        return deleteS3BundleField;
    }

}
