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

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.workspace.WorkspaceView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.notification.NotificationManagerView.Status.EMPTY;
import static com.codenvy.ide.notification.NotificationManagerView.Status.IN_PROGRESS;

/**
 * The implementation of {@link NotificationManagerView}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class NotificationManagerViewImpl extends BaseView<NotificationManagerView.ActionDelegate> implements NotificationManagerView {
    interface NotificationManagerViewImplUiBinder extends UiBinder<Widget, NotificationManagerViewImpl> {
    }

    private static NotificationManagerViewImplUiBinder ourUiBinder = GWT.create(NotificationManagerViewImplUiBinder.class);

    @UiField
    FlowPanel mainPanel;
    //    @UiField
    Label     count       = new Label();
    FlowPanel statusPanel = new FlowPanel();

    SimplePanel iconPanel;


    @UiField(provided = true)
    final Resources res;
//    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    public NotificationManagerViewImpl(WorkspaceView workspaceView,
                                       PartStackUIResources partStackUIResources,
                                       Resources resources) {
        super(partStackUIResources);
        //TODO:need improve this
        iconPanel = new SimplePanel();
        iconPanel.addStyleName(resources.notificationCss().statusPanel());
        count.addStyleName(resources.notificationCss().statusPanel());
        statusPanel.add(iconPanel);
        statusPanel.add(count);
        statusPanel.addStyleName(resources.notificationCss().right25px());
        workspaceView.getStatusPanel().setWidget(statusPanel);
        iconPanel.addDomHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                delegate.onClicked();
            }
        }, ClickEvent.getType());

        this.res = resources;
        container.add(ourUiBinder.createAndBindUi(this));
    }


    /** {@inheritDoc} */
    @Override
    public void setStatus(@NotNull Status status) {
        Image icon = createImage(status);
        iconPanel.setWidget(icon);
    }


    /**
     * Return image for status
     *
     * @param status
     * @return image for status
     */
    private Image createImage(@NotNull Status status) {
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

    @Override
    public void setContainer(NotificationContainer container) {
        mainPanel.add(container);
    }


}