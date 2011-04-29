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
package org.exoplatform.ide.editor.codeassistant.ruby;

import com.google.gwt.core.client.RunAsyncCallback;

import com.google.gwt.core.client.RunAsyncCallback;

import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.ide.editor.api.CodeLine;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.CodeAssistant;
import org.exoplatform.ide.editor.api.codeassitant.NumericProperty;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codeassistant.ruby.model.BuiltinMethodsDatabase;
import org.exoplatform.ide.editor.codeassistant.ruby.model.BuiltinMethodsDatabase.Metaclass;
import org.exoplatform.ide.editor.codeassistant.ruby.model.BuiltinMethodsDatabase.MethodInfo;
import org.exoplatform.ide.editor.codeassistant.ruby.model.BuiltinMethodsDatabase.ModuleMetaclass;
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RubyCodeAssistant Apr 28, 2011 4:46:20 PM evgen $
 *
 */
public class RubyCodeAssistant extends CodeAssistant implements Comparator<Token>
{

   public interface RubyBuandle extends ClientBundle
   {
      @Source("org/exoplatform/ide/editor/public/tokens/ruby_tokens.js")
      ExternalTextResource rubyTokens();
   }

   private static List<Token> defaultTokens;

   private RubyTokenWidgetFactory widgetFactory = new RubyTokenWidgetFactory();

   private int currentLineNumber;

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarckClicked(org.exoplatform.ide.editor.api.Editor, java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarckClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX, int markOffsetY,
      String fileMimeType)
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor, java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String, org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, String mimeType, int cursorOffsetX, int cursorOffsetY,
      String lineContent, int cursorPositionX, int cursorPositionY, final List<Token> tokenList, String lineMimeType,
      final Token currentToken)
   {
      this.editor = editor;
      this.posX = cursorOffsetX;
      this.posY = cursorOffsetY;
      try
      {
         if (lineContent == null)
         {
            beforeToken = "";
            tokenToComplete = "";
            afterToken = "";
            openForm(new ArrayList<Token>(), widgetFactory, this);
            return;
         }
         parseTokenLine(lineContent, cursorPositionX);
         currentLineNumber = cursorPositionY;

         //         printTokens(tokenList, 2);
         if (defaultTokens == null)
         {
            RubyBuandle buandle = GWT.create(RubyBuandle.class);
            buandle.rubyTokens().getText(new ResourceCallback<TextResource>()
            {

               @Override
               public void onSuccess(TextResource resource)
               {
                  JSONTokenParser parser = new JSONTokenParser();
                  defaultTokens = parser.getTokens(new JSONArray(parseJson(resource.getText())));
                  autocompletion(tokenList, currentToken);
               }

               @Override
               public void onError(ResourceException e)
               {
                  e.printStackTrace();
               }
            });

            return;
         }
         autocompletion(tokenList, currentToken);
      }
      catch (ResourceException e)
      {
         e.printStackTrace();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @param lineContent 
    * @param cursorPositionX
    * @param cursorPositionY 
    * @param tokenList
    * @param currentToken
    */
   private void autocompletion(List<Token> tokenList, Token currentToken)
   {
      GWT.runAsync(new RunAsyncCallback()
      {

         @Override
         public void onSuccess()
         {
            List<Token> tokens = new ArrayList<Token>();

            if (beforeToken.endsWith("."))
            {
               Metaclass metaclass = BuiltinMethodsDatabase.get("Object");
               tokens.addAll(getTokenFromModules(metaclass.getMetaClass().getIncludedModules()));
               tokens.addAll(getTokenFromModules(metaclass.getIncludedModules()));
               //               tokens.addAll(getObjectMethods(BuiltinMethodsDatabase.objectMethods));
            }
            else
            {
               tokens.addAll(defaultTokens);
            }

            Collections.sort(tokens, RubyCodeAssistant.this);
            openForm(tokens, widgetFactory, RubyCodeAssistant.this);
         }

         @Override
         public void onFailure(Throwable reason)
         {
            reason.printStackTrace();
         }
      });

   }

   /**
    * @param includedModules
    * @return
    */
   protected Collection<? extends Token> getTokenFromModules(ModuleMetaclass[] includedModules)
   {
      List<Token> tokens = new ArrayList<Token>();
      for (ModuleMetaclass module : includedModules)
      {
         for (MethodInfo method : module.getMethods())
         {
            Token m = new TokenImpl(method.getName(), TokenType.METHOD);
            m.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(module.getName()));
            m.setProperty(TokenProperties.MODIFIERS, new NumericProperty(method.getFlags()));
            m.setProperty(TokenProperties.PARAMETER_TYPES, new StringProperty(getParameters(method.getArity())));
            tokens.add(m);
         }
         tokens.addAll(getTokenFromModules(module.getIncludedModules()));
         tokens.addAll(getTokenFromModules(module.getMetaClass().getSuperClass().getIncludedModules()));
      }

      return tokens;
   }

   /**
    * @param arity
    * @return
    */
   private String getParameters(int arity)
   {
      String par = "(";

      if (arity < 0)
      {
         par += "*args";
      }
      else if (arity > 0)
      {
         for (int i = 0; i <= arity; i++)
         {
            par += "arg" + i + ", ";
         }
         if (par.endsWith(", "))
            par = par.substring(0,par.lastIndexOf(", "));

      }

      par += ")";
      return par;
   }

   /**
    * @param objectMethods
    * @return
    */
   protected Collection<? extends Token> getObjectMethods(String[] objectMethods)
   {
      List<Token> tokens = new ArrayList<Token>();
      for (String method : objectMethods)
      {
         Token m = new TokenImpl(method, TokenType.METHOD);
         tokens.add(m);
      }
      return tokens;
   }

   private void parseTokenLine(String line, int cursorPos)
   {
      String tokenLine = "";
      tokenToComplete = "";
      afterToken = "";
      beforeToken = "";
      if (line.length() > cursorPos - 1)
      {
         afterToken = line.substring(cursorPos - 1, line.length());
         tokenLine = line.substring(0, cursorPos - 1);

      }
      else
      {
         afterToken = "";
         if (line.endsWith(" "))
         {
            tokenToComplete = "";
            beforeToken = line;
            return;
         }

         tokenLine = line;
      }

      for (int i = tokenLine.length() - 1; i >= 0; i--)
      {
         switch (tokenLine.charAt(i))
         {
            case ' ' :
            case '.' :
            case '(' :
            case ')' :
            case '{' :
            case '}' :
            case ';' :
            case '[' :
            case ']' :
            case '\'' :
            case ',' :
            case '/' :
            case '+' :
            case '-' :
               beforeToken = tokenLine.substring(0, i + 1);
               tokenToComplete = tokenLine.substring(i + 1);
               return;

            default :
               break;
         }
         beforeToken = "";
         tokenToComplete = tokenLine;
      }

   }

   private void printTokens(List<? extends Token> tokens, int i)
   {
      String spacer = "";
      for (int j = 0; j < i; j++)
      {
         spacer += " ";
      }
      i++;
      for (Token t : tokens)
      {
         System.out.println(spacer + t.getName() + " " + t.getType());
         TokenProperty p = t.getProperty(TokenProperties.LAST_LINE_NUMBER);
         if (p != null && p.isNumericProperty() != null)
            System.out.println(spacer + p.isNumericProperty().numberValue());
         if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
            && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
         {
            printTokens(t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue(), i);
         }
      }
   }

   /**
    * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
    */
   @Override
   public int compare(Token t1, Token t2)
   {
      return t1.getName().compareTo(t2.getName());
   }

}
