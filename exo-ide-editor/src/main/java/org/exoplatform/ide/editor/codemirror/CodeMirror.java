/*
 * Copyright (C) 2011 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.editor.codemirror;

import com.google.gwt.user.client.ui.AbsolutePanel;

import com.google.gwt.dom.client.Style.Unit;

import com.google.gwt.user.client.ui.DockLayoutPanel;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantEvent;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.event.EditorSaveContentEvent;
import org.exoplatform.ide.editor.api.event.EditorTokenListPreparedEvent;
import org.exoplatform.ide.editor.api.event.EditorTokenListPreparedHandler;
import org.exoplatform.ide.editor.keys.KeyHandler;
import org.exoplatform.ide.editor.keys.KeyManager;
import org.exoplatform.ide.editor.notification.Notification;
import org.exoplatform.ide.editor.notification.OverviewRuler;
import org.exoplatform.ide.editor.problem.LineNumberDoubleClickEvent;
import org.exoplatform.ide.editor.problem.LineNumberDoubleClickHandler;
import org.exoplatform.ide.editor.problem.Markable;
import org.exoplatform.ide.editor.problem.Problem;
import org.exoplatform.ide.editor.problem.ProblemClickEvent;
import org.exoplatform.ide.editor.problem.ProblemClickHandler;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.Document;
import org.exoplatform.ide.editor.text.DocumentEvent;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IDocumentListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeMirror Feb 9, 2011 4:58:14 PM $
 * 
 */
