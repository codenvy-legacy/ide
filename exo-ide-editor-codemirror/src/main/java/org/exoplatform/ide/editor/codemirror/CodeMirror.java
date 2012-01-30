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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;

import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.editor.api.Capability;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.codemirror.codeassitant.ClientCodeAssistant;
import org.exoplatform.ide.editor.codemirror.codeassitant.CodeLine;
import org.exoplatform.ide.editor.codemirror.codeassitant.TokenImplExtended;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeMirror Feb 9, 2011 4:58:14 PM $
 * 
 */
public class CodeMirror extends AbsolutePanel implements Editor
{

   protected String editorId;

   protected TextArea textArea;

   protected JavaScriptObject editorObject;

   private FlowPanel lineHighlighter;

   private final Browser currentBrowser = BrowserResolver.CURRENT_BROWSER;

   private boolean needUpdateTokenList = true; // update token list only after the "initCallback" handler has been called

   private boolean needValidateCode = false;

   private boolean lineNumbers = true;

   private List<TokenImplExtended> tokenList;

   private int lineHeight = 16; // size of line in the CodeMirror in px

   private CodeMirrorConfiguration configuration;

   private int lineNumberFieldWidth = 31; // width of left field with line numbers

   private static int characterWidth = 8; // width of character in the CodeMirror in px

   private static int firstCharacterOffsetLeft = 11; // left offset of character of the line in px

   private static int codeErrorCorrectionPopupOffsetLeft = 6; // top offset of character of the line in px

   private static int codeErrorCorrectionPopupOffsetTop = 22; // top offset of character of the line in px

   private ClientCodeAssistant codeAssistant;

   private String genericMimeType; // type of document itself

   private int cursorRow = 1;

   private int cursorColumn = 1;

   
   
   protected List<CodeLine> codeErrorList = new ArrayList<CodeLine>();
   
   
   
   private String content;
   
   private HashMap<String, Object> params;
   
   /**
    * @param file
    * @param params
    * @param eventBus
    */
   public CodeMirror(String content, HashMap<String, Object> params)
   {
      editorId = "CodeMirror - " + String.valueOf(this.hashCode());
      
      this.content = content;

      if (params == null)
      {
         params = new HashMap<String, Object>();
      }
      this.params = params;

      /*
       * Place TextArea
       */
      textArea = new TextArea();
      DOM.setElementAttribute(textArea.getElement(), "id", getId());
      add(textArea);

      /*
       * Place highlighter
       */
      lineHighlighter = new FlowPanel();
      lineHighlighter.setStyleName("CodeMirror-line-highlighter");      
      add(lineHighlighter);
      setWidgetPosition(lineHighlighter, 0, 5);
      
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
   }
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#getId()
    */
   @Override
   public String getId()
   {
      return editorId;
   }
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#isReadOnly()
    */
   @Override
   public boolean isReadOnly()
   {
      return (Boolean)params.get(EditorParameters.IS_READ_ONLY);
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
      boolean readOnly = params.get(EditorParameters.IS_READ_ONLY) != null ? (Boolean)params.get(EditorParameters.IS_READ_ONLY) : false;
      int continuousScanning = configuration.getContinuousScanning();
      boolean textWrapping = configuration.isTextWrapping();

      lineNumbers = params.get(EditorParameters.SHOW_LINE_NUMERS) != null ? (Boolean)params.get(EditorParameters.SHOW_LINE_NUMERS) : false;
      String parserNames = configuration.getCodeParsers();
      String styleURLs = configuration.getCodeStyles();

      String javaScriptDirectory = configuration.getJsDirectory();

      editorObject = initCodeMirror(editorId, width, height, readOnly, continuousScanning, textWrapping, lineNumbers, styleURLs, parserNames, javaScriptDirectory);
   }

