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

package com.codenvy.ide.ui.menu;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class PopupMenuItem implements MenuItem {

    private ImageResource image;

    /** Title of this item */
    private String title;

    /** Visibility state */
    private boolean visible = true;

    /** Selected state. If selected is true, special check image will be displayed near item's icon */
    private boolean selected = false;

    /** Hot Key is associated with this item */
    private String hotKey;

    /** Enabled state. True as default. */
    private boolean enabled = true;

    /** Map of children. */
    private JsonStringMap<MenuItem> children = JsonCollections.createStringMap();

    /** Command which will be executed when this item will be selected. */
    private Command command;

    private UpdateItemEnablingCallback updateItemEnablingCallback;

    private Map<String, String> attributes = new HashMap<String, String>();

    /**
     * Create PopupMenuItem
     *
     * @param title
     *         - title
     */
    public PopupMenuItem(String title) {
        this.title = title;
    }

    /**
     * Create PopupMenuItem
     *
     * @param image
     *         - image as ImageResource
     * @param title
     *         - title
     */
    public PopupMenuItem(ImageResource image, String title) {
        this.image = image;
        this.title = title;
    }

    /**
     * @param image
     *         - image as ImageResource
     * @param title
     *         - title
     * @param command
     *         - command which will be executed when item will be selected
     */
    public PopupMenuItem(ImageResource image, String title, Command command) {
        this(image, title);
        this.command = command;
    }

    /** {@inheritDoc} */
    public MenuItem addItem(String title) {
        return addItem(null, title, null);
    }

    /** {@inheritDoc} */
    public MenuItem addItem(ImageResource image, String title) {
        return addItem(image, title, null);
    }

    /** {@inheritDoc} */
    public MenuItem addItem(String title, Command command) {
        return addItem(null, title, command);
    }


    /** {@inheritDoc} */
    public MenuItem addItem(ImageResource image, String title, Command command) {
        PopupMenuItem item = new PopupMenuItem(image, title, command);
        children.put(title, item);
        return item;
    }

    @Override
    public void addItem(MenuItem item) {
        children.put(item.getTitle(), item);
    }

    /** {@inheritDoc} */
    public void setTitle(String title) {
        this.title = title;
    }


    /** {@inheritDoc} */
    public String getTitle() {
        return title;
    }

    public void setImage(ImageResource image) {
        this.image = image;
    }

    public ImageResource getImage() {
        return image;
    }

    /** {@inheritDoc} */
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (updateItemEnablingCallback != null) {
            updateItemEnablingCallback.onUpdateItemEnabling();
        }
    }

    /** {@inheritDoc} */
    public boolean isVisible() {
        return visible;
    }

    /** {@inheritDoc} */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /** {@inheritDoc} */
    public boolean isSelected() {
        return selected;
    }

    /** {@inheritDoc} */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** {@inheritDoc} */
    public boolean isEnabled() {
        return enabled;
    }

    /** {@inheritDoc} */
    public String getHotKey() {
        return hotKey;
    }

    /** {@inheritDoc} */
    public void setHotKey(String hotKey) {
        this.hotKey = hotKey;
    }

    /** {@inheritDoc} */
    public JsonArray<MenuItem> getItems() {
        return children.getValues();
    }

    @Override
    public MenuItem getChildren(String title) {
        return children.get(title);
    }

    /** Use for dump */
    @Override
    public String toString() {
        String value =
                "Title: " + title + "\r\n" + "Visible: " + visible + "\r\n" + "Enabled: " + enabled + "\r\n" + "HotKey: "
                + hotKey;
        return value;
    }

    /** {@inheritDoc} */
    public void setCommand(Command command) {
        this.command = command;
    }

    /** {@inheritDoc} */
    public Command getCommand() {
        return command;
    }

    /** @param updateItemEnablingCallback */
    public void setUpdateItemEnablingCallback(UpdateItemEnablingCallback updateItemEnablingCallback) {
        this.updateItemEnablingCallback = updateItemEnablingCallback;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
