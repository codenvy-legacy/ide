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

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.gwtframework.ui.client.component.TextButton.TextAlignment;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class StatusTextControl extends Control<StatusTextControl> {

    private int size = -1;

    private String text = "";

    private TextAlignment textAlignment = TextAlignment.LEFT;

    private GwtEvent<?> event;

    private boolean fireEventOnSingleClick = false;

    public StatusTextControl(String id) {
        super(id);
    }

    public int getSize() {
        return size;
    }

    public StatusTextControl setSize(int size) {
        this.size = size;
        return this;
    }

    public String getText() {
        return text;
    }

    public StatusTextControl setText(String text) {
        if (this.text.equals(text)) {
            return this;
        }

        this.text = text;

        for (ControlStateListener listener : getStateListeners()) {
            ((StatusTextControlStateListener)listener).updateStatusText(text);
        }

        return this;
    }

    /** @return the event */
    public GwtEvent<?> getEvent() {
        return event;
    }

    /**
     * @param event
     *         the event to set
     */
    public StatusTextControl setEvent(GwtEvent<?> event) {
        this.event = event;
        return this;
    }

    public boolean isFireEventOnSingleClick() {
        return fireEventOnSingleClick;
    }

    public StatusTextControl setFireEventOnSingleClick(boolean fireEventOnSingleClick) {
        this.fireEventOnSingleClick = fireEventOnSingleClick;
        return this;
    }

    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    public StatusTextControl setTextAlignment(TextAlignment textAlignment) {
        this.textAlignment = textAlignment;
        return this;
    }

}
