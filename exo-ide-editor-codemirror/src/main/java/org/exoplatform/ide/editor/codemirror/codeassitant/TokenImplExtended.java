/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.editor.codemirror.codeassitant;

import org.exoplatform.ide.codeassistant.jvm.client.ArrayProperty;
import org.exoplatform.ide.codeassistant.jvm.client.Modifier;
import org.exoplatform.ide.codeassistant.jvm.client.NumericProperty;
import org.exoplatform.ide.codeassistant.jvm.client.ObjectProperty;
import org.exoplatform.ide.codeassistant.jvm.client.StringProperty;
import org.exoplatform.ide.codeassistant.jvm.client.TokenProperties;
import org.exoplatform.ide.codeassistant.jvm.client.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 * 
 */
public class TokenImplExtended extends TokenImpl
{

   public TokenImplExtended()
   {
   }

   public TokenImplExtended(String name, TokenType type)
   {
      setName(name);
      setType(type);
   }

   public TokenImplExtended(String name, TokenType type, int lineNumber)
   {
      this(name, type);
      setLineNumber(lineNumber);
   }

   public TokenImplExtended(String name, TokenType type, int lineNumber, String mimeType)
   {
      this(name, type, lineNumber);
      setMimeType(mimeType);
   }

   public TokenImplExtended(String name, TokenType type, int lineNumber, String mimeType, String elementType)
   {
      this(name, type, lineNumber, mimeType);
      setElementType(elementType);
   }

   public TokenImplExtended(String name, TokenType type, int lineNumber, String mimeType, String elementType,
      List<Modifier> modifiers)
   {
      this(name, type, lineNumber, mimeType, elementType);
      setModifiers(modifiers);
   }

   public TokenImplExtended(String name, TokenType type, int lineNumber, String mimeType, String elementType,
      List<Modifier> modifiers, String fqn)
   {
      this(name, type, lineNumber, mimeType, elementType, modifiers);
      setFqn(fqn);
   }

   public TokenImplExtended(String name, TokenType type, int lineNumber, String mimeType, String elementType,
      String initializationStatement)
   {
      this(name, type, lineNumber, mimeType, elementType);
      setInitializationStatement(initializationStatement);
   }

   /**
    * @return the shortDescription
    */
   public String getShortDescription()
   {
      return hasProperty(TokenProperties.SHORT_DESCRIPTION)
         ? ((StringProperty)getProperty(TokenProperties.SHORT_DESCRIPTION)).stringValue() : null;
   }

   /**
    * @return the code
    */
   public String getCode()
   {
      return hasProperty(TokenProperties.CODE) ? ((StringProperty)getProperty(TokenProperties.CODE)).stringValue()
         : null;
   }

   /**
    * @return the fullDescription
    */
   public String getFullDescription()
   {
      return hasProperty(TokenProperties.FULL_DESCRIPTION)
         ? ((StringProperty)getProperty(TokenProperties.FULL_DESCRIPTION)).stringValue() : null;
   }

   public int getLineNumber()
   {
      return hasProperty(TokenProperties.LINE_NUMBER) ? ((NumericProperty)getProperty(TokenProperties.LINE_NUMBER))
         .numericValue().intValue() : 0;
   }

   public void setLastLineNumber(int lastLineNumber)
   {
      setProperty(TokenProperties.LAST_LINE_NUMBER, new NumericProperty(lastLineNumber));
   }

   /**
    * @return number of ended line of token in file content, like "}" for method or class
    */
   public int getLastLineNumber()
   {
      return hasProperty(TokenProperties.LAST_LINE_NUMBER)
         ? ((NumericProperty)getProperty(TokenProperties.LAST_LINE_NUMBER)).numericValue().intValue() : 0;
   }

   public List<TokenImplExtended> getSubTokenList()
   {
      return hasProperty(TokenProperties.SUB_TOKEN_LIST)
         ? (List<TokenImplExtended>)((ArrayProperty)getProperty(TokenProperties.SUB_TOKEN_LIST)).arrayValue() : null;
   }

   public String getMimeType()
   {
      return hasProperty(TokenProperties.MIME_TYPE) ? ((StringProperty)getProperty(TokenProperties.MIME_TYPE))
         .stringValue() : null;
   }

