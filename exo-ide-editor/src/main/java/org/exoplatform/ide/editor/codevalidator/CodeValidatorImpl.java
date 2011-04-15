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
package org.exoplatform.ide.editor.codevalidator;

import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.CodeValidator;
import org.exoplatform.ide.editor.api.DefaultCodeValidator;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;


/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
@SuppressWarnings("serial")
public abstract class CodeValidatorImpl extends CodeValidator
{
   private static HashMap<String, CodeValidator> factory = new HashMap<String, CodeValidator>() {{
      put(MimeType.APPLICATION_GROOVY, new GroovyCodeValidator());
      put(MimeType.APPLICATION_JAVA, new JavaCodeValidator());
   }};  
   
   public static CodeValidator getValidator(String mimeType)
   {
      if (factory.containsKey(mimeType))
      {
         return factory.get(mimeType);
      }

      return new DefaultCodeValidator();
   }

   /**
    * Extract tokens with mimeType from tokenList
    * @param tokenList
    * @param code
    * @param mimeType
    * @return
    */
   public static List<TokenBeenImpl> extractCode(List<TokenBeenImpl> tokenList, List<TokenBeenImpl> code, String mimeType)
   {
      for (TokenBeenImpl token : tokenList)
      {
         analizeToken(token, code, mimeType);  // update groovyCode
      }
      
      return code;
   }

   private static void analizeToken(TokenBeenImpl currentToken, List<TokenBeenImpl> code, String mimeType)
   {
      if (currentToken == null) 
      {
         return;
      }
      
      if (currentToken.getSubTokenList() != null)
      {
         for (TokenBeenImpl token : currentToken.getSubTokenList())
         {
            analizeToken(token, code, mimeType);
         }         
      }
      

      if (currentToken.getSubTokenList() != null)
      {
         if (MimeType.APPLICATION_GROOVY.equals(mimeType))
         {
            if (!TokenType.GROOVY_TAG.equals(currentToken.getType()))
            {
               return;
            }
         }
         
         if (MimeType.APPLICATION_JAVA.equals(mimeType))
         {
            if (!TokenType.JSP_TAG.equals(currentToken.getType()))
            {
               return;
            }
         }
         
         for (TokenBeenImpl subtoken : currentToken.getSubTokenList())
         {
            if (mimeType.equals(subtoken.getMimeType())) 
            {
               code.add(subtoken);
            }
         }
      }   

   }
   
}
