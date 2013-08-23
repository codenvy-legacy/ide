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
package org.exoplatform.gwtframework.ui.client.window;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.ui.client.WindowResource;

/**
 * Window button, displayed at it's header.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 3, 2011 10:03:38 AM anya $
 */
public class WindowButton extends Image implements HasClickHandlers {
    /** Prompt of the button. */
    private String prompt;

    /** Button's icon. */
    private ImageResource icon;

    /** Button's icon in disable state. */
    private ImageResource iconDisabled;

    /** Button's icon in over state. */
    private ImageResource iconOver;

    /** Button's height. */
    private int height = 16;

    /** Button's width. */
    private int width = 16;

    /** Button's enabled state. */
    private boolean enabled = true;

    /**
     * @param icon
     * @param iconOver
     * @param iconDisabled
     */
    public WindowButton(ImageResource icon, ImageResource iconOver, ImageResource iconDisabled) {
        this.icon = icon;
        this.iconOver = iconOver;
        this.iconDisabled = iconDisabled;
        setResource(icon);
        setStyleName(WindowResource.INSTANCE.css().windowButton());
        sinkEvents(Event.MOUSEEVENTS | Event.ONCLICK);
    }

    /** @see com.google.gwt.user.client.ui.Image#onBrowserEvent(com.google.gwt.user.client.Event) */
    @Override
    public void onBrowserEvent(Event event) {
        if (!isEnabled())
            return;

        int type = DOM.eventGetType(event);
        switch (type) {
            case Event.ONMOUSEOUT:
                onMouseOut();
                break;
            case Event.ONMOUSEOVER:
                onMouseOver();
                break;
        }
        super.onBrowserEvent(event);
    }

    /** @return the prompt */
    public String getPrompt() {
        return prompt;
    }

    /**
     * @param prompt
     *         the prompt to set
     */
    public void setPrompt(String prompt) {
        this.prompt = prompt;
        setTitle(prompt);
    }

    /** @return the icon */
    public ImageResource getIcon() {
        return icon;
    }

    /**
     * @param icon
     *         the icon to set
     */
    public void setIcon(ImageResource icon) {
        this.icon = icon;
        updateIcon();
    }

    /** @return the iconDisabled */
    public ImageResource getIconDisabled() {
        return iconDisabled;
    }

    /**
     * @param iconDisabled
     *         the iconDisabled to set
     */
    public void setIconDisabled(ImageResource iconDisabled) {
        this.iconDisabled = iconDisabled;
        updateIcon();
    }

    /** @return the iconOver */
    public ImageResource getIconOver() {
        return iconOver;
    }

    /**
     * @param iconOver
     *         the iconOver to set
     */
    public void setIconOver(ImageResource iconOver) {
        this.iconOver = iconOver;
        updateIcon();
    }

    /** @return the height */
    public int getHeight() {
        return height;
    }

    /**
     * @param height
     *         the height to set
     */
    public void setHeight(int height) {
        this.height = height;
        setHeight(height + "px");
    }

    /** @return the width */
    public int getWidth() {
        return width;
    }

    /**
     * @param width
     *         the width to set
     */
    public void setWidth(int width) {
        this.width = width;
        setWidth(width + "px");
    }

    /**
     * @param width
     *         width
     * @param heigth
     *         height
     */
    public void setSize(int width, int heigth) {
        this.width = width;
        this.height = heigth;
        setHeight(height + "px");
        setWidth(width + "px");
    }

    /** @return the enabled */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     *         the enabled to set
     */
    public void setEnabled(boolean enabled) {
        if (enabled == isEnabled())
            return;
        this.enabled = enabled;
        updateIcon();
    }

    /** Update button's icon */
    private void updateIcon() {
        if (enabled) {
            setResource(icon);
        } else {
            setResource(iconDisabled);
        }
    }

    /** Show button. */
    public void show() {
        setVisible(true);
    }

    /** Hide button. */
    public void hide() {
        setVisible(false);
    }

    /** Change icon onMouseOut event. */
    protected void onMouseOut() {
        setResource(icon);
    }

    /** Change icon onMouseOver event. */
    public void onMouseOver() {
        setResource(iconOver);
    }
}