   private native JavaScriptObject initCodeMirror(String id, String w, String h, boolean readOnly, int cs, boolean tr, boolean lineNumbers, String styleURLs, String parserNames, String jsDirectory) /*-{
      var instance = this;
      var changeFunction = function() {
         instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onContentChanged()();
      };

      var cursorActivity = function(cursor) {
         instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onCursorActivity(Lcom/google/gwt/core/client/JavaScriptObject;)(cursor);
      };

      var onLineNumberClick = function(lineNumber) {
         //instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberClick(I)(lineNumber);
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


   private void onInitialized()
   {
      this.needUpdateTokenList = true; // update token list after the document had been loaded and reindented
      
      setText(content);

      fireEvent(new EditorInitializedEvent(editorId));

//      // turn on code validation time
//      if (configuration.canBeValidated())
//      {
//         this.codeValidateTimer.scheduleRepeating(2000);
//      }
   }

   private void onContentChanged()
   {
      needUpdateTokenList = true;
      fireEvent(new EditorContentChangedEvent(editorId));
   }

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
               if (! (event.ctrlKey || event.altKey) || (event.type != "keydown")) { 
                  return false;
               }

               var fileConfiguration = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::configuration;
               if (fileConfiguration.@org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration::canBeAutocompleted()()) {
                  // check if this is MacOS
                  if (@org.exoplatform.gwtframework.commons.util.BrowserResolver::isMacOs()()) {
                     // check if this is MacOS and the Alt+Space
                     if ( keyCode == 32 && event.altKey ) {
                        event.stop();
                        //instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::ctrlSpaceClickHandler()();                                             
                        return true;
                     }
                  } else {
                     // check if this is non-MacOS and the Ctrl+Space
                     if ( keyCode == 32 && event.ctrlKey ) {
                        event.stop();
                        //instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::ctrlSpaceClickHandler()();                                             
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

//               // find similar key ammong the hotKeyList
//               var hotKeyList = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::getHotKeyList()();                  
//
//               // listen Ctrl+S key pressing if hotKeyList is null
//               if (hotKeyList === null) { 
//                  if (keyPressed == "Ctrl+" + "S".charCodeAt(0)) {
//                     event.stop();
//                     instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onSaveContent()();
//                     return true;                        
//                  } else {
//                     return false;
//                  }                    
//               }
//
//               for (var i = 0; i < hotKeyList.@java.util.List::size()(); i++) {
//                  var currentHotKey = hotKeyList.@java.util.List::get(I)(i); 
//                  if (currentHotKey == keyPressed) {
//                     event.stop();
//
//                     // fire EditorHotKeyCalledEvent
//                     var editorHotKeyCalledEventInstance = @org.exoplatform.ide.editor.api.event.EditorHotKeyCalledEvent::new(Ljava/lang/String;)(currentHotKey);
//                     var eventBus = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::getEventBus()();
//                     eventBus.@com.google.gwt.event.shared.HandlerManager::fireEvent(Lcom/google/gwt/event/shared/GwtEvent;)(editorHotKeyCalledEventInstance);
//                     return true;                
//                  }
//               }
                                                 
               // fix bug with pasting text into emptied document in the IE (IDE-1142)
               if (editor.getCode() == "") {
                  editor.setCode("");
               }

               return false;
            });
         }
      }-*/;


   private native void addScrollAndResizeListener(CodeMirror instance) /*-{
      var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      var highlightLine = function() {
         instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::updateHighlighter()();
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
                  editor.win.addEventHandler(editor.win, "scroll", highlightLine, true);
                  editor.win.addEventHandler(editor.win, "resize", highlightLine, true);
               }
         }
      }
   }-*/;

   /**
    * Set listeners of focus received.
    */
   private native void setFocusReceivedListener() /*-{
      var instance = this;

      var focusReceivedListener = function() {
         instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorFocusReceived()();
      };

      var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
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

   private void fireEditorFocusReceived() {
      fireEvent(new EditorFocusReceivedEvent(editorId));
   }

   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   












   /**
    * Check if CodeMirror editor instance consists of neccessery objects.
    * 
    * @param editor
    * @return
    */
   private native boolean checkGenericCodeMirrorObject(JavaScriptObject editor) /*-{
      return (editor != null) && (typeof editor != 'undefined') && (typeof editor.editor != 'undefined');
   }-*/;

