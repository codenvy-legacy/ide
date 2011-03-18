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
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.api.event.EditorHotKeyCalledEvent;
import org.exoplatform.ide.editor.api.event.EditorHotKeyCalledHandler;
import org.exoplatform.ide.editor.codemirror.autocomplete.JavaScriptAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.parser.JavaScriptParser;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class CodeMirrorGwtTestJavaScriptParser extends Base
{

   CodeMirror editor;
   HandlerManager eventBus;
   
//   final WebClient webClient;
   
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.editor.EditorTest";
   }
   
   /**
    * @see com.google.gwt.junit.client.GWTTestCase#gwtSetUp()
    */
   @Override
   protected void gwtSetUp() throws Exception
   {      
      super.gwtSetUp();
      
//      webClient = new WebClient();
      
      System.out.println(">>>>>>>>>>>>>>>> create editor with codeMirror instance");
      
      final HashMap<String, Object> params = new HashMap<String, Object>();

      params.put(EditorParameters.IS_READ_ONLY, false);
      params.put(EditorParameters.IS_SHOW_LINE_NUMER, true);
      params.put(EditorParameters.HOT_KEY_LIST, new ArrayList<String>());
      params.put(EditorParameters.MIME_TYPE, MimeType.APPLICATION_JAVASCRIPT);      
      params.put(EditorParameters.CONFIGURATION, new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
         "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", true, true, new JavaScriptParser(), new JavaScriptAutocompleteHelper(),
         
         new MockJavaScriptCodeAssistant()  // replace on our class to intercept autocomplete calling event
      ));
      
      HandlerManager eventBus = new HandlerManager(null);   
      
      eventBus.addHandler(EditorHotKeyCalledEvent.TYPE, new EditorHotKeyCalledHandler(){

         public void onEditorHotKeyCalled(EditorHotKeyCalledEvent event)
         {
            System.out.println(">>>>>>>>>>> onCodeMirrorEditorHotKeyCalled = " + event.getHotKey());                  
         }
                      
      });
      
      editor = new CodeMirror("", params, eventBus);

      editor.setHotKeyList(new ArrayList<String>(){{
         add("Ctrl+70"); // Ctrl+F
         add("Ctrl+68"); // Ctrl+D
         add("Ctrl+83"); // Ctrl+S
         add("Alt+70");  // Alt+F             
      }});
      
      RootPanel.get().add(editor);
   }
   
   public void testJavaScriptVariableParsing()
   {
      new Timer()
      {
         
         @Override
         public void run()
         {
            cancel();
            System.out.println(">>>>>>>>>>>>>>>> start checking codeMirror");
            
//            editor.setText("var a = 1; \n function()\n { a. \n  }");
            editor.setText(CodeMirrorTestBundle.INSTANCE.javaScriptParserTest().getText());
            
//            editor.goToPosition(3, 6);   // set cursor after the "a._"
            
            // press Ctrl + Space

            // press Ctrl + S
            keyPress(83, true, false, false, editor.editorId);

            new Timer()
            {

               @Override
               public void run()
               {
                  cancel();
                  System.out.println(">>>>>>>>>>>>>>>> check parsing results");                
                  
                  List<TokenBeenImpl> tokenList = (List<TokenBeenImpl>) editor.getTokenList();

//                testTokenList(correctTokenList, tokenList);
                  
                  assertEquals(2, tokenList.size());
                  assertEquals("a", tokenList.get(0).getName());
                  assertEquals(TokenType.VARIABLE, tokenList.get(0).getType());
                  assertEquals("Number", tokenList.get(0).getElementType());            
                  assertEquals(null, tokenList.get(0).getInitializationStatement());
                  assertEquals(1, tokenList.get(0).getLineNumber());           

                  
                  editor.ctrlSpaceClickHandler();
                  
                  finishTest();
                  
               }


            }.schedule(CODEMIRROR_TEXT_PARSING_PERIOD_MILISEC);
            
            
         }
      }.schedule(CODEMIRROR_LOADING_PERIOD_MILISEC);
      
      delayTestFinish(DELAY_TEST_FINISH_MILISEC);
   }

//   private void testTokenList(List<CodeMirrorTokenImpl> expectedTokenList, List<CodeMirrorTokenImpl> testTokenList)
//   {
//      for (CodeMirrorTokenImpl token: expectedTokenList)
//      {
//         testToken();        
//      }
//
//   }
//
//   private void testToken(CodeMirrorTokenImpl expectedToken, CodeMirrorTokenImpl testToken)
//   {
//      testToken
//      
//      assertEquals(tokenList.size(), 1);
//      assertEquals(tokenList.get(0).getName(), "a");
//      assertEquals(tokenList.get(0).getType(), TokenType.VARIABLE);
//      assertEquals(tokenList.get(0).getElementType(), "Number");            
//      assertEquals(tokenList.get(0).getInitializationStatement(), null);
//      assertEquals(tokenList.get(0).getLineNumber(), 1);
//   }
   
   class MockJavaScriptCodeAssistant extends org.exoplatform.ide.editor.codeassistant.javascript.JavaScriptCodeAssistant
   {

      @Override
      protected void openForm(List<Token> tokens, TokenWidgetFactory factory,
         TokenSelectedHandler handler)
      {
         System.out.println(">>>>>>>>>>>>>>>> openForm");
      }

      @Override
      public void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
         String lineContent, int cursorPositionX, int cursorPositionY, List<Token> tokenList,
         String lineMimeType, Token currentToken)
      {
         System.out.println(">>>>>>>>>>>>>>>> check autocompleteCalled parameters");
         
         assertEquals(MimeType.APPLICATION_JAVASCRIPT, mimeType);
         assertEquals(" { a. ", lineContent);
         assertEquals(6, cursorPositionX);         
         assertEquals(3, cursorPositionY);         
         assertEquals(MimeType.APPLICATION_JAVASCRIPT, lineMimeType);         
         
         // test current token
//         testToken(correctToken, currentToken);
         assertEquals("a", currentToken.getName());
         assertEquals(TokenType.VARIABLE, currentToken.getType());
         assertEquals("Number", ((TokenBeenImpl)currentToken).getElementType());            
         assertEquals(null, ((TokenBeenImpl)currentToken).getInitializationStatement());
         assertEquals(3, ((TokenBeenImpl)currentToken).getLineNumber());                  

         // test token list
//       testTokenList(correctTokenList, tokenList);         
         assertEquals(1, tokenList.size());
         assertEquals("a", tokenList.get(0).getName());
         assertEquals(TokenType.VARIABLE, tokenList.get(0).getType());
         assertEquals("Number", ((TokenBeenImpl)tokenList.get(0)).getElementType());            
         assertEquals(null, ((TokenBeenImpl)tokenList.get(0)).getInitializationStatement());
         assertEquals(1, ((TokenBeenImpl)tokenList.get(0)).getLineNumber());    
      }

      @Override
      public void errorMarckClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX,
         int markOffsetY, String fileMimeType)
      {
         System.out.println(">>>>>>>>>>>>>>>> errorMarckClicked");
         
      }
   }

}
