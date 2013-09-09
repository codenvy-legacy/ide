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

        @Source("toolbar/toolbar.png")
        @ImageResource.ImageOptions(repeatStyle = ImageResource.RepeatStyle.Horizontal)
        ImageResource background();

        @Source("toolbar/delimiter.png")
        ImageResource delimiter();

        @Source("toolbar/statusbar.png")
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
