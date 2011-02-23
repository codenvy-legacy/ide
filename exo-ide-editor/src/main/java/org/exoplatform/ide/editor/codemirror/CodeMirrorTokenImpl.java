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
package org.exoplatform.ide.editor.codemirror;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.exoplatform.ide.editor.api.codeassitant.ArrayProperty;
import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.NumericProperty;
import org.exoplatform.ide.editor.api.codeassitant.ObjectProperty;
import org.exoplatform.ide.editor.api.codeassitant.StringProperty;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperty;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class CodeMirrorTokenImpl extends TokenImpl
{

   public CodeMirrorTokenImpl() 
   {
   }
   
   public CodeMirrorTokenImpl(String name, TokenType type, int lineNumber)
   {
      this(name, type, lineNumber, null, null, null, null, null, null, null, null);
   }

   public CodeMirrorTokenImpl(String name, TokenType type, int lineNumber, String mimeType)
   {
      this(name, type, lineNumber, mimeType, null, null, null, null, null, null, null);
   }

   public CodeMirrorTokenImpl(String name, TokenType type, int lineNumber, String mimeType, String elementType)
   {
      this(name, type, lineNumber, mimeType, null, null, null, null, elementType, null, null);
   }

   public CodeMirrorTokenImpl(String name, TokenType type, int lineNumber, String mimeType, String elementType,
      List<Modifier> lavaModifiers)
   {
      this(name, type, lineNumber, mimeType, null, null, null, null, elementType, lavaModifiers, null);
   }

   public CodeMirrorTokenImpl(String name, TokenType type, int lineNumber, String mimeType, String elementType,
      List<Modifier> lavaModifiers, String fqn)
   {
      this(name, type, lineNumber, mimeType, null, null, null, null, elementType, lavaModifiers, fqn);
   }

   public CodeMirrorTokenImpl(String name, TokenType type, int lineNumber, List<CodeMirrorTokenImpl> subTokenList)
   {
      this(name, type, lineNumber, null, null, null, null, subTokenList, null, null, null);
   }

   public CodeMirrorTokenImpl(String name, TokenType type, String shortDescription, String code, String fullDescription)
   {
      this(name, type, 0, null, shortDescription, code, fullDescription, null, null, null, null);
   }

   public CodeMirrorTokenImpl(String name, TokenType type, int lineNumber, String mimeType, String shortDescription, String code,
      String fullDescription, List<CodeMirrorTokenImpl> subTokenList, String elementType, List<Modifier> modifier, String fqn)
   {
      setName(name);
      setType(type);
      setProperty(TokenProperties.LINE_NUMBER, new NumericProperty(lineNumber));
      setProperty(TokenProperties.MIME_TYPE, new StringProperty(mimeType));
      setProperty(TokenProperties.SHORT_DESCRIPTION, new StringProperty(shortDescription));
      setProperty(TokenProperties.CODE, new StringProperty(code));
      setProperty(TokenProperties.FULL_DESCRIPTION, new StringProperty(fullDescription));
      setProperty(TokenProperties.SUB_TOKEN_LIST, new ArrayProperty(subTokenList));;
      setProperty(TokenProperties.ELEMENT_TYPE, new StringProperty(elementType));
      setProperty(TokenProperties.MODIFIERS, new ObjectProperty(modifier));
      setProperty(TokenProperties.FQN, new StringProperty(fqn));
   }

   /**
    * @return the shortDescription
    */
   public String getShortDescription()
   {
      return ((StringProperty) getProperty(TokenProperties.SHORT_DESCRIPTION)).stringValue();
   }

   /**
    * @return the code
    */
   public String getCode()
   {
      return ((StringProperty) getProperty(TokenProperties.CODE)).stringValue();
   }

   /**
    * @return the fullDescription
    */
   public String getFullDescription()
   {
      return ((StringProperty) getProperty(TokenProperties.FULL_DESCRIPTION)).stringValue();
   }

   public int getLineNumber()
   {
      return ((NumericProperty) getProperty(TokenProperties.LINE_NUMBER)).numberValue().intValue();
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
      return ((NumericProperty) getProperty(TokenProperties.LAST_LINE_NUMBER)).numberValue().intValue();
   }
   
   public List<CodeMirrorTokenImpl> getSubTokenList()
   {
      return (List<CodeMirrorTokenImpl>)((ArrayProperty) getProperty(TokenProperties.SUB_TOKEN_LIST)).arrayValue();
   }

   public String getMimeType()
   {
      return ((StringProperty) getProperty(TokenProperties.MIME_TYPE)).stringValue();
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

   public void setParentToken(CodeMirrorTokenImpl parentToken)
   {
      setProperty(TokenProperties.PARENT_TOKEN, new ObjectProperty(parentToken));
   }

   public CodeMirrorTokenImpl getParentToken()
   {
      return (CodeMirrorTokenImpl)((ObjectProperty) getProperty(TokenProperties.PARENT_TOKEN)).objectValue();
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
   public void setSubTokenList(List<CodeMirrorTokenImpl> subTokenList)
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
      return ((StringProperty) getProperty(TokenProperties.ELEMENT_TYPE)).stringValue();
   }

   public void setFqn(String fqn)
   {
      setProperty(TokenProperties.FQN, new StringProperty(fqn));
   }

   public String getFqn()
   {
      return ((StringProperty) getProperty(TokenProperties.FQN)).stringValue();
   }

   public List<Modifier> getModifiers()
   {
      return (List<Modifier>)((ObjectProperty) getProperty(TokenProperties.MODIFIERS)).objectValue();
   }
   
   public void setModifiers(List<Modifier> modifiers) 
   {
      setProperty(TokenProperties.MODIFIERS, new ObjectProperty(modifiers));
   }

   public void addAnnotation(CodeMirrorTokenImpl annotation)
   {
      List<CodeMirrorTokenImpl> annotations = getAnnotations();
      if (annotations == null)
      {
         annotations = new ArrayList<CodeMirrorTokenImpl>();
      }

      annotations.add(annotation);
      setAnnotations(annotations);
   }

   public List<CodeMirrorTokenImpl> getAnnotations()
   {
      return (List<CodeMirrorTokenImpl>)((ArrayProperty) getProperty(TokenProperties.ANNOTATIONS)).arrayValue();
   }

   public void setAnnotations(List<CodeMirrorTokenImpl> annotations)
   {
      setProperty(TokenProperties.ANNOTATIONS, new ArrayProperty(annotations));
   }

   public void addParameter(CodeMirrorTokenImpl parameter)
   {
      List<CodeMirrorTokenImpl> parameters = getParameters();
      if (parameters == null)
      {
         parameters = new ArrayList<CodeMirrorTokenImpl>();
      }

      parameters.add(parameter);
      setParameters(parameters);
   }

   public List<CodeMirrorTokenImpl> getParameters()
   {
      return (List<CodeMirrorTokenImpl>)((ArrayProperty) getProperty(TokenProperties.PARAMETERS)).arrayValue();
   }

   public void setParameters(List<CodeMirrorTokenImpl> parameters)
   {
      setProperty(TokenProperties.PARAMETERS, new ArrayProperty(parameters));
   }

   /**
    * Set statement used to initialize variable, like "widget.createDiv()" in statement "var a = widget.createDiv('test');"
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
      return ((StringProperty) getProperty(TokenProperties.INITIALIZATION_STATEMENT)).stringValue();
   }
   
   
   /**
    * Adds subToken into the last sub token subTokenList
    * @param token
    * @param subToken
    */
   public void addSubTokenToTheLastSubToken(CodeMirrorTokenImpl subToken)
   {
      if (getSubTokenList() == null || getSubTokenList().size() == 0)
         return;

      CodeMirrorTokenImpl lastToken = getSubTokenList().get(getSubTokenList().size() - 1);
      lastToken.addSubToken(subToken);

      subToken.setParentToken(lastToken);
   }

   public void updateTypeOfLastSubTokenOfLastToken(TokenType newType)
   {
      if (getSubTokenList() == null || getSubTokenList().size() == 0)
         return;

      CodeMirrorTokenImpl lastSubToken = getSubTokenList().get(getSubTokenList().size() - 1);

      lastSubToken.updateTypeOfLastSubToken(newType);
   }
   
   public void updateTypeOfLastSubToken(TokenType newType)
   {
      if (getSubTokenList() == null || getSubTokenList().size() == 0)
         return;

      CodeMirrorTokenImpl lastSubToken = getSubTokenList().get(getSubTokenList().size() - 1);

      lastSubToken.setType(newType);
   }
   
   /**
    * Set token.elementType += elementType  
    * @param elementType
    */
   public void concatElementTypeOfLastSubToken(String elementType)
   {
      if (getSubTokenList() == null || getSubTokenList().size() == 0)
         return;

      CodeMirrorTokenImpl lastSubToken = getSubTokenList().get(getSubTokenList().size() - 1);

      if (lastSubToken.getElementType() == null)
      {
         lastSubToken.setElementType(elementType);
      }
      else
      {
         lastSubToken.setElementType(lastSubToken.getElementType() + elementType);
      }
   }
   
   public void addSubToken(CodeMirrorTokenImpl subToken)
   {
      if (this.getSubTokenList() == null)
      {
         this.setSubTokenList(new ArrayList<CodeMirrorTokenImpl>());
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
      CodeMirrorTokenImpl token = getLastSubToken();

      if (token != null)
      {
         token.setName(name);
      }
   }

   /**
    * 
    * @return last subtoken or null if there is no any subtoken
    */
   public CodeMirrorTokenImpl getLastSubToken()
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
   public CodeMirrorTokenImpl getLastAnnotationToken()
   {
      if (this.getAnnotations() != null && this.getAnnotations().size() != 0)
      {
         return this.getAnnotations().get(this.getAnnotations().size() - 1);
      }

      return null;
   }

   /**
    * Concatenates the specified "string" to the end of name of last token in annotation list
    * @param string
    */
   public void lastAnnotationTokenNameConcat(String string)
   {
      if (getLastAnnotationToken() != null)
      {
         getLastAnnotationToken().setName(getLastAnnotationToken().getName().concat(string)); // update last  annotation token
      }
   }
   
}
