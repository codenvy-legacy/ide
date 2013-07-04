/**
 * Copyright (C) 2010 eXo Platform SAS.
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
 *
 */

package org.exoplatform.gwtframework.ui.client.component;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class Toolbar extends FlowPanel {

    public interface Style extends CssResource {

        String exoToolbarPanel();

        String exoToolbarElementLeft();

        String exoToolbarDelimiter();

        String exoToolbarElementRight();
    }

    public interface Resources extends ClientBundle {
        @Source("toolbar/component-toolbar.css")
        Style css();

        @Source("toolbar/toolbar_Background.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal)
        ImageResource background();

        @Source("toolbar/delimiter.png")
        ImageResource delimiter();

        @Source("toolbar/statusbar_Background.png")
        ImageResource statusbarBackground();
    }

    public static final Resources RESOURCES = GWT.create(Resources.class);

    static {
        RESOURCES.css().ensureInjected();
    }

    private String id;

    /** List of items docked at the left side. */
    private List<ToolbarItem> leftItems = new ArrayList<ToolbarItem>();

    /** List of items docked at the right side. */
    private List<ToolbarItem> rightItems = new ArrayList<ToolbarItem>();

    /** Item's padding from the top of toolbar. */
    private int topItemsPadding = 4;

    /** Create instance of this Toolbar. */
    public Toolbar() {
        this(null);
    }

    /**
     * Create instance of Toolbar with specified ID.
     *
     * @param id
     *         toolbar's id
     */
    public Toolbar(String id) {
        this.id = id;

        setStyleName(RESOURCES.css().exoToolbarPanel());
        if (id != null && !id.isEmpty()) {
            getElement().setId(id);
        }
    }

    /**
     * Add delimiter to the left
     *
     * @return new instance of ToolbarItem, which represents an toolbar delimiter.
     */
    public ToolbarItem addDelimiter() {
        return addItem(null, false);
    }

    /**
     * Add delimiter to the specified side.
     *
     * @param rightDocking
     *         <code>true</code> to add delimiter to the right,
     *         <code>false</code> to add delimiter to the left.
     * @return new instance of ToolbarItem, which represents an toolbar delimiter.
     */
    public ToolbarItem addDelimiter(boolean rightDocking) {
        return addItem(null, rightDocking);
    }

    /**
     * Adds a new item to the left of the toolbar.
     *
     * @param widget
     *         widget of new item
     * @return new instance of ToolbarItem, which is represents a wrapper over added widget.
     */
    public ToolbarItem addItem(Widget widget) {
        ToolbarItem item = new ToolbarItem(widget, topItemsPadding);
        leftItems.add(item);
        add(item);

        return item;
    }

    /**
     * Adds a new item to the specified side of the toolbar.
     *
     * @param widget
     *         widget of new item
     * @param rightDocking
     *         <code>true</code> to add new item to the right,
     *         <code>false</code> to add new item to the left.
     * @return new instance of ToolbarItem, which is represents a wrapper over added widget.
     */
    public ToolbarItem addItem(Widget widget, boolean rightDocking) {
        ToolbarItem item = new ToolbarItem(widget, rightDocking, topItemsPadding);
        if (rightDocking) {
            rightItems.add(item);
        } else {
            leftItems.add(item);
        }

        add(item);
        return item;
    }

    /**
     * Add {@link ToolbarItem} to this {@link Toolbar}.
     *
     * @param item
     */
    public void add(Widget widget) {
        if (widget instanceof ToolbarItem) {
            super.add(widget);
        } else {
            addItem(widget);
        }
    }

    /** Remove all items from toolbar. */
    public void clear() {
        super.clear();
        leftItems.clear();
        rightItems.clear();
    }

    /**
     * Get toolbar's id.
     *
     * @return toolbar's id
     */
    public String getId() {
        return id;
    }

    /**
     * Hide duplicated delimiters in the list of Toolbar Item.
     *
     * @param items
     *         list of Toolbar Items
     */
    private void hideDelimiters(List<ToolbarItem> items) {
        boolean finded = false;

        for (ToolbarItem item : items) {
            if (item.isDelimiter()) {
                item.setVisible(true);
            }
        }

        for (ToolbarItem item : items) {
            if (item.isDelimiter()) {
                if (finded) {
                    item.setVisible(false);
                } else {
                    finded = true;
                }
            } else {
                if (item.isVisible()) {
                    finded = false;
                }
            }
        }

    }

    /** Hide duplicated delimiters. */
    public void hideDuplicatedDelimiters() {
        hideDelimiters(leftItems);
        hideDelimiters(rightItems);
    }

    /**
     * Set background image URL.
     *
     * @param imageURL
     *         background image URL
     */
    public void setBackgroundImage(String imageURL) {
        DOM.setStyleAttribute(getElement(), "background", "url(" + imageURL + ") repeat-x");
    }

    /**
     * Set padding of toolbar items from the top.
     *
     * @param itemsTopPadding
     *         padding of toolbar items from the top.
     */
    public void setItemsTopPadding(int itemsTopPadding) {
        this.topItemsPadding = itemsTopPadding;
    }

    /** Show all items. */
    public void showAllItems() {
        for (ToolbarItem item : leftItems) {
            item.setVisible(true);
        }

        for (ToolbarItem item : rightItems) {
            item.setVisible(true);
        }
    }

}
