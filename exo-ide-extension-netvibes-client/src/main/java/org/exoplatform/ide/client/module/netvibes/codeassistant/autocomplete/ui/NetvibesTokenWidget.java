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
package org.exoplatform.ide.client.module.netvibes.codeassistant.autocomplete.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;
import org.exoplatform.ide.client.framework.codeassistant.TokenWidget;
import org.exoplatform.ide.client.module.netvibes.codeassistant.autocomplete.JsAutocompleteImageBundle;

/**
 * Widget for netvibes tokens for autocomplete form.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 5:00:40 PM evgen $
 *
 */
public class NetvibesTokenWidget  extends TokenWidget<TokenExt>
{

   private Grid grid;

   /**
    * @param token
    * @param number
    */
   public NetvibesTokenWidget(TokenExt token, String restContext)
   {
      super(token);
      grid = new Grid(1, 3);
      grid.setStyleName(JsAutocompleteImageBundle.INSTANCE.css().item());
      grid.setWidth("100%");

      Image i = getImage();
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      Label nameLabel = new Label(token.getName(), false);
      nameLabel.getElement().setInnerHTML(nameLabel.getElement().getInnerHTML());

      grid.setWidget(0, 1, nameLabel);

      String pack = token.getType().name();
      Label l = new Label("-" + pack, false);
      l.setStyleName(JsAutocompleteImageBundle.INSTANCE.css().fqnStyle());
      grid.setWidget(0, 2, l);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 2, "100%");

      initWidget(grid);
      setWidth("100%");
   }

   /**
    * Image that represent current token type(Class, Interface or Annotation)
    * 
    * @return {@link Image}
    */
   private Image getImage()
   {
      switch (token.getType())
      {
         case METHOD :
            return new Image(JsAutocompleteImageBundle.INSTANCE.methodItem());
         case VARIABLE :
            return new Image(JsAutocompleteImageBundle.INSTANCE.varItem());
         case PROPERTY :
            return new Image(JsAutocompleteImageBundle.INSTANCE.propertyItem());
         case FUNCTION :
            return new Image(JsAutocompleteImageBundle.INSTANCE.functionItem());
         case KEYWORD :
            return new Image(JsAutocompleteImageBundle.INSTANCE.keyword());
         case TEMPLATE :
            return new Image(JsAutocompleteImageBundle.INSTANCE.template());
         default :
            return new Image(JsAutocompleteImageBundle.INSTANCE.template());

      }

   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenValue()
    */
   @Override
   public String getTokenValue()
   {
      if (token.getType() == TokenExtType.TEMPLATE)
      {
         return token.getProperty(TokenExtProperties.CODE);
      }
      
      return token.getName();
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenDecription()
    */
   @Override
   public String getTokenDecription()
   {
      if (getToken().getType() == TokenExtType.TEMPLATE)
      {
         return getToken().getProperty(TokenExtProperties.FULL_TEXT);
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
      setStyleName(JsAutocompleteImageBundle.INSTANCE.css().selectedItem());
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setOveredStyle()
    */
   @Override
   public void setOveredStyle()
   {
      setStyleName(JsAutocompleteImageBundle.INSTANCE.css().overedItem());
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#setDefaultStyle()
    */
   @Override
   public void setDefaultStyle()
   {
      setStyleName(JsAutocompleteImageBundle.INSTANCE.css().item());
   }

}
