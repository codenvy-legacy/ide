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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Text area HTML element.
 * Fixes firing value change event on paste and key up.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Oct 21, 2011 10:45:47 AM anya $
 */
public class TextAreaInput extends TextArea {

    /** Default constructor. */
    public TextAreaInput() {
        addKeyUpHandler(new KeyUpHandler() {

            @Override
            public void onKeyUp(KeyUpEvent event) {
                valueChange();
            }
        });

        sinkEvents(Event.ONPASTE);
    }

    /**
     * @param name
     *         form element's name
     */
    public TextAreaInput(String name) {
        this();
        setName(name);
    }

    /** @see com.google.gwt.user.client.ui.ValueBoxBase#onBrowserEvent(com.google.gwt.user.client.Event) */
    @Override
    public void onBrowserEvent(Event event) {
        if (!isEnabled())
            return;
        int type = DOM.eventGetType(event);
        switch (type) {
            case Event.ONPASTE:
                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        valueChange();
                    }
                });
                break;
        }
        super.onBrowserEvent(event);
    }

    /** Fires value changed event for the input. */
    public void valueChange() {
        ValueChangeEvent.fire(this, getText());
    }

    /** Sets focus in text input. */
    public void focus() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                setFocus(true);
            }
        });
    }

}
