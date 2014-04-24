/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.actions.find;

import com.codenvy.ide.ui.window.Window;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTMLPanel;

/**
 * @author Evgen Vidolob
 */
public class FindActionViewImpl extends Window implements FindActionView {
    private static FindActionViewImplUiBinder ourUiBinder = GWT.create(FindActionViewImplUiBinder.class);

    public FindActionViewImpl() {
        HTMLPanel rootElement = ourUiBinder.createAndBindUi(this);
        setWidget(rootElement);
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {

    }

    @Override
    protected void onClose() {

    }

    interface FindActionViewImplUiBinder
            extends UiBinder<HTMLPanel, FindActionViewImpl> {
    }
}