   @Override
   protected void onUnload()
   {
      super.onUnload();
//      if (configuration.canBeValidated())
//      {
//         codeValidateTimer.cancel();
//      }

      System.out.println("CodeMirror.onUnload(). Remove handlers!!!");
      //eventBus.removeHandler(EditorTokenListPreparedEvent.TYPE, this);

      if (configuration.getParser() != null)
      {
         configuration.getParser().stopParsing();
      }
   }

   /***************************************************************************************************
    * 
    * HOT KEYS
    * 
    ***************************************************************************************************/   

//   @SuppressWarnings("unchecked")
//   private List<String> getHotKeyList()
//   {
//      return (List<String>)params.get(EditorParameters.HOT_KEYS);
//   }
//
//   /**
//    * @see org.exoplatform.ide.editor.api.Editor#setHotKeyList(java.util.List)
//    */
//   @Override
//   public void setHotKeyList(List<String> hotKeyList)
//   {
//      params.put(EditorParameters.HOT_KEY_LIST, hotKeyList);
//   }
   
   
   /***************************************************************************************************
    * 
    * GET TEXT, SET TEXT
    * 
    ***************************************************************************************************/
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#getText()
    */
   @Override
   public native String getText() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      return editor.getCode();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setText(java.lang.String)
    */
   @Override
   public native void setText(String text) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
         return;
      }
      
      if (text === "") {
         text = "\n"; // fix error with initial cursor position and size (WBT-324)
      }

      editor.setCode(text);
      editor.focus();
   }-*/;

   /***************************************************************************************************
    * 
    * FOCUS
    * 
    ***************************************************************************************************/

   @Override
   public void focus()
   {
      setCursorPositionNative(editorObject, cursorRow, cursorColumn);
      focusNative(editorObject);
   }

   private native void focusNative(JavaScriptObject editor) /*-{
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
         return;
      }

      editor.focus();
   }-*/;

   /***************************************************************************************************
    * 
    * CAPABILITIES
    * 
    ***************************************************************************************************/

   /**
    * @see org.exoplatform.ide.editor.api.Editor#isCapable(org.exoplatform.ide.editor.api.Capability)
    */
   @Override
   public boolean isCapable(Capability capability)
   {
      switch (capability)
      {
         case FIND_AND_REPLACE :
         case DELETE_LINE :
         case FORMAT_SOURCE :
         case SET_CURSOR_POSITION :
         case SHOW_LINE_NUMBERS :
            return true;

         default :
            return false;
      }
   }
 
   /***************************************************************************************************
    * 
    * CURSOR
    * 
    ***************************************************************************************************/
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorRow()
    */
   @Override
   public int getCursorRow()
   {
      return cursorRow;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorColumn()
    */
   @Override
   public int getCursorColumn() {
      return cursorColumn;
   }   
   
   /**
    * @param cursor
    */
   private void onCursorActivity(JavaScriptObject cursor)
   {
      cursorColumn = getCursorColumnNative();

      if (BrowserResolver.CURRENT_BROWSER == Browser.IE)
      {
         if (getLastLineNumber(getText()) == 1)
         {
            cursorRow = 1;
         }
         else
         {
            cursorRow = getCursorActivityRow(cursor, cursorColumn);
         }
      }
      else
      {
         cursorRow = getCursorRowNative();
      }

      // highlight current line
      updateHighlighter();
      
      fireEvent(new EditorCursorActivityEvent(editorId, cursorRow, cursorColumn));
   }   
   
   /**
    * @return
    */
   public native int getCursorColumnNative() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor) || typeof editor.win.select == 'undefined') {
         return 1;
      }

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
    * @return
    */
   public native int getCursorRowNative() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor) || typeof editor.win.select == 'undefined') {
         return 1;
      }

      switch (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {
         case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
            if (editor.editor.selectionSnapshot) {
               return editor.lineNumber(editor.editor.selectionSnapshot.from.node);
            } else {
               return 1;
            }

         default:
            var cursor = editor.cursorPosition(true);
            return editor.lineNumber(cursor.line) || 1;
      }
   }-*/;

   /**
    * @param cursor object - the argument of native codemirror cursorActivity event
    * @return editor.lineNumber(cursor) - 1
    */
   private native int getCursorActivityRow(JavaScriptObject cursor, int cursorCol) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor) || typeof editor.win.select == 'undefined' || !cursor) {
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
   
   @Override
   public void setCursorPosition(int row, int column)
   {
      cursorRow = row;
      cursorColumn = column;
      setCursorPositionNative(editorObject, row, column);
   }
   
   private native void setCursorPositionNative(JavaScriptObject editor, int row, int column) /*-{
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor) || typeof editor.win.select == 'undefined') {
         return;
      }

      if (column && !isNaN(Number(column)) && row && !isNaN(Number(row))) {
         if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::canGoToLine(I)(row)) {
            editor.selectLines(editor.nthLine(row), column - 1);
            //this.@org.exoplatform.ide.editor.codemirror.CodeMirror::highlightLine(I)(row);
            this.@org.exoplatform.ide.editor.codemirror.CodeMirror::updateHighlighter()();
            //this.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorCursorActivityEvent(Ljava/lang/String;II)(this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getEditorId()(), row, column);
         }
      }
   }-*/;
   
   private native boolean canGoToLine(int lineNumber) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (editor == null) {
         return false;
      }

      return editor.nthLine(lineNumber) !== false;
   }-*/;
   
   /***************************************************************************************************
    * 
    * CURRENT LINE HIGHLIGHTING
    * 
    ***************************************************************************************************/

   private void updateHighlighter()
   {
      if (this.currentBrowser == Browser.IE)
      {
         fixCodeMirrorIframeTransparencyInIE();
      }

      setWidgetPosition(lineHighlighter, 0, 5 + getCursorOffsetY(cursorRow));
   }
   
   /**
    * set codemirror iframe transparency for the IE
    */
   private native void fixCodeMirrorIframeTransparencyInIE() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (editor !== null && editor.frame !== null && editor.frame.allowTransparency !== true) {
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
      if (editor == null) {
         return;
      }

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
         currentLine = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cursorRow;
      }

      cursorOffsetY = (currentLine - 1) * this.@org.exoplatform.ide.editor.codemirror.CodeMirror::lineHeight;
      cursorOffsetY -= verticalScrollBarPosition;

      return cursorOffsetY;
   }-*/;
   
   /***************************************************************************************************
    * 
    * LINE NUMBERS
    * 
    ***************************************************************************************************/
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#showLineNumbers(boolean)
    */
   @Override
   public void showLineNumbers(boolean lineNumbers)
   {
      this.lineNumbers = lineNumbers;
      showLineNumbersNative(lineNumbers);      
   };
   
   private native void showLineNumbersNative(boolean lineNumbers) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (editor != null) {
         editor.setLineNumbers(lineNumbers);
      }      
   }-*/;
   
   /***************************************************************************************************
    * 
    * INSERT LINE, DELETE LINE, SET LINE TEXT, GET LINE TEXT
    * 
    ***************************************************************************************************/
   
   @Override
   public void insetLine(int lineNumber, String text)
   {
      Window.alert("INSERT LINE > " + lineNumber);
   }

   @Override
   public void deleteLine(int lineNumber)
   {
      Window.alert("DELETE LINE > " + lineNumber);
   }

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

   @Override
   public native String getLineText(int line) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      var handler = editor.nthLine(lineNumber);
      return editor.lineContent(handler);
   }-*/;

   @Override
   public void setLineText(int line, String text) {
      Window.alert("CodeMirror.setLineText()");
   }
   
   private native int getLastLineNumber(String content) /*-{
      if (!content) {
         return 1;
      }

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
   

//   /**
//    * @see org.exoplatform.ide.editor.api.Editor#replaceTextAtCurrentLine(java.lang.String, int)
//    */
//   @Override
//   public native void replaceTextAtCurrentLine(String line, int cursorPosition) /*-{
//      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
//      if (editor != null && line) {
//         var currentLine = editor.cursorPosition(true).line;
//         editor.setLineContent(currentLine, line);
//         // set cursor at the cursor position  
//         editor.selectLines(currentLine, cursorPosition);
//      }
//   }-*/;
//

