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

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;

/**
 * Toolbar is visual component, represents IDE toolbar.
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class Toolbar extends Composite {
    static final ToolbarResources RESOURCES = GWT.create(ToolbarResources.class);

    static {
        RESOURCES.toolbar().ensureInjected();
    }

    private FlowPanel panel;

    public Toolbar() {
        panel = new FlowPanel();
        initWidget(panel);
        setStyleName(RESOURCES.toolbar().toolbarPanel());
    }

    /**
     * Add item on toolbar
     *
     * @param item
     *         the item
     */
    public void addItem(ToolbarItem item) {
        item.asWidget().getElement().getStyle().setFloat(Style.Float.LEFT);
        panel.add(item);
    }

    /**
     * Insert item at specific position
     *
     * @param item
     *         the item
     * @param index
     *         item position
     */
    public void insertItem(ToolbarItem item, int index) {
        item.asWidget().getElement().getStyle().setFloat(Style.Float.LEFT);
        panel.insert(item, index);
    }

    /**
     * Return item index at toolbar
     *
     * @param item
     *         the item
     * @return item position in toolbar
     */
    public int getItemIndex(ToolbarItem item) {
        return panel.getWidgetIndex(item);
    }

    /**
     * Add delimiter, vertical bar separator for item groups
     *
     * @return new delimiter
     */
    public DelimiterItem addDelimiter() {
        DelimiterItem delimiter = new DelimiterItem();
        addItem(delimiter);
        return delimiter;
    }
}
