/*
 * Copyright (C) 2012 eXo Platform SAS.
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
