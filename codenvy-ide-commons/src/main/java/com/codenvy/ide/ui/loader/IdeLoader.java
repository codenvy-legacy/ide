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
package com.codenvy.ide.ui.loader;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.inject.Inject;

/**
 * The loader for rest request.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class IdeLoader extends Loader {
    private PopupPanel loader;

    /**
     * Create loader.
     *
     * @param resources
     */
    @Inject
    public IdeLoader(LoaderResources resources) {
        loader = new PopupPanel();
        FlowPanel container = new FlowPanel();

        Image ajaxImage = new Image(resources.loader());
        Grid grid = new Grid(1, 2);
        grid.setWidget(0, 0, ajaxImage);
        grid.setText(0, 1, getMessage());
        container.add(grid);

        loader.add(container);
    }

    /** {@inheritDoc} */
    @Override
    public void show() {
        loader.center();
        loader.show();
    }

    /** {@inheritDoc} */
    @Override
    public void hide() {
        loader.hide();
    }
}