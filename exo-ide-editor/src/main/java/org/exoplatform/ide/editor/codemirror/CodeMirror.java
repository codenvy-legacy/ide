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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorCapability;
import org.exoplatform.ide.editor.api.EditorTokenListPreparedEvent;
import org.exoplatform.ide.editor.api.EditorTokenListPreparedHandler;
import org.exoplatform.ide.editor.api.SelectionRange;
import org.exoplatform.ide.editor.api.codeassitant.CanInsertImportStatement;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantEvent;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorContextMenuEvent;
import org.exoplatform.ide.editor.api.event.EditorContextMenuHandler;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedHandler;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedEvent;
import org.exoplatform.ide.editor.api.event.EditorHotKeyPressedHandler;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedHandler;
import org.exoplatform.ide.editor.marking.EditorLineNumberContextMenuEvent;
import org.exoplatform.ide.editor.marking.EditorLineNumberContextMenuHandler;
import org.exoplatform.ide.editor.marking.EditorLineNumberDoubleClickEvent;
import org.exoplatform.ide.editor.marking.EditorLineNumberDoubleClickHandler;
import org.exoplatform.ide.editor.marking.Markable;
import org.exoplatform.ide.editor.marking.Marker;
import org.exoplatform.ide.editor.marking.ProblemClickEvent;
import org.exoplatform.ide.editor.marking.ProblemClickHandler;
import org.exoplatform.ide.editor.notification.NotificationWidget;
import org.exoplatform.ide.editor.notification.OverviewRuler;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.Document;
import org.exoplatform.ide.editor.text.DocumentEvent;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IDocumentListener;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.rebind.rpc.ProblemReport.Problem;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeMirror Feb 9, 2011 4:58:14 PM $
 * 
 */
public class CodeMirror extends AbsolutePanel implements Editor, Markable, IDocumentListener, CanInsertImportStatement
{
  
   /**
    * Height of line in the CodeMirror in pixels.
    */
   public static final int LINE_HEIGHT = 16;
   
   /**
    * Width of column with line numbers in pixels.
    */
   public static final int LINE_NUMBERS_COLUMN_WIDTH = 48;
   
   /**
    * Width of character in the CodeMirror in pixels.
    */
   public static final int CHARACTER_WIDTH = 8;

   /**
    * Offset of the lines from left in pixels.
    */
   public static final int LINE_OFFSET_LEFT = 11;

   
   public static final int codeErrorCorrectionPopupOffsetLeft = 6; // top offset of character of the line in px

   private static int codeErrorCorrectionPopupOffsetTop = 22; // top offset of character of the line in px
   
   
   private final Browser currentBrowser = BrowserResolver.CURRENT_BROWSER;   
   
   
   
   /**
    * Editor's ID.
    */
   private final String id;
   
   /**
    * Media type of document.
    */
   private final String mimeType;
   
   /**
    * Configuration of CodeMirror.
    */
   protected final CodeMirrorConfiguration configuration;
   
   /**
    * Visibility of line numbers column.
    */
   private boolean showLineNumbers = false;

   /**
    * Visibility of overview column.
    */
   protected boolean showOverview = false;
   
   
   protected TextArea textArea;

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
   
   /**
    * Creates new CodeMirror instance.
    * 
    * @param mimeType
    */
   public CodeMirror(String mimeType)
   {
      this(mimeType, new CodeMirrorConfiguration());
   }
   
   /**
    * Creates new CodeMirror instance.
    * 
    * @param mimeType
    * @param configuration
    */
   public CodeMirror(String mimeType, CodeMirrorConfiguration configuration)
   {
      id = "CodeMirror - " + String.valueOf(this.hashCode());
      this.mimeType = mimeType;
      this.configuration = configuration;
   }
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#getMimeType()
    */
   @Override
   public String getMimeType()
   {
      return mimeType;
   }

