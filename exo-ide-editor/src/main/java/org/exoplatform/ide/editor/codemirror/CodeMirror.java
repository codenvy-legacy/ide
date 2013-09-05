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
package org.exoplatform.ide.editor.codemirror;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.BodyElement;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.rebind.rpc.ProblemReport.Problem;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.EditorTokenListPreparedEvent;
import org.exoplatform.ide.editor.api.EditorTokenListPreparedHandler;
import org.exoplatform.ide.editor.client.api.FileContentLoader;
import org.exoplatform.ide.editor.api.codeassitant.CanInsertImportStatement;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.client.api.SelectionRange;
import org.exoplatform.ide.editor.client.api.event.*;
import org.exoplatform.ide.editor.client.marking.*;
import org.exoplatform.ide.editor.shared.text.*;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.*;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeMirror Feb 9, 2011 4:58:14 PM $
 */
public class CodeMirror extends AbsolutePanel implements Editor, Markable, IDocumentListener, CanInsertImportStatement {

    /** Height of line in the CodeMirror in pixels. */
    public static final int LINE_HEIGHT = 16;

    /** Width of column with line numbers in pixels. */
    public static final int LINE_NUMBERS_COLUMN_WIDTH = 48;

    /** Width of character in the CodeMirror in pixels. */
    public static final int CHARACTER_WIDTH = 8;

    /** Offset of the lines from left in pixels. */
    public static final int LINE_OFFSET_LEFT = 11;


    public static final int codeErrorCorrectionPopupOffsetLeft = 6; // top offset of character of the line in px

    private static int codeErrorCorrectionPopupOffsetTop = 22; // top offset of character of the line in px


    private final Browser currentBrowser = BrowserResolver.CURRENT_BROWSER;


    /** Editor's ID. */
    private final String id;

    /** Media type of document. */
    private final String mimeType;

    /** Configuration of CodeMirror. */
    protected final CodeMirrorConfiguration configuration;

    /** Visibility of line numbers column. */
    private boolean showLineNumbers = true;

    /** Visibility of overview column. */
    protected boolean showOverview = false;


    protected JavaScriptObject editorObject;

    private FlowPanel lineHighlighter;


    private boolean needUpdateTokenList = true; // update token list only after the "initCallback" handler has been called

    private boolean needValidateCode = false;


    private List<TokenBeenImpl> tokenList;

    private int cursorPositionCol = 1;

    private int cursorPositionRow = 1;

    protected List<CodeLine> codeErrorList = new ArrayList<CodeLine>();

    private IDocument document;

    private boolean needUpdateDocument = false;

    private boolean readOnly = false;

    private String initialText;


    private FrameElement frameElement;
    private FileModel file;


    /**
     * Creates new CodeMirror instance.
     *
     * @param mimeType
     */
    public CodeMirror(String mimeType) {
        this(mimeType, new CodeMirrorConfiguration());
    }

    /**
     * Creates new CodeMirror instance.
     *
     * @param mimeType
     * @param configuration
     */
    public CodeMirror(String mimeType, CodeMirrorConfiguration configuration) {
        this(mimeType, configuration, false);
    }

    /**
     * Creates new CodeMirror instance.
     *
     * @param mimeType
     * @param configuration
     */
    public CodeMirror(String mimeType, CodeMirrorConfiguration configuration, boolean showOverview) {
        id = "CodeMirror - " + String.valueOf(this.hashCode());
        this.mimeType = mimeType;
        if (configuration == null) {
            configuration = new CodeMirrorConfiguration();
        }

        this.configuration = configuration;
        this.showOverview = showOverview;

        document = new Document("");
        document.addDocumentListener(this);

        createFrame();
    }

    private Frame frame;

