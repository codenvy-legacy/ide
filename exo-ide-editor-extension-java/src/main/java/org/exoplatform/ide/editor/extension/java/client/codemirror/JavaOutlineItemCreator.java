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
package org.exoplatform.ide.editor.extension.java.client.codemirror;

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreatorImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.extension.java.client.JavaClientBundle;


/**
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id
 *
 */
public class JavaOutlineItemCreator extends OutlineItemCreatorImpl
{  
   @Override
   public String getTokenIcon(TokenBeenImpl token)
   {
      switch (token.getType())
      {
         case VARIABLE :
            return JavaClientBundle.INSTANCE.variable().getURL();
            
         case PROPERTY :            
            if (isPrivate(token))
            {
               return JavaClientBundle.INSTANCE.privateField().getURL();
            }
            
            else if (isProtected(token))
            {
               return JavaClientBundle.INSTANCE.protectedField().getURL();
            }
            
            else if (isPublic(token))
            {
               return JavaClientBundle.INSTANCE.publicField().getURL();
            }

            return JavaClientBundle.INSTANCE.defaultField().getURL();
            
         case METHOD :
            if (isPrivate(token))
            {
               return JavaClientBundle.INSTANCE.privateMethod().getURL();
            }
            
            else if (isProtected(token))
            {
               return JavaClientBundle.INSTANCE.protectedMethod().getURL();
            }

            else if (isPublic(token))
            {
               return JavaClientBundle.INSTANCE.publicMethod().getURL();
            }

            return JavaClientBundle.INSTANCE.defaultMethod().getURL();
            
            
         case CLASS :
            return JavaClientBundle.INSTANCE.classItem().getURL();

         case INTERFACE :
            return JavaClientBundle.INSTANCE.interfaceItem().getURL();              
            
         case JSP_TAG :
            return JavaClientBundle.INSTANCE.jspTagItem().getURL();
            
         default :
            return "";
      }
   }
   
   @Override
   public String getTokenDisplayTitle(TokenBeenImpl token)
   {
      String label = token.getName();

      //icon, that displays in right bottom corner, if token is CLASS, 
      //and shows access modifier
      String modfImg = "";
      
      // add special images at the bottom right part of icon to mark access modificators
      if (TokenType.CLASS.equals(token.getType()) || TokenType.INTERFACE.equals(token.getType()))
      {      
         if (isPrivate(token))
         {
            modfImg =
               "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                  + " suppress=\"TRUE\" src=\"" + JavaClientBundle.INSTANCE.classPrivateItem().getURL() + "\" />";
         }
         else if (isProtected(token))
         {
            modfImg =
               "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                  + " suppress=\"TRUE\" src=\"" + JavaClientBundle.INSTANCE.classProtectedItem().getURL() + "\" />";
         }
         else if (isPublic(token))
         {
         }
         else
         {
            modfImg =
               "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:-10px; margin-top:8px;\"  border=\"0\""
                  + " suppress=\"TRUE\" src=\"" + JavaClientBundle.INSTANCE.classDefaultItem().getURL() + "\" />";
         }
      }

      String synchImg = "";
      if (isSynchronized(token))
      {
         final String marginLeft = modfImg.length() > 0 ? "-3" : "-10";
         synchImg =
            "<img id=\"resourceLocked\" style=\"position:absolute; margin-left:" + marginLeft
               + "px; margin-top:8px;\"  border=\"0\"" + " suppress=\"TRUE\" src=\"" + JavaClientBundle.INSTANCE.clockItem().getURL() + "\" />";
      }
      
      String deprecateSign = isDeprecated(token) ? "style='text-decoration:line-through;'" : "";
 
      label = getModifiersContainer(token) + modfImg + synchImg + "<span class='item-name' " + deprecateSign + " style='margin-left: 5px;' title=\"" + getAnnotationList(token)
            + "\">" + label + "</span>";            

      // Add parameter list 
      if (TokenType.METHOD.equals(token.getType()))
      {
         label += getParametersList(token); 
      }
      
      // Add field type or method return type
      if (token.getElementType() != null)
      {
         label += "<span style='color:#644a17;' class='item-type' title=\"" + getAnnotationList(token) + "\">" + getElementType(token) + "</span>";
      }
      
      return label;
   }
   
   /**
    * Return parameters list from token.getParameters()
    * @param token
    * @return parameters list like '(String, int)', or ($a, $b) for PHP-code, or '()' if there are no parameters
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

            String annotationList = getAnnotationList(parameter);

            parametersDescription +=
               "<span title=\"" + annotationList + "\">" + getAnnotationSign(annotationList)
                  + "<span class='item-parameter'>" + parameter.getElementType() + "</span></span>";
         }
      }

      return parametersDescription + ")";
   }

   /**
    * @param annotationList 
    * @return HTML code to display "@" sign near the groovy token if annotationList is not empty, or "" otherwise
    */
   protected String getAnnotationSign(String annotationList)
   {
      return (!annotationList.isEmpty()
         ? "<span style = \"font-family: Verdana, Bitstream Vera Sans, sans-serif; color: #525252; position: relative; top: -5px;\">@</span>"
         : "");
   }
}