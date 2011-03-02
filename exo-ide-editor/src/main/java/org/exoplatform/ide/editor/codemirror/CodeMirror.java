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

import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorSaveContentEvent;
import org.exoplatform.ide.editor.codemirror.parser.CodeMirrorParserImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeMirror Feb 9, 2011 4:58:14 PM $
 *
 */
public class CodeMirror extends Editor
{

   protected String editorId;

   protected TextArea textArea;

   protected JavaScriptObject editorObject;

   private FlowPanel lineHighlighter;

   private final Browser currentBrowser = BrowserResolver.CURRENT_BROWSER;

   private boolean needUpdateTokenList = false; // update token list only after the "initCallback" handler has been called

   private boolean needRevalidateCode = false; // revalidate code

   private boolean showLineNumbers = true;

   private List<TokenBeenImpl> tokenList;

   private int lineHeight = 16; // size of line in the CodeMirror in px

   private CodeMirrorConfiguration configuration;

   private int lineNumberFieldWidth = 31; // width of left field with line numbers

   private static int characterWidth = 8; // width of character in the CodeMirror in px

   private static int firstCharacterOffsetLeft = (BrowserResolver.CURRENT_BROWSER.equals(Browser.IE) ? 0 : 5); // left offset of character of the line in px

   private CodeAssistant codeAssistant;

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

      textArea = new TextArea();
      DOM.setElementAttribute(textArea.getElement(), "id", getEditorId());
      add(textArea);

      lineHighlighter = getLineHighlighter();
      add(lineHighlighter);
      setWidgetPosition(lineHighlighter, 0, 5);

      if (params.get(CodeMirrorParams.CONFIGURATION) != null)
         configuration = (CodeMirrorConfiguration) params.get(CodeMirrorParams.CONFIGURATION);
      else
         configuration = new CodeMirrorConfiguration();
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
      boolean readOnly = (Boolean)params.get(CodeMirrorParams.IS_READ_ONLY);
      int continuousScanning = configuration.getContinuousScanning();
      boolean textWrapping = configuration.isTextWrapping();

      showLineNumbers = (Boolean)params.get(CodeMirrorParams.IS_SHOW_LINE_NUMER);
      String parserNames = configuration.getCodeParsers();
      String styleURLs = configuration.getCodeStyles();

      String javaScriptDirectory = configuration.getJsDirectory();

      codeAssistant = configuration.getCodeAssistant();

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

		var cursorActivity = function() {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onCursorActivity()();
		};

		var onLineNumberClick = function(lineNumber) {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberClick(I)(lineNumber);
		};

		var initCallback = function() {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onInitialized()();

			// set hot keys click listener
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::setHotKeysClickListener()();

			// set focusReceived listener
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::setFocusReceivedListener()();
		};

		var activeTokensFunction = function() {
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::needUpdateTokenList = true;
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::needRevalidateCode = true;
		};

		var editor = $wnd.CodeMirror.fromTextArea(id, {
			width : w,
			height : h,
			parserfile : eval(parserNames),
			stylesheet : eval(styleURLs),
			path : jsDirectory,
			continuousScanning : cs,
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

   private void onInitialized()
   {
      this.needUpdateTokenList = true; // update token list after the document had been loaded and reindented
      this.needRevalidateCode = true;
      //      eventBus.fireEvent(new EditorInitializedEvent(editorId));
      //       turn on code validation timer
      setText(content);
      if (configuration.canBeValidated())
      {
         this.codeValidateTimer.scheduleRepeating(2000);
      }
   }

   private void onContentChanged()
   {
      this.needUpdateTokenList = true;
      this.needRevalidateCode = true;
      eventBus.fireEvent(new EditorContentChangedEvent(getEditorId()));
   }

   private void onCursorActivity()
   {
      // highlight current line
      highlightLine(0);
      eventBus.fireEvent(new EditorCursorActivityEvent(editorId, getCursorRow(), getCursorCol()));
   }

   private void highlightLine(int lineNumber)
   {
      if (this.currentBrowser == Browser.IE)
      {
         fixCodeMirrorIframeTransparencyInIE();
      }

      setWidgetPosition(lineHighlighter, 0, 5 + getCursorOffsetY(lineNumber));
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
			currentLine = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCursorRow()();
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
      return false;

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

      return false;
      });
      }
   }-*/;

   @SuppressWarnings("unchecked")
   private List<String> getHotKeyList()
   {
      return (List<String>)params.get(CodeMirrorParams.HOT_KEY_LIST);
   }

   /**
    * Set listener to call this.onCtrlSpaceClick() method just after the clicking on "Ctrl + Space" keys
    */
   public native void ctrlSpaceClickHandler() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (editor == null) return;   

      // calculate cursorOffsetY
      var cursorOffsetY = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getAbsoluteTop()() + this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCursorOffsetY(I)(0);

      // calculate cursorOffsetX
      var cursorCol = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCursorCol()();
      var cursorOffsetX = (cursorCol - 2) * @org.exoplatform.ide.editor.codemirror.CodeMirror::characterWidth + this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getAbsoluteLeft()() + 11;   // 8px per symbol 

      var hasLineNumbers = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::showLineNumbers;
      if (hasLineNumbers) {
         cursorOffsetX += this.@org.exoplatform.ide.editor.codemirror.CodeMirror::lineNumberFieldWidth;
      }

      var cursor = editor.cursorPosition(true);     
      var lineContent = editor.lineContent(cursor.line);

      // read mimeType
      var mimeType =  this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCurrentLineMimeType()();

      // get fqn of current node
      if (editor.nextLine(cursor.line) != null 
            && editor.nextLine(cursor.line).previousSibling)
      {
         var currentNode = editor.nextLine(cursor.line).previousSibling;
      }

      var token = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getTokenBeforeCursor(Lcom/google/gwt/core/client/JavaScriptObject;II)(
         currentNode, 
         this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCursorRow()(),
         cursorCol         
      );

      // fire editorAutoCompleteCalledEvent
      var callback = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::codeAssistant;
      callback.@org.exoplatform.ide.editor.api.codeassitant.CodeAssistant::autocompleteCalled(Lorg/exoplatform/ide/editor/api/Editor;Ljava/lang/String;IILjava/lang/String;IILjava/util/List;Ljava/lang/String;Lorg/exoplatform/ide/editor/api/codeassitant/Token;)(
         this,
         mimeType,
         cursorOffsetX,
         cursorOffsetY,
         lineContent,
         cursorCol,
         this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCursorRow()(),
         this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getTokenList()(),
         this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCurrentLineMimeType()(),
         token
      );
