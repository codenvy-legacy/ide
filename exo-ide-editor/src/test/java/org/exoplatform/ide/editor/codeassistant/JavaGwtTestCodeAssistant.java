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
package org.exoplatform.ide.editor.codeassistant;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.CodeLine.CodeType;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.ui.AssistImportDeclarationHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenSelectedHandler;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.codeassistant.java.JavaTokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantServiceImpl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaGwtTestCodeAssistant Mar 4, 2011 4:57:07 PM evgen $
 *
 */
public class JavaGwtTestCodeAssistant extends Base
{
   private HandlerManager eventBus = new HandlerManager(null);

   private CodeAssistantServiceImpl service = new CodeAssistantServiceImpl(eventBus, "/rest", new EmptyLoader());

   public void testJavaParseLineSpace()
   {
      String line = "java ";
      paseLine(line, line, "", "", line.length() + 1);
   }

   public void testJavaParseLineWithDot()
   {
      final String line = "String.";
      class JavaCodeAssist extends JavaCodeAssistant
      {
         public JavaCodeAssist()
         {
            super(new JavaTokenWidgetFactory(""), new JavaCodeAssistantErrorHandler()
            {
               @Override
               public void handleError(Throwable exception)
               {
                  finishTest();
                  fail();
               }
            });
         }

         @Override
         protected void openForm(List<Token> tokens, TokenWidgetFactory factory, TokenSelectedHandler handler)
         {
            assertEquals(line, beforeToken);
            assertEquals("", tokenToComplete);
            assertEquals("", afterToken);
            assertEquals(73, tokens.size());
            assertEquals("CASE_INSENSITIVE_ORDER", tokens.get(0).getName());
            finishTest();
         }
      }
      delayTestFinish(3000);
      Token currentToken = new TokenImpl("String", TokenType.VARIABLE);
      currentToken.setProperty(TokenProperties.FQN, new StringProperty("java.lang.String"));
      JavaCodeAssist javaCodeAssist = new JavaCodeAssist();
      javaCodeAssist.setactiveFileHref("http://127.0.0.1:8888/rest/jcr/repository/dev-monit/1.txt");
      javaCodeAssist.autocompleteCalled(null, "", 0, 0, line, line.length() + 1, 0, new ArrayList<Token>(),
         MimeType.APPLICATION_GROOVY, currentToken);
   }

   public void testImportAssistant()
   {
      class JavaCodeAssist extends JavaCodeAssistant
      {
         public JavaCodeAssist()
         {
            super(new JavaTokenWidgetFactory(""), new JavaCodeAssistantErrorHandler()
            {
               @Override
               public void handleError(Throwable exception)
               {
                  finishTest();
                  fail();
               }
            });
         }
         @Override
         protected void openImportForm(int left, int top, List<Token> tokens, TokenWidgetFactory factory,
            AssistImportDeclarationHandler handler)
         {
            Token token =  tokens.get(0);
            assertNotNull(token);
             assertEquals("Base64",token.getName());
             assertTrue(token.hasProperty(TokenProperties.FQN));
             assertNotNull(token.getProperty(TokenProperties.FQN));
             
             TokenProperty property = token.getProperty(TokenProperties.FQN);
             assertNotNull(property.isStringProperty());
             assertNotNull(property.isStringProperty().stringValue());
             
             assertEquals("java.util.prefs.Base64", property.isStringProperty().stringValue());
             finishTest();
         }
      }
      delayTestFinish(3000);
      
      List<CodeLine> codeErrorList = new ArrayList<CodeLine>();
      CodeLine codeLine = new CodeLine(CodeType.IMPORT_STATEMENT, "Base64", 5);
      codeErrorList.add(codeLine);
      JavaCodeAssist javaCodeAssist = new JavaCodeAssist();
      
      javaCodeAssist.errorMarckClicked(null, codeErrorList, 0, 0, MimeType.APPLICATION_GROOVY);
      
   }
   
   public void testFindClass()
   {
      final String line = "Strin";
      class JavaCodeAssist extends JavaCodeAssistant
      {
         public JavaCodeAssist()
         {
            super(new JavaTokenWidgetFactory(""), new JavaCodeAssistantErrorHandler()
            {
               @Override
               public void handleError(Throwable exception)
               {
                  finishTest();
                  fail();
               }
            });
         }

         @Override
         protected void openForm(List<Token> tokens, TokenWidgetFactory factory, TokenSelectedHandler handler)
         {
            assertEquals("", beforeToken);
            assertEquals(line, tokenToComplete);
            assertEquals("", afterToken);
            assertEquals(69, tokens.size());
            finishTest();
         }
      }
      delayTestFinish(3000);
      Token currentToken = new TokenImpl("String", TokenType.VARIABLE);
      currentToken.setProperty(TokenProperties.FQN, new StringProperty("java.lang.String"));
      JavaCodeAssist javaCodeAssist = new JavaCodeAssist();
      javaCodeAssist.autocompleteCalled(null, "", 0, 0, line, line.length() + 1, 0, new ArrayList<Token>(),
         MimeType.APPLICATION_GROOVY, currentToken);
   }
   
   private void paseLine(final String line, final String before, final String token, final String after,
      final int curPos)
   {
      class JavaCodeAssist extends JavaCodeAssistant
      {
         public JavaCodeAssist()
         {
            super(new JavaTokenWidgetFactory(""), new JavaCodeAssistantErrorHandler()
            {

               @Override
               public void handleError(Throwable exception)
               {
                  fail();
               }
            });
         }

         @Override
         protected void openForm(List<Token> tokens, TokenWidgetFactory factory, TokenSelectedHandler handler)
         {
            assertEquals(before, beforeToken);
            assertEquals(token, tokenToComplete);
            assertEquals(after, afterToken);
         }
      }

      JavaCodeAssist javaCodeAssist = new JavaCodeAssist();
      javaCodeAssist.setactiveFileHref("/rest/jcr/repository/dev-monit/1.txt");
      javaCodeAssist.autocompleteCalled(null, "", 0, 0, line, curPos, 0, new ArrayList<Token>(),
         MimeType.APPLICATION_GROOVY, null);
   }
}
