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

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TextInputControl extends Control<TextInputControl> {

    public static final int DEFAULT_WIDTH = 100;

    private int size = DEFAULT_WIDTH;

    private String text = "";

    private TextEnteredEvent<?> event;

    public TextInputControl(String id) {
        super(id);
    }

    public int getSize() {
        return size;
    }

    public TextInputControl setSize(int size) {
        this.size = size;
        return this;
    }

    public String getText() {
        return text;
    }

    public TextInputControl setText(String text) {
        if (this.text == text) {
            return this;
        }

        this.text = text;
        for (ControlStateListener listener : getStateListeners()) {
            ((TextInputControlStateListener)listener).updateControlText(text);
        }

        return this;
    }

    public TextEnteredEvent<?> getEvent() {
        return event;
    }

    public TextInputControl setEvent(TextEnteredEvent<?> event) {
        this.event = event;
        return this;
    }

}
