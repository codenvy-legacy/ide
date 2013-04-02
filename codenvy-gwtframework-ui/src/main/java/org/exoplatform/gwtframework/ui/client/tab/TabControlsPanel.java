/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.gwtframework.ui.client.tab;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RequiresResize;
import com.google.gwt.user.client.ui.Widget;

/**
 * This panel is a container for user defined buttons which are shows in the right upper corner.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TabControlsPanel extends ComplexPanel implements RequiresResize {

    /** Parent panel for ScrollLeft and ScrollRight buttons. */
    private FlowPanel panel;

    /**
     * Creates new instance of TabControlsPanel which is based on Table cell element.
     *
     * @param e
     *         table cell element
     */
    public TabControlsPanel(Element e) {
        setElement(e);
        onAttach();

        panel = new FlowPanel();
        panel.setHeight("24px");
        DOM.setStyleAttribute(panel.getElement(), "overflow", "hidden");
        panel.getElement().getStyle().setFloat(Float.RIGHT);
        add(panel, getElement());

        DOM.setStyleAttribute(getElement(), "overflow", "hidden");
        setWidth("0px");
    }

    /**
     * Adds a new child widget to the panel.
     *
     * @param w
     *         the widget to be added
     */
    @Override
    public void add(Widget w) {
        panel.add(w);
    }

    /**
     * Removes specified widget from this Panel.
     *
     * @see com.google.gwt.user.client.ui.ComplexPanel#remove(com.google.gwt.user.client.ui.Widget)
     */
    @Override
    public boolean remove(Widget w) {
        return panel.remove(w);
    }

    /** @see com.google.gwt.user.client.ui.RequiresResize#onResize() */
    @Override
    public void onResize() {
        int panelWidth = 0;

        for (int i = 0; i < panel.getWidgetCount(); i++) {
            Widget w = panel.getWidget(i);

            int wWidth = w.getOffsetWidth();
            panelWidth += wWidth;
        }

        setWidth(panelWidth + "px");
    }

}
