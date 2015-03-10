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
package org.eclipse.che.ide.outline;

import org.eclipse.che.ide.api.parts.PartStackUIResources;
import org.eclipse.che.ide.api.parts.base.BaseView;
import org.eclipse.che.ide.util.loging.Log;
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
 */
public class OutlinePartViewImpl extends BaseView<OutlinePartView.ActionDelegate> implements OutlinePartView {

    interface OutlinePartViewImplUiBinder extends UiBinder<SimplePanel, OutlinePartViewImpl> {
    }

    interface Style extends CssResource {
    }

    @UiField
    Style style;

    @UiField
    SimplePanel outlineContainer;

    @UiField
    DockLayoutPanel noOutline;

    @UiField
    Label noOutlineCause;

    private boolean outlineEnabled = false;

    @Inject
    public OutlinePartViewImpl(PartStackUIResources resources,
                               OutlinePartViewImplUiBinder uiBinder) {
        super(resources);
        setContentWidget(uiBinder.createAndBindUi(this));
        minimizeButton.ensureDebugId("outline-minimizeBut");
    }

    /** {@inheritDoc} */
    @Override
    public void disableOutline(String cause) {
        outlineEnabled = false;

        clear();
        noOutlineCause.setText(cause);
        outlineContainer.add(noOutline);
    }

    @Override
    public void enableOutline() {
        outlineEnabled = true;

        Element el = outlineContainer.getElement().getFirstChildElement().cast();
        el.getStyle().setProperty("position", "relative");
        el.getStyle().setProperty("width", "100%");
        el.getStyle().setProperty("height", "100%");
    }

    /** {@inheritDoc} */
    @Override
    public void clear() {
        outlineContainer.clear();
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContainer() {
        return outlineContainer;
    }

    @Override
    protected void focusView() {
        try {
            // TODO The case must be investigated.
            // TODO It looks the outline tree element is exist when outline is enabled for active file.
            if (outlineEnabled) {
                outlineContainer.getElement().getFirstChildElement().getFirstChildElement().focus();
            }
        } catch (Exception e) {
            Log.trace(e.getMessage());
        }
    }

}
