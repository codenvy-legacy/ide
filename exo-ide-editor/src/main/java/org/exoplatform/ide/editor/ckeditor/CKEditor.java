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
package org.exoplatform.ide.editor.ckeditor;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.editor.client.api.FileContentLoader;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.client.api.event.*;
import org.exoplatform.ide.editor.shared.text.Document;
import org.exoplatform.ide.editor.shared.text.DocumentEvent;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.exoplatform.ide.editor.shared.text.IDocumentListener;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmitry Nochevnov</a>
 * @version $
 */

public class CKEditor extends AbsolutePanel implements Editor {

    private final String mimeType;

    private final CKEditorConfiguration configuration;

    protected final String id;


    private String initialContent;

    private boolean readOnly;

    private Label label;

    private JavaScriptObject editorObject;

    private int onContentChangeListenerId;

    private int onEditorResizeListenerId;

    private String prefix = "";

    private String suffix = "";

    public CKEditor(String mimeType) {
        this(mimeType, new CKEditorConfiguration());
    }

    public CKEditor(String mimeType, CKEditorConfiguration configuration) {
        this.mimeType = mimeType;
        id = "CKEditor - " + String.valueOf(this.hashCode());

        if (configuration == null) {
            configuration = new CKEditorConfiguration();
        }
        this.configuration = configuration;

        if (mimeType.equals(MimeType.TEXT_HTML)) {
            CKEditorConfiguration.setFullPage(true);
        }

        label = new Label();
        DOM.setElementAttribute(label.getElement(), "id", id);
        //DOM.setElementAttribute(label.getElement(), "style", "overflow: auto; width: 100%; height: 100%;"); // to show scrollbars
        DOM.setElementAttribute(label.getElement(), "style",
                                "overflow: hidden; position:absolute; left:0px; top:0px; width: 100%; height: 100%;");
        add(label);
    }

    @Override
    protected void onLoad() {
        try {
            editorObject = initCKEditor(id,
                                        CKEditorConfiguration.BASE_PATH,
                                        CKEditorConfiguration.TOOLBAR.toString(),
                                        // aditional default configuration can be found in config.js
                                        CKEditorConfiguration.THEME.toString(),
                                        CKEditorConfiguration.SKIN.toString(),
                                        CKEditorConfiguration.LANGUAGE.toString(),
                                        CKEditorConfiguration.CONTINUOUS_SCANNING,
                                        CKEditorConfiguration.isFullPage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * remove listeners and restore functions
     */
    protected void onUnload() {
        removeEditorListeners();
        removeOnContentChangeListener();
        removeOnEditorResizeListener();
        restoreNativeAlertAndConfirm();
    }

    private native JavaScriptObject initCKEditor(String id, String basePath, String toolbar, String theme, String skin,
                                                 String language, int continuousScanning, boolean fullPage)
   /*-{
       var instance = this;
       if (toolbar !== undefined) {
           $wnd.CKEDITOR.config.toolbar = toolbar;
       }

       if (theme !== undefined) {
           $wnd.CKEDITOR.config.theme = theme;
       }

       if (language !== undefined) {
           $wnd.CKEDITOR.config.language = language;
       }

       if (basePath !== undefined) {
           $wnd.CKEDITOR.basePath = basePath;
           $wnd.CKEDITOR.config.contentsCss = basePath + "contents.css"; // reflects the CSS used in the final pages where the contents are to be used.
           $wnd.CKEDITOR.plugins.basePath = basePath + "plugins/"; // set base path to the plugins folder
           $wnd.CKEDITOR.config.templates_files[0] = basePath + "plugins/templates/templates/default.js"; // set default template path
           $wnd.CKEDITOR.config.smiley_path = basePath + "plugins/smiley/images/"; // The base path used to build the URL for the smiley images.
       }

       if (skin !== undefined) {
           $wnd.CKEDITOR.config.skin = skin + ',' + basePath + 'skins/' + skin + '/';
       }

       if (fullPage !== undefined) {
           $wnd.CKEDITOR.config.fullPage = fullPage;
       }

       // create editor instance
       var editor = $wnd.CKEDITOR.appendTo(id, $wnd.CKEDITOR.config);

       // add listeners
       if (editor !== null) {
           // init editor content variable
           editor.exoSavedContent = "";

           // set onContentChangeListener
           editor.exoChangeFunction = function () {
               // check if content was changed
               if (editor.checkDirty()) {
                   editor.resetDirty();
                   if (editor.getData() != editor.exoSavedContent) {
                       editor.exoSavedContent = editor.getData();
                       instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChanged()();
                   }
               }
           }

           instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChangeListenerId = $wnd.setInterval(editor.exoChangeFunction,
               continuousScanning);

           // add Hot Keys Listener
           instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::setHotKeysClickListener(Lcom/google/gwt/core/client/JavaScriptObject;)
               (editor);

           // add onFocus listener
           editor.onFocusReceived = function () {
               instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onFocusReceived()();
           }
           editor.on('focus', editor.onFocusReceived);

           // set init callback
           editor.exoInitCallback = function () {
               instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onInitialized()();
           }

           editor.on('instanceReady', editor.exoInitCallback);
       }

       editor.exoNativeAlert = $wnd.alert;
       editor.exoNativeConfirm = $wnd.confirm;
       instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::overrideNativeAlertAndConfirm()();

       return editor;
   }-*/;

    private void onContentChanged() {
        fireEvent(new EditorContentChangedEvent(this));
    }

    private void onCursorActivity() {
        fireEvent(new EditorCursorActivityEvent(this, 0, 0));
    }

    private void onFocusReceived() {
        fireEvent(new EditorFocusReceivedEvent(this));
    }

//   private void onInitialized()
//   {
//      fireEvent(new EditorInitializedEvent(this));
//      setText(initialContent);
//   }


    private void onInitialized() {
        updateDimensions();
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                updateDimensions();
            }
        });

        fireEvent(new EditorInitializedEvent(this));
        setText(initialContent);
    }

