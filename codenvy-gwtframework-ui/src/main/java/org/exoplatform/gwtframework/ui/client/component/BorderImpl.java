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
package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class BorderImpl extends Border {

    /** Default color of border */
    private static final String DEFAULT_BORDER_COLOR = "#A7ABB4";

    /** Default size of border */
    private static final int DEFAULT_BORDER_SIZE = 1;

    /** Size of border */
    private int borderSize = DEFAULT_BORDER_SIZE;

    /** Color of border */
    private String borderColor = DEFAULT_BORDER_COLOR;

    /** The container element at the center of the panel. */
    private Element containerElem;

    /** The table body element. */
    private Element tbody;

    /**
     * Create a new row. The row will contain three cells.
     *
     * @return the new row {@link Element}
     */
    static Element createTR() {
        Element trElem = DOM.createTR();
        DOM.appendChild(trElem, createTD());
        DOM.appendChild(trElem, createTD());
        DOM.appendChild(trElem, createTD());
        return trElem;
    }

    /**
     * Create a new table cell with a specific style name.
     *
     * @return the new cell {@link Element}
     */
    private static Element createTD() {
        Element tdElem = DOM.createTD();
        Element inner = DOM.createDiv();
        DOM.appendChild(tdElem, inner);
        return tdElem;
    }

    /** Creates new instance of this Border */
    public BorderImpl() {
//      super(DOM.createTable());

        getElement().setAttribute("component", "Border");
        DOM.setStyleAttribute(getElement(), "overflow", "hidden");

        // Add a tbody
        Element table = getElement();
        tbody = DOM.createTBody();
        DOM.appendChild(table, tbody);
        DOM.setElementPropertyInt(table, "cellSpacing", 0);
        DOM.setElementPropertyInt(table, "cellPadding", 0);
        DOM.setElementAttribute(table, "border", "0");
        DOM.setStyleAttribute(table, "borderCollapse", "collapse");

        for (int i = 0; i < 3; i++) {
            Element row = createTR();
            DOM.appendChild(tbody, row);
            if (i == 1) {
                containerElem = DOM.getFirstChild(DOM.getChild(row, 1));
            }
        }

        containerElem.setAttribute("component", "Border-Container");
        containerElem.getStyle().setOverflow(Overflow.HIDDEN);

        updateBorder();
    }


    /**
     * Get a specific Element from the panel.
     *
     * @param row
     *         the row index
     * @param cell
     *         the cell index
     * @return the Element at the given row and cell
     */
    protected Element getCellElement(int row, int cell) {
        Element tr = DOM.getChild(tbody, row);
        Element td = DOM.getChild(tr, cell);
        return DOM.getFirstChild(td);
    }


    /**
     * Sets new border's size
     *
     * @param borderSize
     *         new border's size
     */
    @Override
    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        updateBorder();
    }

    /**
     * Sets new border's color
     *
     * @param borderColor
     *         new border's color
     */
    @Override
    public void setBorderColor(String borderColor) {
        this.borderColor = borderColor;
        updateBorder();
    }

    /**
     * Get border's color.
     *
     * @return border's color
     */
    @Override
    public String getBorderColor() {
        return borderColor;
    }

    /** Update border's color and size */
    private void updateBorder() {
        for (int row = 0; row < 3; row++) {
            Element element = getCellElement(row, 0);
            DOM.setStyleAttribute(element, "width", borderSize + "px");
            DOM.setStyleAttribute(DOM.getParent(element), "width", borderSize + "px");
            DOM.setStyleAttribute(DOM.getParent(element), "background", borderColor);

            Element element2 = getCellElement(row, 2);
            DOM.setStyleAttribute(element2, "width", borderSize + "px");
            DOM.setStyleAttribute(DOM.getParent(element2), "width", borderSize + "px");
            DOM.setStyleAttribute(DOM.getParent(element2), "background", borderColor);
        }

        for (int column = 0; column < 3; column++) {
            Element element = getCellElement(0, column);
            DOM.setStyleAttribute(element, "height", borderSize + "px");
            DOM.setStyleAttribute(DOM.getParent(element), "height", borderSize + "px");
            DOM.setStyleAttribute(DOM.getParent(element), "background", borderColor);

            Element element2 = getCellElement(2, column);
            DOM.setStyleAttribute(element2, "height", borderSize + "px");
            DOM.setStyleAttribute(DOM.getParent(element2), "height", borderSize + "px");
            DOM.setStyleAttribute(DOM.getParent(element2), "background", borderColor);
        }
    }

    /** @see org.exoplatform.gwtframework.ui.client.component.Border#setMargin(int) */
    @Override
    public void setMargin(int margin) {
    }
}