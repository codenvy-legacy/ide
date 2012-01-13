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
package org.exoplatform.ide.client.framework.outline.ui;

import java.util.List;

import org.exoplatform.gwtframework.commons.util.StringEscapeUtils;
import org.exoplatform.ide.editor.api.codeassitant.Modifier;
import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenBeenImpl;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * This implementation of interface OutlineItemCreatorImpl which is used to create code outline item widget from OutlineTreeGrid
 * class of exo.ide.client library. Also consists of some utility functions to select outline item icon and define its display
 * label. Function getOutlineItemWidget(Token token) is extended in the specific {FileType}OutlineItemCreator classes of
 * exo-ide-editor-{FileType} libraries.
 * 
 * @author <a href="mailto:dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $
 */

public abstract class OutlineItemCreatorImpl implements OutlineItemCreator
{
   public Widget getOutlineItemWidget(Token token)
   {
      Grid grid = new Grid(1, 2);
      grid.setWidth("100%");

      Image i = new Image(getTokenIcon((TokenBeenImpl)token));
      i.setHeight("16px");
      grid.setWidget(0, 0, i);
      Label l = new Label();
      l.getElement().setInnerHTML(getTokenDisplayTitle((TokenBeenImpl)token));
      l.setWordWrap(false);
      grid.setWidget(0, 1, l);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 1, "100%");
      DOM.setStyleAttribute(grid.getElement(), "display", "block");
      return grid;
   }

   /**
    * Get icon for token.
    * 
    * @param token token
    * @return icon
    */
   public abstract ImageResource getTokenIcon(TokenBeenImpl token);

   /**
    * Get the string to display token.
    * 
    * @param token to display
    * @return {@link String} display string of the token
    */
   public abstract String getTokenDisplayTitle(TokenBeenImpl token);

   /**
    * Return parameters list from token.getParameters()
    * 
    * @param token
    * @return parameters list like '(String, int)', or '()' if there are no parameters
    */
   protected String getParametersList(TokenBeenImpl token)
   {
      StringBuffer parametersDescription = new StringBuffer("(");

      if (token.getParameters() != null && token.getParameters().size() > 0)
      {

         List<TokenBeenImpl> parameters = token.getParameters();

         for (int i = 0; i < parameters.size(); i++)
         {
            TokenBeenImpl parameter = parameters.get(i);
            if (i > 0)
            {
               parametersDescription.append(", ");
            }

            parametersDescription.append("<span class='item-parameter'>")
               .append(StringEscapeUtils.htmlEncode(parameter.getElementType())).append("</span>");
         }
      }
      parametersDescription.append(")");
      return parametersDescription.toString();
   }

   /**
    * get formatted string with java type from token.getElementType() like " : java.lang.String"
    * 
    * @param token
    * @return string like " : java.lang.String", or "".
    */
   protected String getElementType(TokenBeenImpl token)
   {
      if (token.getElementType() != null)
      {
         // encode "<>" in HTML entities
         return "<span style='color:#644a17;' class='item-type' > : "
            + StringEscapeUtils.htmlEncode(token.getElementType()) + "</span>";
      }

      return "";
   }

   protected boolean isFinal(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.FINAL);
   }

   protected boolean isAbstract(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.ABSTRACT);
   }

   protected boolean isTransient(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.TRANSIENT);
   }

   protected boolean isVolative(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.VOLATILE);
   }

   protected boolean isStatic(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.STATIC);
   }

   protected boolean isProtected(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.PROTECTED);
   }

   protected boolean isPrivate(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.PRIVATE);
   }

   protected boolean isPublic(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.PUBLIC);
   }

   protected boolean isSynchronized(TokenBeenImpl token)
   {
      return token.getModifiers() != null && token.getModifiers().contains(Modifier.SYNCHRONIZED);
   }

   /**
    * Checks, whether method has deprecated annotation.
    * 
    * @param token method
    * @return boolean whether method is deprecated
    */
   protected boolean isDeprecated(TokenBeenImpl token)
   {
      if (token.getAnnotations() == null)
         return false;

      for (TokenBeenImpl annotation : token.getAnnotations())
      {
         if ("@deprecated".equalsIgnoreCase(annotation.getName()))
         {
            return true;
         }
      }
      return false;
   }

   /**
    * Return formatted annotation list from token.getAnnotations()
    * 
    * @param token
    * @return annotations like '@Path; @PathParam(&#34;name&#34;)' or "", if there are no annotations in the token
    */
   protected String getAnnotationList(TokenBeenImpl token)
   {
      if (token.getAnnotations() != null && token.getAnnotations().size() > 0)
      {
         StringBuffer title = new StringBuffer();

         for (TokenBeenImpl annotation : token.getAnnotations())
         {
            title.append(annotation.getName()).append("; ");
         }
         // replace all '"' on HTML Entity "&#34;"
         return StringEscapeUtils.htmlEncode(title.toString());
      }

      return "";
   }

   /**
    * @param token {@link TokenBeenImpl}
    * @return html element with modifiers and annotation sign
    */
   protected String getModifiersContainer(TokenBeenImpl token)
   {
      if (isTransient(token) || isVolative(token) || isStatic(token) || isFinal(token) || isAbstract(token)
         || getAnnotationList(token).length() > 0)
      {

         String span =
            "<span style = \"position: relative; top: -5px; margin-left: -3px; font-family: Verdana,Bitstream Vera Sans,sans-serif; font-size: 9px; text-align: right;' \">";
         span += (isTransient(token)) ? "<span class='item-modifier' color ='#6d0000'>t</span>" : "";
         span += (isVolative(token)) ? "<span class='item-modifier' color ='#6d0000'>v</span>" : "";
         span += (isStatic(token)) ? "<span class='item-modifier' color ='#6d0000'>s</span>" : "";
         span += (isFinal(token)) ? "<span class='item-modifier' color ='#174c83'>f</span>" : "";
         span += (isAbstract(token)) ? "<span class='item-modifier' color ='#004e00'>a</span>" : "";
         span += (getAnnotationList(token).length() > 0) ? "<span color ='#000000'>@</span>" : "";
         span += "</span>";

         return span;
      }

      return "";
   }
}