// @Override
// public void setLineText(int line, String text) /*-{
// 
// 
// }-*/;

// @Override
// public String getLineText(int line) {
//    return getLineNumber(editorObject, line);
// }
// 
// private native String getLineNumber(JavaScriptObject editor, int lineNumber) /*-{
// var handler = editor.nthLine(lineNumber);
// return editor.lineContent(handler);
//
// }-*/;
   
   
   /***************************************************************************************************
    * 
    * UNDO, REDO
    * 
    ***************************************************************************************************/
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasUndoChanges()
    */
   @Override
   public native boolean hasUndoChanges() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;      
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
         return false;
      }

      return editor.historySize().undo > 0;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasRedoChanges()
    */
   @Override
   public native boolean hasRedoChanges() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
   
      if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor)) {
         return false;
      }
                                                                  
      return editor.historySize().redo > 0;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#undo()
    */
   @Override
   public native void undo() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      editor.undo();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#redo()
    */
   @Override
   public native void redo() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      editor.redo();
   }-*/;
   
   /***************************************************************************************************
    * 
    * FIND, REPLACE
    * 
    ***************************************************************************************************/
   
   @Override
   public boolean findText(String text, boolean caseSensitive)
   {
      return false;
   }

   @Override
   public void replaceSelection(String text)
   {
   }
   
   
