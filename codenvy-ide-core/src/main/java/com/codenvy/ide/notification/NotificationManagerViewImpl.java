/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.notification;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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

    @UiField(provided = true)
    final Resources res;
//    private ActionDelegate delegate;

    /**
     * Create view.
     *
     * @param resources
     */
    @Inject
    public NotificationManagerViewImpl(PartStackUIResources partStackUIResources,
                                       Resources resources) {
        super(partStackUIResources);
        this.res = resources;
        count.setStyleName(resources.notificationCss().countLabel());
        count.setVisible(false);

        container.add(ourUiBinder.createAndBindUi(this));
        minimizeButton.ensureDebugId("notification-minimizeBut");
    }

    /** {@inheritDoc} */
    @Override
    public void setNotificationCount(int count) {
        String text = count > 0 ? String.valueOf(count) : "";
        this.count.setText(text);
        this.count.setVisible(count > 0);
    }

    @Override
    public void setContainer(NotificationContainer container) {
        mainPanel.add(container);
    }

    /** {@inheritDoc} */
    @Override
    public IsWidget getCountLabel() {
        return count;
    }


}