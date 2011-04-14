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
package org.exoplatform.ide.editor.codeassistant.html;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: HtmlTokenWidget Feb 23, 2011 1:12:49 PM evgen $
 *
 */
public class HtmlTokenWidget extends TokenWidget
{
   
   private Grid grid;
   /**
    * @param token
    */
   public HtmlTokenWidget(Token token)
   {
      super(token);
      grid = new Grid(1, 3);
      grid.setStyleName(CodeAssistantClientBundle.INSTANCE.css().item());
      grid.setWidth("100%");

      Image i = getImage();
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      String name = token.getName();
      if (token.hasProperty(TokenProperties.SHORT_HINT)  && token.getType()!= TokenType.TEMPLATE)
      {
         String hint = token.getProperty(TokenProperties.SHORT_HINT).isStringProperty().stringValue();
         Label hintLabel = new Label(hint, false);
         grid.setWidget(0, 2, hintLabel);
         
      }

      Label nameLabel = new Label(name, false);

      grid.setWidget(0, 1, nameLabel);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 2, "100%");

      initWidget(grid);
   }

   /**
    * @return
    */
   private Image getImage()
   {
      if(token.getType() == TokenType.ATTRIBUTE)
         return new Image(CodeAssistantClientBundle.INSTANCE.attribute());
      else if(token.getType() == TokenType.TAG)
         return new Image(CodeAssistantClientBundle.INSTANCE.tag());
      
      return new Image(CodeAssistantClientBundle.INSTANCE.property());
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenValue()
    */
   @Override
   public String getTokenValue()
   {
      if (token.hasProperty(TokenProperties.CODE))
         return token.getProperty(TokenProperties.CODE).isStringProperty().stringValue();
      else
         return token.getName();
   }

   /**
    * @see org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidget#getTokenDecription()
    */
   @Override
   public Widget getTokenDecription()
   {
      if(token.hasProperty(TokenProperties.FULL_TEXT))
      {
         Widget w = new SimplePanel();
         w.getElement().setInnerHTML(token.getProperty(TokenProperties.FULL_TEXT).isStringProperty().stringValue());
         return w;
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenName()
    */
   @Override
   public String getTokenName()
   {
      return getToken().getName();
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setSelectedStyle()
    */
   @Override
   public void setSelectedStyle()
   {
      setStyleName(CodeAssistantClientBundle.INSTANCE.css().selectedItem());
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setOveredStyle()
    */
   @Override
   public void setOveredStyle()
   {
      setStyleName(CodeAssistantClientBundle.INSTANCE.css().overedItem());
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setDefaultStyle()
    */
   @Override
   public void setDefaultStyle()
   {
      setStyleName(CodeAssistantClientBundle.INSTANCE.css().item());
   }

}
