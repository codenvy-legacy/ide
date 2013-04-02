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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Image;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: $
 */
public class TreeIcon extends AbsolutePanel {

    private Map<TreeIconPosition, Image> icons = new HashMap<TreeIconPosition, Image>();
    private Image mainImage;

    @Deprecated
    public TreeIcon(String icon) {
        this(icon, null, null);
    }

    /**
     *
     */
    public TreeIcon(ImageResource icon) {
        this(icon, null, null);
    }

    @Deprecated
    public TreeIcon(String icon, ImageResource buttomRightIcon, ImageResource topRightIcon) {
        setWidth("20px");
        setHeight("20px");

        Image mainImage = new Image(icon);
        add(mainImage, 0, 0);
        if (topRightIcon != null) {
            Image topImage = new Image(topRightIcon);
            icons.put(TreeIconPosition.TOPRIGHT, topImage);
            add(topImage, 10, 0);
        }
        if (buttomRightIcon != null) {

            Image buttomImage = new Image(buttomRightIcon);
            icons.put(TreeIconPosition.BOTTOMRIGHT, buttomImage);
            add(buttomImage, 11, 8);
        }
    }

    public TreeIcon(ImageResource icon, ImageResource buttomRightIcon, ImageResource topRightIcon) {
        setWidth("20px");
        setHeight("20px");

        mainImage = new Image(icon);
        add(mainImage, 0, 0);
        if (topRightIcon != null) {
            Image topImage = new Image(topRightIcon);
            icons.put(TreeIconPosition.TOPRIGHT, topImage);
            add(topImage, 10, 0);
        }
        if (buttomRightIcon != null) {

            Image buttomImage = new Image(buttomRightIcon);
            icons.put(TreeIconPosition.BOTTOMRIGHT, buttomImage);
            add(buttomImage, 11, 8);
        }
    }

    /**
     * Add Icon to specified position
     *
     * @param position
     *         of icon
     * @param icon
     *         URL
     */
    public void addIcon(TreeIconPosition position, ImageResource icon) {
        switch (position) {
            case TOPRIGHT:
                if (icon != null) {
                    removeIcon(position);
                    Image topImage = new Image(icon);
                    icons.put(TreeIconPosition.TOPRIGHT, topImage);
                    add(topImage, 10, 0);
                }
                break;

            case BOTTOMRIGHT:
                if (icon != null) {
                    removeIcon(position);
                    Image buttomImage = new Image(icon);
                    icons.put(TreeIconPosition.BOTTOMRIGHT, buttomImage);
                    add(buttomImage, 11, 8);
                }
                break;

            default:
                break;
        }
    }

    /**
     * Remove icon
     *
     * @param position
     *         of removed icon
     */
    public void removeIcon(TreeIconPosition position) {
        if (icons.containsKey(position)) {
            icons.get(position).removeFromParent();
            icons.remove(position);
        }
    }

    /** @param itemIcon */
    public void setIcon(ImageResource itemIcon) {
        mainImage.setResource(itemIcon);
    }
}