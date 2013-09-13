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