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
package org.exoplatform.gwtframework.ui.client.command;

import com.google.gwt.resources.client.ImageResource;

import java.util.ArrayList;
import java.util.List;

/**
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