//   /**
//    * @see org.exoplatform.ide.editor.api.Editor#findAndSelect(java.lang.String, boolean)
//    */
//   @Override
//   public native boolean findAndSelect(String find, boolean caseSensitive) /*-{
//                                                                           var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
//                                                                           if (editor == null)
//                                                                           return;
//
//                                                                           var isFound = false;
//                                                                           var cursor = editor.getSearchCursor(find, true, !caseSensitive); // getSearchCursor(string, atCursor, caseFold) -> cursor
//                                                                           if (isFound = cursor.findNext()) {
//                                                                           cursor.select();
//                                                                           }
//
//                                                                           return isFound;
//                                                                           }-*/;

//   /**
//    * @see org.exoplatform.ide.editor.api.Editor#replaceFoundedText(java.lang.String, java.lang.String, boolean)
//    */
//   @Override
//   public void replaceFoundedText(String find, String replace, boolean caseSensitive)
//   {
//      replaceFoundedText(editorObject, find, replace, caseSensitive);
//   }

//   private native void replaceFoundedText(JavaScriptObject editor, String find, String replace, boolean caseSensitive) /*-{
//                                                                                                                       if (editor == null)
//                                                                                                                       return;
//                                                                                                                       var selected = editor.selection();
//
//                                                                                                                       if (!caseSensitive) {
//                                                                                                                       selected = selected.toLowerCase();
//                                                                                                                       find = find.toLowerCase();
//                                                                                                                       }
//
//                                                                                                                       if (selected == find) {
//                                                                                                                       editor.replaceSelection(replace);
//                                                                                                                       }
//
//                                                                                                                       editor.focus();
//                                                                                                                       }-*/;
   
   
   /***************************************************************************************************
    * 
    * REINDENTATION
    * 
    ***************************************************************************************************/

   /**
    * @see org.exoplatform.ide.editor.api.Editor#format()
    */
   @Override
   public native void format() /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (editor == null) {
        return;
      }
      
      var text = editor.getCode();
      if (text != ' ') {
        editor.reindent();
      }      
   }-*/;
   
   /***************************************************************************************************
    * 
    * OUTLINE, AUTOCOMPLETE, VALIDATION
    * 
    ***************************************************************************************************/