    private void createFrame() {
        frame = new Frame(CodeMirrorConfiguration.CODEMIRROR_START_PAGE);
        add(frame);
        frame.getElement().getStyle().setPosition(Position.ABSOLUTE);
        frame.setSize("100%", "100%");
        frame.getElement().setAttribute("frameborder", "0");
        frame.getElement().getStyle().setBorderStyle(BorderStyle.NONE);
        frame.getElement().getStyle().setBackgroundColor("white");

        frame.addLoadHandler(new LoadHandler() {
            @Override
            public void onLoad(LoadEvent event) {
                frameElement = frame.getElement().cast();

                Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                    @Override
                    public void execute() {
                        injectCssToFrameElement();
                        buildEditor();
                    }
                });
            }
        });
    }

    public FrameElement getFrameElement() {
        return frameElement;
    }

    private com.google.gwt.dom.client.Node getChildByName(com.google.gwt.dom.client.Node node, String childName) {
        for (int i = 0; i < node.getChildCount(); i++) {
            com.google.gwt.dom.client.Node child = node.getChild(i);
            if (childName.equalsIgnoreCase(node.getNodeName()) &&
                Node.ELEMENT_NODE == node.getNodeType()) {
                return child;
            }
        }

        return null;
    }

    private void injectCssToFrameElement() {
        if (frameElement == null) {
            return;
        }

        addCssNative(injectCss);
    }

    protected JavaScriptObject cssLinkElement;

    protected JavaScriptObject cssLinkElement2;

    private native void addCssNative(String cssStyleName) /*-{
        var frameElement = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::frameElement;
        var frameDocument = frameElement.contentWindow.document;

        var head = frameDocument.getElementsByTagName('head')[0];
        //alert (head.innerHTML);

        var cssLinkElement = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cssLinkElement;
        if (cssLinkElement != null) {
            head.removeChild(cssLinkElement);
            this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cssLinkElement = null;
        }

        //alert('cssStyleName > ' + cssStyleName);

        if (cssStyleName != null) {
            var link = frameDocument.createElement('link');
            link.rel = 'stylesheet';
            link.type = 'text/css';
            link.href = cssStyleName;
            head.appendChild(link);
            this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cssLinkElement = link;
        }

        var editorFrameElement = frameDocument.getElementsByTagName('iframe')[0];
        //alert('editor frame > ' + editorFrameElement);

        if (editorFrameElement != undefined && editorFrameElement != null) {
            var editorFrameDocument = editorFrameElement.contentWindow.document;
            //alert('editor frame document > ' + editorFrameDocument);

            var editorHead = editorFrameDocument.getElementsByTagName('head')[0];
            //alert('editor head > ' + editorHead);

            var cssLinkElement2 = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cssLinkElement2;
            if (cssLinkElement2 != null) {
                //alert('remove existed css link element...........');
                editorHead.removeChild(cssLinkElement2);
                this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cssLinkElement2 = null;
            }

            if (cssStyleName != null) {
                //alert('creating link element...');

                var link = editorFrameDocument.createElement('link');
                link.rel = 'stylesheet';
                link.type = 'text/css';
                link.href = cssStyleName;
                editorHead.appendChild(link);
                this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cssLinkElement2 = link;
            }
        }

    }-*/;

    private String injectCss;

    public void injectStyle(String injectCss) {
        this.injectCss = injectCss;

        if (frameElement != null) {
            addCssNative(injectCss);
        }
    }

    private class FrameBodyWidget extends AbsolutePanel {
        public FrameBodyWidget(BodyElement body) {
            super(body.<com.google.gwt.user.client.Element>cast());
            onAttach();
        }
    }

    private void buildEditor() {
        com.google.gwt.dom.client.Document frameDocument = frameElement.getContentDocument();
        final BodyElement bodyElement = frameDocument.getBody();
        FrameBodyWidget body = new FrameBodyWidget(bodyElement);

        DockLayoutPanel doc = new DockLayoutPanel(Unit.PX);
        doc.setSize("100%", "100%");
        body.add(doc, 0, 0);

        if (showOverview) {
            overviewRuler = new OverviewRuler(this);
            doc.addEast(overviewRuler, 13);
        }

        absPanel = new AbsolutePanel();
        doc.add(absPanel);

        TextArea textArea = new TextArea();
        DOM.setElementAttribute(textArea.getElement(), "id", getId());
        textArea.setVisible(false);
        absPanel.add(textArea);

        lineHighlighter = getLineHighlighter();
        absPanel.add(lineHighlighter);
        absPanel.setWidgetPosition(lineHighlighter, 0, 5);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                editorObject =
                        initCodeMirror(id, "", "100%", readOnly, configuration.getContinuousScanning(),
                                       configuration.isTextWrapping(), showLineNumbers, configuration.getCodeStyles(),
                                       configuration.getCodeParsers(), configuration.getJsDirectory(),
                                       configuration.getTabMode().toString());
                injectCssToFrameElement();
            }
        });
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getMimeType() */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    @Override
    protected void onUnload() {
        try {
            codeValidateTimer.cancel();

            if (configuration.getParser() != null) {
                configuration.getParser().stopParsing();
            }
        } catch (Exception e) {
            Window.alert("Exception > " + e.getMessage());
            e.printStackTrace();
        }
    }

    private native JavaScriptObject initCodeMirror(String id, String width, String height, boolean readOnly, int cs, boolean tr,
                                                   boolean lineNumbers, String styleURLs, String parserNames, String jsDirectory,
                                                   String modeTab) /*-{
       var instance = this;
       var frame = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::frameElement;

       var changeFunction = function () {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onContentChanged()();
       };

       var cursorActivity = function (cursor) {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onCursorActivity(Lcom/google/gwt/core/client/JavaScriptObject;)
               (cursor);
       };

       var onLineNumberClick = function (lineNumber) {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberClick(I)(lineNumber);
       };

       var onLineNumberDoubleClick = function (lineNumber) {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberDoubleClick(I)(lineNumber);
       }

       var onLineNumberContextMenu = function (lineNumber, e) {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberContextMenu(ILcom/google/gwt/dom/client/NativeEvent;)
               (lineNumber, e);
       }

       var initCallback = function () {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onInitialized()();
       };

       var activeTokensFunction = function () {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::needUpdateTokenList = true;
       };

       var editor = frame.contentWindow.CodeMirror.fromTextArea(id, {
           'width': width,
           'height': height,
           'parserfile': eval(parserNames),
           'stylesheet': eval(styleURLs),
           'path': jsDirectory,
           'continuousScanning': cs || false,
           'undoDelay': 50, // decrease delay before calling 'onChange' callback
           'lineNumbers': lineNumbers,
           'readOnly': readOnly,
           'textWrapping': tr,
           'tabMode': modeTab,
           'content': "", // to fix bug with blocked deleting function of CodeMirror just after opening file [WBT-223]
           'onChange': changeFunction,
           'reindentOnLoad': false, // to fix problem with getting token list after the loading content
           'onCursorActivity': cursorActivity,
           'onLineNumberClick': onLineNumberClick,
           'onLineNumberDoubleClick': onLineNumberDoubleClick,
           'onLineNumberContextMenu': onLineNumberContextMenu,
           'onLoad': initCallback,
           'autoMatchParens': true,

           // Take the token before the cursor. If it contains a character in '()[]{}', search for the matching paren/brace/bracket, and
           // highlight them in green for a moment, or red if no proper match was found.
           'markParen': function (node, ok) {
               node.id = ok ? "parenCorrect" : "parenIncorrect";
           },

           'unmarkParen': function (node) {
               node.id = null;
           },

           // to update outline panel after the new line has being highlighted
           'activeTokens': activeTokensFunction
       });

       return editor;
    }-*/;

    /** Called by CodeMirror after it's instance has been initialized. */
    private void onInitialized() {
        addHighlighterListeners();
        addKeyPressedListener();
        addFocusReceivedListeners();
        addContextMenuListener();

        this.needUpdateTokenList = true; // update token list after the document had been loaded and reindented
        // turn on code validation time
        if (configuration.canBeValidated()) {
            needValidateCode = true;
            codeValidateTimer.scheduleRepeating(2000);
        }

        fireEvent(new EditorInitializedEvent(this));

        if (initialText != null) {
            setText(initialText);
        }

        if (cursorPositionRow != 0 || cursorPositionCol != 0) {
            setCursorPosition(cursorPositionRow, cursorPositionCol);
        }
    }

    private Timer codeValidateTimer = new Timer() {
        @Override
        public void run() {
            validateCode();
        }
    };

    private void onLineNumberDoubleClick(int lineNumber) {
        clickHandler.cancel();
        fireEvent(new EditorLineNumberDoubleClickEvent(lineNumber));
        if (activeNotification != null)
            activeNotification.update();
    }

    private void onLineNumberContextMenu(int lineNumber, NativeEvent event) {
        event.stopPropagation();
        event.preventDefault();
        fireEvent(new EditorLineNumberContextMenuEvent(lineNumber, event.getClientX(), event.getClientY()));
    }

    private void onContentChanged() {
        needUpdateTokenList = true;
        needUpdateDocument = true;
        fireEvent(new EditorContentChangedEvent(this));
    }

    private void onCursorActivity(JavaScriptObject cursor) {
        cursorPositionCol = getCursorColumn();

        if (BrowserResolver.CURRENT_BROWSER == Browser.IE) {
            if (getNumberOfLines() == 1) {
                cursorPositionRow = 1;
            } else {
                cursorPositionRow = getCursorActivityRow(cursor, cursorPositionCol);
            }
        } else {
            cursorPositionRow = getNativeCursorRow();
        }

        // highlight current line
        highlightLine(cursorPositionRow);

        fireEvent(new EditorCursorActivityEvent(this, cursorPositionRow, cursorPositionCol));
    }

    private void highlightLine(int lineNumber) {
        if (this.currentBrowser == Browser.IE) {
            fixCodeMirrorIframeTransparencyInIE();
        }

        absPanel.setWidgetPosition(lineHighlighter, 0, 5 + getCursorOffsetY(lineNumber));
    }

    /** set codemirror iframe transparency for the IE */
    private native void fixCodeMirrorIframeTransparencyInIE() /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor !== null && editor.frame !== null
           && editor.frame.allowTransparency !== true) {
           editor.frame.allowTransparency = true;
       }
    }-*/;

    /**
     * returns line position number of vertical scroll bar in the body with text in the CodeMirror iframe
     *
     * @param currentLine
     *         if equals 0 or null, then will get current line position
     */
    public native int getCursorOffsetY(int currentLine) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor == null)
           return;

       var verticalScrollBarPosition = 0;

       switch (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
           case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
               if (editor.frame && editor.frame.contentWindow) {
                   verticalScrollBarPosition = editor.frame.contentWindow.document.documentElement.scrollTop;
               }
               break;

           default:
               if (editor.editor && editor.editor.container) {
                   verticalScrollBarPosition = editor.editor.container.scrollTop;
               }
       }

       // calculate cursorOffsetY
       var cursorOffsetY = 0;

       if (!currentLine) {
           currentLine = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cursorPositionRow;
       }

       cursorOffsetY = (currentLine - 1) * @org.exoplatform.ide.editor.codemirror.CodeMirror::LINE_HEIGHT;
       cursorOffsetY -= verticalScrollBarPosition;
       return cursorOffsetY;
    }-*/;

    private boolean handleKeyPressing(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode) {
        if ((isCtrl || (BrowserResolver.isMacOs() && isAlt)) && keyCode == ' ') {
            onAutocomplete();
            return true;
        }
        EditorHotKeyPressedEvent event = new EditorHotKeyPressedEvent(isCtrl, isAlt, isShift, keyCode);
        fireEvent(event);
        return event.isHotKeyHandled();
    }

    /**
     *
     */
    private native void addKeyPressedListener() /*-{
       var instance = this;
       var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;

       editor.grabKeys(
           function (event) {
           },
           function (keyCode, event) {
               if (event.type == "keydown") {
                   var isCtrl = event.ctrlKey || event.metaKey;
                   var isAlt = event.altKey;
                   var isShift = event.shiftKey;

                   var cancelEvent = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::handleKeyPressing(ZZZI)(isCtrl, isAlt,
                       isShift, keyCode);
                   if (cancelEvent) {
                       event.stop();
                       return true;
                   }
               }

               return false;
           });
    }-*/;

    /** Handle autocompletion. */
    public native void onAutocomplete() /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor == null) {
           return;
       }

       var cursor = editor.cursorPosition(true);
       var lineContent = editor.lineContent(cursor.line);

       // get fqn of current node
       if (editor.nextLine(cursor.line) != null && editor.nextLine(cursor.line).previousSibling) {
           var currentNode = editor.nextLine(cursor.line).previousSibling;
       }

       this.@org.exoplatform.ide.editor.codemirror.CodeMirror::callAutocompleteHandler(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(lineContent, currentNode);
    }-*/;

    private void callAutocompleteHandler(String lineContent, JavaScriptObject currentNode) {
        int cursorRow = cursorPositionRow;

        // calculate cursorOffsetY
        int cursorOffsetY = getCursorOffsetY();

        // calculate cursorOffsetX
        int cursorOffsetX = getCursorOffsetX();

        if (needUpdateTokenList) {
            needUpdateTokenList = false;
            tokenList = (List<TokenBeenImpl>)getTokenList();

            // to update token's FQNs
            if (configuration.canBeValidated()) {
                needValidateCode = false;
                validateCode(tokenList);
            }
        }

        Token tokenBeforeCursor = getTokenBeforeCursor(this.tokenList, currentNode, cursorRow, cursorPositionCol);

        List<? extends Token> selectedTokenList = this.tokenList;

        // read mimeType
        String currentLineMimeType = getCurrentLineMimeType();
        if (configuration.canHaveSeveralMimeTypes() && !mimeType.equals(currentLineMimeType)) {
            selectedTokenList =
                    (List<TokenBeenImpl>)CodeValidator.extractCode((List<TokenBeenImpl>)this.tokenList,
                                                                   new LinkedList<TokenBeenImpl>(), currentLineMimeType);
        }

        if (configuration.getCodeAssistant() != null) {
            configuration.getCodeAssistant().autocompleteCalled(this, cursorOffsetX, cursorOffsetY, (List<Token>)selectedTokenList,
                                                                currentLineMimeType, tokenBeforeCursor);
        }
    }

    /**
     * @return
     */
    public int getCursorOffsetX() {
        int cursorOffsetX = (cursorPositionCol - 2) * CHARACTER_WIDTH + getAbsoluteLeft() + LINE_OFFSET_LEFT; // 8px per symbol
        if (showLineNumbers) {
            cursorOffsetX += LINE_NUMBERS_COLUMN_WIDTH;
        }
        return cursorOffsetX;
    }

    /** @return  */
    public int getCursorOffsetY() {
        return getAbsoluteTop() + getCursorOffsetY(0);
    }

    /**
     *
     */
    private native void addHighlighterListeners() /*-{
       var instance = this;
       var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;

       var highlightFunction = function () {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::highlightLine(I)(0);
       };

       // draw highlighter at start
       highlightFunction();

       if (editor.win) {
           switch (instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
               case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
                   if (editor.win.attachEvent) {
                       editor.win.attachEvent("onscroll", highlightFunction);
                       editor.win.attachEvent("onresize", highlightFunction);
                   }
                   break;

               default:
                   if (editor.win.addEventListener) {
                       editor.win.addEventHandler(editor.win, "scroll", highlightFunction, true);
                       editor.win.addEventHandler(editor.win, "resize", highlightFunction, true);
                   }
           }
       }
    }-*/;

    /** @return mimeType of current line content */
    private String getCurrentLineMimeType() {
        if (configuration.canHaveSeveralMimeTypes()) {
            if (needUpdateTokenList) {
                needUpdateTokenList = false;
                tokenList = (List<TokenBeenImpl>)getTokenList();
            }

            String mimeType = CodeMirrorParserImpl.getLineMimeType(cursorPositionRow, tokenList);
            if (mimeType != null) {
                return mimeType;
            }
        }

        return this.mimeType;
    }

    /**
     * Updates token's FQNs and returns token before "." in position like "address._", or "address.inde_", or "String._"
     *
     * @param tokenList
     * @param node
     *         the ended node of current line
     * @param lineNumber
     * @param cursorPosition
     * @return FQN of current cursor content before "." symbol or null, if this fqn is unknown
     */
    private Token getTokenBeforeCursor(List<? extends Token> tokenList, JavaScriptObject node, int lineNumber,
                                       int cursorPosition) {
        if (configuration.canBeAutocompleted()) {
            if (configuration.getAutocompleteHelper() != null) {
                return configuration.getAutocompleteHelper().getTokenBeforeCursor(node, lineNumber, cursorPosition,
                                                                                  tokenList, getCurrentLineMimeType());
            }
        }

        return null;
    }

    /** Set listeners of focus received. */
    private native void addFocusReceivedListeners() /*-{
       var instance = this;
       var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;

       var focusReceivedListener = function () {
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorFocusReceivedEvent()();
       };

       if (editor) {
           switch (instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
               case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
                   if (editor.win.document.body.attachEvent) {
                       editor.win.document.body.attachEvent("onmouseup", focusReceivedListener);
                   }
                   break;

               default:
                   if (editor.win.addEventListener) {
                       editor.win.addEventHandler(editor.win, "mouseup", focusReceivedListener, true);
                   }
           }
       }
    }-*/;

    private void fireEditorFocusReceivedEvent() {
        fireEvent(new EditorFocusReceivedEvent(this));
    }

    /** if there is line numbers left field, then validate code and mark lines with errors */
    public void validateCode() {
        if (needUpdateTokenList && showLineNumbers) {
            needValidateCode = true;
            configuration.getParser().getTokenListInBackground(id, editorObject, tokenListReceivedHandler);
        }
    }

    public void forceValidateCode() {
        if (!showLineNumbers) {
            return;
        }

        if (needUpdateTokenList) {
            needValidateCode = true;
            configuration.getParser().getTokenListInBackground(id, editorObject, tokenListReceivedHandler);
        } else {
            validateCode(this.tokenList);
        }
    }

    private void validateCode(List<? extends Token> tokenList) {
        if (showLineNumbers) {
            needValidateCode = false;

            // Updates list of code errors and error marks. Also updates the fqn of tokens within the tokenList
            if (tokenList == null || tokenList.isEmpty()) {
                // clear code error marks
                for (CodeLine lastCodeError : codeErrorList) {
                    clearErrorMark(lastCodeError.getLineNumber());
                }
                return;
            }

            List<CodeLine> newCodeErrorList = configuration.getCodeValidator().getCodeErrorList(tokenList);
            udpateErrorMarks(newCodeErrorList);
        }
    }

    private ClickHandlerTimer clickHandler = new ClickHandlerTimer();

    private class ClickHandlerTimer extends Timer {

        private int lineNumber;

        @Override
        public void run() {
            // test if this is line with code error
            if (CodeValidator.isExistedCodeError(lineNumber, codeErrorList)) {
                if (configuration.getCodeAssistant() != null) {
                    configuration.getCodeAssistant()
                                 .errorMarkClicked(CodeMirror.this, CodeValidator.getCodeErrorList(lineNumber, codeErrorList),
                                                   (getAbsoluteTop() + getCursorOffsetY(lineNumber) + codeErrorCorrectionPopupOffsetTop),
                                                   (getAbsoluteLeft() + LINE_NUMBERS_COLUMN_WIDTH + codeErrorCorrectionPopupOffsetLeft),
                                                   mimeType);
                }
            }

            try {
                // collect all problems on this line
                if (problems.containsKey(lineNumber)) {
                    List<Marker> list = problems.get(lineNumber);

                    fireEvent(new ProblemClickEvent(list.toArray(new Marker[list.size()])));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * @param lineNumber
         *         the lineNumber to set
         */
        public void setLineNumber(int lineNumber) {
            this.lineNumber = lineNumber;
        }

    }

    private void onLineNumberClick(int lineNumber) {
        clickHandler.setLineNumber(lineNumber);
        clickHandler.schedule(300);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getId() */
    @Override
    public String getId() {
        return id;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getText() */
    @Override
    public native String getText() /*-{
       var instance = this;
       var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       return editor.getCode();
    }-*/;

    @Override
    public void setFile(FileModel file) {
        this.file = file;
        FileContentLoader.getFileContent(file, new FileContentLoader.ContentCallback() {
            @Override
            public void onContentReceived(String content) {
                setText(content);
            }
        });
    }

    @Override
    public FileModel getFile() {
        return file;
    }

    public void setText(String text) {
        if (editorObject == null) {
            initialText = text;
            return;
        }

        setText(editorObject, text);
    }

    private native void setText(JavaScriptObject editor, String text) /*-{
       if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
           return;
       }

       if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::CHROME) {
           if (text === "") {
               text = "\n"; // fix error with initial cursor position and size (WBT-324)
           }
       }
       editor.setCode(text);
       editor.focus();
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#isCapable(org.exoplatform.ide.editor.client.api.EditorCapability) */
    @Override
    public boolean isCapable(EditorCapability capability) {
        switch (capability) {
            case AUTOCOMPLETION:
                return configuration.canBeAutocompleted();

            case OUTLINE:
                return configuration.canBeOutlined();

            case VALIDATION:
                return configuration.canBeValidated();

            case FIND_AND_REPLACE:
            case DELETE_LINES:
            case FORMAT_SOURCE:
            case SET_CURSOR_POSITION:
            case SHOW_LINE_NUMBERS:
            case COMMENT_SOURCE:
                return true;

            default:
                return false;
        }
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#formatSource() */
    @Override
    public void formatSource() {
        formatSource(getText(), editorObject);
    }

    private native void formatSource(String text, JavaScriptObject editor) /*-{
       if (text != ' ') {
           editor.reindent();
       }
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#showLineNumbers(boolean) */
    public void showLineNumbers(boolean showLineNumbers) {
        this.showLineNumbers = showLineNumbers;

        if (editorObject == null) {
            return;
        }

        showLineNumbersNative(showLineNumbers);
        if (showLineNumbers && configuration.canBeValidated()) {
            udpateErrorMarks(codeErrorList);
            needValidateCode = true;
        }
    }

    /** @param showLineNumbers */
    private native void showLineNumbersNative(boolean showLineNumbers) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       editor.setLineNumbers(showLineNumbers);
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#setFocus() */
    @Override
    public void setFocus() {
        setFocus(editorObject);
        try {            
            setCursorPosition(cursorPositionRow, cursorPositionCol);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private native void setFocus(JavaScriptObject editor) /*-{
       if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
           return;
       }
       editor.focus();
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#setCursorPosition(int, int) */
    @Override
    public void setCursorPosition(int row, int column) {
        cursorPositionRow = row;
        cursorPositionCol = column;
        goToPosition(editorObject, row, column);
    }

    private void fireEditorCursorActivityEvent(String editorId, int cursorRow, int cursorCol) {
        fireEvent(new EditorCursorActivityEvent(this, cursorRow, cursorCol));
    }

    private native void goToPosition(JavaScriptObject editor, int row, int column) /*-{
       try {
           if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)
               || typeof editor.win.select == 'undefined') {
               return;
           }
    
           if (column && !isNaN(Number(column)) && row && !isNaN(Number(row))) {
               if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::canGoToLine(I)(row)) {
                   editor.selectLines(editor.nthLine(row), column - 1);
                   this.@org.exoplatform.ide.editor.codemirror.CodeMirror::highlightLine(I)(row);
                   this.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorCursorActivityEvent(Ljava/lang/String;II)(this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getId()(), row, column);
               }
           }
       
       } catch (e) {
           //this.@org.exoplatform.ide.editor.codemirror.CodeMirror::trace(Ljava/lang/String;)(e.message);
           return;
       }
    }-*/;

    public native boolean canGoToLine(int lineNumber) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor == null) {
           return false;
       }

       return editor.nthLine(lineNumber) !== false;
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#deleteCurrentLine() */
    @Override
    public native void deleteCurrentLine() /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor == null) {
           return;
       }

       var currentLineNumber = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cursorPositionRow;
       var currentLine = editor.nthLine(currentLineNumber);

       if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE
           && this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getNumberOfLines()() == currentLineNumber) {
           // clear current line
           this.@org.exoplatform.ide.editor.codemirror.CodeMirror::clearLastLine()();
       }
       else {
           editor.removeLine(currentLine);
       }

       currentLineNumber = editor.lineNumber(currentLine);
       this.@org.exoplatform.ide.editor.codemirror.CodeMirror::setCursorPosition(II)(currentLineNumber, 1);
    }-*/;

    /** Correct clear the last line of content that the line break is being remained */
    private native void clearLastLine() /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor == null) {
           return;
       }

       var content = editor.getCode();
       var lastLineHandler = editor.lastLine();
       if (content.charAt(content.length - 1) == "\n") {
           editor.setLineContent(lastLineHandler, "");
       }
       else {
           editor.setLineContent(lastLineHandler, "\n");
       }
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#hasUndoChanges() */
    @Override
    public boolean hasUndoChanges() {
        return hasUndoChanges(editorObject);
    }

    private native boolean hasUndoChanges(JavaScriptObject editor) /*-{
       if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
           return false;
       }

       return editor.historySize().undo > 0;
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#undo() */
    @Override
    public void undo() {
        undo(editorObject);
    }

    private native void undo(JavaScriptObject editor) /*-{
       editor.undo();
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#hasRedoChanges() */
    @Override
    public boolean hasRedoChanges() {
        return hasRedoChanges(editorObject);
    }

    private native boolean hasRedoChanges(JavaScriptObject editor) /*-{
       if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
           return false;
       }

       return editor.historySize().redo > 0;
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#redo() */
    public native void redo() /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       editor.redo();
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#isReadOnly() */
    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public int getCursorRow() {
        return cursorPositionRow;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getCursorRow() */
    public native int getNativeCursorRow() /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)
           || typeof editor.win.select == 'undefined')
           return 1;

       switch (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
           case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
               if (editor.editor.selectionSnapshot) {
                   return editor
                       .lineNumber(editor.editor.selectionSnapshot.from.node);
               } else {
                   return 1;
               }

           default:
               var cursor = editor.cursorPosition(true);
               return editor.lineNumber(cursor.line) || 1;
       }
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#getCursorColumn() */
    @Override
    public native int getCursorColumn() /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)
           || typeof editor.win.select == 'undefined')
           return 1;

       switch (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
           case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
               if (editor.editor.selectionSnapshot) {
                   return editor.editor.selectionSnapshot.from.offset + 1;
               } else {
                   return 1;
               }

           default:
               var cursor = editor.cursorPosition(true);
               return (cursor.character + 1) || 1;
       }
    }-*/;

    /**
     * @param cursor
     *         object - the argument of native codemirror cursorActivity event
     * @return editor.lineNumber(cursor) - 1
     */
    private native int getCursorActivityRow(JavaScriptObject cursor, int cursorCol) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)
           || typeof editor.win.select == 'undefined' || !cursor) {
           return 1;
       }

       // hack of position of cursor row at first line and first column
       if (editor.nthLine(editor.lineNumber(cursor)) == cursor) {
           if (cursorCol == 1) {
               return editor.lineNumber(cursor);
           }
       }

       return editor.lineNumber(cursor) - 1;
    }-*/;


    public List<? extends Token> getTokenList() {
        return (List<TokenBeenImpl>)configuration.getParser().getTokenList(id, editorObject);
    }

    public void getTokenListInBackground() {
        if (needUpdateTokenList) {
            configuration.getParser().getTokenListInBackground(id, editorObject, tokenListReceivedHandler);
        } else {
            tokenListReceivedHandler.onEditorTokenListPrepared(new EditorTokenListPreparedEvent(id, this.tokenList));
        }
    }

    private EditorTokenListPreparedHandler tokenListPreparedHandler;

    public void getTokenList(EditorTokenListPreparedHandler tokenListPreparedHandler) {
        this.tokenListPreparedHandler = tokenListPreparedHandler;
        getTokenListInBackground();
    }

    private EditorTokenListPreparedHandler tokenListReceivedHandler = new EditorTokenListPreparedHandler() {
        @Override
        public void onEditorTokenListPrepared(EditorTokenListPreparedEvent event) {
            if (!id.equals(event.getEditorId())) {
                return;
            }

            if (needUpdateTokenList) {
                needUpdateTokenList = false;
                tokenList = (List<TokenBeenImpl>)event.getTokenList();
            }

            if (needValidateCode) {
                validateCode(tokenList);
            }

            if (tokenListPreparedHandler != null) {
                tokenListPreparedHandler.onEditorTokenListPrepared(event);
            }

        }
    };

    /** @see org.exoplatform.ide.editor.client.api.Editor#replaceTextAtCurrentLine(java.lang.String, int) */
    @Override
    public native void replaceTextAtCurrentLine(String line, int cursorPosition) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor != null && line) {
           var currentLine = editor.cursorPosition(true).line;
           editor.setLineContent(currentLine, line);

           // set cursor at the cursor position
           editor.selectLines(currentLine, cursorPosition);
       }
    }-*/;

    /**
     * @param newText
     * @param lineNumber
     *         started from 1
     */
    private native void insertIntoLine(String newText, int lineNumber) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor != null && newText) {
           var handler = editor.nthLine(lineNumber);
           editor.insertIntoLine(handler, 0, newText);
       }
    }-*/;

    private FlowPanel getLineHighlighter() {
        FlowPanel highlighter = new FlowPanel();
        highlighter.setStyleName("CodeMirror-line-highlighter");
        return highlighter;
    }

    @Override
    public void insertImportStatement(String fqn) {
        if (configuration.canBeValidated()) {
            if (needUpdateTokenList) {
                needUpdateTokenList = false;
                this.tokenList = (List<TokenBeenImpl>)getTokenList();
            }

            CodeLine importStatement = configuration.getCodeValidator().getImportStatement(this.tokenList, fqn);
            if (importStatement != null) {
                insertIntoLine(importStatement.getLineContent(), importStatement.getLineNumber());
            }
        }
    }

    private native String getLineContent(JavaScriptObject editor, int lineNumber) /*-{
       var handler = editor.nthLine(lineNumber);
       return editor.lineContent(handler);
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#getLineText(int) */
    @Override
    public String getLineText(int line) {
        return getLineContent(editorObject, line);
    }

    /**
     * Check if CodeMirror editor instance consists of neccessery objects.
     *
     * @param editor
     * @return
     */
    private native boolean checkGenericCodeMirrorObject(JavaScriptObject editor) /*-{
       return (editor != null) && (typeof editor != 'undefined')
           && (typeof editor.editor != 'undefined');
    }-*/;


    /*********************************************************************************
     *
     * Marking Support
     *
     * public class {@link CodeMirror} implements Markable
     *
     ********************************************************************************/

    /** List of {@link Problem} */
    private Map<Integer, List<Marker>> problems = new HashMap<Integer, List<Marker>>();

    /** Visible notification. */
    private NotificationWidget activeNotification;

    private AbsolutePanel absPanel;

    private OverviewRuler overviewRuler;

    /** @see org.exoplatform.ide.editor.client.marking.Markable#markProblem(org.exoplatform.ide.editor.client.marking.Marker) */
    @Override
    public void markProblem(Marker problem) {
        if (!problems.containsKey(problem.getLineNumber()))
            problems.put(problem.getLineNumber(), new ArrayList<Marker>());
        problems.get(problem.getLineNumber()).add(problem);

        StringBuilder message = new StringBuilder();
        List<Marker> problemList = problems.get(problem.getLineNumber());
        boolean hasError = fillMessages(problemList, message);

        String markStyle = getStyleForLine(problemList, hasError);

        markProblemmeLine(problem.getLineNumber(), message.toString(), markStyle);
        if (overviewRuler != null) {
            overviewRuler.addProblem(problem, message.toString());
        }
    }

    /**
     * @param markerList
     * @param hasError
     * @return
     */
    private String getStyleForLine(List<Marker> markerList, boolean hasError) {
        String markStyle = null;
        if (hasError) {
            //markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkError();
            markStyle = CodeMirrorStyles.CODE_MARK_ERROR;
        } else {
            //markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkBreakpoint();
            markStyle = CodeMirrorStyles.CODE_MARK_BREAKPOINT;
            for (Marker p : markerList) {
                if (p.isWarning()) {
                    //markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkWarning();
                    markStyle = CodeMirrorStyles.CODE_MARK_WARNING;
                }
                if (p.isCurrentBreakPoint()) {
                    //markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkBreakpointCurrent();
                    markStyle = CodeMirrorStyles.CODE_MARK_BREAKPOINT_CURRENT;
                    break;
                }
            }
        }
        return markStyle;
    }

    private boolean fillMessages(List<Marker> markers, StringBuilder message) {
        boolean hasError = false;
        List<String> messages = new ArrayList<String>();

        for (Marker p : markers) {
            messages.add(p.getMessage());
            if (!hasError && p.isError()) {
                hasError = true;
            }
        }

        if (messages.size() == 1) {
            message.append(markers.get(0).getMessage());
        } else {
            message.append("Multiple markers at this line<br>");
            for (String m : messages) {
                message.append("&nbsp;&nbsp;&nbsp;-&nbsp;").append(m).append("<br>");
            }
        }

        return hasError;
    }

    /** @see org.exoplatform.ide.editor.client.marking.Markable#unmarkAllProblems() */
    @Override
    public void unmarkAllProblems() {
        removeNotification();

        List<Marker> breakpoins = new ArrayList<Marker>();
        for (Integer key : problems.keySet()) {
            unmarkNative(key);
            List<Marker> list = problems.get(key);
            Marker breakpoint = getBreakpoint(list);
            if (breakpoint != null)
                breakpoins.add(breakpoint);
            list.clear();
        }

        if (overviewRuler != null)
            overviewRuler.clearProblems();
        for (Marker p : breakpoins)
            markProblem(p);
    }

    private Marker getBreakpoint(List<Marker> problems) {
        for (Marker p : problems) {
            if (p.isBreakpoint()) {
                return p;
            }
        }
        return null;
    }

    /**
     *
     */
    private void removeNotification() {
        if (activeNotification != null) {
            activeNotification.destroy();
            activeNotification = null;
        }
    }

    /** @see org.exoplatform.ide.editor.client.marking.Markable#unmarkProblem(org.exoplatform.ide.editor.client.marking.Marker) */
    @Override
    public void unmarkProblem(Marker problem) {
        removeNotification();
        if (problems.containsKey(problem.getLineNumber())) {
            List<Marker> list = problems.get(problem.getLineNumber());
            list.remove(problem);
            unmarkNative(problem.getLineNumber());
            if (list.isEmpty())
                return;
            StringBuilder message = new StringBuilder();
            boolean hasError = fillMessages(list, message);
            String markStyle = getStyleForLine(list, hasError);
            markProblemmeLine(problem.getLineNumber(), message.toString(), markStyle);
        }
    }

    /** Removes style from mark element ( is line number element ) */
    private native void unmarkNative(int lineNumber) /*-{
        try {
            var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
            editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].removeAttribute("class");
            editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].removeAttribute("title");
            editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseover = null;
            editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseout = null;
        } catch (e) {
            //this.@org.exoplatform.ide.editor.codemirror.CodeMirror::trace(Ljava/lang/String;)(e.message);
        } 
    }-*/;

    @Override
    public HandlerRegistration addProblemClickHandler(ProblemClickHandler handler) {
        return addHandler(handler, ProblemClickEvent.TYPE);
    }

    @Override
    public HandlerRegistration addLineNumberDoubleClickHandler(EditorLineNumberDoubleClickHandler handler) {
        return addHandler(handler, EditorLineNumberDoubleClickEvent.TYPE);
    }

    /**
     * Handler mouse over event on marker.
     *
     * @param jso
     */
    private void markerMouseOver(JavaScriptObject jso) {
        com.google.gwt.user.client.Event event = jso.cast();
        Element el = event.getEventTarget().cast();

        if (activeNotification != null) {
            activeNotification.destroy();
        }

        activeNotification = new NotificationWidget(el, getAbsoluteLeft(), getAbsoluteTop());
    }

    /**
     * Handler mouse over event on marker.
     *
     * @param jso
     *         line number element
     */
    private void markerMouseOut(JavaScriptObject jso) {
        if (activeNotification != null) {
            activeNotification.destroy();
        }
    }

    /**
     * Marks line as problem.
     *
     * @param lineNumber
     * @param errorSummary
     */
    public void setErrorMark(int lineNumber, String errorSummary) {
        markProblemmeLine(lineNumber, errorSummary, configuration.getCodeErrorMarkStyle());
    }

    void udpateErrorMarks(List<CodeLine> newCodeErrorList) {
        for (CodeLine lastCodeError : codeErrorList) {
            clearErrorMark(lastCodeError.getLineNumber());
        }

        List<CodeLine> lineCodeErrorList;
        for (CodeLine newCodeError : newCodeErrorList) {
            // TODO supress repetitevly setting error mark if there are several errors in the one line
            lineCodeErrorList = CodeValidator.getCodeErrorList(newCodeError.getLineNumber(), newCodeErrorList);
            markProblemmeLine(newCodeError.getLineNumber(), CodeValidator.getErrorSummary(lineCodeErrorList),
                              configuration.getCodeErrorMarkStyle());
        }

        configuration.getParser().getTokenListInBackground(id, editorObject, tokenListReceivedHandler);
        codeErrorList = newCodeErrorList;
    }

    /**
     * Marks line as problem.
     *
     * @param lineNumber
     *         line number, starts at 1
     * @param errorSummary
     * @param markStyle
     */
    public native void markProblemmeLine(int lineNumber, String errorSummary, String markStyle) /*-{
       try {
           var instance = this;
           var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
    
           var over = function (jso) {
               instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::markerMouseOver(Lcom/google/gwt/core/client/JavaScriptObject;)(jso);
           };
    
           var out = function (jso) {
               instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::markerMouseOut(Lcom/google/gwt/core/client/JavaScriptObject;)(jso);
           };
    
           editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].setAttribute("class", markStyle);
           editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].setAttribute("title", errorSummary);
           editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseover = over;
           editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseout = out;
       } catch (e) {
       }
    }-*/;

    /**
     * Clear error mark from lineNumbers field
     *
     * @param lineNumber
     *         starting from 1
     */
    public native void clearErrorMark(int lineNumber) /*-{
        try {
            var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
                if (editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]) {
                    editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].removeAttribute('class');
                    editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].removeAttribute('title');
                }
        } catch (e) {
        }        
    }-*/;

    @Override
    public IDocument getDocument() {
        if (needUpdateDocument) {
            needUpdateDocument = false;
            document.removeDocumentListener(this);
            document.set(getText());
            document.addDocumentListener(this);
        }
        return document;
    }

    @Override
    public native void setLineText(int line, String text) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       var handle = editor.nthLine(line);
       editor.setLineContent(handle, text);
    }-*/;

    private native void deleteLine(JavaScriptObject editor, int line) /*-{
       var lineHandler = editor.nthLine(line);

       if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE
           && this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getNumberOfLines()() == line) {
           // clear current line
           this.@org.exoplatform.ide.editor.codemirror.CodeMirror::clearLastLine()();
       } else {
           editor.removeLine(lineHandler);
       }
    }-*/;


    /** {@inheritDoc} */
    @Override
    public void documentAboutToBeChanged(DocumentEvent event) {
        // TODO Auto-generated method stub
    }


    /** {@inheritDoc} */
    @Override
    public void documentChanged(DocumentEvent event) {
        try {
            IDocument document = event.getDocument();
            int lineNumber = document.getLineOfOffset(event.getOffset());
            int col = event.getOffset() - document.getLineOffset(lineNumber);
            // lineNumber start from 0, but editor store lines starting form 1
            lineNumber++;
            StringBuilder b = new StringBuilder(getLineText(lineNumber));
            int length = col + event.getLength();
            int nextLine = lineNumber + 1;
            while (length > b.length()) {
                b.append(getLineText(nextLine));
                deleteLine(editorObject, nextLine);
                // symbol '\n' not present in line content
                length--;
            }
            b.replace(col, length, event.getText());
            setLineText(lineNumber, b.toString());
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public native SelectionRange getSelectionRange() /*-{
       try {
           var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
           if (editor == null) {
               return null;
           }
    
           var start = editor.cursorPosition(true);
           var startLine = editor.lineNumber(start.line);
           var end = editor.cursorPosition(false);
           var endLine = editor.lineNumber(end.line);
           return @org.exoplatform.ide.editor.client.api.SelectionRange::new(IIII)(startLine, start.character, endLine, end.character);
       } catch (e) {
           //this.@org.exoplatform.ide.editor.codemirror.CodeMirror::trace(Ljava/lang/String;)(e.message);
           return null;
       }
    }-*/;

    private native void addContextMenuListener() /*-{
       var instance = this;
       var frame = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::frameElement;

       var contextMenuListener = function (e) {
           if (!e)
               var e = $wnd.event;
           //var e = frame.contentWindow.event; //TODO
           instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onContextMenu(Lcom/google/gwt/dom/client/NativeEvent;)(e);
       };

       var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor) {
           switch (instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
               case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
                   if (editor.win.document.body.attachEvent) {
                       editor.win.document.body.attachEvent("oncontextmenu",
                           contextMenuListener);
                   }
                   break;
               default:
                   if (editor.win.addEventListener) {
                       editor.win.addEventHandler(editor.win, "contextmenu",
                           contextMenuListener, true);
                   }
           }
       }
    }-*/;

    private void onContextMenu(NativeEvent event) {
        event.stopPropagation();
        event.preventDefault();
        int x = event.getClientX() + getAbsoluteLeft() + LINE_NUMBERS_COLUMN_WIDTH;
        int y = event.getClientY() + getAbsoluteTop();
        fireEvent(new EditorContextMenuEvent(this, x, y));
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#selectAll() */
    @Override
    public void selectAll() {
        executeCommand("selectAll");
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#cut() */
    @Override
    public void cut() {
        executeCommand("cut");
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#copy() */
    @Override
    public void copy() {
        executeCommand("copy");
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#paste() */
    @Override
    public void paste() {
        executeCommand("paste");
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#delete() */
    @Override
    public void delete() {
        executeCommand("delete");
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

    private native void executeCommand(String command) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       var frame = editor.frame;
       if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser == @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE) {
           frame.contentWindow.document.execCommand(command, false, null);
       }
       else {
           frame.contentDocument.execCommand(command, false, null);
       }
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#getNumberOfLines() */
    @Override
    public native int getNumberOfLines() /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       return editor.lineNumber(editor.lastLine());
    }-*/;

    @Override
    public native void selectRange(int startLine, int startOffset, int endLine, int endOffset) /*-{
       try {
           var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
           var startHandle = editor.nthLine(startLine);
           var endHandle = editor.nthLine(endLine);
           editor.selectLines(startHandle, startOffset, endHandle, endOffset)
       }
       catch (e) {
           this.@org.exoplatform.ide.editor.codemirror.CodeMirror::trace(Ljava/lang/String;)(e.message);
       }
    }-*/;

    private void trace(String message) {
        System.out.println("CodeMirror: " + message);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addLineNumberContextMenuHandler(EditorLineNumberContextMenuHandler handler) {
        return addHandler(handler, EditorLineNumberContextMenuEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#setReadOnly(boolean) */
    @Override
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getName() */
    @Override
    public String getName() {
        return "Source";
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getCursorOffsetLeft() */
    @Override
    public int getCursorOffsetLeft() {
        return 0;
    }

    /** @see org.exoplatform.ide.editor.client.api.Editor#getCursorOffsetTop() */
    @Override
    public int getCursorOffsetTop() {
        return 0;
    }


    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addContentChangedHandler(EditorContentChangedHandler handler) {
        return addHandler(handler, EditorContentChangedEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addContextMenuHandler(EditorContextMenuHandler handler) {
        return addHandler(handler, EditorContextMenuEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addCursorActivityHandler(EditorCursorActivityHandler handler) {
        return addHandler(handler, EditorCursorActivityEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addFocusReceivedHandler(EditorFocusReceivedHandler handler) {
        return addHandler(handler, EditorFocusReceivedEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addHotKeyPressedHandler(EditorHotKeyPressedHandler handler) {
        return addHandler(handler, EditorHotKeyPressedEvent.TYPE);
    }

    /** {@inheritDoc} */
    @Override
    public HandlerRegistration addInitializedHandler(EditorInitializedHandler handler) {
        return addHandler(handler, EditorInitializedEvent.TYPE);
    }

    /** @see org.exoplatform.ide.editor.client.marking.Markable#addProblems(org.exoplatform.ide.editor.client.marking.Marker[]) */
    @Override
    public void addProblems(Marker[] problems) {
        for (Marker m : problems)
            markProblem(m);
    }

    /** {@inheritDoc} */
    @Override
    public void search(final String query, final boolean caseSensitive, final SearchCompleteCallback searchCompleteCallback) {
        if (searchCompleteCallback == null) {
            return;
        }

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                boolean found = searchNative(query, caseSensitive);
                searchCompleteCallback.onSearchComplete(found);
            }
        });
    }

    private native boolean searchNative(String query, boolean caseSensitive) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor == null) {
           return;
       }

       var found = false;
       var cursor = editor.getSearchCursor(query, true, !caseSensitive); // getSearchCursor(string, atCursor, caseFold) -> cursor
       if (found = cursor.findNext()) {
           cursor.select();
       }

       return found;
    }-*/;

    /** @see org.exoplatform.ide.editor.client.api.Editor#replaceMatch(java.lang.String) */
    @Override
    public native void replaceMatch(String replacement) /*-{
       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
       if (editor == null) {
           return;
       }

       editor.replaceSelection(replacement);
    }-*/;

}
