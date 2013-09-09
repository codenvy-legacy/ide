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
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.ListBox;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSError;
import org.exoplatform.ide.extension.aws.client.s3.events.BucketCreatedEvent;
import org.exoplatform.ide.extension.aws.shared.s3.S3Region;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: CreateBucketPresenter.java Sep 24, 2012 vetal $
 */
public class CreateBucketPresenter implements ViewClosedHandler {
    interface Display extends IsView {
        TextFieldItem getBucketName();

        ListBox getRegion();

        HasClickHandlers getCreateButton();

        HasClickHandlers getCancelButton();

        void enableCreateButton(boolean enable);

        void focusInName();

    }

    private Display display;

    public CreateBucketPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getCreateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doCreate();

            }
        });

        display.getBucketName().addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 && isFieldsFullFilled()) {
                    doCreate();
                }
            }
        });

        display.getRegion().addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                if (event.getNativeKeyCode() == 13 && isFieldsFullFilled()) {
                    doCreate();
                }
            }
        });

        display.getBucketName().addValueChangeHandler(new ValueChangeHandler<String>() {

            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                display.enableCreateButton(isFieldsFullFilled());
            }
        });

        setRegions();
        display.enableCreateButton(false);

    }

    private void setRegions() {
        S3Region[] regions = S3Region.values();
        for (int i = 0; i < regions.length; i++) {
            S3Region s3Region = regions[i];
            display.getRegion().addItem(s3Region.toString());
        }

    }

    /** @return {@link Boolean} <code>true</code> if fields are full filled */
    private boolean isFieldsFullFilled() {
        return (display.getBucketName().getValue() != null && !display.getBucketName().getValue().isEmpty());
    }

    /** Perform login operation. */
    public void doCreate() {
        try {
            S3Service.getInstance()
                     .createBucket(
                             new AsyncRequestCallback<String>() {

                                 @Override
                                 protected void onSuccess(String result) {
                                     IDE.getInstance().closeView(display.asView().getId());
                                     IDE.fireEvent(new BucketCreatedEvent());
                                 }

                                 @Override
                                 protected void onFailure(Throwable exception) {
                                     IDE.getInstance().closeView(display.asView().getId());
                                     AWSError awsError = new AWSError(exception.getMessage());
                                     if (awsError.getAwsErrorMessage() != null) {
                                         Dialogs.getInstance().showError(
                                                 awsError.getAwsService() + " (" + awsError.getStatusCode() + ")",
                                                 awsError.getAwsErrorCode() + " : " + awsError.getAwsErrorMessage());
                                     } else {
                                         Dialogs.getInstance().showError("Amazon S3 Service", awsError.getAwsErrorMessage());
                                     }
                                 }

                             }, display.getBucketName().getValue(),
                             display.getRegion().getItemText(display.getRegion().getSelectedIndex()));
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    public void onCreateBucket() {

        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }

    }
}