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

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This class wraps the elements before placing them on {@link Toolbar}.
 * <p/>
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ToolbarItem extends FlowPanel {

    private static Toolbar.Style STYLE       = Toolbar.RESOURCES.css();
    /**
     * Docking of the item in the toolbar.
     * <code>false</code> - element is left side,
     * <code>true<code> - element is right side.
     */
    private        boolean       rightDocked = false;

    /** Is this element is toolbar delimiter. */
    private boolean delimiter = false;

    /** Widget which is wrapped by this {@link ToolbarItem}. */
    private Widget widget;

    /** Creates new instance of this {@link ToolbarItem} */
    public ToolbarItem() {
    }

    /**
     * Create new instance of this {@link ToolbarItem}.
     *
     * @param item
     *         element to be added to the toolbar.
     */
    public ToolbarItem(Widget item) {
        this(item, false);
    }

    /**
     * Create new ToolbarItem instance.
     *
     * @param item
     *         element to be added to the toolbar.
     * @param topPadding
     */
    public ToolbarItem(Widget item, int topPadding) {
        this(item, false, topPadding);
    }

    /**
     * @param item
     * @param rightDocked
     */
    public ToolbarItem(Widget item, boolean rightDocked) {
        this(item, rightDocked, 4);
    }

    /**
     * @param widget
     * @param rightDocked
     * @param margin
     */
    public ToolbarItem(Widget widget, boolean rightDocked, int topPadding) {
        setRightDocked(rightDocked);
        setPaddingTop(topPadding);

        if (widget == null) {
            setDelimiter(true);
        } else {
            add(widget);
        }
    }

    /**
     * Get docking of this {@link ToolbarItem}.
     * <code>false</code> - element is left side,
     * <code>true<code> - element is right side.
     *
     * @return docking of the item in the toolbar
     */
    public boolean isRightDocked() {
        return rightDocked;
    }

    /**
     * Makes this {@link ToolbarItem} docked on right.
     *
     * @param rightDocked
     *         <b>true</b> makes this {@link ToolbarItem} docked on right, <b>false</b> otherwise
     */
    public void setRightDocked(boolean rightDocked) {
        this.rightDocked = rightDocked;
        setStyleName(rightDocked ? STYLE.exoToolbarElementRight() : STYLE.exoToolbarElementLeft());
    }

    /**
     * Get is this element is toolbar delimiter.
     *
     * @return is this element is toolbar delimiter.
     */
    public boolean isDelimiter() {
        return delimiter;
    }

    /**
     * True shows this {@link ToolbarItem} as delimiter.
     *
     * @param delimiter
     */
    public void setDelimiter(boolean delimiter) {
        this.delimiter = delimiter;

        clear();
        widget = new FlowPanel();
        widget.setStyleName(STYLE.exoToolbarDelimiter());
        add(widget);
        delimiter = true;
    }

    /**
     * Sets padding from the top.
     *
     * @param padding
     */
    public void setPaddingTop(int padding) {
        getElement().getStyle().setPaddingTop(padding, Unit.PX);
        //DOM.setStyleAttribute(getElement(), "paddingTop", "" + padding + "px");
    }

    /**
     * Sets padding from the right.
     *
     * @param padding
     */
    public void setPaddingRight(int padding) {
        getElement().getStyle().setPaddingRight(padding, Unit.PX);
    }

    public void add(Widget widget) {
        this.widget = widget;
        clear();
        super.add(widget);
    }

}
