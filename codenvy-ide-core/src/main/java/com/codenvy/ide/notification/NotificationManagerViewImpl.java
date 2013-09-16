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
 * from Codenvy S.A.
 */
package com.codenvy.ide.notification;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.notification.NotificationManagerView.Status.EMPTY;
import static com.codenvy.ide.notification.NotificationManagerView.Status.IN_PROGRESS;

/**
 * The implementation of {@link NotificationManagerView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class NotificationManagerViewImpl extends Composite implements NotificationManagerView {
    interface NotificationManagerViewImplUiBinder extends UiBinder<Widget, NotificationManagerViewImpl> {
    }

    private static NotificationManagerViewImplUiBinder ourUiBinder = GWT.create(NotificationManagerViewImplUiBinder.class);

    @UiField
    FlowPanel   mainPanel;
    @UiField
    Label       count;
    @UiField
    SimplePanel iconPanel;
    @UiField(provided = true)
    final   NotificationResources res;
    private ActionDelegate        delegate;

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    public NotificationManagerViewImpl(NotificationResources resources) {
        this.res = resources;
        initWidget(ourUiBinder.createAndBindUi(this));
        mainPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onClicked(event.getClientX(), event.getClientY());
            }
        }, ClickEvent.getType());
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    /** {@inheritDoc} */
    @Override
    public void setStatus(Status status) {
        Image icon = createImage(status);
        iconPanel.setWidget(icon);
    }

    /**
     * Return image for status
     *
     * @param status
     * @return image for status
     */
    private Image createImage(Status status) {
        Image icon;
        if (status.equals(IN_PROGRESS)) {
            icon = new Image(res.progress());
        } else if (status.equals(EMPTY)) {
            icon = new Image(res.message());
        } else {
            icon = new Image(res.message());
        }

        return icon;
    }

    /** {@inheritDoc} */
    @Override
    public void setNotificationCount(int count) {
        String text = count > 0 ? String.valueOf(count) : "";
        this.count.setText(text);
    }
}