//      var editorAutoCompleteCalledEventInstance = @org.exoplatform.ide.editor.api.event.EditorAutoCompleteCalledEvent::new(Ljava/lang/String;Ljava/lang/String;IILjava/lang/String;IILjava/util/List;Ljava/lang/String;Lorg/exoplatform/ide/editor/api/codeassitant/Token;)(
//         this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getEditorId()(),
//         mimeType,
//         cursorOffsetX,
//         cursorOffsetY,
//         lineContent,
//         cursorCol,
//         this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCursorRow()(),
//         this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getTokenList()(),
//         this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCurrentLineMimeType()(),
//         token
//      );
//      var eventBus = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getEventBus()();
//      eventBus.@com.google.gwt.event.shared.HandlerManager::fireEvent(Lcom/google/gwt/event/shared/GwtEvent;)(editorAutoCompleteCalledEventInstance);
   }-*/;

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
      String genericMimeType = (String)params.get(CodeMirrorParams.MIME_TYPE);
      if (configuration.canHaveSeveralMimeTypes())
      {
         String mimeType = CodeMirrorParserImpl.getLineMimeType(getCursorRow(), (List<TokenBeenImpl>) getTokenList());

         if (mimeType != null)
         {
            return mimeType;
         }
      }

      return genericMimeType;
   }

   /**
    * Updates token's FQNs and returns token before "." in position like "address._", or "address.inde_", or "String._" 
    * @param node the ended node of current line
    * @param lineNumber
    * @param cursorPosition
    * @return FQN of current cursor content before "." symbol or null, if this fqn is unknown 
    */
   private Token getTokenBeforeCursor(JavaScriptObject node, int lineNumber, int cursorPosition)
   {
      if (configuration.canBeAutocompleted())
      {
         validateCode(); // to update token's FQNs        
         return configuration.getAutocompleteHelper().getTokenBeforeCursor(node, lineNumber, cursorPosition,
            (List<Token>) getTokenList());
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
      if (this.needRevalidateCode && showLineNumbers)
      {
         needRevalidateCode = false;

         if (needUpdateTokenList)
         {
            needUpdateTokenList = false;
            tokenList = (List<TokenBeenImpl>) configuration.getParser().getTokenList(editorObject);
         }

         configuration.getCodeValidator().validateCode(tokenList, this);
      }
   }

   private void onLineNumberClick(int lineNumber)
   {
      // test if this is line with code error
      if (configuration.getCodeValidator().isExistedCodeError(lineNumber))
      {
         codeAssistant.errorMarckClicked(this, configuration.getCodeValidator().getCodeErrorList(lineNumber),
            (getAbsoluteTop() + getCursorOffsetY(lineNumber)), (getAbsoluteLeft() + lineNumberFieldWidth),
            (String)params.get(CodeMirrorParams.MIME_TYPE));
      }
   };

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
         case CREATE_TOKEN_LIST :
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

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setShowLineNumbers(boolean)
    */
   @Override
   public void setShowLineNumbers(boolean showLineNumbers)
   {

   }

   private native void setLineNumbers(JavaScriptObject edotor, boolean showLineNumbers)/*-{
		if (editor == null)
			return;

		editor.setLineNumbers(showLineNumbers);
		//   var configuration = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getConfiguration()();
		//   configuration.@org.exoplatform.gwtframework.editor.api.EditorConfiguration::setLineNumbers(Z)(showLineNumbers);
		this.@org.exoplatform.ide.editor.codemirror.CodeMirror::showLineNumbers = showLineNumbers;

		// to show code error marks in the lineNumbers field
		if (showLineNumbers) {
			this.@org.exoplatform.ide.editor.codemirror.CodeMirror::needRevalidateCode = true;
		}
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setFocus()
    */
   @Override
   public void setFocus()
   {
      setFocus(editorObject);
   }

   private native void setFocus(JavaScriptObject editor)/*-{
		if (editor != null) {
			editor.focus();
		}
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#goToPosition(int, int)
    */
   @Override
   public void goToPosition(int row, int column)
   {
      goToPosition(editorObject, row, column);
   }

   private void fireEditorCursorActivityEvent(String editorId, int cursorRow, int cursorCol)
   {
      eventBus.fireEvent(new EditorCursorActivityEvent(editorId, cursorRow, cursorCol));
   }

   private native void goToPosition(JavaScriptObject editor, int row, int column) /*-{
      if (editor == null) return;

      if (column && !isNaN(Number(column)) && row && !isNaN(Number(row))) {
         if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::canGoToLine(I)(row))
         {
            editor.selectLines(editor.nthLine(row), column - 1);
            this.@org.exoplatform.ide.editor.codemirror.CodeMirror::highlightLine(I)(row);
            this.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorCursorActivityEvent(Ljava/lang/String;II)(
               this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getEditorId()(),
               row,
               column
            );
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
      if (editor == null) return;

      var currentLineNumber = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCursorRow()();
      var currentLine = editor.nthLine(currentLineNumber);

      if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE
            && 
          this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getLastLineNumber(Ljava/lang/String;)(editor.getCode()) == currentLineNumber) 
      {
        // clear current line
        this.@org.exoplatform.ide.editor.codemirror.CodeMirror::clearLastLine()();
      }
      else 
      {
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
		var lastLineHandler = editor
				.nthLine(this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getLastLineNumber(Ljava/lang/String;)(content));

		if (content.charAt(content.length - 1) == "\n") {
			editor.setLineContent(lastLineHandler, "");
		} else {
			editor.setLineContent(lastLineHandler, "\n");
		}
   }-*/;

   /**
    * returns line quantity in the content
    * @param content
    * @return
    */
   private native int getLastLineNumber(String content) /*-{
		if (!content)
			return 1;

		// test if content is not ended with line break
		if (content.charAt(content.length - 1) !== "\n") {
			return content.split("\n").length;
		}

		// in the Internet Explorer editor.setCode("\n") is displayed as 2 lines 
		if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser == @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE) {
			return content.split("\n").length;
		}

		return content.split("\n").length - 1;
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

   private native void replaceFoundedText(JavaScriptObject rditor, String find, String replace, boolean caseSensitive) /*-{
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
		if (editor == null) {
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

   private native boolean hasRedoChanges(JavaScriptObject editor)/*-{
		if (editor == null) {
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
      return (Boolean)params.get(CodeMirrorParams.IS_READ_ONLY);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorRow()
    */
   @Override
   public native int getCursorRow() /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
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
		if (editor == null)
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
    * @see org.exoplatform.ide.editor.api.Editor#setHotKeyList(java.util.List)
    */
   @Override
   public void setHotKeyList(List<String> hotKeyList)
   {
      params.put(CodeMirrorParams.HOT_KEY_LIST, hotKeyList);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getTokenList()
    */
   @Override
   public List<? extends Token> getTokenList()
   {
      if (needUpdateTokenList)
      {
         needUpdateTokenList = false;
         tokenList = (List<TokenBeenImpl>) configuration.getParser().getTokenList(editorObject);
      }

      return tokenList;
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
    * Set error mark in lineNumbers field
    * @param lineNumber starting from 1
    * @param errorSummary text summary of errors within the line
    */
   public native void setErrorMark(int lineNumber, String errorSummary) /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		var fileConfiguration = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::configuration;
		var codeErrorMarkStyle = fileConfiguration.@org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration::getCodeErrorMarkStyle()();
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].setAttribute("class", codeErrorMarkStyle);
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].setAttribute("title", errorSummary);
   }-*/;

   /**
    * Clear error mark from lineNumbers field
    * @param lineNumber starting from 1
    */
   public native void clearErrorMark(int lineNumber) /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
				.removeAttribute('class');
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
				.removeAttribute('title');
   }-*/;

   /** 
    * @param newText
    * @param lineNumber started from 1
    */
   public native void insertIntoLine(String newText, int lineNumber) /*-{
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

   private HandlerManager getEventBus()
   {
      return eventBus;
   }

   private CodeMirror getCodeMirror()
   {
      return this;
   }
}
