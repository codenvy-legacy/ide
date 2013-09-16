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
package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.extension.googleappengine.client.backends.BackendGrid;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 23, 2012 3:47:59 PM anya $
 */
public class BackendsTabPane extends Composite {
    private static BackendsTabPaneUiBinder uiBinder = GWT.create(BackendsTabPaneUiBinder.class);

    interface BackendsTabPaneUiBinder extends UiBinder<Widget, BackendsTabPane> {
    }

    public BackendsTabPane() {
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiField
    ImageButton configureBackendButton;

    @UiField
    ImageButton deleteBackendButton;

    @UiField
    ImageButton rollbackBackendButton;

    @UiField
    ImageButton rollbackAllBackendsButton;

    @UiField
    ImageButton updateAllBackendsButton;

    @UiField
    ImageButton updateBackendButton;

    @UiField
    BackendGrid backendsGrid;

    /** @return the configureBackendButton */
    public ImageButton getConfigureBackendButton() {
        return configureBackendButton;
    }

    /** @return the deleteBackendButton */
    public ImageButton getDeleteBackendButton() {
        return deleteBackendButton;
    }

    /** @return the rollbackBackendButton */
    public ImageButton getRollbackBackendButton() {
        return rollbackBackendButton;
    }

    /** @return the rollbackAllBackendsButton */
    public ImageButton getRollbackAllBackendsButton() {
        return rollbackAllBackendsButton;
    }

    /** @return the updateAllBackendsButton */
    public ImageButton getUpdateAllBackendsButton() {
        return updateAllBackendsButton;
    }

    /** @return the updateBackendButton */
    public ImageButton getUpdateBackendButton() {
        return updateBackendButton;
    }

    public BackendGrid getBackendGrid() {
        return backendsGrid;
    }
}