// public void onEditorTokenListPrepared(EditorTokenListPreparedEvent event)
// {
//    if (!this.editorId.equals(event.getEditorId()))
//    {
//       return;
//    }
//
//    if (needUpdateTokenList)
//    {
//       needUpdateTokenList = false;
//       this.tokenList = (List<TokenBeenImpl>)event.getTokenList();
//    }
//
//    if (needValidateCode)
//    {
//       validateCode(this.tokenList);
//    }
// }

//   /**
//    * Set listener to call this.onCtrlSpaceClick() method just after the clicking on "Ctrl + Space" keys
//    */
//   public native void ctrlSpaceClickHandler() /*-{
//      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
//      if (editor == null) {
//         return;
//      }   
//
//      var cursor = editor.cursorPosition(true);     
//      var lineContent = editor.lineContent(cursor.line);
//
//      // get fqn of current node
//      if (editor.nextLine(cursor.line) != null && editor.nextLine(cursor.line).previousSibling) {
//         var currentNode = editor.nextLine(cursor.line).previousSibling;
//      }
//
//      this.@org.exoplatform.ide.editor.codemirror.CodeMirror::callAutocompleteHandler(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(lineContent, currentNode);
//   }-*/;

//   private void callAutocompleteHandler(String lineContent, JavaScriptObject currentNode)
//   {
//      int cursorRow = cursorPositionRow;
//
//      // calculate cursorOffsetY
//      int cursorOffsetY = getAbsoluteTop() + getCursorOffsetY(0);
//
//      // calculate cursorOffsetX
//      int cursorCol = cursorPositionCol;
//      int cursorOffsetX = (cursorCol - 2) * characterWidth + getAbsoluteLeft() + firstCharacterOffsetLeft; // 8px per symbol
//
//      if (this.showLineNumbers)
//      {
//         cursorOffsetX += this.lineNumberFieldWidth;
//      }
//
//      if (needUpdateTokenList)
//      {
//         needUpdateTokenList = false;
//         this.tokenList = (List<TokenImplExtended>)getTokenList();
//
//         // to update token's FQNs
//         if (configuration.canBeValidated())
//         {
//            needValidateCode = false;
//            validateCode(this.tokenList);
//         }
//      }
//
//      Token tokenBeforeCursor = getTokenBeforeCursor(this.tokenList, currentNode, cursorRow, cursorCol);
//
//      List<? extends Token> selectedTokenList = this.tokenList;
//
//      // read mimeType
//      String currentLineMimeType = getCurrentLineMimeType();
//      if (configuration.canHaveSeveralMimeTypes() && !genericMimeType.equals(currentLineMimeType))
//      {
//         selectedTokenList =
//            (List<TokenBeenImpl>)CodeValidator.extractCode((List<TokenBeenImpl>)this.tokenList,
//               new LinkedList<TokenBeenImpl>(), currentLineMimeType);
//      }
//
//      codeAssistant.autocompleteCalled(this, cursorOffsetX, cursorOffsetY, (List<Token>)selectedTokenList,
//         currentLineMimeType, tokenBeforeCursor);
//   }
   
//   public void insertImportStatement(String fqn)
//   {
//      if (configuration.canBeValidated())
//      {
//         if (needUpdateTokenList)
//         {
//            needUpdateTokenList = false;
//            this.tokenList = (List<TokenBeenImpl>)getTokenList();
//         }
//
//         CodeLine importStatement = configuration.getCodeValidator().getImportStatement(this.tokenList, fqn);
//         if (importStatement != null)
//         {
//            insertIntoLine(importStatement.getLineContent(), importStatement.getLineNumber());
//         }
//      }
//   }

