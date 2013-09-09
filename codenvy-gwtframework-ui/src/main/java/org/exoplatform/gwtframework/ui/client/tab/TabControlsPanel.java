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
