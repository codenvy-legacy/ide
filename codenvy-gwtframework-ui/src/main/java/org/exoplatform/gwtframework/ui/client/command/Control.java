/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.gwtframework.ui.client.command;

import com.google.gwt.resources.client.ImageResource;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class Control<T extends Control<?>> {

    private String id;

    private String icon;

    private ImageResource normalImage;

    private ImageResource disabledImage;

    private String prompt;

    private boolean enabled;

    private boolean visible;

    private boolean hasDelimiterBefore;

    public Control(String id) {
        this.id = id;
    }

    /**
     * List of command state listeners.
     * Listeners are uses for enabling, disabling, showing or hiding item in menu or toolbar.
     */
    private List<ControlStateListener> stateListeners = new ArrayList<ControlStateListener>();

    
    
    /**
     * Use this carefully if it set true you cannot change control state using only setEnable(true) 
     * need call resetPermanentDisable() firstly 
     */
    private boolean disablePermanently;

    public String getId() {
        return id;
    }

    public ImageResource getNormalImage() {
        return normalImage;
    }

    @SuppressWarnings("unchecked")
    public T setNormalImage(ImageResource normalImage) {
        this.normalImage = normalImage;
        return (T)this;
    }

    public ImageResource getDisabledImage() {
        return disabledImage;
    }

    @SuppressWarnings("unchecked")
    public T setDisabledImage(ImageResource disabledImage) {
        this.disabledImage = disabledImage;
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T setImages(ImageResource normalImage, ImageResource disabledImage) {
        if (this.normalImage == normalImage && this.disabledImage == disabledImage) {
            return (T)this;
        }

        this.normalImage = normalImage;
        this.disabledImage = disabledImage;

        for (ControlStateListener listener : stateListeners) {
            listener.updateControlIcon(icon);
        }

        return (T)this;
    }

    public String getIcon() {
        return icon;
    }

    @SuppressWarnings("unchecked")
    public T setIcon(String icon) {
        if (this.icon == icon) {
            return (T)this;
        }

        this.icon = icon;
        for (ControlStateListener listener : stateListeners) {
            listener.updateControlIcon(icon);
        }

        return (T)this;
    }

    public String getPrompt() {
        return prompt;
    }

    @SuppressWarnings("unchecked")
    public T setPrompt(String prompt) {
        if (this.prompt == prompt) {
            return (T)this;
        }

        this.prompt = prompt;
        for (ControlStateListener listener : stateListeners) {
            listener.updateControlPrompt(prompt);
        }

        return (T)this;
    }

    public boolean isEnabled() {
        return enabled;
    }

    @SuppressWarnings("unchecked")
    public T setEnabled(boolean enabled) {
        if (disablePermanently)
            return (T)this;
        
        if (this.enabled == enabled) {
            return (T)this;
        }

        this.enabled = enabled;
        for (ControlStateListener listener : stateListeners) {
            listener.updateControlEnabling(enabled);
        }

        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T enable() {
        if (disablePermanently)
            return (T)this;
        setEnabled(true);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T disable() {
        setEnabled(false);
        return (T)this;
    }
    
    
    /**
     * Use this carefully if it set true you cannot change control state using only setEnable(true) 
     * need call resetPermanentDisable() firstly 
     */
    public void disablePermanently() {
        disablePermanently = true;
        setEnabled(false);
    }
    
    public void resetPermanentDisable() {
        disablePermanently = false;
    }

    public boolean isVisible() {
        return visible;
    }

    @SuppressWarnings("unchecked")
    public T setVisible(boolean visible) {
        if (this.visible == visible) {
            return (T)this;
        }

        this.visible = visible;
        for (ControlStateListener listener : stateListeners) {
            listener.updateControlVisibility(visible);
        }

        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T show() {
        setVisible(true);
        return (T)this;
    }

    @SuppressWarnings("unchecked")
    public T hide() {
        setVisible(false);
        return (T)this;
    }

    public List<ControlStateListener> getStateListeners() {
        return stateListeners;
    }

    public boolean hasDelimiterBefore() {
        return hasDelimiterBefore;
    }

    @SuppressWarnings("unchecked")
    public T setDelimiterBefore(boolean hasDelimiterBefore) {
        this.hasDelimiterBefore = hasDelimiterBefore;
        return (T)this;
    }

}
