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
package com.codenvy.ide.ui.toolbar;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.ui.menu.Item;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Toolbar item that represent 'delimiter'
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DelimiterItem extends Composite implements ToolbarItem {
    public DelimiterItem() {
        FlowPanel widget = new FlowPanel();
        widget.setStyleName(Toolbar.RESOURCES.toolbar().toolbarDelimiter());
        initWidget(widget);
    }

    /** {@inheritDoc} */
    @Override
    public Command getCommand() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String getHotKey() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getImage() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public JsonArray<Item> getItems() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEnabled() {
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSelected() {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabled(boolean enabled) {
    }

    /** {@inheritDoc} */
    @Override
    public void setSelected(boolean selected) {
    }
}