public class CodeMirror extends Editor implements EditorTokenListPreparedHandler, Markable, IDocumentListener,
   KeyManager
{

   protected String editorId;

   protected TextArea textArea;

   protected JavaScriptObject editorObject;

   private FlowPanel lineHighlighter;

   private final Browser currentBrowser = BrowserResolver.CURRENT_BROWSER;

   private boolean needUpdateTokenList = true; // update token list only after the "initCallback" handler has been called

   private boolean needValidateCode = false;

   private boolean showLineNumbers = true;

   private List<TokenBeenImpl> tokenList;

   private int lineHeight = 16; // size of line in the CodeMirror in px

   private CodeMirrorConfiguration configuration;

   private int lineNumberFieldWidth = 48; // width of left field with line numbers

   private static int characterWidth = 8; // width of character in the CodeMirror in px

   private static int firstCharacterOffsetLeft = 11; // left offset of character of the line in px

   private static int codeErrorCorrectionPopupOffsetLeft = 6; // top offset of character of the line in px

   private static int codeErrorCorrectionPopupOffsetTop = 22; // top offset of character of the line in px

   private CodeAssistant codeAssistant;

   private String genericMimeType; // type of document itself

   private int cursorPositionCol = 1;

   private int cursorPositionRow = 1;

   protected List<CodeLine> codeErrorList = new ArrayList<CodeLine>();

   private IDocument document;

   private boolean needUpdateDocument = false;

   /**
    * @param file
    * @param params
    * @param eventBus
    */
   public CodeMirror(String content, HashMap<String, Object> params, HandlerManager eventBus)
   {
      super(content, params, eventBus);
      this.editorId = "CodeMirror - " + String.valueOf(this.hashCode());

      if (params == null)
      {
         params = new HashMap<String, Object>();
      }

      DockLayoutPanel doc = new DockLayoutPanel(Unit.PX);
      doc.setSize("100%", "100%");
      add(doc);
      if (params.get(EditorParameters.IS_SHOW_OVERVIEW_PANEL) != null
         && (Boolean)params.get(EditorParameters.IS_SHOW_OVERVIEW_PANEL) == Boolean.TRUE)
      {
         overviewRuler = new OverviewRuler(this);
         doc.addEast(overviewRuler, 13);
      }
      absPanel = new AbsolutePanel();
      doc.add(absPanel);
      textArea = new TextArea();
      DOM.setElementAttribute(textArea.getElement(), "id", getEditorId());
      absPanel.add(textArea);

      lineHighlighter = getLineHighlighter();
      absPanel.add(lineHighlighter);
      absPanel.setWidgetPosition(lineHighlighter, 0, 5);

      if (params.get(EditorParameters.CONFIGURATION) != null)
         configuration = (CodeMirrorConfiguration)params.get(EditorParameters.CONFIGURATION);
      else
         configuration = new CodeMirrorConfiguration();

      genericMimeType = (String)params.get(EditorParameters.MIME_TYPE);

      codeAssistant = configuration.getCodeAssistant();

      // validate code at start after the "initCallback" handler has been called
      if (configuration.canBeValidated())
      {
         needValidateCode = true;
      }

      eventBus.addHandler(EditorTokenListPreparedEvent.TYPE, this);
      document = new Document(content);
      document.addDocumentListener(this);
   }

   /**
    * @see com.google.gwt.user.client.ui.Panel#onLoad()
    */
   @Override
   protected void onLoad()
   {
      super.onLoad();

      String width = "";
      String height = "100%";
      boolean readOnly = (Boolean)params.get(EditorParameters.IS_READ_ONLY);
      int continuousScanning = configuration.getContinuousScanning();
      boolean textWrapping = configuration.isTextWrapping();

      showLineNumbers = (Boolean)params.get(EditorParameters.IS_SHOW_LINE_NUMER);
      String parserNames = configuration.getCodeParsers();
      String styleURLs = configuration.getCodeStyles();

      String javaScriptDirectory = configuration.getJsDirectory();

      editorObject =
         initCodeMirror(editorId, width, height, readOnly, continuousScanning, textWrapping, showLineNumbers,
            styleURLs, parserNames, javaScriptDirectory);

   }

   private native JavaScriptObject initCodeMirror(String id, String w, String h, boolean readOnly, int cs, boolean tr,
      boolean lineNumbers, String styleURLs, String parserNames, String jsDirectory) /*-{
		var instance = this;
		var changeFunction = function() {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onContentChanged()();
		};

		var cursorActivity = function(cursor) {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onCursorActivity(Lcom/google/gwt/core/client/JavaScriptObject;)(cursor);
		};

		var onLineNumberClick = function(lineNumber) {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberClick(I)(lineNumber);
		};

		var onLineNumberDoubleClick = function(lineNumber) {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberDoubleClick(I)(lineNumber);
		}

		var initCallback = function() {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onInitialized()();

			// set hot keys click listener
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::setHotKeysClickListener()();

			// set focusReceived listener
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::setFocusReceivedListener()();
		};

		var activeTokensFunction = function() {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::needUpdateTokenList = true;
		};

		var editor = $wnd.CodeMirror.fromTextArea(id, {
			width : w,
			height : h,
			parserfile : eval(parserNames),
			stylesheet : eval(styleURLs),
			path : jsDirectory,
			continuousScanning : cs || false,
			undoDelay : 50, // decrease delay before calling 'onChange' callback
			lineNumbers : lineNumbers,
			readOnly : readOnly,
			textWrapping : tr,
			tabMode : "spaces",
			content : "", // to fix bug with blocked deleting function of CodeMirror just after opening file [WBT-223]
			onChange : changeFunction,
			//           saveFunction: saveFunction,
			reindentOnLoad : false, // to fix problem with getting token list after the loading content
			onCursorActivity : cursorActivity,
			onLineNumberClick : onLineNumberClick,
			onLineNumberDoubleClick : onLineNumberDoubleClick,
			onLoad : initCallback,
			autoMatchParens : true,

			// Take the token before the cursor. If it contains a character in '()[]{}', search for the matching paren/brace/bracket, and
			// highlight them in green for a moment, or red if no proper match was found.
			markParen : function(node, ok) {
				node.id = ok ? "parenCorrect" : "parenIncorrect";
			},
			unmarkParen : function(node) {
				node.id = null;
			},

			// to update outline panel after the new line has being highlighted
			activeTokens : activeTokensFunction
		});

		return editor;
   }-*/;

   private Timer codeValidateTimer = new Timer()
   {
      @Override
      public void run()
      {
         validateCode();
      }
   };

   private void onLineNumberDoubleClick(int lineNumber)
   {
      clickHandler.cancel();
      fireEvent(new LineNumberDoubleClickEvent(lineNumber));
      if (activeNotification != null)
         activeNotification.update();
   }

   private void onInitialized()
   {
      this.needUpdateTokenList = true; // update token list after the document had been loaded and reindented

      setText(content);

      eventBus.fireEvent(new EditorInitializedEvent(editorId));

      // turn on code validation time
      if (configuration.canBeValidated())
      {
         this.codeValidateTimer.scheduleRepeating(2000);
      }
   }

   private void onContentChanged()
   {
      this.needUpdateTokenList = true;
      needUpdateDocument = true;

      eventBus.fireEvent(new EditorContentChangedEvent(getEditorId()));
   }

   private void onCursorActivity(JavaScriptObject cursor)
   {
      cursorPositionCol = getCursorCol();

      if (BrowserResolver.CURRENT_BROWSER == Browser.IE)
      {
         if (getLastLineNumber() == 1)
         {
            cursorPositionRow = 1;
         }
         else
         {
            cursorPositionRow = getCursorActivityRow(cursor, cursorPositionCol);
         }
      }
      else
      {
         cursorPositionRow = getNativeCursorRow();
      }

      // highlight current line
      highlightLine(cursorPositionRow);

      eventBus.fireEvent(new EditorCursorActivityEvent(editorId, cursorPositionRow, cursorPositionCol));
   }

   private void highlightLine(int lineNumber)
   {
      if (this.currentBrowser == Browser.IE)
      {
         fixCodeMirrorIframeTransparencyInIE();
      }

      absPanel.setWidgetPosition(lineHighlighter, 0, 5 + getCursorOffsetY(lineNumber));
   }

   /**
    * set codemirror iframe transparency for the IE
    */
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
    * @param currentLine if equals 0 or null, then will get current line position
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

		cursorOffsetY = (currentLine - 1)
				* this.@org.exoplatform.ide.editor.codemirror.CodeMirror::lineHeight;
		cursorOffsetY -= verticalScrollBarPosition;

		return cursorOffsetY;
   }-*/;

   /**
    * Set listeners of hot keys clicking. Listen "Ctrl+S" key pressing if hotKeyList is null. Listen Ctrl+Space in any case
    */
   private native void setHotKeysClickListener() /*-{
                                                 var instance = this;

                                                 var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;      
                                                 if (editor) {
                                                 instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::addScrollAndResizeListener(Lorg/exoplatform/ide/editor/codemirror/CodeMirror;)(instance);

                                                 editor.grabKeys(
                                                 function (event) {},

                                                 function (keyCode, event){
                                                 // filter key pressed without ctrl or alt or event type not "keypress"                  
                                                 if (! (event.ctrlKey || event.altKey) || (event.type != "keydown")) 
                                                 return instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::handleKey(Lcom/google/gwt/user/client/Event;)(event);

                                                 var fileConfiguration = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::configuration;
                                                 if (fileConfiguration.@org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration::canBeAutocompleted()())
                                                 {
                                                 // check if this is MacOS
                                                 if (@org.exoplatform.gwtframework.commons.util.BrowserResolver::isMacOs()())
                                                 {
                                                 // check if this is MacOS and the Alt+Space
                                                 if ( keyCode == 32 && event.altKey ) {
                                                 event.stop();
                                                 instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::ctrlSpaceClickHandler()();                                             
                                                 return true;
                                                 }
                                                 }
                                                 else
                                                 {
                                                 // check if this is non-MacOS and the Ctrl+Space
                                                 if ( keyCode == 32 && event.ctrlKey ) {
                                                 event.stop();
                                                 instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::ctrlSpaceClickHandler()();                                             
                                                 return true;
                                                 }
                                                 }
                                                 }   

                                                 //       for Ctrl+l firstly called <event.keyCode = 76, event.charCode = 0>, then <event.keyCode = 0, event.charCode = 108>  
                                                 //       for Ctrl+L firstly called <event.keyCode = 76, event.charCode = 0>, then <event.keyCode = 0, event.charCode = 76>            
                                                 var keyPressed = "";
                                                 event.ctrlKey ? keyPressed += "Ctrl+" : keyPressed += "";
                                                 event.altKey ? keyPressed += "Alt+" : keyPressed += "";
                                                 keyPressed += keyCode;              

                                                 // find similar key ammong the hotKeyList
                                                 var hotKeyList = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::getHotKeyList()();                  

                                                 // listen Ctrl+S key pressing if hotKeyList is null
                                                 if (hotKeyList === null) { 
                                                 if (keyPressed == "Ctrl+" + "S".charCodeAt(0)) {
                                                 event.stop();
                                                 instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onSaveContent()();
                                                 return true;                        
                                                 } else {
                                                 return false;
                                                 }                    
                                                 }

                                                 for (var i = 0; i < hotKeyList.@java.util.List::size()(); i++) {
                                                 var currentHotKey = hotKeyList.@java.util.List::get(I)(i); 
                                                 if (currentHotKey == keyPressed) {
                                                 event.stop();

                                                 // fire EditorHotKeyCalledEvent
                                                 var editorHotKeyCalledEventInstance = @org.exoplatform.ide.editor.api.event.EditorHotKeyCalledEvent::new(Ljava/lang/String;)(
                                                 currentHotKey
                                                 );
                                                 var eventBus = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::getEventBus()();
                                                 eventBus.@com.google.gwt.event.shared.HandlerManager::fireEvent(Lcom/google/gwt/event/shared/GwtEvent;)(editorHotKeyCalledEventInstance);

                                                 return true;                
                                                 }
                                                 }
                                                 
                                                 // fix bug with pasting text into emptied document in the IE (IDE-1142)
                                                 if (editor.getCode() == "")
                                                 {
                                                 editor.setCode("");
                                                 }

                                                 return false;
                                                 });
                                                 }
                                                 }-*/;

   @SuppressWarnings("unchecked")
   private List<String> getHotKeyList()
   {
      return (List<String>)params.get(EditorParameters.HOT_KEY_LIST);
   }

   public boolean handleKey(Event event)
   {
      if (Event.ONKEYDOWN != event.getTypeInt() || handler == null)
         return false;

      return handler.handleEvent(event);
   }

   /**
    * Set listener to call this.onCtrlSpaceClick() method just after the clicking on "Ctrl + Space" keys
    */
   public native void ctrlSpaceClickHandler() /*-{
                                              var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
                                              if (editor == null) return;   


                                              var cursor = editor.cursorPosition(true);     
                                              var lineContent = editor.lineContent(cursor.line);


                                              // get fqn of current node
                                              if (editor.nextLine(cursor.line) != null 
                                              && editor.nextLine(cursor.line).previousSibling)
                                              {
                                              var currentNode = editor.nextLine(cursor.line).previousSibling;
                                              }

                                              this.@org.exoplatform.ide.editor.codemirror.CodeMirror::callAutocompleteHandler(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(
                                              lineContent,
                                              currentNode
                                              );

                                              }-*/;

   private void callAutocompleteHandler(String lineContent, JavaScriptObject currentNode)
   {
      if (genericMimeType.equals(MimeType.APPLICATION_JAVA))
      {
         eventBus.fireEvent(new RunCodeAssistantEvent());
         return;
      }
      int cursorRow = cursorPositionRow;

      // calculate cursorOffsetY
      int cursorOffsetY = getCursorOffsetY();

      // calculate cursorOffsetX
      int cursorOffsetX = getCursorOffsetX();

      if (needUpdateTokenList)
      {
         needUpdateTokenList = false;
         this.tokenList = (List<TokenBeenImpl>)getTokenList();

         // to update token's FQNs
         if (configuration.canBeValidated())
         {
            needValidateCode = false;
            validateCode(this.tokenList);
         }
      }

      Token tokenBeforeCursor = getTokenBeforeCursor(this.tokenList, currentNode, cursorRow, cursorPositionCol);

      List<? extends Token> selectedTokenList = this.tokenList;

      // read mimeType
      String currentLineMimeType = getCurrentLineMimeType();
      if (configuration.canHaveSeveralMimeTypes() && !genericMimeType.equals(currentLineMimeType))
      {
         selectedTokenList =
            (List<TokenBeenImpl>)CodeValidator.extractCode((List<TokenBeenImpl>)this.tokenList,
               new LinkedList<TokenBeenImpl>(), currentLineMimeType);
      }

      codeAssistant.autocompleteCalled(this, cursorOffsetX, cursorOffsetY, (List<Token>)selectedTokenList,
         currentLineMimeType, tokenBeforeCursor);
   }

   /**
    * @param cursorCol
    * @return
    */
   public int getCursorOffsetX()
   {
      int cursorOffsetX = (cursorPositionCol - 2) * characterWidth + getAbsoluteLeft() + firstCharacterOffsetLeft; // 8px per
                                                                                                                   // symbol
      if (this.showLineNumbers)
      {
         cursorOffsetX += this.lineNumberFieldWidth;
      }
      return cursorOffsetX;
   }

   /**
    * @return
    */
   public int getCursorOffsetY()
   {
      return getAbsoluteTop() + getCursorOffsetY(0);
   }

   private native void addScrollAndResizeListener(CodeMirror instance) /*-{
		var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		var highlightLine = function() {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::highlightLine(I)(0);
		};

		// draw highligher at start           
		highlightLine();

		if (editor.win) {
			switch (instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
			case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
				if (editor.win.attachEvent) {
					editor.win.attachEvent("onscroll", highlightLine);
					editor.win.attachEvent("onresize", highlightLine);
				}
				break;
			default:
				if (editor.win.addEventListener) {
					editor.win.addEventHandler(editor.win, "scroll",
							highlightLine, true);
					editor.win.addEventHandler(editor.win, "resize",
							highlightLine, true);
				}
			}
		}
   }-*/;

   private void onSaveContent()
   {
      eventBus.fireEvent(new EditorSaveContentEvent(getEditorId()));
   }

   /**
    * @return mimeType of current line content
    */
   private String getCurrentLineMimeType()
   {
      if (configuration.canHaveSeveralMimeTypes())
      {
         if (needUpdateTokenList)
         {
            needUpdateTokenList = false;
            this.tokenList = (List<TokenBeenImpl>)getTokenList();
         }

         String mimeType = CodeMirrorParserImpl.getLineMimeType(cursorPositionRow, this.tokenList);

         if (mimeType != null)
         {
            return mimeType;
         }
      }

      return genericMimeType;
   }

   /**
    * Updates token's FQNs and returns token before "." in position like "address._", or "address.inde_", or "String._"
    * 
    * @param tokenList
    * @param node the ended node of current line
    * @param lineNumber
    * @param cursorPosition
    * @return FQN of current cursor content before "." symbol or null, if this fqn is unknown
    */
   private Token getTokenBeforeCursor(List<? extends Token> tokenList, JavaScriptObject node, int lineNumber,
      int cursorPosition)
   {
      if (configuration.canBeAutocompleted())
      {
         if (configuration.getAutocompleteHelper() != null)
         {
            return configuration.getAutocompleteHelper().getTokenBeforeCursor(node, lineNumber, cursorPosition,
               tokenList, getCurrentLineMimeType());
         }
      }

      return null;
   }

   /**
    * Set listeners of focus received.
    */
   private native void setFocusReceivedListener() /*-{
		var instance = this;

		var focusReceivedListener = function() {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorFocusReceivedEvent()();
		};

		var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor) {
			switch (instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
			case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
				if (editor.win.document.body.attachEvent) {
					editor.win.document.body.attachEvent("onmouseup",
							focusReceivedListener);
				}
				break;
			default:
				if (editor.win.addEventListener) {
					editor.win.addEventHandler(editor.win, "mouseup",
							focusReceivedListener, true);
				}
			}
		}
   }-*/;

   private void fireEditorFocusReceivedEvent()
   {
      eventBus.fireEvent(new EditorFocusReceivedEvent(getEditorId()));
   }

   /**
    * if there is line numbers left field, then validate code and mark lines with errors
    */
   public void validateCode()
   {
      if (needUpdateTokenList && showLineNumbers)
      {
         needValidateCode = true;
         configuration.getParser().getTokenListInBackground(this.editorId, editorObject, eventBus);
      }
   }

   public void forceValidateCode()
   {
      if (!showLineNumbers)
         return;

      if (needUpdateTokenList)
      {
         needValidateCode = true;
         configuration.getParser().getTokenListInBackground(this.editorId, editorObject, eventBus);
      }
      else
      {
         validateCode(this.tokenList);
      }
   }

   private void validateCode(List<? extends Token> tokenList)
   {
      if (showLineNumbers)
      {
         needValidateCode = false;

         // Updates list of code errors and error marks. Also updates the fqn of tokens within the tokenList
         if (tokenList == null || tokenList.isEmpty())
         {
            // clear code error marks
            for (CodeLine lastCodeError : codeErrorList)
            {
               clearErrorMark(lastCodeError.getLineNumber());
            }
            return;
         }

         List<CodeLine> newCodeErrorList = configuration.getCodeValidator().getCodeErrorList(tokenList);

         udpateErrorMarks(newCodeErrorList);
      }
   }

   private ClickHandlerTimer clickHandler = new ClickHandlerTimer();

   private class ClickHandlerTimer extends Timer
   {

      private int lineNumber;

      @Override
      public void run()
      {
         // test if this is line with code error
         if (CodeValidator.isExistedCodeError(lineNumber, codeErrorList))
         {
            codeAssistant.errorMarkClicked(CodeMirror.this, CodeValidator.getCodeErrorList(lineNumber, codeErrorList),
               (getAbsoluteTop() + getCursorOffsetY(lineNumber) + codeErrorCorrectionPopupOffsetTop),
               (getAbsoluteLeft() + lineNumberFieldWidth + codeErrorCorrectionPopupOffsetLeft),
               (String)params.get(EditorParameters.MIME_TYPE));
         }

         try
         {
            // collect all problems on this line
            if (problems.containsKey(lineNumber))
            {
               List<Problem> list = problems.get(lineNumber);

               fireEvent(new ProblemClickEvent(list.toArray(new Problem[list.size()])));
            }
         }
         catch (Exception e)
         {
            e.printStackTrace();
         }
      }

      /**
       * @param lineNumber the lineNumber to set
       */
      public void setLineNumber(int lineNumber)
      {
         this.lineNumber = lineNumber;
      }

   };

   private void onLineNumberClick(int lineNumber)
   {
      clickHandler.setLineNumber(lineNumber);
      clickHandler.schedule(300);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getEditorId()
    */
   @Override
   public String getEditorId()
   {
      return editorId;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getText()
    */
   @Override
   public String getText()
   {
      return getText(editorObject);
   }

   private native String getText(JavaScriptObject editor)/*-{
		return editor.getCode();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setText(java.lang.String)
    */
   @Override
   public void setText(String text)
   {
      setText(editorObject, text);
   }

   private native void setText(JavaScriptObject editor, String text)/*-{
		if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
			return;
		}

		if (text === "") {
			text = "\n"; // fix error with initial cursor position and size (WBT-324)
		}
		editor.setCode(text);
		editor.focus();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#isCapable(org.exoplatform.ide.editor.api.EditorCapability)
    */
   @Override
   public boolean isCapable(EditorCapability capability)
   {
      switch (capability)
      {
         case CAN_BE_AUTOCOMPLETED :
            return configuration.canBeAutocompleted();

         case CAN_BE_OUTLINED :
            return configuration.canBeOutlined();

         case CAN_BE_VALIDATED :
            return configuration.canBeValidated();

         case FIND_AND_REPLACE :
         case DELETE_CURRENT_LINE :
         case FORMAT_SOURCE :
         case GO_TO_POSITION :
         case SHOW_LINE_NUMBERS :
            return true;

         default :
            return false;

      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#formatSource()
    */
   @Override
   public void formatSource()
   {
      formatSource(getText(), editorObject);
   }

   private native void formatSource(String text, JavaScriptObject editor)/*-{
		if (text != ' ') {
			editor.reindent();
		}
   }-*/;

   public void showLineNumbers(boolean showLineNumbers)
   {
      this.setLineNumbers(showLineNumbers);
      this.showLineNumbers = showLineNumbers;

      // to show code error marks in the lineNumbers field
      if (showLineNumbers && configuration.canBeValidated())
      {
         this.needValidateCode = true;
      }
   };

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setFocus()
    */
   @Override
   public void setFocus()
   {
      goToPosition(cursorPositionRow, cursorPositionCol);

      setFocus(editorObject);
   }

   private native void setFocus(JavaScriptObject editor)/*-{
		if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
			return;
		}

		editor.focus();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#goToPosition(int, int)
    */
   @Override
   public void goToPosition(int row, int column)
   {
      cursorPositionRow = row;
      cursorPositionCol = column;
      goToPosition(editorObject, row, column);
   }

   private void fireEditorCursorActivityEvent(String editorId, int cursorRow, int cursorCol)
   {
      eventBus.fireEvent(new EditorCursorActivityEvent(editorId, cursorRow, cursorCol));
   }

   private native void goToPosition(JavaScriptObject editor, int row, int column) /*-{
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)
         || typeof editor.win.select == 'undefined')
      {
         return;
      }

      if (column && !isNaN(Number(column)) && row && !isNaN(Number(row)))
      {
         if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::canGoToLine(I)(row))
         {
            editor.selectLines(editor.nthLine(row), column - 1);
            this.@org.exoplatform.ide.editor.codemirror.CodeMirror::highlightLine(I)(row);
            this.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorCursorActivityEvent(Ljava/lang/String;II)(this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getEditorId()(),row,column);
         }
      }
   }-*/;

   public native boolean canGoToLine(int lineNumber) /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
			return false;

		return editor.nthLine(lineNumber) !== false;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#deleteCurrentLine()
    */
   @Override
   public native void deleteCurrentLine() /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null) {
			return;
		}

		var currentLineNumber = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cursorPositionRow;
		var currentLine = editor.nthLine(currentLineNumber);

		if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE
				&& this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getLastLineNumber()() == currentLineNumber) {
			// clear current line
			this.@org.exoplatform.ide.editor.codemirror.CodeMirror::clearLastLine()();
		} else {
			editor.removeLine(currentLine);
		}

		currentLineNumber = editor.lineNumber(currentLine);
		this.@org.exoplatform.ide.editor.codemirror.CodeMirror::goToPosition(II)(currentLineNumber,1);
   }-*/;

   /**
    * Correct clear the last line of content that the line break is being remained
    */
   private native void clearLastLine() /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
			return;

		var content = editor.getCode();
		var lastLineHandler = editor.lastLine();
		//				.nthLine(this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getLastLineNumber(Ljava/lang/String;)(content));

		if (content.charAt(content.length - 1) == "\n") {
			editor.setLineContent(lastLineHandler, "");
		} else {
			editor.setLineContent(lastLineHandler, "\n");
		}
   }-*/;

   /**
    * returns line quantity in the content
    * 
    * @return
    */
   public native int getLastLineNumber() /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		return editor.lineNumber(editor.lastLine());
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#findAndSelect(java.lang.String, boolean)
    */
   @Override
   public native boolean findAndSelect(String find, boolean caseSensitive) /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
			return;

		var isFound = false;
		var cursor = editor.getSearchCursor(find, true, !caseSensitive); // getSearchCursor(string, atCursor, caseFold) -> cursor
		if (isFound = cursor.findNext()) {
			cursor.select();
		}

		return isFound;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#replaceFoundedText(java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void replaceFoundedText(String find, String replace, boolean caseSensitive)
   {
      replaceFoundedText(editorObject, find, replace, caseSensitive);
   }

   private native void replaceFoundedText(JavaScriptObject editor, String find, String replace, boolean caseSensitive) /*-{
		if (editor == null)
			return;
		var selected = editor.selection();

		if (!caseSensitive) {
			selected = selected.toLowerCase();
			find = find.toLowerCase();
		}

		if (selected == find) {
			editor.replaceSelection(replace);
		}

		editor.focus();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasUndoChanges()
    */
   @Override
   public boolean hasUndoChanges()
   {
      return hasUndoChanges(editorObject);
   }

   private native boolean hasUndoChanges(JavaScriptObject editor)/*-{
		if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
			return false;
		}

		return editor.historySize().undo > 0;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#undo()
    */
   @Override
   public void undo()
   {
      undo(editorObject);
   }

   private native void undo(JavaScriptObject editor)/*-{
		editor.undo();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasRedoChanges()
    */
   @Override
   public boolean hasRedoChanges()
   {
      return hasRedoChanges(editorObject);
   }

   private native boolean hasRedoChanges(JavaScriptObject editor) /*-{
		if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
			return false;
		}

		return editor.historySize().redo > 0;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#redo()
    */
   public native void redo()/*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		editor.redo();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#isReadOnly()
    */
   @Override
   public boolean isReadOnly()
   {
      return (Boolean)params.get(EditorParameters.IS_READ_ONLY);
   }

   @Override
   public int getCursorRow()
   {
      return cursorPositionRow;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorRow()
    */
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

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorCol()
    */
   @Override
   public native int getCursorCol() /*-{
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
    * @param cursor object - the argument of native codemirror cursorActivity event
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

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setHotKeyList(java.util.List)
    */
   @Override
   public void setHotKeyList(List<String> hotKeyList)
   {
      params.put(EditorParameters.HOT_KEY_LIST, hotKeyList);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getTokenList()
    */
   @Override
   public List<? extends Token> getTokenList()
   {
      return (List<TokenBeenImpl>)configuration.getParser().getTokenList(this.editorId, this.editorObject,
         this.eventBus);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getTokenList()
    */
   @Override
   public void getTokenListInBackground()
   {
      if (needUpdateTokenList)
      {
         configuration.getParser().getTokenListInBackground(this.editorId, this.editorObject, this.eventBus);
      }
      else
      {
         eventBus.fireEvent(new EditorTokenListPreparedEvent(this.editorId, this.tokenList));
      }
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#replaceTextAtCurrentLine(java.lang.String, int)
    */
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
    * @param lineNumber started from 1
    */
   private native void insertIntoLine(String newText, int lineNumber) /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor != null && newText) {
			var handler = editor.nthLine(lineNumber);
			editor.insertIntoLine(handler, 0, newText);
		}
   }-*/;

   private FlowPanel getLineHighlighter()
   {
      FlowPanel highlighter = new FlowPanel();
      highlighter.setStyleName("CodeMirror-line-highlighter");
      return highlighter;
   }

   public void insertImportStatement(String fqn)
   {
      if (configuration.canBeValidated())
      {
         if (needUpdateTokenList)
         {
            needUpdateTokenList = false;
            this.tokenList = (List<TokenBeenImpl>)getTokenList();
         }

         CodeLine importStatement = configuration.getCodeValidator().getImportStatement(this.tokenList, fqn);
         if (importStatement != null)
         {
            insertIntoLine(importStatement.getLineContent(), importStatement.getLineNumber());
         }
      }
   }

   private HandlerManager getEventBus()
   {
      return eventBus;
   }

   private CodeMirror getCodeMirror()
   {
      return this;
   }

   /**
    * Set show/hide line numbers and revalidate code if needed
    * 
    * @param showLineNumbers
    */
   private void setLineNumbers(boolean showLineNumbers)
   {
      setLineNumbersNative(showLineNumbers);
      if (configuration.canBeValidated() && showLineNumbers)
      {
         udpateErrorMarks(codeErrorList);
      }
   }

   private native void setLineNumbersNative(boolean showLineNumbers) /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor != null)
			editor.setLineNumbers(showLineNumbers);
   }-*/;

   private native String getLineContent(JavaScriptObject editor, int lineNumber)/*-{
		var handler = editor.nthLine(lineNumber);
		return editor.lineContent(handler);

   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getLineContent(int)
    */
   @Override
   public String getLineContent(int line)
   {
      return getLineContent(editorObject, line);
   }

   public void onEditorTokenListPrepared(EditorTokenListPreparedEvent event)
   {
      if (!this.editorId.equals(event.getEditorId()))
      {
         return;
      }

      if (needUpdateTokenList)
      {
         needUpdateTokenList = false;
         this.tokenList = (List<TokenBeenImpl>)event.getTokenList();
      }

      if (needValidateCode)
      {
         validateCode(this.tokenList);
      }
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

   @Override
   protected void onUnload()
   {
      super.onUnload();
      if (configuration.canBeValidated())
      {
         codeValidateTimer.cancel();
      }

      eventBus.removeHandler(EditorTokenListPreparedEvent.TYPE, this);

      if (configuration.getParser() != null)
      {
         configuration.getParser().stopParsing();
      }
   }

   /*********************************************************************************
    * 
    * Marking Support
    * 
    * public class {@link CodeMirror} implements Makable
    * 
    ********************************************************************************/

   /**
    * List of {@link Problem}
    */
   private Map<Integer, List<Problem>> problems = new HashMap<Integer, List<Problem>>();

   /**
    * Visible notification.
    */
   private Notification activeNotification;

   private KeyHandler handler;

   private AbsolutePanel absPanel;

   private OverviewRuler overviewRuler;

   /**
    * @see org.exoplatform.ide.editor.problem.Markable#markProblem(org.exoplatform.ide.editor.problem.Problem)
    */
   @Override
   public void markProblem(Problem problem)
   {
      if (!problems.containsKey(problem.getLineNumber()))
         problems.put(problem.getLineNumber(), new ArrayList<Problem>());
      problems.get(problem.getLineNumber()).add(problem);

      StringBuilder message = new StringBuilder();
      List<Problem> problemList = problems.get(problem.getLineNumber());
      boolean hasError = fillMessages(problemList, message);

      String markStyle = getStyleForLine(problemList, hasError);

      markProblemmeLine(problem.getLineNumber(), message.toString(), markStyle);
      if (overviewRuler != null)
         overviewRuler.addProblem(problem, message.toString());
   }

   /**
    * @param problemList
    * @param hasError
    * @return
    */
   private String getStyleForLine(List<Problem> problemList, boolean hasError)
   {
      String markStyle = null;
      if (hasError)
      {
         markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkError();
      }
      else
      {
         markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkBreakpoint();
         for (Problem p : problemList)
         {
            if (p.isWarning())
            {
               markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkWarning();
               break;
            }
         }
      }
      return markStyle;
   }

   private boolean fillMessages(List<Problem> problems, StringBuilder message)
   {
      boolean hasError = false;
      List<String> messages = new ArrayList<String>();

      for (Problem p : problems)
      {
         messages.add(p.getMessage());
         if (!hasError && p.isError())
         {
            hasError = true;
         }
      }

      if (messages.size() == 1)
      {
         message.append(problems.get(0).getMessage());
      }
      else
      {
         message.append("Multiple markers at this line<br>");
         for (String m : messages)
         {
            message.append("&nbsp;&nbsp;&nbsp;-&nbsp;").append(m).append("<br>");
         }
      }

      return hasError;
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Markable#unmarkAllProblems()
    */
   @Override
   public void unmarkAllProblems()
   {
      removeNotification();

      List<Problem> breakpoins = new ArrayList<Problem>();
      for (Integer key : problems.keySet())
      {
         unmarkNative(key);
         List<Problem> list = problems.get(key);
         Problem breakpoint = getBreakpoint(list);
         if (breakpoint != null)
            breakpoins.add(breakpoint);
         list.clear();
      }

      if (overviewRuler != null)
         overviewRuler.clearProblems();
      for (Problem p : breakpoins)
         markProblem(p);
   }

   private Problem getBreakpoint(List<Problem> problems)
   {
      for (Problem p : problems)
      {
         if (p.isBreakpoint())
         {
            return p;
         }
      }
      return null;
   }

   /**
    * 
    */
   private void removeNotification()
   {
      if (activeNotification != null)
      {
         activeNotification.destroy();
         activeNotification = null;
      }
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Markable#unmarkProblem(org.exoplatform.ide.editor.problem.Problem)
    */
   @Override
   public void unmarkProblem(Problem problem)
   {
      removeNotification();
      if (problems.containsKey(problem.getLineNumber()))
      {
         List<Problem> list = problems.get(problem.getLineNumber());
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

   /**
    * Removes style from mark element ( is line number element )
    */
   private native void unmarkNative(int lineNumber) /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
				.removeAttribute("class");
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
				.removeAttribute("title");
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseover = null;
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseout = null;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.problem.Markable#addProblemClickHandler(org.exoplatform.ide.editor.problem.ProblemClickHandler)
    */
   @Override
   public HandlerRegistration addProblemClickHandler(ProblemClickHandler handler)
   {
      return addHandler(handler, ProblemClickEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.problem.Markable#addLineNumberDoubleClickHandler(org.exoplatform.ide.editor.problem.LineNumberDoubleClickHandler)
    */
   @Override
   public HandlerRegistration addLineNumberDoubleClickHandler(LineNumberDoubleClickHandler handler)
   {
      return addHandler(handler, LineNumberDoubleClickEvent.TYPE);
   }

   /**
    * Handler mouse over event on marker.
    * 
    * @param jso
    */
   private void markerMouseOver(JavaScriptObject jso)
   {
      com.google.gwt.user.client.Event event = jso.cast();
      Element el = event.getEventTarget().cast();

      if (activeNotification != null)
      {
         activeNotification.destroy();
      }

      activeNotification = new Notification(el);
   }

   /**
    * Handler mouse over event on marker.
    * 
    * @param jso line number element
    */
   private void markerMouseOut(JavaScriptObject jso)
   {
      if (activeNotification != null)
      {
         activeNotification.destroy();
      }
   }

   /**
    * Marks line as problemme.
    * 
    * @param lineNumber
    * @param errorSummary
    */
   public void setErrorMark(int lineNumber, String errorSummary)
   {
      markProblemmeLine(lineNumber, errorSummary, configuration.getCodeErrorMarkStyle());
   }

   void udpateErrorMarks(List<CodeLine> newCodeErrorList)
   {
      for (CodeLine lastCodeError : codeErrorList)
      {
         clearErrorMark(lastCodeError.getLineNumber());
      }

      List<CodeLine> lineCodeErrorList;
      for (CodeLine newCodeError : newCodeErrorList)
      {
         // TODO supress repetitevly setting error mark if there are several errors in the one line
         lineCodeErrorList = CodeValidator.getCodeErrorList(newCodeError.getLineNumber(), newCodeErrorList);
         markProblemmeLine(newCodeError.getLineNumber(), CodeValidator.getErrorSummary(lineCodeErrorList),
            configuration.getCodeErrorMarkStyle());
      }
      configuration.getParser().getTokenListInBackground(this.editorId, editorObject, eventBus);

      codeErrorList = newCodeErrorList;
   }

   /**
    * Marks line as problemme
    * 
    * @param lineNumber line number, starts at 1
    * @param errorSummary
    * @param markStyle
    */
   public native void markProblemmeLine(int lineNumber, String errorSummary, String markStyle) /*-{
		var instance = this;
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;

		var over = function(jso) {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::markerMouseOver(Lcom/google/gwt/core/client/JavaScriptObject;)(jso);
		};

		var out = function(jso) {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::markerMouseOut(Lcom/google/gwt/core/client/JavaScriptObject;)(jso);
		};

		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
				.setAttribute("class", markStyle);
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
				.setAttribute("title", errorSummary);

		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseover = over;
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseout = out;
   }-*/;

   /**
    * Clear error mark from lineNumbers field
    * 
    * @param lineNumber starting from 1
    */
   public native void clearErrorMark(int lineNumber) /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]) {
			editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
					.removeAttribute('class');
			editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
					.removeAttribute('title');
		}
   }-*/;

   @Override
   public IDocument getDocument()
   {
      if (needUpdateDocument)
      {
         needUpdateDocument = false;
         document.removeDocumentListener(this);
         document.set(getText());
         document.addDocumentListener(this);
      }
      return document;
   }

   /**
    * @see org.exoplatform.ide.editor.text.IDocumentListener#documentAboutToBeChanged(org.exoplatform.ide.editor.text.DocumentEvent)
    */
   @Override
   public void documentAboutToBeChanged(DocumentEvent event)
   {
      // Nothing to do
   }

   private native void updateLineContent(int line, String text)/*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		var handle = editor.nthLine(line);
		editor.setLineContent(handle, text);
   }-*/;

   private native void deleteLine(JavaScriptObject editor, int line)/*-{
		var lineHandler = editor.nthLine(line);

		if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE
				&& this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getLastLineNumber()() == line) {
			// clear current line
			this.@org.exoplatform.ide.editor.codemirror.CodeMirror::clearLastLine()();
		} else {
			editor.removeLine(lineHandler);
		}
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.text.IDocumentListener#documentChanged(org.exoplatform.ide.editor.text.DocumentEvent)
    */
   @Override
   public void documentChanged(DocumentEvent event)
   {
      try
      {
         IDocument document = event.getDocument();
         int lineNumber = document.getLineOfOffset(event.getOffset());
         int col = event.getOffset() - document.getLineOffset(lineNumber);
         // lineNumber start from 0, but editor store lines starting form 1
         lineNumber++;
         StringBuilder b = new StringBuilder(getLineContent(lineNumber));
         int length = col + event.getLength();
         int nextLine = lineNumber + 1;
         while (length > b.length())
         {
            b.append(getLineContent(nextLine));
            deleteLine(editorObject, nextLine);
            // symbol '\n' not present in line content
            length --;
         }
         b.replace(col, length, event.getText());
         updateLineContent(lineNumber, b.toString());
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.editor.keys.KeyManager#addHandler(org.exoplatform.ide.editor.keys.KeyHandler)
    */
   @Override
   public HandlerRegistration addHandler(KeyHandler handler)
   {
      this.handler = handler;
      return new HandlerRegistration()
      {

         @Override
         public void removeHandler()
         {
            CodeMirror.this.handler = null;
         }
      };
   }

}
