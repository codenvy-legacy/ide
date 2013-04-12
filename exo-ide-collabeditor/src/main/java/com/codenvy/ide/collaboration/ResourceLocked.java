/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.collaboration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ResourceLocked extends ViewImpl implements ResourceLockedView {
    interface ResourceLockedUiBinder
            extends UiBinder<DockLayoutPanel, ResourceLocked> {
    }

    private static ResourceLockedUiBinder ourUiBinder = GWT.create(ResourceLockedUiBinder.class);

    private ActionDelegate delegate;

    @UiField
    ImageButton notifyButton;

    @UiField
    ImageButton cancelButton;

    @UiField
    HTML messageLabel;

    @UiField
    HTML userList;

    public ResourceLocked() {
        super(ID, ViewType.MODAL, "Warning", null, 350, 100, false);
        add(ourUiBinder.createAndBindUi(this));

    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setMessageText(SafeHtml message) {
        messageLabel.setHTML(message);
    }

    /** {@inheritDoc} */
    @Override
    public void setUserList(SafeHtml userList) {
        this.userList.setHTML(userList);
    }

    @Override
    public void setNotifyButtonEnabled(boolean enabled) {
        notifyButton.setEnabled(enabled);
    }

    @UiHandler("cancelButton")
    void onOkButtonClicked(ClickEvent event) {
        delegate.onClose();
    }

    @UiHandler("notifyButton")
    void onNotifyButtonClicked(ClickEvent event) {
        delegate.onNotify();
    }
}