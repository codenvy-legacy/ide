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
package com.codenvy.ide.outline;

import com.codenvy.ide.api.parts.PartStackUIResources;
import com.codenvy.ide.api.parts.base.BaseView;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.Inject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class OutlinePartViewImpl extends BaseView<OutlinePartView.ActionDelegate> implements OutlinePartView {

    private SimplePanel container;

    private Label noOutline;

    @Inject
    public OutlinePartViewImpl(PartStackUIResources resources) {

        super(resources);
        //TODO extract message constant
        noOutline = new Label("An outline is not available.");
        container = new SimplePanel();
        super.container.add(container);
        minimizeButton.ensureDebugId("outline-minimizeBut");
    }

    /** {@inheritDoc} */
    @Override
    public void showNoOutline() {
        container.clear();
        container.add(noOutline);
    }

    /** {@inheritDoc} */
    @Override
    public AcceptsOneWidget getContainer() {
        return container;
    }
}
