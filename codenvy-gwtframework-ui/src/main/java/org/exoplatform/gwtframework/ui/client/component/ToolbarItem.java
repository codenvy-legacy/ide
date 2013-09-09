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