   /**
    * @see com.google.gwt.user.client.ui.Panel#onLoad()
    */
   @Override
   protected void onLoad()
   {
      super.onLoad();
      
//      System.out.println("CodeMirror.onLoad() Editor ID > " + id);
      
      DockLayoutPanel doc = new DockLayoutPanel(Unit.PX);
      doc.setSize("100%", "100%");
      add(doc);

      if (showOverview)
      {
         overviewRuler = new OverviewRuler(this);
         doc.addEast(overviewRuler, 13);         
      }
      
      absPanel = new AbsolutePanel();
      doc.add(absPanel);
      textArea = new TextArea();
      DOM.setElementAttribute(textArea.getElement(), "id", getId());
      textArea.setVisible(false);
      absPanel.add(textArea);

      lineHighlighter = getLineHighlighter();
      absPanel.add(lineHighlighter);
      absPanel.setWidgetPosition(lineHighlighter, 0, 5);

      //eventBus.addHandler(EditorTokenListPreparedEvent.TYPE, this);
      
      document = new Document("");
      document.addDocumentListener(this);
      
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            editorObject =
               initCodeMirror(id, "", "100%", readOnly, configuration.getContinuousScanning(), configuration.isTextWrapping(), showLineNumbers,
                  configuration.getCodeStyles(), configuration.getCodeParsers(), configuration.getJsDirectory(), configuration.getTabMode().toString());
         }
      });
   }

   @Override
   protected void onUnload()
   {
      super.onUnload();
      
//      System.out.println("CodeMirror.onUnload() Editor ID > " + id);
      
      codeValidateTimer.cancel();
      
       if (configuration.getParser() != null)
       {
          configuration.getParser().stopParsing();
       }
      
      
      
//
//      eventBus.removeHandler(EditorTokenListPreparedEvent.TYPE, this);
//
   }
   

   private native JavaScriptObject initCodeMirror(String id, String width, String height, boolean readOnly, int cs, boolean tr,
      boolean lineNumbers, String styleURLs, String parserNames, String jsDirectory, String modeTab)
   /*-{
		var instance = this;
		var changeFunction = function()
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onContentChanged()();
		};

		var cursorActivity = function(cursor)
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onCursorActivity(Lcom/google/gwt/core/client/JavaScriptObject;)(cursor);
		};

		var onLineNumberClick = function(lineNumber)
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberClick(I)(lineNumber);
		};

		var onLineNumberDoubleClick = function(lineNumber)
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberDoubleClick(I)(lineNumber);
		}

		var onLineNumberContextMenu = function(lineNumber, e)
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onLineNumberContextMenu(ILcom/google/gwt/dom/client/NativeEvent;)(lineNumber, e);
		}

		var initCallback = function()
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::onInitialized()();
		};

		var activeTokensFunction = function()
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::needUpdateTokenList = true;
		};

		var editor = $wnd.CodeMirror.fromTextArea(id, {
			width : width,
			height : height,
			parserfile : eval(parserNames),
			stylesheet : eval(styleURLs),
			path : jsDirectory,
			continuousScanning : cs || false,
			undoDelay : 50, // decrease delay before calling 'onChange' callback
			lineNumbers : lineNumbers,
			readOnly : readOnly,
			textWrapping : tr,
			tabMode : modeTab,
			content : "", // to fix bug with blocked deleting function of CodeMirror just after opening file [WBT-223]
			onChange : changeFunction,
			reindentOnLoad : false, // to fix problem with getting token list after the loading content
			onCursorActivity : cursorActivity,
			onLineNumberClick : onLineNumberClick,
			onLineNumberDoubleClick : onLineNumberDoubleClick,
			onLineNumberContextMenu : onLineNumberContextMenu,
			onLoad : initCallback,
			autoMatchParens : true,

			// Take the token before the cursor. If it contains a character in '()[]{}', search for the matching paren/brace/bracket, and
			// highlight them in green for a moment, or red if no proper match was found.
			markParen : function(node, ok)
			{
				node.id = ok ? "parenCorrect" : "parenIncorrect";
			},
			
			unmarkParen : function(node)
			{
				node.id = null;
			},

			// to update outline panel after the new line has being highlighted
			activeTokens : activeTokensFunction
		});

		return editor;
   }-*/;

   /**
    * Called by CodeMirror after it's instance has been initialized.
    */
   private void onInitialized()
   {
      addHighlighterListeners();
      addKeyPressedListener();
      addFocusReceivedListeners();
      addContextMenuListener();
      
      //setText(initialContent);

      this.needUpdateTokenList = true; // update token list after the document had been loaded and reindented
      // turn on code validation time
      if (configuration.canBeValidated())
      {
         needValidateCode = true;
         codeValidateTimer.scheduleRepeating(2000);
      }

      //eventBus.fireEvent(new EditorInitializedEvent(id));
      fireEvent(new EditorInitializedEvent(id));
      
      if (initialText != null)
      {
         setText(initialText);
      }
      
      if (cursorPositionRow != 0 || cursorPositionCol != 0)
      {
         setCursorPosition(cursorPositionRow, cursorPositionCol);
      }      
   }

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
      fireEvent(new EditorLineNumberDoubleClickEvent(lineNumber));
      if (activeNotification != null)
         activeNotification.update();
   }

   private void onLineNumberContextMenu(int lineNumber, NativeEvent event)
   {
      event.stopPropagation();
      event.preventDefault();
      fireEvent(new EditorLineNumberContextMenuEvent(lineNumber, event.getClientX(), event.getClientY()));
      //eventBus.fireEvent(new EditorLineNumberContextMenuEvent(lineNumber, event.getClientX(), event.getClientY()));
   }

   private void onContentChanged()
   {
      needUpdateTokenList = true;
      needUpdateDocument = true;
      fireEvent(new EditorContentChangedEvent(getId()));
      //eventBus.fireEvent(new EditorContentChangedEvent(getId()));
   }

   private void onCursorActivity(JavaScriptObject cursor)
   {
      cursorPositionCol = getCursorColumn();

      if (BrowserResolver.CURRENT_BROWSER == Browser.IE)
      {
         if (getNumberOfLines() == 1)
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

      fireEvent(new EditorCursorActivityEvent(id, cursorPositionRow, cursorPositionCol));
      //eventBus.fireEvent(new EditorCursorActivityEvent(id, cursorPositionRow, cursorPositionCol));
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
   private native void fixCodeMirrorIframeTransparencyInIE()
   /*-{
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
   public native int getCursorOffsetY(int currentLine)
   /*-{
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

   private boolean handleKeyPressing(boolean isCtrl, boolean isAlt, boolean isShift, int keyCode)
   {
      EditorHotKeyPressedEvent event = new EditorHotKeyPressedEvent(isCtrl, isAlt, isShift, keyCode);
      fireEvent(event);
      //eventBus.fireEvent(event);
      return event.isHotKeyHandled();
   }

   /**
    *
    */
   private native void addKeyPressedListener()
   /*-{
		var instance = this;
		var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;

      editor.grabKeys(
         function(event) {},
         function(keyCode, event)
         {
            if (event.type == "keydown")
            {
               var isCtrl = event.ctrlKey || event.metaKey;               
               var isAlt = event.altKey;
               var isShift = event.shiftKey;
               
               var cancelEvent = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::handleKeyPressing(ZZZI)(isCtrl, isAlt, isShift, keyCode);
               if (cancelEvent)
               {
                  event.stop();
                  return true;
               }
            }

            return false;
         });
   }-*/;

   /**
    * Handle autocompletion.
    */
   public native void onAutocomplete()
   /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
		{
			return;
		}

		var cursor = editor.cursorPosition(true);
		var lineContent = editor.lineContent(cursor.line);

		// get fqn of current node
		if (editor.nextLine(cursor.line) != null && editor.nextLine(cursor.line).previousSibling)
		{
			var currentNode = editor.nextLine(cursor.line).previousSibling;
		}

		this.@org.exoplatform.ide.editor.codemirror.CodeMirror::callAutocompleteHandler(Ljava/lang/String;Lcom/google/gwt/core/client/JavaScriptObject;)(lineContent, currentNode);
   }-*/;

   private void callAutocompleteHandler(String lineContent, JavaScriptObject currentNode)
   {
      if (mimeType.equals(MimeType.APPLICATION_JAVA))
      {
         fireEvent(new RunCodeAssistantEvent());
         //eventBus.fireEvent(new RunCodeAssistantEvent());
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
         tokenList = (List<TokenBeenImpl>)getTokenList();

         // to update token's FQNs
         if (configuration.canBeValidated())
         {
            needValidateCode = false;
            validateCode(tokenList);
         }
      }

      Token tokenBeforeCursor = getTokenBeforeCursor(this.tokenList, currentNode, cursorRow, cursorPositionCol);

      List<? extends Token> selectedTokenList = this.tokenList;

      // read mimeType
      String currentLineMimeType = getCurrentLineMimeType();
      if (configuration.canHaveSeveralMimeTypes() && !mimeType.equals(currentLineMimeType))
      {
         selectedTokenList =
            (List<TokenBeenImpl>)CodeValidator.extractCode((List<TokenBeenImpl>)this.tokenList,
               new LinkedList<TokenBeenImpl>(), currentLineMimeType);
      }

      if (configuration.getCodeAssistant() != null)
      {
         configuration.getCodeAssistant().autocompleteCalled(this, cursorOffsetX, cursorOffsetY, (List<Token>)selectedTokenList,
            currentLineMimeType, tokenBeforeCursor);         
      }      
   }

   /**
    * @param cursorCol
    * @return
    */
   public int getCursorOffsetX()
   {
      int cursorOffsetX = (cursorPositionCol - 2) * CHARACTER_WIDTH + getAbsoluteLeft() + LINE_OFFSET_LEFT; // 8px per symbol
      if (this.showLineNumbers)
      {
         cursorOffsetX += LINE_NUMBERS_COLUMN_WIDTH;
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

   /**
    * 
    */
   private native void addHighlighterListeners()
   /*-{
      var instance = this;
		var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		
		var highlightFunction = function()
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::highlightLine(I)(0);
		};

		// draw highlighter at start           
		highlightFunction();

		if (editor.win)
		{
			switch (instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser)
			{
			   case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
				   if (editor.win.attachEvent)
				   {
					   editor.win.attachEvent("onscroll", highlightFunction);
					   editor.win.attachEvent("onresize", highlightFunction);
				   }
               break;
			
            default:
				   if (editor.win.addEventListener)
				   {
					   editor.win.addEventHandler(editor.win, "scroll", highlightFunction, true);
					   editor.win.addEventHandler(editor.win, "resize", highlightFunction, true);
				   }
         }
		}
   }-*/;

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
            tokenList = (List<TokenBeenImpl>)getTokenList();
         }

         String mimeType = CodeMirrorParserImpl.getLineMimeType(cursorPositionRow, tokenList);
         if (mimeType != null)
         {
            return mimeType;
         }
      }

      return this.mimeType;
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
   private native void addFocusReceivedListeners()
   /*-{
		var instance = this;
        var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;

		var focusReceivedListener = function()
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorFocusReceivedEvent()();
		};

		if (editor)
		{
			switch (instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser)
			{
			   case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE:
				   if (editor.win.document.body.attachEvent)
				   {
					   editor.win.document.body.attachEvent("onmouseup", focusReceivedListener);
               }
				   break;
			
			   default:
				   if (editor.win.addEventListener)
				   {
					   editor.win.addEventHandler(editor.win, "mouseup", focusReceivedListener, true);
               }
			}
		}
   }-*/;

   private void fireEditorFocusReceivedEvent()
   {
      fireEvent(new EditorFocusReceivedEvent(getId()));
   }

   /**
    * if there is line numbers left field, then validate code and mark lines with errors
    */
   public void validateCode()
   {
      if (needUpdateTokenList && showLineNumbers)
      {
         needValidateCode = true;
         configuration.getParser().getTokenListInBackground(id, editorObject, tokenListReceivedHandler);
      }
   }

   public void forceValidateCode()
   {
      if (!showLineNumbers)
      {
         return;
      }

      if (needUpdateTokenList)
      {
         needValidateCode = true;
         configuration.getParser().getTokenListInBackground(id, editorObject, tokenListReceivedHandler);
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
            if (configuration.getCodeAssistant() != null)
            {
               configuration.getCodeAssistant().errorMarkClicked(CodeMirror.this, CodeValidator.getCodeErrorList(lineNumber, codeErrorList),
                  (getAbsoluteTop() + getCursorOffsetY(lineNumber) + codeErrorCorrectionPopupOffsetTop),
                  (getAbsoluteLeft() + LINE_NUMBERS_COLUMN_WIDTH + codeErrorCorrectionPopupOffsetLeft),
                  mimeType);
            }            
         }

         try
         {
            // collect all problems on this line
            if (problems.containsKey(lineNumber))
            {
               List<Marker> list = problems.get(lineNumber);

               fireEvent(new ProblemClickEvent(list.toArray(new Marker[list.size()])));
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
    * @see org.exoplatform.ide.editor.api.Editor#getId()
    */
   @Override
   public String getId()
   {
      return id;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getText()
    */
   @Override
   public native String getText()
   /*-{
      var instance = this;
      var editor = instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      return editor.getCode();
   }-*/;
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#setText(java.lang.String)
    */
   @Override
   public void setText(String text)
   {
      if (editorObject == null)
      {
         initialText = text;
         return;
      }
      
      setText(editorObject, text);
   }

   private native void setText(JavaScriptObject editor, String text)
   /*-{
		if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor))
		{
			return;
		}

      if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::CHROME)
      {
         if (text === "")
         {
            text = "\n"; // fix error with initial cursor position and size (WBT-324)
         }
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

   private native void formatSource(String text, JavaScriptObject editor)
   /*-{
		if (text != ' ')
		{
			editor.reindent();
		}
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#showLineNumbers(boolean)
    */
   public void showLineNumbers(boolean showLineNumbers)
   {
      this.showLineNumbers = showLineNumbers;
      
      if (editorObject == null)
      {
         return;
      }
      
      showLineNumbersNative(showLineNumbers);
      if (showLineNumbers && configuration.canBeValidated())
      {
         udpateErrorMarks(codeErrorList);
         needValidateCode = true;
      }
   };
   
   /**
    * @param showLineNumbers
    */
   private native void showLineNumbersNative(boolean showLineNumbers)
   /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      editor.setLineNumbers(showLineNumbers);
   }-*/;   

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setFocus()
    */
   @Override
   public void setFocus()
   {
      setCursorPosition(cursorPositionRow, cursorPositionCol);

      setFocus(editorObject);
   }

   private native void setFocus(JavaScriptObject editor)
   /*-{
		if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor))
		{
			return;
		}

		editor.focus();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setCursorPosition(int, int)
    */
   @Override
   public void setCursorPosition(int row, int column)
   {
      cursorPositionRow = row;
      cursorPositionCol = column;
      goToPosition(editorObject, row, column);
   }

   private void fireEditorCursorActivityEvent(String editorId, int cursorRow, int cursorCol)
   {
      fireEvent(new EditorCursorActivityEvent(editorId, cursorRow, cursorCol));
   }

   private native void goToPosition(JavaScriptObject editor, int row, int column)
   /*-{
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
            this.@org.exoplatform.ide.editor.codemirror.CodeMirror::fireEditorCursorActivityEvent(Ljava/lang/String;II)(this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getId()(),row,column);
         }
      }
   }-*/;

   public native boolean canGoToLine(int lineNumber)
   /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
		{
			return false;
		}

		return editor.nthLine(lineNumber) !== false;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#deleteCurrentLine()
    */
   @Override
   public native void deleteCurrentLine()
   /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
		{
			return;
		}

		var currentLineNumber = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::cursorPositionRow;
		var currentLine = editor.nthLine(currentLineNumber);

		if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE
				&& this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getNumberOfLines()() == currentLineNumber)
      {
			// clear current line
			this.@org.exoplatform.ide.editor.codemirror.CodeMirror::clearLastLine()();
		}
		else
		{
			editor.removeLine(currentLine);
		}

		currentLineNumber = editor.lineNumber(currentLine);
		this.@org.exoplatform.ide.editor.codemirror.CodeMirror::setCursorPosition(II)(currentLineNumber,1);
   }-*/;

   /**
    * Correct clear the last line of content that the line break is being remained
    */
   private native void clearLastLine()
   /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
		{
			return;
		}

		var content = editor.getCode();
		var lastLineHandler = editor.lastLine();
		if (content.charAt(content.length - 1) == "\n")
		{
			editor.setLineContent(lastLineHandler, "");
		}
		else
		{
			editor.setLineContent(lastLineHandler, "\n");
		}
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#findAndSelect(java.lang.String, boolean)
    */
   @Override
   public native boolean findAndSelect(String find, boolean caseSensitive)
   /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null)
		{
			return;
		}

		var isFound = false;
		var cursor = editor.getSearchCursor(find, true, !caseSensitive); // getSearchCursor(string, atCursor, caseFold) -> cursor
		if (isFound = cursor.findNext())
		{
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

   private native void replaceFoundedText(JavaScriptObject editor, String find, String replace, boolean caseSensitive)
   /*-{
		if (editor == null)
		{
			return;
		}
		
		var selected = editor.selection();

		if (!caseSensitive)
		{
			selected = selected.toLowerCase();
			find = find.toLowerCase();
		}

		if (selected == find)
		{
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

   private native boolean hasUndoChanges(JavaScriptObject editor)
   /*-{
		if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor))
		{
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

   private native void undo(JavaScriptObject editor)
   /*-{
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

   private native boolean hasRedoChanges(JavaScriptObject editor)
   /*-{
		if (!this.@org.exoplatform.ide.editor.codemirror.CodeMirror::checkGenericCodeMirrorObject(Lcom/google/gwt/core/client/JavaScriptObject;)(editor))
		{
			return false;
		}

		return editor.historySize().redo > 0;
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#redo()
    */
   public native void redo()
   /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		editor.redo();
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#isReadOnly()
    */
   @Override
   public boolean isReadOnly()
   {
      return readOnly;
   }

   @Override
   public int getCursorRow()
   {
      return cursorPositionRow;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorRow()
    */
   public native int getNativeCursorRow()
   /*-{
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
    * @see org.exoplatform.ide.editor.api.Editor#getCursorColumn()
    */
   @Override
   public native int getCursorColumn()
   /*-{
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
   private native int getCursorActivityRow(JavaScriptObject cursor, int cursorCol)
   /*-{
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
    * @see org.exoplatform.ide.editor.api.Editor#getTokenList()
    */
   public List<? extends Token> getTokenList()
   {
      return (List<TokenBeenImpl>)configuration.getParser().getTokenList(id, editorObject);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getTokenList()
    */
   public void getTokenListInBackground()
   {
      if (needUpdateTokenList)
      {
         configuration.getParser().getTokenListInBackground(id, editorObject, tokenListReceivedHandler);
      }
      else
      {
         tokenListReceivedHandler.onEditorTokenListPrepared(new EditorTokenListPreparedEvent(id, this.tokenList));
         //eventBus.fireEvent(new EditorTokenListPreparedEvent(id, this.tokenList));
      }
   }

   private EditorTokenListPreparedHandler tokenListReceivedHandler = new EditorTokenListPreparedHandler()
   {
      @Override
      public void onEditorTokenListPrepared(EditorTokenListPreparedEvent event)
      {
         if (!id.equals(event.getEditorId()))
         {
            return;
         }

         if (needUpdateTokenList)
         {
            needUpdateTokenList = false;
            tokenList = (List<TokenBeenImpl>)event.getTokenList();
         }

         if (needValidateCode)
         {
            validateCode(tokenList);
         }         
      }
   };
   
   /**
    * @see org.exoplatform.ide.editor.api.Editor#replaceTextAtCurrentLine(java.lang.String, int)
    */
   @Override
   public native void replaceTextAtCurrentLine(String line, int cursorPosition)
   /*-{
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
   private native void insertIntoLine(String newText, int lineNumber)
   /*-{
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

   @Override
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

   private native String getLineContent(JavaScriptObject editor, int lineNumber)
   /*-{
		var handler = editor.nthLine(lineNumber);
		return editor.lineContent(handler);
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getLineText(int)
    */
   @Override
   public String getLineText(int line)
   {
      return getLineContent(editorObject, line);
   }   
   
   /**
    * Check if CodeMirror editor instance consists of neccessery objects.
    * 
    * @param editor
    * @return
    */
   private native boolean checkGenericCodeMirrorObject(JavaScriptObject editor)
   /*-{
		return (editor != null) && (typeof editor != 'undefined')
				&& (typeof editor.editor != 'undefined');
   }-*/;


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
   private Map<Integer, List<Marker>> problems = new HashMap<Integer, List<Marker>>();

   /**
    * Visible notification.
    */
   private NotificationWidget activeNotification;

   private AbsolutePanel absPanel;

   private OverviewRuler overviewRuler;

   /**
    * @see org.exoplatform.ide.editor.marking.Markable#markProblem(org.exoplatform.ide.editor.marking.Marker)
    */
   @Override
   public void markProblem(Marker problem)
   {
      if (!problems.containsKey(problem.getLineNumber()))
         problems.put(problem.getLineNumber(), new ArrayList<Marker>());
      problems.get(problem.getLineNumber()).add(problem);

      StringBuilder message = new StringBuilder();
      List<Marker> problemList = problems.get(problem.getLineNumber());
      boolean hasError = fillMessages(problemList, message);

      String markStyle = getStyleForLine(problemList, hasError);

      markProblemmeLine(problem.getLineNumber(), message.toString(), markStyle);
      if (overviewRuler != null)
      {
         overviewRuler.addProblem(problem, message.toString());
      }
   }

   /**
    * @param markerList
    * @param hasError
    * @return
    */
   private String getStyleForLine(List<Marker> markerList, boolean hasError)
   {
      String markStyle = null;
      if (hasError)
      {
         markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkError();
      }
      else
      {
         markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkBreakpoint();
         for (Marker p : markerList)
         {
            if (p.isWarning())
            {
               markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkWarning();

            }
            if (p.isCurrentBreakPoint())
            {
               markStyle = CodeMirrorClientBundle.INSTANCE.css().codeMarkBreakpointCurrent();
               break;
            }
         }
      }
      return markStyle;
   }

   private boolean fillMessages(List<Marker> markers, StringBuilder message)
   {
      boolean hasError = false;
      List<String> messages = new ArrayList<String>();

      for (Marker p : markers)
      {
         messages.add(p.getMessage());
         if (!hasError && p.isError())
         {
            hasError = true;
         }
      }

      if (messages.size() == 1)
      {
         message.append(markers.get(0).getMessage());
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
    * @see org.exoplatform.ide.editor.marking.Markable#unmarkAllProblems()
    */
   @Override
   public void unmarkAllProblems()
   {
      removeNotification();

      List<Marker> breakpoins = new ArrayList<Marker>();
      for (Integer key : problems.keySet())
      {
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

   private Marker getBreakpoint(List<Marker> problems)
   {
      for (Marker p : problems)
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
    * @see org.exoplatform.ide.editor.marking.Markable#unmarkProblem(org.exoplatform.ide.editor.marking.Marker)
    */
   @Override
   public void unmarkProblem(Marker problem)
   {
      removeNotification();
      if (problems.containsKey(problem.getLineNumber()))
      {
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

   /**
    * Removes style from mark element ( is line number element )
    */
   private native void unmarkNative(int lineNumber)
   /*-{
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
   public HandlerRegistration addLineNumberDoubleClickHandler(EditorLineNumberDoubleClickHandler handler)
   {
      return addHandler(handler, EditorLineNumberDoubleClickEvent.TYPE);
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

      activeNotification = new NotificationWidget(el);
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
    * Marks line as problem.
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
         markProblemmeLine(newCodeError.getLineNumber(), CodeValidator.getErrorSummary(lineCodeErrorList), configuration.getCodeErrorMarkStyle());
      }
      
      configuration.getParser().getTokenListInBackground(id, editorObject, tokenListReceivedHandler);

      codeErrorList = newCodeErrorList;
   }

   /**
    * Marks line as problem.
    * 
    * @param lineNumber line number, starts at 1
    * @param errorSummary
    * @param markStyle
    */
   public native void markProblemmeLine(int lineNumber, String errorSummary, String markStyle)
   /*-{
		var instance = this;
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;

		var over = function(jso)
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::markerMouseOver(Lcom/google/gwt/core/client/JavaScriptObject;)(jso);
		};

		var out = function(jso)
		{
			instance.@org.exoplatform.ide.editor.codemirror.CodeMirror::markerMouseOut(Lcom/google/gwt/core/client/JavaScriptObject;)(jso);
		};

		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].setAttribute("class", markStyle);
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].setAttribute("title", errorSummary);
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseover = over;
		editor.lineNumbers.childNodes[0].childNodes[lineNumber - 1].onmouseout = out;
   }-*/;

   /**
    * Clear error mark from lineNumbers field
    * 
    * @param lineNumber starting from 1
    */
   public native void clearErrorMark(int lineNumber)
   /*-{
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

   @Override
   public native void setLineText(int line, String text)
   /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      var handle = editor.nthLine(line);
      editor.setLineContent(handle, text);
   }-*/;

   private native void deleteLine(JavaScriptObject editor, int line)
   /*-{
		var lineHandler = editor.nthLine(line);

		if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser != @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE
				&& this.@org.exoplatform.ide.editor.codemirror.CodeMirror::getNumberOfLines()() == line) {
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
         StringBuilder b = new StringBuilder(getLineText(lineNumber));
         int length = col + event.getLength();
         int nextLine = lineNumber + 1;
         while (length > b.length())
         {
            b.append(getLineText(nextLine));
            deleteLine(editorObject, nextLine);
            // symbol '\n' not present in line content
            length--;
         }
         b.replace(col, length, event.getText());
         setLineText(lineNumber, b.toString());
      }
      catch (BadLocationException e)
      {
         e.printStackTrace();
      }
   }

   public native SelectionRange getSelectionRange()
   /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		if (editor == null) {
			return null;
		}

		var start = editor.cursorPosition(true);
		var startLine = editor.lineNumber(start.line);
		var end = editor.cursorPosition(false);
		var endLine = editor.lineNumber(end.line);
		return @org.exoplatform.ide.editor.api.SelectionRange::new(IIII)(startLine, start.character, endLine, end.character);
   }-*/;

   private native void addContextMenuListener()
   /*-{
		var instance = this;
		var contextMenuListener = function(e) {
			if (!e)
				var e = $wnd.event;
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

   private void onContextMenu(NativeEvent event)
   {
      event.stopPropagation();
      event.preventDefault();
      int x = event.getClientX() + getAbsoluteLeft() + LINE_NUMBERS_COLUMN_WIDTH;
      int y = event.getClientY() + getAbsoluteTop();
      fireEvent(new EditorContextMenuEvent(x, y, getId()));
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#selectAll()
    */
   @Override
   public void selectAll()
   {
      executeCommand("selectAll");
   };

   /**
    * @see org.exoplatform.ide.editor.api.Editor#cut()
    */
   @Override
   public void cut()
   {
      executeCommand("cut");
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#copy()
    */
   @Override
   public void copy()
   {
      executeCommand("copy");
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#paste()
    */
   @Override
   public void paste()
   {
      executeCommand("paste");
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#delete()
    */
   @Override
   public void delete()
   {
      executeCommand("delete");
   }

   private native void executeCommand(String command)
   /*-{
		var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
		var frame = editor.frame;
		if (this.@org.exoplatform.ide.editor.codemirror.CodeMirror::currentBrowser == @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE)
		{
			frame.contentWindow.document.execCommand(command, false, null);
		}
		else
		{
			frame.contentDocument.execCommand(command, false, null);
		}
   }-*/;

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getNumberOfLines()
    */
   @Override
   public native int getNumberOfLines()
   /*-{
      var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
      return editor.lineNumber(editor.lastLine());
   }-*/;

   @Override
   public native void selectRange(int startLine, int startOffset, int endLine, int endOffset)
   /*-{
      try
      {
         var editor = this.@org.exoplatform.ide.editor.codemirror.CodeMirror::editorObject;
         var startHandle = editor.nthLine(startLine);
         var endHandle = editor.nthLine(endLine);
         editor.selectLines(startHandle, startOffset, endHandle, endOffset)
      }
      catch (e)
      {
        alert('error > ' + e.message);
      }
   }-*/;
   
   /**
    * @see org.exoplatform.ide.editor.problem.Markable#addLineNumberContextMenuHandler(org.exoplatform.ide.editor.problem.LineNumberContextMenuHandler)
    */
   @Override
   public HandlerRegistration addLineNumberContextMenuHandler(EditorLineNumberContextMenuHandler handler)
   {
      return addHandler(handler, EditorLineNumberContextMenuEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#setReadOnly(boolean)
    */
   @Override
   public void setReadOnly(boolean readOnly)
   {
      this.readOnly = readOnly;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getName()
    */
   @Override
   public String getName()
   {
      return "Source";
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorOffsetLeft()
    */
   @Override
   public int getCursorOffsetLeft()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#getCursorOffsetTop()
    */
   @Override
   public int getCursorOffsetTop()
   {
      return 0;
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addContentChangedHandler(org.exoplatform.ide.editor.api.event.EditorContentChangedHandler)
    */
   @Override
   public HandlerRegistration addContentChangedHandler(EditorContentChangedHandler handler)
   {
      return addHandler(handler, EditorContentChangedEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addContextMenuHandler(org.exoplatform.ide.editor.api.event.EditorContextMenuHandler)
    */
   @Override
   public HandlerRegistration addContextMenuHandler(EditorContextMenuHandler handler)
   {
      return addHandler(handler, EditorContextMenuEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addCursorActivityHandler(org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler)
    */
   @Override
   public HandlerRegistration addCursorActivityHandler(EditorCursorActivityHandler handler)
   {
      return addHandler(handler, EditorCursorActivityEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addFocusReceivedHandler(org.exoplatform.ide.editor.api.event.EditorFocusReceivedHandler)
    */
   @Override
   public HandlerRegistration addFocusReceivedHandler(EditorFocusReceivedHandler handler)
   {
      return addHandler(handler, EditorFocusReceivedEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addHotKeyPressedHandler(org.exoplatform.ide.editor.api.event.EditorHotKeyPressedHandler)
    */
   @Override
   public HandlerRegistration addHotKeyPressedHandler(EditorHotKeyPressedHandler handler)
   {
      return addHandler(handler, EditorHotKeyPressedEvent.TYPE);
   }

   /**
    * @see org.exoplatform.ide.editor.api.Editor#addInitializedHandler(org.exoplatform.ide.editor.api.event.EditorInitializedHandler)
    */
   @Override
   public HandlerRegistration addInitializedHandler(EditorInitializedHandler handler)
   {
      return addHandler(handler, EditorInitializedEvent.TYPE);
   }
   
}