//   /**
//    * @see org.exoplatform.ide.editor.api.Editor#getTokenList()
//    */
//   @Override
//   public List<? extends Token> getTokenList()
//   {
//      return (List<TokenBeenImpl>)configuration.getParser().getTokenList(this.editorId, this.editorObject,
//         this.eventBus);
//   }

//   /**
//    * @see org.exoplatform.ide.editor.api.Editor#getTokenList()
//    */
//   @Override
//   public void getTokenListInBackground()
//   {
//      if (needUpdateTokenList)
//      {
//         configuration.getParser().getTokenListInBackground(this.editorId, this.editorObject, this.eventBus);
//      }
//      else
//      {
//         eventBus.fireEvent(new EditorTokenListPreparedEvent(this.editorId, this.tokenList));
//      }
//   }

   
//   /**
//    * @return mimeType of current line content
//    */
//   private String getCurrentLineMimeType()
//   {
//      if (configuration.canHaveSeveralMimeTypes())
//      {
//         if (needUpdateTokenList)
//         {
//            needUpdateTokenList = false;
//            this.tokenList = (List<TokenBeenImpl>)getTokenList();
//         }
//
//         String mimeType = CodeMirrorParserImpl.getLineMimeType(cursorPositionRow, this.tokenList);
//
//         if (mimeType != null)
//         {
//            return mimeType;
//         }
//      }
//
//      return genericMimeType;
//   }

   
   
   

//   /**
//    * if there is line numbers left field, then validate code and mark lines with errors
//    */
//   public void validateCode()
//   {
//      if (needUpdateTokenList && showLineNumbers)
//      {
//         needValidateCode = true;
//         configuration.getParser().getTokenListInBackground(this.editorId, editorObject, eventBus);
//      }
//   }

//   private void validateCode(List<? extends Token> tokenList)
//   {
//      if (showLineNumbers)
//      {
//         needValidateCode = false;
//
//         // Updates list of code errors and error marks. Also updates the fqn of tokens within the tokenList
//         if (tokenList == null || tokenList.isEmpty())
//         {
//            // clear code error marks
//            for (CodeLine lastCodeError : codeErrorList)
//            {
//               clearErrorMark(lastCodeError.getLineNumber());
//            }
//            return;
//         }
//
//         List<CodeLine> newCodeErrorList = configuration.getCodeValidator().getCodeErrorList(tokenList);
//
//         udpateErrorMarks(newCodeErrorList);
//      }
//   }
   
   
//   /**
//    * Updates token's FQNs and returns token before "." in position like "address._", or "address.inde_", or "String._"
//    * 
//    * @param tokenList
//    * @param node the ended node of current line
//    * @param lineNumber
//    * @param cursorPosition
//    * @return FQN of current cursor content before "." symbol or null, if this fqn is unknown
//    */
//   private Token getTokenBeforeCursor(List<? extends Token> tokenList, JavaScriptObject node, int lineNumber,
//      int cursorPosition)
//   {
//      if (configuration.canBeAutocompleted())
//      {
//         if (configuration.getAutocompleteHelper() != null)
//         {
//            return configuration.getAutocompleteHelper().getTokenBeforeCursor(node, lineNumber, cursorPosition,
//               tokenList, getCurrentLineMimeType());
//         }
//      }
//
//      return null;
//   }
   
   
//   void udpateErrorMarks(List<CodeLine> newCodeErrorList)
//   {
//      for (CodeLine lastCodeError : codeErrorList)
//      {
//         clearErrorMark(lastCodeError.getLineNumber());
//      }
//
//      List<CodeLine> lineCodeErrorList;
//      for (CodeLine newCodeError : newCodeErrorList)
//      {
//         // TODO supress repetitevly setting error mark if there are several errors in the one line
//         lineCodeErrorList = CodeValidator.getCodeErrorList(newCodeError.getLineNumber(), newCodeErrorList);
//         setErrorMark(newCodeError.getLineNumber(), CodeValidator.getErrorSummary(lineCodeErrorList));
//      }
//
//      codeErrorList = newCodeErrorList;
//   }

