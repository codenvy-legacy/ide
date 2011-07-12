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
package org.exoplatform.ide.editor.extension.php.client.outline;

import java.util.List;

import org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreatorImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.extension.php.client.PhpClientBundle;


/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class PhpOutlineItemCreator extends OutlineItemCreatorImpl
{  
   @Override
   public String getTokenIcon(TokenBeenImpl token)
   {
      switch (token.getType())
      {
         case PHP_TAG:
            return PhpClientBundle.INSTANCE.phpTag().getURL();
         
         case FUNCTION :
            return PhpClientBundle.INSTANCE.phpTag().getURL();
            
         case VARIABLE :
            return PhpClientBundle.INSTANCE.variable().getURL();

         case CONSTANT :
            return PhpClientBundle.INSTANCE.constantItem().getURL();
            
         case METHOD :
            if (isPrivate(token))
            {
               return PhpClientBundle.INSTANCE.privateMethod().getURL();
            }
            
            else if (isProtected(token))
            {
               return PhpClientBundle.INSTANCE.protectedMethod().getURL();
            }

            else if (isPublic(token))
            {
               return PhpClientBundle.INSTANCE.publicMethod().getURL();
            }

            return PhpClientBundle.INSTANCE.publicMethod().getURL();

         case PROPERTY :            
            if (isPrivate(token))
            {
               return PhpClientBundle.INSTANCE.privateField().getURL();
            }
            
            else if (isProtected(token))
            {
               return PhpClientBundle.INSTANCE.protectedField().getURL();
            }
            
            else if (isPublic(token))
            {
               return PhpClientBundle.INSTANCE.publicField().getURL();
            }
           
            return PhpClientBundle.INSTANCE.publicField().getURL();
            
         case CLASS :
            return PhpClientBundle.INSTANCE.classItem().getURL();

         case INTERFACE :
            return PhpClientBundle.INSTANCE.interfaceItem().getURL();              
            
         case CLASS_CONSTANT:
            return PhpClientBundle.INSTANCE.classConstant().getURL();

         case NAMESPACE:
            return PhpClientBundle.INSTANCE.namespace().getURL();
            
         default :
            return "";
      }
   }
   
   @Override
   public String getTokenDisplayTitle(TokenBeenImpl token)
   {
      String label = token.getName();
 
      label = getModifiersContainer(token) + "<span class='item-name' style='margin-left: 5px;'>" + label + "</span>";            

      // Add parameter list 
      if (TokenType.FUNCTION.equals(token.getType())
               || TokenType.METHOD.equals(token.getType()))
      {
         label += getParametersList(token); 
      }
      
      // Add field type or method return type
      if (token.getElementType() != null)
      {
         label += "<span style='color:#644a17;' class='item-type'>" + getElementType(token) + "</span>";
      }
      
      return label;
   }
   
   /**
    * Return parameters list from token.getParameters()
    * @param token
    * @return parameters list like ($a, $b), or '()' if there are no parameters
    */
   protected String getParametersList(TokenBeenImpl token)
   {
      String parametersDescription = "(";

      if (token.getParameters() != null && token.getParameters().size() > 0)
      {

         List<TokenBeenImpl> parameters = token.getParameters();

         for (int i = 0; i < parameters.size(); i++)
         {
            TokenBeenImpl parameter = parameters.get(i);
            if (i > 0)
            {
               parametersDescription += ", ";
            }

            parametersDescription +=
                  "<span class='item-parameter'>" + parameter.getName() + getElementType(parameter) + "</span>";               
         }
      }

      return parametersDescription + ")";
   }
   
   /**
    * @param token {@link TokenBeenImpl} 
    * @return html element with modifiers sign
    */
   protected String getModifiersContainer(TokenBeenImpl token)
   {
      if (isStatic(token)
          || isFinal(token)
          || isAbstract(token))
      {      
         String span =
            "<span style = \"position: relative; top: -5px; margin-left: -3px; font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 9px; text-align: right;' \">";
         span += (isStatic(token)) ? "<span class='item-modifier' color ='#6d0000'>s</span>" : "";
         span += (isFinal(token)) ? "<span class='item-modifier' color ='#174c83'>f</span>" : "";      
         span += (isAbstract(token)) ? "<span class='item-modifier' color ='#004e00'>a</span>" : "";
         span += "</span>";
         
         return span;
      }
      
      return "";
   }
}