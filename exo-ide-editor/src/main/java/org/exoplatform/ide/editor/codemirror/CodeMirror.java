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
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeMirror Feb 9, 2011 4:58:14 PM evgen $
 *
 */
public class CodeMirror extends Editor
{

   protected String editorId;

   protected TextArea textArea;

   protected JavaScriptObject editorObject;

   private FlowPanel lineHighlighter;

   private final Browser currentBrowser = BrowserResolver.CURRENT_BROWSER;

   private final String codeErrorMarkBackgroundStyle = "transparent url('" + GWT.getModuleBaseURL()
      + "/images/editor/code-error.png') no-repeat scroll center center";

   private boolean needUpdateTokenList = false; // update token list only after the "initCallback" handler has been called

   private boolean needRevalidateCode = false; // revalidate code

   private boolean showLineNumbers = true;

   private List<Token> tokenList;

   private int lineHeight = 16; // size of line in the CodeMirror in px

   private CodeMirrorConfiguration configuration;

   /**
    * @param file
    * @param params
    * @param eventBus
    */
   public CodeMirror(File file, HashMap<String, Object> params, HandlerManager eventBus)
   {
      super(file, params, eventBus);
      this.editorId = "CodeMirror - " + String.valueOf(this.hashCode());

      textArea = new TextArea();
      DOM.setElementAttribute(textArea.getElement(), "id", getEditorId());
      add(textArea);

      lineHighlighter = getLineHighlighter();
      add(lineHighlighter);
      setWidgetPosition(lineHighlighter, 0, 5);

      if (params.get(CodeMirrorParams.CONFIGURATION) != null)
         configuration = (CodeMirrorConfiguration)params.get(CodeMirrorParams.CONFIGURATION);
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
      //TODO create configuration

      boolean readOnly = (Boolean)params.get(CodeMirrorParams.IS_READ_ONLY);
      int continuousScanning = configuration.getContinuousScanning();
      boolean textWrapping = configuration.isTextWrapping();

      showLineNumbers = (Boolean)params.get(CodeMirrorParams.IS_SHOW_LINE_NUMER);
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
      width: w,
      height: h,
      parserfile: eval(parserNames),
      stylesheet: eval(styleURLs),
      path: jsDirectory,
      continuousScanning: cs,
      undoDelay: 50,   // decrease delay before calling 'onChange' callback
      lineNumbers: lineNumbers,
      readOnly: readOnly,
      textWrapping: tr,
      tabMode: "spaces",
      content: "",     // to fix bug with blocked deleting function of CodeMirror just after opening file [WBT-223]
      onChange: changeFunction,
      //           saveFunction: saveFunction,
      reindentOnLoad: false,   // to fix problem with getting token list after the loading content
      onCursorActivity: cursorActivity,
      onLineNumberClick: onLineNumberClick,
      onLoad: initCallback,
      autoMatchParens: true,

      // Take the token before the cursor. If it contains a character in '()[]{}', search for the matching paren/brace/bracket, and
      // highlight them in green for a moment, or red if no proper match was found.
      markParen: function(node, ok) {
      node.id = ok ? "parenCorrect" : "parenIncorrect";
      },
      unmarkParen: function(node) {
      node.id = null;
      },

      // to update outline panel after the new line has being highlighted
      activeTokens: activeTokensFunction
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
      setText(file.getContent());
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
      if (editor !== null && editor.frame !== null && editor.frame.allowTransparency !== true) {
      editor.frame.allowTransparency = true;
      }
   }-*/;

   /**
    * returns line position number of vertical scroll bar in the body with text in the CodeMirror iframe
    * @param currentLine if equals 0 or null, then will get current line position
    */
   public native int getCursorOffsetY(int currentLine) /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (editor == null) return;   

      var verticalScrollBarPosition = 0;

