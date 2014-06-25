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
package com.codenvy.ide.texteditor.embeddedimpl.codemirror;


import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.Notification.Type;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.RegionImpl;
import com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso.BeforeSelectionEventParamOverlay;
import com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso.CMEditorOverlay;
import com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso.CMPositionOverlay;
import com.codenvy.ide.texteditor.embeddedimpl.common.EditorWidget;
import com.codenvy.ide.texteditor.embeddedimpl.common.EmbeddedDocument;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.BeforeSelectionChangeEvent;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.BeforeSelectionChangeHandler;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.CursorActivityEvent;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.CursorActivityHandler;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.GutterClickEvent;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.GutterClickHandler;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.HasBeforeSelectionChangeHandlers;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.HasCursorActivityHandlers;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.HasGutterClickHandlers;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.HasViewPortChangeHandlers;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.ViewPortChangeEvent;
import com.codenvy.ide.texteditor.embeddedimpl.common.events.ViewPortChangeHandler;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayInteger;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasFocusHandlers;
import com.google.gwt.event.dom.client.HasScrollHandlers;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

/**
 * The CodeMirror implementation of {@link EditorWidget}.
 * 
 * @author "Mickaël Leduque"
 */
public class CodeMirrorEditorWidget extends Composite implements EditorWidget, HasChangeHandlers, HasFocusHandlers, HasBlurHandlers,
                                                     HasScrollHandlers, HasCursorActivityHandlers, HasBeforeSelectionChangeHandlers,
                                                     HasViewPortChangeHandlers, HasGutterClickHandlers {

    private final SimplePanel             panel                       = new SimplePanel();
    private final CMEditorOverlay         editorOverlay;
    private final KeyBindings             keyBindings                 = KeyBindings.create();
    private final NotificationManager     notificationManager;

    private com.codenvy.ide.text.Document document;
    private CodeMirrorDocument            embeddedDocument;

    private boolean                       changeHandlerAdded          = false;
    private boolean                       focusHandlerAdded           = false;
    private boolean                       blurHandlerAdded            = false;
    private boolean                       scrollHandlerAdded          = false;
    private boolean                       cursorHandlerAdded          = false;
    private boolean                       beforeSelectionHandlerAdded = false;
    private boolean                       viewPortHandlerAdded        = false;
    private boolean                       gutterClickHandlerAdded     = false;


    @AssistedInject
    public CodeMirrorEditorWidget(final NotificationManager notificationManager,
                                  @Assisted final String editorMode,
                                  @Assisted final com.codenvy.ide.text.Document document) {
        this.panel.setSize("100%", "100%");
        initWidget(this.panel);

        this.notificationManager = notificationManager;

        this.editorOverlay = CMEditorOverlay.createEditor(this.panel.getElement(), getConfiguration());
        this.editorOverlay.setSize("100%", "100%");
        this.editorOverlay.refresh();

        setMode(editorMode);

        this.keyBindings.addBinding("Ctrl-Space", new KeyBindingAction() {

            public void action() {
                Log.info(CodeMirrorEditorWidget.class, "Completion binding used.");
                autoComplete();
            }
        });

        this.keyBindings.addBinding("Shift-Ctrl-Alt-K", new KeyBindingAction() {

            public void action() {
                Log.info(CodeMirrorEditorWidget.class, "Keymap change binding used.");
                changeKeymap();
            }
        });
    }

    @Override
    public String getValue() {
        return this.editorOverlay.getValue();
    }

    @Override
    public void setValue(final String newValue) {
        this.editorOverlay.setValue(newValue);
        // reset history, else the setValue is undo-able
        this.editorOverlay.getDoc().clearHistory();
    }

    private JavaScriptObject getConfiguration() {
        final JSONObject json = new JSONObject();

        // set up key bindings
        json.put("extraKeys", new JSONObject(keyBindings));

        // show line numbers
        json.put("lineNumbers", JSONBoolean.getInstance(true));

        // set a theme
        json.put("theme", new JSONString("solarized dark"));


        // autoclose brackets/tags, match brackets/tags
        json.put("autoCloseBrackets", JSONBoolean.getInstance(true));
        json.put("matchBrackets", JSONBoolean.getInstance(true));
        json.put("autoCloseTags", JSONBoolean.getInstance(true));

        // folding
        json.put("foldGutter", JSONBoolean.getInstance(true));
        JSONArray gutters = new JSONArray();
        gutters.set(0, new JSONString("CodeMirror-linenumbers"));
        gutters.set(1, new JSONString("CodeMirror-foldgutter"));
        json.put("gutters", gutters);

        JSONObject matchTagsConfig = new JSONObject();
        matchTagsConfig.put("bothTags", JSONBoolean.getInstance(true));
        json.put("matchTags", matchTagsConfig);

        // highlight active line
        json.put("styleActiveLine", JSONBoolean.getInstance(true));

        return json.getJavaScriptObject();
    }

    protected void autoComplete() {
        this.editorOverlay.showHint();
    }

    @Override
    public void setMode(final String modeName) {
        Log.info(CodeMirrorEditorWidget.class, "Setting editor mode : " + modeName);
        if (modeName != "html") {
            this.editorOverlay.setOption("mode", modeName);
        } else {
            Log.info(CodeMirrorEditorWidget.class, "... actually, changing to text/html.");
            this.editorOverlay.setOption("mode", "text/html");
        }
    }

    public void selectVimKeymap() {
        this.editorOverlay.setOption("keyMap", Keymap.VIM.getCodeMirrorKey());
        this.editorOverlay.setOption("showCursorWhenSelecting", true);
    }

    public void selectEmacsKeymap() {
        this.editorOverlay.setOption("keyMap", Keymap.EMACS.getCodeMirrorKey());
        this.editorOverlay.setOption("showCursorWhenSelecting", false);
    }

    public void selectSublimeKeymap() {
        this.editorOverlay.setOption("keyMap", Keymap.SUBLIME.getCodeMirrorKey());
        this.editorOverlay.setOption("showCursorWhenSelecting", false);
    }

    public void selectDefaultKeymap() {
        this.editorOverlay.setOption("keyMap", Keymap.DEFAULT.getCodeMirrorKey());
        this.editorOverlay.setOption("showCursorWhenSelecting", false);
    }

    protected void changeKeymap() {
        final String keymapCode = this.editorOverlay.getStringOption("keyMap");
        final Keymap current = Keymap.fromCodeMirrorKey(keymapCode);
        final Keymap next = Keymap.fromIndex((current.getIndex() + 1) % 4);
        Log.info(CodeMirrorEditorWidget.class, "Setting editor keymap: " + next.getCodeMirrorKey());
        notificationManager.showNotification(new Notification("Changed key binding: " + next.getCodeMirrorKey(), Type.INFO));
        switch (next) {
            case DEFAULT:
                selectDefaultKeymap();
                break;
            case VIM:
                selectVimKeymap();
                break;
            case EMACS:
                selectEmacsKeymap();
                break;
            case SUBLIME:
                selectSublimeKeymap();
                break;
            default:
                throw new RuntimeException("Unknown keymap type: " + next);
        }
    }

    @Override
    public HandlerRegistration addChangeHandler(final ChangeHandler handler) {
        if (!changeHandlerAdded) {
            changeHandlerAdded = true;
            this.editorOverlay.on("change", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireChangeEvent();
                }
            });
        }
        return addHandler(handler, ChangeEvent.getType());
    }

    private void fireChangeEvent() {
        DomEvent.fireNativeEvent(Document.get().createChangeEvent(), this);
    }

    @Override
    public HandlerRegistration addFocusHandler(final FocusHandler handler) {
        if (!focusHandlerAdded) {
            focusHandlerAdded = true;
            this.editorOverlay.on("focus", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireFocusEvent();
                }
            });
        }
        return addHandler(handler, FocusEvent.getType());
    }

    private void fireFocusEvent() {
        DomEvent.fireNativeEvent(Document.get().createFocusEvent(), this);
    }

    @Override
    public HandlerRegistration addBlurHandler(final BlurHandler handler) {
        if (!blurHandlerAdded) {
            blurHandlerAdded = true;
            this.editorOverlay.on("blur", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireBlurEvent();
                }
            });
        }
        return addHandler(handler, BlurEvent.getType());
    }

    private void fireBlurEvent() {
        DomEvent.fireNativeEvent(Document.get().createBlurEvent(), this);
    }

    @Override
    public HandlerRegistration addScrollHandler(final ScrollHandler handler) {
        if (!scrollHandlerAdded) {
            scrollHandlerAdded = true;
            this.editorOverlay.on("scroll", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireScrollEvent();
                }
            });
        }
        return addHandler(handler, ScrollEvent.getType());
    }

    private void fireScrollEvent() {
        DomEvent.fireNativeEvent(Document.get().createScrollEvent(), this);
    }

    @Override
    public HandlerRegistration addCursorActivityHandler(final CursorActivityHandler handler) {
        if (!cursorHandlerAdded) {
            cursorHandlerAdded = true;
            this.editorOverlay.on("cursorActivity", new CMEditorOverlay.EventHandlerNoParameters() {

                @Override
                public void onEvent() {
                    fireCursorActivityEvent();
                }
            });
        }
        return addHandler(handler, CursorActivityEvent.TYPE);
    }

    private void fireCursorActivityEvent() {
        fireEvent(new CursorActivityEvent());
    }

    @Override
    public HandlerRegistration addBeforeSelectionChangeHandler(final BeforeSelectionChangeHandler handler) {
        if (!beforeSelectionHandlerAdded) {
            beforeSelectionHandlerAdded = true;
            this.editorOverlay.on("beforeSelectionChange",
                                  new CMEditorOverlay.EventHandlerOneParameter<BeforeSelectionEventParamOverlay>() {

                                      @Override
                                      public void onEvent(final BeforeSelectionEventParamOverlay param) {
                                          fireBeforeSelectionChangeEvent(); // TODO : use the event parameters
                                      }
                                  });
        }
        return addHandler(handler, BeforeSelectionChangeEvent.TYPE);
    }

    private void fireBeforeSelectionChangeEvent() {
        fireEvent(new BeforeSelectionChangeEvent());
    }

    @Override
    public HandlerRegistration addViewPortChangeHandler(ViewPortChangeHandler handler) {
        if (!viewPortHandlerAdded) {
            viewPortHandlerAdded = true;
            this.editorOverlay.on("viewportChange", new CMEditorOverlay.EventHandlerMultipleParameters<JavaScriptObject>() {

                @Override
                public void onEvent(final JsArray<JavaScriptObject> param) {
                    JsArrayInteger asIntegers = param.cast();
                    final int from = asIntegers.get(0);
                    final int to = asIntegers.get(1);
                    fireViewPortChangeEvent(from, to);
                }
            });
        }
        return addHandler(handler, ViewPortChangeEvent.TYPE);
    }

    private void fireViewPortChangeEvent(final int from, final int to) {
        fireEvent(new ViewPortChangeEvent(from, to));
    }

    @Override
    public HandlerRegistration addGutterClickHandler(GutterClickHandler handler) {
        if (!gutterClickHandlerAdded) {
            gutterClickHandlerAdded = true;
            this.editorOverlay.on("gutterClick", new CMEditorOverlay.EventHandlerMultipleParameters<JavaScriptObject>() {

                @Override
                public void onEvent(final JsArray<JavaScriptObject> params) {
                    fireGutterClickEvent();
                }
            });
        }
        return addHandler(handler, GutterClickEvent.TYPE);
    }

    private void fireGutterClickEvent() {
        fireEvent(new GutterClickEvent());
    }


    @Override
    public void setReadOnly(final boolean isReadOnly) {
        this.editorOverlay.setOption("readOnly", isReadOnly);
    }

    @Override
    public boolean isReadOnly() {
        return this.editorOverlay.getBooleanOption("readOnly");
    }

    @Override
    public boolean isDirty() {
        return !this.editorOverlay.isClean();
    }

    @Override
    public void markClean() {
        this.editorOverlay.markClean();
    }

    @Override
    public EmbeddedDocument getDocument() {
        if (this.embeddedDocument == null) {
            this.embeddedDocument = new CodeMirrorDocument(this.editorOverlay.getDoc(), this.document, this);
        }
        return this.embeddedDocument;
    }

    @Override
    public Region getSelectedRange() {
        // will only support a single selection here

        /* multiple selection support would use listSelections() */
        final CMPositionOverlay from = this.editorOverlay.getDoc().getCursorFrom();
        final CMPositionOverlay to = this.editorOverlay.getDoc().getCursorTo();

        final int startOffset = this.editorOverlay.getDoc().indexFromPos(from);
        final int endOffset = this.editorOverlay.getDoc().indexFromPos(to);

        final int lastLine = this.editorOverlay.getDoc().getLastLine();
        final int lastPosition = this.editorOverlay.getDoc().getLine(lastLine).length();

        if (startOffset < 0 || endOffset > lastPosition || startOffset > endOffset) {
            throw new RuntimeException("Invalid selection");
        }
        return new RegionImpl(startOffset, endOffset - startOffset);
    }
}
