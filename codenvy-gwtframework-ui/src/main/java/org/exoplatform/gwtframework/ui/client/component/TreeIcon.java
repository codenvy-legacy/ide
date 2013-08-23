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