    /** This hack method is needs to automatically update size of CKEditor toolbar and frame. */
    private void updateDimensions() {
        Element spanElement = label.getElement().getFirstChild().cast();
        tuneElement(spanElement, false);

        Element span1Element = spanElement.getFirstChildElement().cast();
        tuneElement(span1Element, false);

        Element span2Element = span1Element.getFirstChildElement().cast();
        tuneElement(span2Element, false);

        Element tableElement = span2Element.getFirstChildElement().cast();
        tuneElement(tableElement, true);

        Element tBodyElement = tableElement.getFirstChildElement().cast();

        Element tr1Element = tBodyElement.getChild(0).cast();
        Element tr1TDElement = tr1Element.getFirstChildElement().cast();
        tr1TDElement.getStyle().setPosition(Position.RELATIVE);

        Element tr2Element = tBodyElement.getChild(1).cast();
        tr2Element.getStyle().setHeight(100, Unit.PCT);

        Element tr2TDElement = tr2Element.getFirstChildElement().cast();
        tr2TDElement.getStyle().setPosition(Position.RELATIVE);
        tr2TDElement.getStyle().setWidth(100, Unit.PCT);
        tr2TDElement.getStyle().setProperty("height", "auto");

        Element iFrameElement = tr2TDElement.getFirstChildElement().cast();
        if ("cke_browser_gecko".equals(span1Element.getClassName())) {
            iFrameElement.getStyle().setPosition(Position.RELATIVE);
        } else {
            iFrameElement.getStyle().setPosition(Position.ABSOLUTE);
        }
    }

    private void tuneElement(Element e, boolean absolutePosition) {
        if (absolutePosition) {
            e.getStyle().setPosition(Position.ABSOLUTE);
        } else {
            e.getStyle().setPosition(Position.RELATIVE);
        }

        e.getStyle().setLeft(0, Unit.PX);
        e.getStyle().setTop(0, Unit.PX);
        e.getStyle().setWidth(100, Unit.PCT);
        e.getStyle().setHeight(100, Unit.PCT);
        e.getStyle().setOverflow(Overflow.HIDDEN);
    }


