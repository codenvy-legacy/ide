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

package org.exoplatform.ide.editor.client.api.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class EditorHotKeyPressedEvent extends GwtEvent<EditorHotKeyPressedHandler> {

    public static final GwtEvent.Type<EditorHotKeyPressedHandler> TYPE = new GwtEvent.Type<EditorHotKeyPressedHandler>();

    private boolean hotKeyHandled = false;

    private boolean isCtrl;

    private boolean isAlt;

    private boolean isShift;

    private int keyCode;

    public EditorHotKeyPressedEvent(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode) {
        this.isCtrl = isCtrl;
        this.isAlt = isAlt;
        this.isShift = isShift;
        this.keyCode = keyCode;
    }

    public boolean isCtrl() {
        return isCtrl;
    }

    public boolean isAlt() {
        return isAlt;
    }

    public boolean isShift() {
        return isShift;
    }

    public int getKeyCode() {
        return keyCode;
    }

    public void setHotKeyHandled(boolean hotKeyHandled) {
        this.hotKeyHandled = hotKeyHandled;
    }

    public boolean isHotKeyHandled() {
        return hotKeyHandled;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<EditorHotKeyPressedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(EditorHotKeyPressedHandler handler) {
        handler.onEditorHotKeyPressed(this);
    }

}
