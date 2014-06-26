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
package com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;

public class CMEditorOverlay extends JavaScriptObject {

    protected CMEditorOverlay() {
    }

    public final native String getValue() /*-{
        return this.getValue();
    }-*/;

    public final native CMDocumentOverlay getDoc() /*-{
        return this.getDoc();
    }-*/;

    public final native void setValue(final String newValue) /*-{
        this.setValue(newValue);
    }-*/;

    public final native void setSize(final String newWidth, final String newHeight) /*-{
        this.setSize(newWidth, newHeight);
    }-*/;

    public final native void refresh() /*-{
        this.refresh();
    }-*/;

    public final native CMModeOverlay getModeAt(final CMPositionOverlay position) /*-{
        return this.getModeAt(position);
    }-*/;

    public final native CMModeOverlay getMode() /*-{
        return this.getMode();
    }-*/;

    public final native void showHint(CMHintFunctionOverlay hintFunc) /*-{
        this.showHint(hintFunc);
    }-*/;

    public final native void showHint() /*-{
        this.showHint();
    }-*/;

    /**
     * Change option value for the editor. Obviously only works for options that take a string value.
     * 
     * @param propertyName the option name
     * @param value the new value
     */
    public final native void setOption(final String propertyName, final String value) /*-{
        this.setOption(propertyName, value);
    }-*/;

    /**
     * Change option value for the editor.
     * 
     * @param propertyName the option name
     * @param value the new value
     */
    public final native void setOption(final String propertyName, final JavaScriptObject value) /*-{
        this.setOption(propertyName, value);
    }-*/;

    public final native String getStringOption(final String propertyName) /*-{
        return this.getOption(propertyName);
    }-*/;

    public final native boolean getBooleanOption(final String propertyName) /*-{
        return this.getOption(propertyName);
    }-*/;

    public final native JavaScriptObject getOption(final String propertyName) /*-{
        return this.getOption(propertyName);
    }-*/;

    /**
     * Change option value for the editor.
     * 
     * @param propertyName the option name
     * @param value the new value
     */
    public final native void setOption(final String propertyName, final boolean value) /*-{
        this.setOption(propertyName, value);
    }-*/;

    public final static native CMHintFunctionOverlay getHintFunction(String name) /*-{
        return $wnd.CodeMirror.hint.html;
    }-*/;

    public final static native CMEditorOverlay createEditor(final Element element) /*-{
        return $wnd.CodeMirror(element, {});
    }-*/;

    public final static native CMEditorOverlay createEditor(final Element element,
                                                            final JavaScriptObject options) /*-{
        return $wnd.CodeMirror(element, options);
    }-*/;

    // events handling - the cm.off(...) method is not that easy to do...

    public final native <T extends JavaScriptObject> void on(String eventType, EventHandlerMultipleParameters<T> handler) /*-{
        this.on(eventType,
            function() {
                var params = [];
                for (var i = 0; i < arguments.length; i++) {
                    params.push(arguments[i]);
                }
                handler.@com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso.CMEditorOverlay.EventHandlerMultipleParameters::onEvent(*)(params);
            });
    }-*/;

    public final native void on(String eventType, EventHandlerNoParameters handler) /*-{
        this.on(eventType,
            function() {
                handler.@com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso.CMEditorOverlay.EventHandlerNoParameters::onEvent()();
            });
    }-*/;

    public final native <T extends JavaScriptObject> void on(String eventType, EventHandlerOneParameter<T> handler) /*-{
        this.on(eventType,
            function(param) {
                handler.@com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso.CMEditorOverlay.EventHandlerNoParameters::onEvent(*)(param);
            });
    }-*/;

    public interface EventHandlerNoParameters {
        void onEvent();
    }

    public interface EventHandlerOneParameter<T extends JavaScriptObject> {
        void onEvent(T param);
    }

    public interface EventHandlerMultipleParameters<T extends JavaScriptObject> {
        void onEvent(JsArray<T> param);
    }

    // clean/dirty state

    public final native boolean isClean() /*-{
        return this.isClean();
    }-*/;

    public final native void markClean() /*-{
        this.markClean();
    }-*/;

    public final native Element getWrapperElement() /*-{
        return this.getWrapperElement();
    }-*/;

}
