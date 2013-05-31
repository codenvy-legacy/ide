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