/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.texteditor.embeddedimpl.orion.jso;

import com.codenvy.ide.texteditor.embeddedimpl.orion.Action;
import com.google.gwt.core.client.JavaScriptObject;

public class OrionTextViewOverlay extends JavaScriptObject {

    protected OrionTextViewOverlay() {
    }

    public final native OrionTextViewOptionsOverlay getOptions() /*-{
        return this.getOptions();
    }-*/;

    public final native void setOptions(final OrionTextViewOptionsOverlay newValue) /*-{
        this.setOptions(newValue);
    }-*/;

    public final native void focus() /*-{
        this.focus();
    }-*/;

    public final native boolean hasFocus() /*-{
        this.hasFocus();
    }-*/;

    public final native void redraw() /*-{
        this.redraw();
    }-*/;

    public final native void setRedraw(boolean redraw) /*-{
        this.setRedraw(redraw);
    }-*/;

    public final native OrionTextModelOverlay getModel() /*-{
        return this.getModel();
    }-*/;

    // selection

    public final native OrionSelectionOverlay getSelection() /*-{
        return this.getSelection();
    }-*/;

    public final native boolean showSelection() /*-{
        return this.showSelection();
    }-*/;

    public final native boolean showSelection(double additionalFractionScroll) /*-{
        return this.showSelection(additionalFractionScroll);
    }-*/;

    public final native boolean showSelection(double additionalFractionScroll, SimpleCallBack callback) /*-{
        return this
                .showSelection(
                        additionalFractionScroll,
                        function() {
                            callback.@com.codenvy.ide.texteditor.embeddedimpl.orion.jso.OrionTextViewOverlay.SimpleCallBack::onFinished()();
                        });
    }-*/;

    public final native boolean showSelection(OrionTextViewShowOptionsOverlay options, SimpleCallBack callback) /*-{
        return this
                .showSelection(
                        options,
                        function() {
                            callback.@com.codenvy.ide.texteditor.embeddedimpl.orion.jso.OrionTextViewOverlay.SimpleCallBack::onFinished()();
                        });
    }-*/;

    public final native void setSelection(int start, int end) /*-{
        this.setSelection(start, end);
    }-*/;

    public final native void setSelection(int start, int end, boolean show) /*-{
        this.setSelection(start, end, show);
    }-*/;

    public final native void setSelection(int start, int end, double show) /*-{
        this.setSelection(start, end, show);
    }-*/;

    public final native void setSelection(int start, int end, OrionTextViewShowOptionsOverlay show) /*-{
        this.setSelection(start, end, show);
    }-*/;

    /* there are variants with callbacks also */

    public final native void setCaretOffset(int offset) /*-{
        this.setCaretOffset(offset);
    }-*/;

    public final native void setCaretOffset(int offset, boolean show) /*-{
        this.setCaretOffset(offset, show);
    }-*/;

    public final native void setCaretOffset(int offset, double show) /*-{
        this.setCaretOffset(offset, show);
    }-*/;

    public final native void setCaretOffset(int offset, OrionTextViewShowOptionsOverlay show) /*-{
        this.setCaretOffset(offset, show);
    }-*/;

    public final native int getCaretOffset() /*-{
        return this.getCaretOffset();
    }-*/;


    // keymodes

    public final native void addKeyMode(OrionKeyModeOverlay keyMode) /*-{
        this.addKeyMode(keyMode);
    }-*/;

    public final native void removeKeyMode(OrionKeyModeOverlay keyMode) /*-{
        this.removeKeyMode(keyMode);
    }-*/;

    public final native OrionKeyModeOverlay getKeyModes() /*-{
        return this.getKeyModes();
    }-*/;

    // actions

    public final native void setAction(String actionId, Action action) /*-{
        this.setAction(actionId, function() {
            action.@com.codenvy.ide.texteditor.embeddedimpl.orion.Action::onAction()();
        });
    }-*/;

    public final native void setAction(String actionId, Action action, String description) /*-{
        this.setAction(actionId, function() {
            action.@com.codenvy.ide.texteditor.embeddedimpl.orion.Action::onAction()();
        }, description);
    }-*/;

    public final native void update() /*-{
        this.update();
    }-*/;

    // events

    public final native void addEventListener(String eventType, EventHandlerNoParameter handler, boolean useCapture) /*-{
        this
                .addEventListener(
                        eventType,
                        function() {
                            handler.@com.codenvy.ide.texteditor.embeddedimpl.orion.jso.OrionTextViewOverlay.EventHandlerNoParameter::onEvent()();
                        }, useCapture);
    }-*/;

    public final native <T extends OrionEventOverlay> void addEventListener(String eventType, EventHandler<T> handler, boolean useCapture) /*-{
        this
                .addEventListener(
                        eventType,
                        function(param) {
                            handler.@com.codenvy.ide.texteditor.embeddedimpl.orion.jso.OrionTextViewOverlay.EventHandler::onEvent(*)(param);
                        }, useCapture);
    }-*/;

    public final void addEventListener(String type, EventHandlerNoParameter handler) {
        addEventListener(type, handler, false);
    }

    public interface EventHandlerNoParameter {
        void onEvent();
    }

    public interface EventHandler<T extends OrionEventOverlay> {
        void onEvent(T parameter);
    }

    public interface SimpleCallBack {
        void onFinished();
    }
}