    public String getText() {
        // replace "\t" delimiter on space symbol
        return getTextNative().replace("\t", " ");
    }

    /** {@inheritDoc} */
    @Override
    public void setFile(FileModel file) {
        FileContentLoader.getFileContent(file, new FileContentLoader.ContentCallback() {
            @Override
            public void onContentReceived(String content) {
                setText(content);
            }
        });
    }

    public native String getTextNative()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.exoSavedContent = editor.getData();
           return this.@org.exoplatform.ide.editor.ckeditor.CKEditor::prefix
               + editor.exoSavedContent
               + this.@org.exoplatform.ide.editor.ckeditor.CKEditor::suffix;
       }
   }-*/;

    public String extractHtmlCodeFromGoogleGadget(String text) {
        this.prefix = GoogleGadgetParser.getPrefix(text);
        String content = GoogleGadgetParser.getContentSection(text);
        this.suffix = GoogleGadgetParser.getSuffix(text);
        return content;
    }

    ;

    public void setText(String text) {
        // removed odd "\r" symbols
        text = text.replace("\r", "");

        // extract CDATA section from google gadget
        if (getMimeType().equals(MimeType.GOOGLE_GADGET)) {
            this.prefix = this.suffix = "";

            // test if it is possible to localize CDATA section
            if (GoogleGadgetParser.hasContentSection(text)) {
                // extract HTML-code from <Content> tag
                text = this.extractHtmlCodeFromGoogleGadget(text);
            }
        }

        this.setData(text);
    }

    private native void setEditorMode(String mode)
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.setMode(mode);
       }
   }-*/;

    private native void setData(String data)
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.setData(data, function () {
               editor.checkDirty(); // reset ckeditor content changed indicator (http://docs.cksource.com/ckeditor_api/symbols/CKEDITOR.editor.html#setData)
           });

           editor.exoSavedContent = data;
           editor.focus();
       }
   }-*/;

    public native void undo()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.execCommand("undo");
       }
   }-*/;

    public native void redo()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.execCommand("redo");
       }
   }-*/;

    public native void setFocus()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       var instance = this;
       if (editor != null) {
           $wnd.setTimeout(function (a, b) {
               editor.focus();
               instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onFocusReceived()();
           }, 200);
       }
   }-*/;

    private native void restoreNativeAlertAndConfirm()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (typeof editor.exoNativeAlert === "function" && typeof editor.exoNativeConfirm === "function") {
           $wnd.alert = editor.exoNativeAlert;
           $wnd.confirm = editor.exoNativeConfirm;
       }
   }-*/;

    private native void removeOnContentChangeListener()
   /*-{
       var onContentChangeListenerId = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::onContentChangeListenerId;
       if (onContentChangeListenerId !== null) {
           $wnd.clearInterval(onContentChangeListenerId);
       }
   }-*/;

    private native void removeOnEditorResizeListener()
   /*-{
       var onEditorResizeListenerId = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::onEditorResizeListenerId;
       if (onEditorResizeListenerId !== null) {
           $wnd.clearInterval(onEditorResizeListenerId);
       }
   }-*/;

    @Override
    public void setHeight(String height) {
        super.setHeight(height);
        setHeightNative(height);
    }

    /*
     * set editor height
     */
    public native void setHeightNative(String height)
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor !== null) {
           editor.resize("100%", height);
       }
   }-*/;

    private native void removeEditorListeners()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor !== null) {
           // remove 'instanceReady' listener
           if (editor.hasListeners('instanceReady')) {
               editor.removeListener('instanceReady', editor.exoInitCallback)
           }

           if (editor.hasListeners('key')) {
               editor.removeListener('key', editor.exoHotKeysClickListener);
           }
       }
   }-*/;

    public boolean isReadOnly() {
        return readOnly;
    }

    private int getLabelOffsetHeight() {
        return label.getOffsetHeight();
    }

    private static void showErrorDialog(String title, String message) {
        Dialogs.getInstance().showError(title, message);
    }

    /**
     * replace window.alert() function on org.exoplatform.gwtframework.ui.client.dialogs.Dialogs.showError() and hide
     * window.confirm() function
     */
    private native void overrideNativeAlertAndConfirm()
   /*-{
       (function () {
           var proxied = $wnd.alert;
           $wnd.alert = function (message) {
               // test if this is a in context of ckeditor
               if (typeof $wnd.CKEDITOR !== "undefined") {
                   @org.exoplatform.ide.editor.ckeditor.CKEditor::showErrorDialog(Ljava/lang/String;Ljava/lang/String;)("WYSIWYG Editor Error", message);
               }
               else {
                   return proxied(message);
               }
           };
       })(this);

       (function () {
           var proxied = $wnd.confirm;

           $wnd.confirm = function (message) {
               // test if this is a ckeditor
               if (typeof $wnd.CKEDITOR !== "undefined") {
                   return true;
               }
               else {
                   return proxied(message);
               }
           };
       })();
   }-*/;

    private boolean handleShortcut(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode) {
        EditorHotKeyPressedEvent event = new EditorHotKeyPressedEvent(isCtrl, isAlt, isShift, keyCode);
        fireEvent(event);
        return event.isHotKeyHandled();
    }

    private void logMessage(String message) {
        System.out.println(message);
    }

    /** Set listeners of hot keys clicking */
    private native void setHotKeysClickListener(JavaScriptObject editor)
   /*-{
       var instance = this;

       try {
           editor.exoHotKeysClickListener = function (event) {
               var keyCode = event.data.keyCode;
               var isShift = false;
               var isAlt = false;
               var isCtrl = false;

               if (event.data.keyCode > $wnd.CKEDITOR.ALT) {
                   isAlt = true;
                   keyCode = keyCode - $wnd.CKEDITOR.ALT;
               }

               if (event.data.keyCode > $wnd.CKEDITOR.SHIFT) {
                   isShift = true;
                   keyCode = keyCode - $wnd.CKEDITOR.SHIFT;
               }

               if (event.data.keyCode > $wnd.CKEDITOR.CTRL) {
                   isCtrl = true;
                   keyCode = keyCode - $wnd.CKEDITOR.CTRL;
               }

               var stopEvent = instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::handleShortcut(ZZZI)(isCtrl, isAlt, isShift,
                   keyCode);
               if (stopEvent) {
                   event.cancel();
                   return false;
               }

               instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::onCursorActivity()();
           }

           editor.on('key', editor.exoHotKeysClickListener);
       }
       catch (e) {
           instance.@org.exoplatform.ide.editor.ckeditor.CKEditor::logMessage(Ljava/lang/String;)("" + e.name + " exception. " + e.message);
       }
   }-*/;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean isCapable(EditorCapability capability) {
        switch (capability) {
            default:
                return false;
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getMimeType() */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getDocument() */
    @Override
    public IDocument getDocument() {
        Document document = new Document(getText());
        document.addDocumentListener(new IDocumentListener() {
            @Override
            public void documentAboutToBeChanged(DocumentEvent event) {
            }

            @Override
            public void documentChanged(DocumentEvent event) {
                setText(event.getDocument().get());
            }
        });
        return document;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getSelectionRange() */
    @Override
    public SelectionRange getSelectionRange() {
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#selectAll() */
    @Override
    public native void selectAll()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.execCommand("SelectAll");
       }
   }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#cut() */
    @Override
    public native void cut()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.execCommand("cut");
       }
   }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#copy() */
    @Override
    public native void copy()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.execCommand("copy");
       }
   }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#paste() */
    @Override
    public native void paste()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.execCommand("paste");
       }
   }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#delete() */
    @Override
    public native void delete()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       if (editor != null) {
           editor.execCommand("delete");
       }
   }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#setReadOnly(boolean) */
    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#collapse() */
    @Override
    public void collapse() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#expand() */
    @Override
    public void expand() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#collapseAll() */
    @Override
    public void collapseAll() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#expandAll() */
    @Override
    public void expandAll() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#foldSelection() */
    @Override
    public void foldSelection() {
        throw new UnsupportedOperationException();
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getName() */
    @Override
    public String getName() {
        return "Design";
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#addContentChangedHandler(org.exoplatform.ide.editor.client.api.event
     * .EditorContentChangedHandler) */
    @Override
    public HandlerRegistration addContentChangedHandler(EditorContentChangedHandler handler) {
        return addHandler(handler, EditorContentChangedEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#addContextMenuHandler(org.exoplatform.ide.editor.client.api.event
     * .EditorContextMenuHandler) */
    @Override
    public HandlerRegistration addContextMenuHandler(EditorContextMenuHandler handler) {
        return addHandler(handler, EditorContextMenuEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#addCursorActivityHandler(org.exoplatform.ide.editor.client.api.event
     * .EditorCursorActivityHandler) */
    @Override
    public HandlerRegistration addCursorActivityHandler(EditorCursorActivityHandler handler) {
        return addHandler(handler, EditorCursorActivityEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#addFocusReceivedHandler(org.exoplatform.ide.editor.client.api.event
     * .EditorFocusReceivedHandler) */
    @Override
    public HandlerRegistration addFocusReceivedHandler(EditorFocusReceivedHandler handler) {
        return addHandler(handler, EditorFocusReceivedEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#addHotKeyPressedHandler(org.exoplatform.ide.editor.client.api.event
     * .EditorHotKeyPressedHandler) */
    @Override
    public HandlerRegistration addHotKeyPressedHandler(EditorHotKeyPressedHandler handler) {
        return addHandler(handler, EditorHotKeyPressedEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#addInitializedHandler(org.exoplatform.ide.editor.client.api.event
     * .EditorInitializedHandler) */
    @Override
    public HandlerRegistration addInitializedHandler(EditorInitializedHandler handler) {
        return addHandler(handler, EditorInitializedEvent.TYPE);
    }

    @Override
    public void formatSource() {
    }

    @Override
    public void showLineNumbers(boolean showLineNumbers) {
    }

    @Override
    public void setCursorPosition(int row, int column) {
    }

    @Override
    public void deleteCurrentLine() {
    }

    @Override
    public native boolean hasUndoChanges()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       return editor._.commands.undo.state != $wnd.CKEDITOR.TRISTATE_DISABLED;
   }-*/;

    @Override
    public native boolean hasRedoChanges()
   /*-{
       var editor = this.@org.exoplatform.ide.editor.ckeditor.CKEditor::editorObject;
       return editor._.commands.redo.state != $wnd.CKEDITOR.TRISTATE_DISABLED;
   }-*/;

    @Override
    public int getCursorRow() {
        return 0;
    }

    @Override
    public int getCursorColumn() {
        return 0;
    }

    @Override
    public void replaceTextAtCurrentLine(String line, int cursorPosition) {
    }

    @Override
    public String getLineText(int line) {
        return null;
    }

    @Override
    public void setLineText(int line, String text) {
    }

    @Override
    public int getNumberOfLines() {
        return 0;
    }

    @Override
    public void selectRange(int startLine, int startChar, int endLine, int endChar) {
    }

    @Override
    public int getCursorOffsetLeft() {
        return 0;
    }

    @Override
    public int getCursorOffsetTop() {
        return 0;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#search(java.lang.String, boolean, org.exoplatform.ide.editor.client.api.event
     * .SearchCompleteCallback) */
    @Override
    public void search(String query, boolean caseSensitive, SearchCompleteCallback searchCompleteCallback) {
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#replaceMatch(java.lang.String) */
    @Override
    public void replaceMatch(String replacement) {
    }

}