      switch (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {          
      case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE :
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

      if (! currentLine) {
      currentLine = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getCursorRow()();
      }      

      cursorOffsetY = (currentLine - 1) * this.@org.exoplatform.ide.editor.codemirror.CodeMirror::lineHeight;         
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

      //            var fileConfiguration = instance.@org.exopaltform.ide.editor.codemirror.CodeMirror::configuration;
      //            if (fileConfiguration.@org.exoplatform.gwtframework.editor.codemirror.FileConfiguration::canBeAutocompleted()())
      //            {
      //               // check if this is MacOS
      //               if (@org.exoplatform.gwtframework.commons.util.BrowserResolver::isMacOs()())
      //               {
      //                  // check if this is MacOS and the Alt+Space
      //                  if ( keyCode == 32 && event.altKey ) {
      //                     event.stop();
      //                     instance.@org.exopaltform.ide.editor.codemirror.CodeMirror::ctrlSpaceClickHandler()();                                             
      //                     return true;
      //                  }
      //               }
      //               else
      //               {
      //                  // check if this is non-MacOS and the Ctrl+Space
      //                  if ( keyCode == 32 && event.ctrlKey ) {
      //                     event.stop();
      //                     instance.@org.exopaltform.ide.editor.codemirror.CodeMirror::ctrlSpaceClickHandler()();                                             
      //                     return true;
      //                  }
      //               }
      //            }   

      // for Ctrl+l firstly called <event.keyCode = 76, event.charCode = 0>, then <event.keyCode = 0, event.charCode = 108>  
      // for Ctrl+L firstly called <event.keyCode = 76, event.charCode = 0>, then <event.keyCode = 0, event.charCode = 76>            
      //            var keyPressed = "";
      //            event.ctrlKey ? keyPressed += "Ctrl+" : keyPressed += "";
      //            event.altKey ? keyPressed += "Alt+" : keyPressed += "";
      //            keyPressed += keyCode;              

      // find similar key ammong the hotKeyList
      //            var configuration = instance.@org.exopaltform.ide.editor.codemirror.CodeMirror::getConfiguration()();
      //            var hotKeyList = configuration.@org.exoplatform.gwtframework.editor.api.EditorConfiguration::getHotKeyList()();                  
      //               
      //            // listen Ctrl+S key pressing if hotKeyList is null
      //            if (hotKeyList === null) { 
      //               if (keyPressed == "Ctrl+" + "S".charCodeAt(0)) {
      //                  event.stop();
      //                  instance.@org.exoplatform.gwtframework.editor.codemirror.CodeMirror::onSaveContent()();
      //                  return true;                        
      //               } else {
      //                  return false;
      //               }                    
      //            }
      //                  
      //            for (var i = 0; i < hotKeyList.@java.util.List::size()(); i++) {
      //              var currentHotKey = hotKeyList.@java.util.List::get(I)(i); 
      //              if (currentHotKey == keyPressed) {
      //                event.stop();
      //
      //                // fire EditorHotKeyCalledEvent
      //                var editorHotKeyCalledEventInstance = @org.exoplatform.gwtframework.editor.event.EditorHotKeyCalledEvent::new(Ljava/lang/String;)(
      //                  currentHotKey
      //                );
      //                var eventBus = instance.@org.exoplatform.gwtframework.editor.codemirror.CodeMirror::getEventBus()();
      //                eventBus.@com.google.gwt.event.shared.HandlerManager::fireEvent(Lcom/google/gwt/event/shared/GwtEvent;)(editorHotKeyCalledEventInstance);
      //
      //                return true;                
      //              }
      //            }

      return false;
      });
      }
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
      case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE :
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
         instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorFocusReceivedEvent()();
      };

      var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;         
      if (editor) {
        switch (instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser) {          
           case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE :
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
            tokenList = configuration.getParser().getTokenList(editorObject);
         }

         configuration.getCodeValidator().validateCode(tokenList, this);
      }
   }

   private void onLineNumberClick(int lineNumber)
   {
      // test if this is line with code error
      if (configuration.getCodeValidator().isExistedCodeError(lineNumber))
      {
         System.out.println("Error Mark  Clicked!");
         //         eventBus.fireEvent(new EditorErrorMarkClickedEvent(getEditorId(), fileConfiguration.getCodeValidator()
         //            .getCodeErrorList(lineNumber), (getAbsoluteTop() + getCursorOffsetY(lineNumber)),
         //            (getAbsoluteLeft() + lineNumberFieldWidth), configuration.getMimeType()));
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
   public native String getText()/*-{      
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject; 
      return editor.getCode();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setText(java.lang.String)
    */
   @Override
   public native void setText(String text)/*-{         
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      if (text === "") {
         text = "\n";     // fix error with initial cursor position and size (WBT-324)
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
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#formatSource()
    */
   @Override
   public void formatSource()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setShowLineNumbers(boolean)
    */
   @Override
   public void setShowLineNumbers(boolean showLineNumbers)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setFocus()
    */
   @Override
   public void setFocus()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#goToPosition(int, int)
    */
   @Override
   public void goToPosition(int row, int column)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#deleteCurrentLine()
    */
   @Override
   public void deleteCurrentLine()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#findAndSelect(java.lang.String, boolean)
    */
   @Override
   public boolean findAndSelect(String find, boolean caseSensitive)
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#replaceFoundedText(java.lang.String, java.lang.String, boolean)
    */
   @Override
   public void replaceFoundedText(String find, String replace, boolean caseSensitive)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasUndoChanges()
    */
   @Override
   public boolean hasUndoChanges()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#undo()
    */
   @Override
   public void undo()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#hasRedoChanges()
    */
   @Override
   public boolean hasRedoChanges()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#redo()
    */
   @Override
   public void redo()
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#isReadOnly()
    */
   @Override
   public boolean isReadOnly()
   {
      // TODO Auto-generated method stub
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorRow()
    */
   @Override
   public int getCursorRow()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorCol()
    */
   @Override
   public int getCursorCol()
   {
      // TODO Auto-generated method stub
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setHotKeyList(java.util.List)
    */
   @Override
   public void setHotKeyList(List<String> hotKeyList)
   {
      // TODO Auto-generated method stub

   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getTokenList()
    */
   @Override
   public List<Token> getTokenList()
   {
      // TODO Auto-generated method stub
      return null;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#replaceTextAtCurrentLine(java.lang.String, int)
    */
   @Override
   public void replaceTextAtCurrentLine(String line, int cursorPosition)
   {
      // TODO Auto-generated method stub
   }

   private FlowPanel getLineHighlighter()
   {
      FlowPanel highlighter = new FlowPanel();
      highlighter.setStyleName("CodeMirror-line-highlighter");
      return highlighter;
   }

}
