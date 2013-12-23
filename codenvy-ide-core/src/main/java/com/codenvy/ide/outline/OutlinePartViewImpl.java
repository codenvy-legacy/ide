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