   /**
    * @param lineNumber the lineNumber to set
    */
   public void setLineNumber(int lineNumber)
   {
      setProperty(TokenProperties.LINE_NUMBER, new NumericProperty(lineNumber));
   }

   /**
    * @param shortDescription the shortDescription to set
    */
   public void setShortDescription(String shortDescription)
   {
      setProperty(TokenProperties.SHORT_DESCRIPTION, new StringProperty(shortDescription));
   }

   /**
    * @param code the code to set
    */
   public void setCode(String code)
   {
      setProperty(TokenProperties.CODE, new StringProperty(code));
   }

   public void setParentToken(TokenImplExtended parentToken)
   {
      setProperty(TokenProperties.PARENT_TOKEN, new ObjectProperty(parentToken));
   }

   public TokenImplExtended getParentToken()
   {
      return hasProperty(TokenProperties.PARENT_TOKEN)
         ? (TokenImplExtended)((ObjectProperty)getProperty(TokenProperties.PARENT_TOKEN)).objectValue() : null;
   }

   /**
    * @param fullDescription the fullDescription to set
    */
   public void setFullDescription(String fullDescription)
   {
      setProperty(TokenProperties.FULL_DESCRIPTION, new StringProperty(fullDescription));
   }

   /**
    * @param subTokenList the subTokenList to set
    */
   public void setSubTokenList(List<TokenImplExtended> subTokenList)
   {
      setProperty(TokenProperties.SUB_TOKEN_LIST, new ArrayProperty(subTokenList));
   }

   /**
    * @param mimeType the mimeType to set
    */
   public void setMimeType(String mimeType)
   {
      setProperty(TokenProperties.MIME_TYPE, new StringProperty(mimeType));
   }

   public void setElementType(String elementType)
   {
      setProperty(TokenProperties.ELEMENT_TYPE, new StringProperty(elementType));
   }

   public String getElementType()
   {
      return hasProperty(TokenProperties.ELEMENT_TYPE) ? ((StringProperty)getProperty(TokenProperties.ELEMENT_TYPE))
         .stringValue() : null;
   }

   public void setFqn(String fqn)
   {
      setProperty(TokenProperties.FQN, new StringProperty(fqn));
   }

   public String getFqn()
   {
      return hasProperty(TokenProperties.FQN) ? ((StringProperty)getProperty(TokenProperties.FQN)).stringValue() : null;
   }

   public List<Modifier> getModifiers()
   {
      return hasProperty(TokenProperties.MODIFIERS)
         ? (List<Modifier>)((ObjectProperty)getProperty(TokenProperties.MODIFIERS)).objectValue() : null;
   }

   public void setModifiers(List<Modifier> modifiers)
   {
      setProperty(TokenProperties.MODIFIERS, new ObjectProperty(modifiers));
   }

   public void addAnnotation(TokenImplExtended annotation)
   {
      List<TokenImplExtended> annotations = getAnnotations();
      if (annotations == null)
      {
         annotations = new ArrayList<TokenImplExtended>();
      }

      annotations.add(annotation);
      setAnnotations(annotations);
   }

   public List<TokenImplExtended> getAnnotations()
   {
      return hasProperty(TokenProperties.ANNOTATIONS)
         ? (List<TokenImplExtended>)((ArrayProperty)getProperty(TokenProperties.ANNOTATIONS)).arrayValue() : null;
   }

   public void setAnnotations(List<TokenImplExtended> annotations)
   {
      setProperty(TokenProperties.ANNOTATIONS, new ArrayProperty(annotations));
   }

   public void addParameter(TokenImplExtended parameter)
   {
      List<TokenImplExtended> parameters = getParameters();
      if (parameters == null)
      {
         parameters = new ArrayList<TokenImplExtended>();
      }

      parameters.add(parameter);
      setParameters(parameters);
   }

   public List<TokenImplExtended> getParameters()
   {
      return hasProperty(TokenProperties.PARAMETERS)
         ? (List<TokenImplExtended>)((ArrayProperty)getProperty(TokenProperties.PARAMETERS)).arrayValue() : null;
   }

   public void setParameters(List<TokenImplExtended> parameters)
   {
      setProperty(TokenProperties.PARAMETERS, new ArrayProperty(parameters));
   }

