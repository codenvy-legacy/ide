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
package org.exoplatform.ide.editor.ruby.client.codeassistant;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ExternalTextResource;
import com.google.gwt.resources.client.ResourceCallback;
import com.google.gwt.resources.client.ResourceException;
import com.google.gwt.resources.client.TextResource;

import org.exoplatform.gwtframework.commons.util.Log;
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
import org.exoplatform.ide.editor.codeassistant.util.JSONTokenParser;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase.ClassMetaclass;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase.Metaclass;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase.MethodInfo;
import org.exoplatform.ide.editor.ruby.client.codeassistant.model.BuiltinMethodsDatabase.ModuleMetaclass;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RubyCodeAssistant Apr 28, 2011 4:46:20 PM evgen $
 * 
 */
public class RubyCodeAssistant extends CodeAssistant implements Comparator<Token>
{

   public interface RubyBundle extends ClientBundle
   {
      @Source("org/exoplatform/ide/editor/ruby/client/tokens/ruby_tokens.js")
      ExternalTextResource rubyTokens();

   }

   private static List<Token> defaultTokens;

   private RubyTokenWidgetFactory widgetFactory = new RubyTokenWidgetFactory();

   private int currentLineNumber;

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#errorMarkClicked(org.exoplatform.ide.editor.api.Editor,
    *      java.util.List, int, int, java.lang.String)
    */
   @Override
   public void errorMarkClicked(Editor editor, List<CodeLine> codeErrorList, int markOffsetX, int markOffsetY,
      String fileMimeType)
   {
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.CodeAssistant#autocompleteCalled(org.exoplatform.ide.editor.api.Editor,
    *      java.lang.String, int, int, java.lang.String, int, int, java.util.List, java.lang.String,
    *      org.exoplatform.ide.editor.api.codeassitant.Token)
    */
   @Override
   public void autocompleteCalled(Editor editor, int cursorOffsetX, int cursorOffsetY, final List<Token> tokenList,
      String lineMimeType, final Token currentToken)
   {
      this.editor = editor;
      this.posX = cursorOffsetX;
      this.posY = cursorOffsetY;
      try
      {
         currentLineNumber = editor.getCursorRow();
         String lineContent = editor.getLineContent(currentLineNumber);
         if (lineContent == null)
         {
            beforeToken = "";
            tokenToComplete = "";
            afterToken = "";
            openForm(new ArrayList<Token>(), widgetFactory, this);
            return;
         }
         parseTokenLine(lineContent, editor.getCursorCol());

         if (defaultTokens == null)
         {
            RubyBundle buandle = GWT.create(RubyBundle.class);
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
                  Log.info(e.getMessage());
               }
            });

            return;
         }
         autocompletion(tokenList, currentToken);
      }
      catch (ResourceException e)
      {
         Log.info(e.getMessage());
      }
      catch (Exception e)
      {
         Log.info(e.getMessage());
      }
   }

   /**
    * Do autocompletion
    * 
    * @param tokenList
    * @param currentToken
    */
   private void autocompletion(final List<Token> tokenList, final Token currentToken)
   {
      GWT.runAsync(new RunAsyncCallback()
      {

         @Override
         public void onSuccess()
         {
            List<Token> tokens = new ArrayList<Token>();

            Map<String, Token> clazz = getClassesFromTokens(tokenList);
            if (beforeToken.endsWith("."))
            {
               Map<String, Token> tokenMap = new HashMap<String, Token>();
               Metaclass metaclass = null;
               if (currentToken != null)
               {
                  String type = currentToken.getProperty(TokenProperties.ELEMENT_TYPE).isStringProperty().stringValue();
                  if (clazz.containsKey(type))
                  {
                     Token rubyClass = clazz.get(type);
                     addMethodFromClass(rubyClass, tokens);
                  }
                  else
                  {
                     metaclass = BuiltinMethodsDatabase.get(type);
                     selectClassTokensFromMetaClass((ClassMetaclass)metaclass, tokenMap);
                  }
               }
               else
               {
                  metaclass = BuiltinMethodsDatabase.get("Object");
                  tokenMap.putAll(getTokenFromModules(metaclass.getIncludedModules()));
                  convertMethodToToken(metaclass.getMetaClass().getSuperClass().getName(), metaclass.getMetaClass()
                     .getSuperClass().getMethods(), tokenMap);
               }
               tokens.addAll(tokenMap.values());

            }
            else if (!"".equals(tokenToComplete) && Character.isUpperCase(tokenToComplete.charAt(0)))
            {
               for (Object className : BuiltinMethodsDatabase.metaclasses.keySet())
               {
                  tokens.add(getClassToken((Metaclass)BuiltinMethodsDatabase.metaclasses.get(className)));
               }
               tokens.addAll(clazz.values());
               getConstants(tokenList, tokens);
            }
            else
            {
               if (currentToken != null)
               {
                  if (currentToken.getType() == TokenType.METHOD)
                  {
                     Token klass =
                        (Token)currentToken.getProperty(TokenProperties.PARENT_TOKEN).isObjectProperty().objectValue();
                     addTokenFromMethod(currentToken, tokens);
                     addMethodsAndGlobal(klass, klass.getName(), tokens);
                     addGlobalScriptVariables(tokenList, tokens);
                  }
                  else if (currentToken.getType() == TokenType.CLASS)
                  {
                     addMethodsAndGlobal(currentToken, currentToken.getName(), tokens);
                  }
               }
               else
               {
                  addScriptVariables(tokenList, tokens);
               }
               tokens.addAll(defaultTokens);
            }
            try
            {
               Collections.sort(tokens, RubyCodeAssistant.this);
               openForm(tokens, widgetFactory, RubyCodeAssistant.this);
            }
            catch (Exception e)
            {
               Log.info(e.getMessage());
            }
         }

         @Override
         public void onFailure(Throwable reason)
         {
            Log.info(reason.getMessage());
         }
      });

   }

   private void addMethodFromClass(final Token clazz, final List<Token> tokens)
   {
      if (clazz.hasProperty(TokenProperties.SUB_TOKEN_LIST)
         && clazz.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
      {
         for (Token t : clazz.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue())
         {
            if (t.getType() == TokenType.METHOD)
            {
               t.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(clazz.getName()));
               tokens.add(t);
            }
         }
      }
   }

   /**
    * Find, recursive, all global variables defined in Ruby script
    * 
    * @param tokenList List of tokens
    * @param tokens List of tokens where global and local variables will be stored
    */
   @SuppressWarnings("unchecked")
   private void addGlobalScriptVariables(final List<Token> tokenList, final List<Token> tokens)
   {
      for (Token t : tokenList)
      {
         if (t.getType() == TokenType.GLOBAL_VARIABLE)
         {
            tokens.add(t);
         }
         if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
            && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
         {
            addGlobalScriptVariables((List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty()
               .arrayValue(), tokens);
         }
      }
   }

   /**
    * Find all global and local variables defined on root of Ruby script
    * 
    * @param tokenList List of tokens, received from editor
    * @param tokens List of tokens where global and local variables will be stored
    */
   private void addScriptVariables(final List<Token> tokenList, final List<Token> tokens)
   {
      for (Token t : tokenList)
      {
         if (t.getType() == TokenType.GLOBAL_VARIABLE || t.getType() == TokenType.LOCAL_VARIABLE)
         {
            tokens.add(t);
         }
      }
   }

   /**
    * Get all methods and variables (of class and of instance)
    * 
    * @param classToken Token that describes Ruby class
    * @param tokens List of tokens
    */
   private void addMethodsAndGlobal(final Token classToken, final String className, final List<Token> tokens)
   {
      if (classToken.hasProperty(TokenProperties.SUB_TOKEN_LIST))
      {
         for (Token t : classToken.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue())
         {
            t.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(className));
            if (t.getType() == TokenType.METHOD)
            {
               tokens.add(t);
               addMethodsAndGlobal(t, className, tokens);
               continue;
            }
            if (t.getType() == TokenType.CLASS_VARIABLE || t.getType() == TokenType.INSTANCE_VARIABLE)
            {
               tokens.add(t);
            }
         }
      }
   }

   /**
    * Add all tokens from methodToken to tokens, also filter local variables defined after
    * {@link RubyCodeAssistant#currentLineNumber}
    * 
    * @param methodToken token that represent Ruby method
    * @param tokens List of tokens
    */
   private void addTokenFromMethod(final Token methodToken, final List<Token> tokens)
   {
      if (methodToken.hasProperty(TokenProperties.PARAMETERS))
      {
         tokens.addAll(methodToken.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue());
      }
      if (methodToken.hasProperty(TokenProperties.SUB_TOKEN_LIST))
         for (Token t : methodToken.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue())
         {
            if (t.getProperty(TokenProperties.LINE_NUMBER).isNumericProperty().numericValue().intValue() <= currentLineNumber)
            {
               tokens.add(t);
            }
         }
   }

   /**
    * Get all Ruby constants form token list
    * 
    * @param tokenList List of tokens received from editor
    * @param tokenConstant List where constants store
    */
   @SuppressWarnings("unchecked")
   private void getConstants(List<Token> tokenList, List<Token> tokenConstant)
   {
      for (Token t : tokenList)
      {
         if (t.getType() == TokenType.CONSTANT)
         {
            tokenConstant.add(t);
         }
         if (t.hasProperty(TokenProperties.SUB_TOKEN_LIST)
            && t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue() != null)
         {
            getConstants((List<Token>)t.getProperty(TokenProperties.SUB_TOKEN_LIST).isArrayProperty().arrayValue(),
               tokenConstant);
         }
      }
   }

   /**
    * Recursive get tokens form all hierarchy of classes
    * 
    * @param metaclass where do search classes
    * @param tokenMap Map that store tokens
    */
   private void selectClassTokensFromMetaClass(ClassMetaclass metaclass, Map<String, Token> tokenMap)
   {
      convertMethodToToken(metaclass.getName(), metaclass.getMethods(), tokenMap);
      tokenMap.putAll(getTokenFromModules(metaclass.getIncludedModules()));
      if (metaclass.getSuperClass() != null)
      {
         selectClassTokensFromMetaClass(metaclass.getSuperClass(), tokenMap);
      }

   }

   /**
    * Get Classes form token list
    * 
    * @param tokenList tokens received from editor
    * @return {@link Map} of Defined classes
    */
   private Map<String, Token> getClassesFromTokens(List<Token> tokenList)
   {
      Map<String, Token> classes = new HashMap<String, Token>();
      for (Token t : tokenList)
      {
         if (t.getType() == TokenType.CLASS)
         {
            classes.put(t.getName(), t);
         }
      }
      return classes;
   }

   /**
    * Convert {@link Metaclass} to {@link Token} with type CLASS
    * 
    * @param metaclass
    * @return token
    */
   protected Token getClassToken(Metaclass metaclass)
   {
      Token clazz = new TokenImpl(metaclass.getName(), TokenType.CLASS);
      clazz.setProperty(TokenProperties.FQN, new StringProperty(metaclass.getName() + ".rb"));
      return clazz;
   }

   /**
    * Get {@link Token}s from {@link ModuleMetaclass} array
    * 
    * @param includedModules
    * @return Map of converted tokens
    */
   protected Map<String, Token> getTokenFromModules(ModuleMetaclass[] includedModules)
   {
      Map<String, Token> tokenMap = new HashMap<String, Token>();
      for (ModuleMetaclass module : includedModules)
      {
         convertMethodToToken(module.getName(), module.getMethods(), tokenMap);
         tokenMap.putAll(getTokenFromModules(module.getIncludedModules()));
         tokenMap.putAll(getTokenFromModules(module.getMetaClass().getSuperClass().getIncludedModules()));
         convertMethodToToken(module.getMetaClass().getSuperClass().getName(), module.getMetaClass().getSuperClass()
            .getMethods(), tokenMap);
         convertMethodToToken(module.getMetaClass().getName(), module.getMetaClass().getMethods(), tokenMap);

      }

      return tokenMap;
   }

   /**
    * Convert {@link MethodInfo} array to {@link Token}s and put its to tokens map
    * 
    * @param container Name of Class
    * @param methods array
    * @param tokens Map that store converted tokens
    */
   private void convertMethodToToken(String container, MethodInfo[] methods, Map<String, Token> tokens)
   {
      for (MethodInfo method : methods)
      {
         Token m = new TokenImpl(method.getName(), TokenType.METHOD);
         String param = getParameters(method.getArity());
         m.setProperty(TokenProperties.DECLARING_CLASS, new StringProperty(container));
         m.setProperty(TokenProperties.MODIFIERS, new NumericProperty(method.getFlags()));
         m.setProperty(TokenProperties.PARAMETER_TYPES, new StringProperty(param));
         m.setProperty(TokenProperties.CODE, new StringProperty(method.getName() + param));

         // to avoid overriding methods from Kernel
         if (tokens.containsKey(m.getName() + param)
            && tokens.get(m.getName() + param).getProperty(TokenProperties.DECLARING_CLASS).equals("Kernel"))
            continue;

         tokens.put(m.getName() + param, m);

      }
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
            par += "arg" + (i + 1) + ", ";
         }
         if (par.endsWith(", "))
            par = par.substring(0, par.lastIndexOf(", "));

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
         TokenProperty p = t.getProperty(TokenProperties.LAST_LINE_NUMBER);
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
      if (t1.getType() == t2.getType())
      {
         return t1.getName().compareTo(t2.getName());
      }

      if ((t1.getType() == TokenType.PARAMETER && t2.getType() == TokenType.LOCAL_VARIABLE)
         || (t1.getType() == TokenType.LOCAL_VARIABLE && t2.getType() == TokenType.PARAMETER))
      {
         return t1.getName().compareTo(t2.getName());
      }

      if (t2.getType() == TokenType.PARAMETER)
      {
         return 1;
      }
      if (t1.getType() == TokenType.PARAMETER)
      {
         return -1;
      }

      if (t2.getType() == TokenType.LOCAL_VARIABLE)
      {
         return 1;
      }
      if (t1.getType() == TokenType.LOCAL_VARIABLE)
      {
         return -1;
      }

      if (t1.getType() == TokenType.CLASS_VARIABLE)
      {
         return -1;
      }

      if (t2.getType() == TokenType.CLASS_VARIABLE)
      {
         return 1;
      }

      if (t1.getType() == TokenType.INSTANCE_VARIABLE)
      {
         return -1;
      }

      if (t2.getType() == TokenType.INSTANCE_VARIABLE)
      {
         return 1;
      }

      if (t1.getType() == TokenType.GLOBAL_VARIABLE)
      {
         return -1;
      }

      if (t2.getType() == TokenType.GLOBAL_VARIABLE)
      {
         return 1;
      }

      if (t1.getType() == TokenType.METHOD || t1.getType() == TokenType.PROPERTY)
      {
         return -1;
      }

      if (t2.getType() == TokenType.METHOD || t2.getType() == TokenType.PROPERTY)
      {
         return 1;
      }

      return t1.getName().compareTo(t2.getName());
   }

}
