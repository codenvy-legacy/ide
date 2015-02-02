/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.outline;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlinePartViewImpl extends BaseView<OutlinePartView.ActionDelegate> implements OutlinePartView {

    private static OutlinePartViewImplUiBinder ourUiBinder = GWT.create(OutlinePartViewImplUiBinder.class);

    @UiField
    Style style;

    @UiField
    SimplePanel container;

    @UiField
    DockLayoutPanel noOutline;

    @UiField
    Label noOutlineCause;

    private boolean outlineEnabled = false;

    @Inject
    public OutlinePartViewImpl(PartStackUIResources resources) {
        super(resources);
        ourUiBinder.createAndBindUi(this);
        super.container.add(container);
        minimizeButton.ensureDebugId("outline-minimizeBut");
    }

    /** {@inheritDoc} */
    @Override
    public void disableOutline(String cause) {
        outlineEnabled = false;

        clear();
        noOutlineCause.setText(cause);
        container.add(noOutline);
    }

    @Override
    public void enableOutline() {
        outlineEnabled = true;

        Element el = container.getElement().getFirstChildElement().cast();
        el.getStyle().setProperty("position", "relative");
        el.getStyle().setProperty("width", "100%");
        el.getStyle().setProperty("height", "100%");
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        container.clear();
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContainer() {
        return container;
    }

    interface OutlinePartViewImplUiBinder extends UiBinder<SimplePanel, OutlinePartViewImpl> {
    }

    interface Style extends CssResource {
    }

    @Override
    protected void updateFocus() {
        try {
            if (outlineEnabled) {
                if (isFocused()) {
                    container.getElement().getFirstChildElement().getFirstChildElement().focus();
                } else {
                    container.getElement().getFirstChildElement().getFirstChildElement().blur();
                }
            }
        } catch (Exception e) {
            Log.trace("ERROR: " + e.getMessage());
        }
    }
}