//   private void onLineNumberClick(int lineNumber)
//   {
//      // test if this is line with code error
//      if (CodeValidator.isExistedCodeError(lineNumber, codeErrorList))
//      {
//         codeAssistant.errorMarkClicked(this, CodeValidator.getCodeErrorList(lineNumber, codeErrorList),
//            (getAbsoluteTop() + getCursorOffsetY(lineNumber) + codeErrorCorrectionPopupOffsetTop), (getAbsoluteLeft()
//               + lineNumberFieldWidth + codeErrorCorrectionPopupOffsetLeft),
//            (String)params.get(EditorParameters.MIME_TYPE));
//      }
//   };
   
//   /**
//    * Set error mark in lineNumbers field
//    * 
//    * @param lineNumber starting from 1
//    * @param errorSummary text summary of errors within the line
//    */
//   private native void setErrorMark(int lineNumber, String errorSummary) /*-{
//                                                                         var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
//                                                                         var fileConfiguration = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::configuration;
//                                                                         var codeErrorMarkStyle = fileConfiguration.@org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration::getCodeErrorMarkStyle()();
//                                                                         editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
//                                                                         .setAttribute("class", codeErrorMarkStyle);
//                                                                         editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
//                                                                         .setAttribute("title", errorSummary);
//                                                                         }-*/;

//   /**
//    * Clear error mark from lineNumbers field
//    * 
//    * @param lineNumber starting from 1
//    */
//   private native void clearErrorMark(int lineNumber) /*-{
//                                                      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
//                                                      if (editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1])
//                                                      {
//                                                      editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
//                                                      .removeAttribute('class');
//                                                      editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1]
//                                                      .removeAttribute('title');
//                                                      }
//                                                      }-*/;   

// private Timer codeValidateTimer = new Timer()
// {
//    @Override
//    public void run()
//    {
//       validateCode();
//    }
// };


//   /**
//    * @see org.exoplatform.ide.editor.api.Editor#deleteCurrentLine()
//    */
//   @Override
//   public native void deleteCurrentLine() /*-{
//                                          var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
//                                          if (editor == null) return;
//
//                                          var currentLineNumber = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cursorPositionRow;
//                                          var currentLine = editor.nthLine(currentLineNumber);
//
//                                          if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE
//                                          && 
//                                          this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getLastLineNumber(Ljava/lang/String;)(editor.getCode()) == currentLineNumber) 
//                                          {
//                                          // clear current line
//                                          this.@org.exoplatform.ide.editor.codemirror.CodeMirror::clearLastLine()();
//                                          }
//                                          else 
//                                          {
//                                          editor.removeLine(currentLine);
//                                          }
//
//                                          currentLineNumber = editor.lineNumber(currentLine);
//                                          this.@org.exoplatform.ide.editor.codemirror.CodeMirror::goToPosition(II)(currentLineNumber,1);        
//                                          }-*/;

//   /**
//    * Correct clear the last line of content that the line break is being remained
//    */
//   private native void clearLastLine() /*-{
//                                       var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
//                                       if (editor == null)
//                                       return;
//
//                                       var content = editor.getCode();
//                                       var lastLineHandler = editor
//                                       .nthLine(this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getLastLineNumber(Ljava/lang/String;)(content));
//
//                                       if (content.charAt(content.length - 1) == "\n") {
//                                       editor.setLineContent(lastLineHandler, "");
//                                       } else {
//                                       editor.setLineContent(lastLineHandler, "\n");
//                                       }
//                                       }-*/;

//   /**
//    * returns line quantity in the content
//    * 
//    * @param content
//    * @return
//    */
   
}