   /**
    * Set statement used to initialize variable, like "widget.createDiv()" in statement "var a = widget.createDiv('test');"
    * 
    * @param initializationStatement
    */
   public void setInitializationStatement(String initializationStatement)
   {
      setProperty(TokenProperties.INITIALIZATION_STATEMENT, new StringProperty(initializationStatement));
   }

   /**
    * @return Statement used to initialize variable, like "widget.createDiv()" in statement "var a = widget.createDiv('test');"
    */
   public String getInitializationStatement()
   {
      return hasProperty(TokenProperties.INITIALIZATION_STATEMENT)
         ? ((StringProperty)getProperty(TokenProperties.INITIALIZATION_STATEMENT)).stringValue() : null;
   }

   /**
    * Adds subToken into the last sub token subTokenList
    * 
    * @param token
    * @param subToken
    */
   public void addSubTokenToTheLastSubToken(TokenImplExtended subToken)
   {
      if (getSubTokenList() == null || getSubTokenList().size() == 0)
         return;

      TokenImplExtended lastToken = getSubTokenList().get(getSubTokenList().size() - 1);
      lastToken.addSubToken(subToken);

      subToken.setParentToken(lastToken);
   }

   public void updateTypeOfLastSubTokenOfLastToken(TokenType newType)
   {
      if (getSubTokenList() == null || getSubTokenList().size() == 0)
         return;

      TokenImplExtended lastSubToken = getSubTokenList().get(getSubTokenList().size() - 1);

      lastSubToken.updateTypeOfLastSubToken(newType);
   }

   public void updateTypeOfLastSubToken(TokenType newType)
   {
      if (getSubTokenList() == null || getSubTokenList().size() == 0)
         return;

      TokenImplExtended lastSubToken = getSubTokenList().get(getSubTokenList().size() - 1);

      lastSubToken.setType(newType);
   }

   /**
    * Set token.elementType += elementType
    * 
    * @param elementType
    */
   public void concatElementTypeOfLastSubToken(String elementType)
   {
      if (getSubTokenList() == null || getSubTokenList().size() == 0)
         return;

      TokenImplExtended lastSubToken = getSubTokenList().get(getSubTokenList().size() - 1);

      if (lastSubToken.getElementType() == null)
      {
         lastSubToken.setElementType(elementType);
      }
      else
      {
         lastSubToken.setElementType(lastSubToken.getElementType() + elementType);
      }
   }

   public void addSubToken(TokenImplExtended subToken)
   {
      if (this.getSubTokenList() == null)
      {
         this.setSubTokenList(new ArrayList<TokenImplExtended>());
      }

      this.getSubTokenList().add(subToken);
      subToken.setParentToken(this);
   }

   /**
    * remove last subtoken if subtoken list isn't empty
    */
   public void removeLastSubToken()
   {
      if (getSubTokenList() != null && getSubTokenList().size() != 0)
      {
         getSubTokenList().remove(getSubTokenList().size() - 1);
      }
   }

   public void setLastSubTokenName(String name)
   {
      TokenImplExtended token = getLastSubToken();

      if (token != null)
      {
         token.setName(name);
      }
   }

   /**
    * 
    * @return last subtoken or null if there is no any subtoken
    */
   public TokenImplExtended getLastSubToken()
   {
      if (getSubTokenList() != null && getSubTokenList().size() != 0)
      {
         return getSubTokenList().get(getSubTokenList().size() - 1);
      }

      return null;
   }

   /**
    * @return last annotation token or null if there is no any annotation token
    */
   public TokenImplExtended getLastAnnotationToken()
   {
      if (this.getAnnotations() != null && this.getAnnotations().size() != 0)
      {
         return this.getAnnotations().get(this.getAnnotations().size() - 1);
      }

      return null;
   }

   /**
    * Concatenates the specified "string" to the end of name of last token in annotation list
    * 
    * @param string
    */
   public void lastAnnotationTokenNameConcat(String string)
   {
      if (getLastAnnotationToken() != null)
      {
         getLastAnnotationToken().setName(getLastAnnotationToken().getName().concat(string)); // update last annotation token
      }
   }

}
