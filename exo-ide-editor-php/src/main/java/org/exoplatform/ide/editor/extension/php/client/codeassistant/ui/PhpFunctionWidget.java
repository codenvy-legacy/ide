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
package org.exoplatform.ide.editor.extension.php.client.codeassistant.ui;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.extension.php.client.PhpClientBundle;

/**
 * Ui component that represent PHP function or class method.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class PhpFunctionWidget extends PhpTokenWidgetBase
{

   /**
    * @param token
    */
   public PhpFunctionWidget(Token token)
   {
      super(token);
      grid = new Grid(1, 3);
      grid.setStyleName(PhpClientBundle.INSTANCE.css().item());

      Image i = getImage();
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      String name = token.getName();
      if (token.hasProperty(TokenProperties.PARAMETER_TYPES))
         name += token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
      else
         name += getParameters();

      Label nameLabel = new Label(name, false);
      nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());
      grid.setWidget(0, 1, nameLabel);

      String pack = token.getProperty(TokenProperties.DECLARING_CLASS).isStringProperty().stringValue();
      Label label = new Label("-" + pack, false);
      label.setStyleName(PhpClientBundle.INSTANCE.css().fqnStyle());
      grid.setWidget(0, 2, label);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 2, "100%");

      initWidget(grid);
      setWidth("100%");
   }

   /**
    * @return
    */
   private String getParameters()
   {
      String param = "(";
      if (token.hasProperty(TokenProperties.PARAMETERS))
      {
         for (Token t : token.getProperty(TokenProperties.PARAMETERS).isArrayProperty().arrayValue())
         {
            param += t.getName() + ",";
         }
         if (param.endsWith(","))
         {
            param = param.substring(0, param.length() - 1);
         }

      }
      return param += ")";
   }

   /**
    * @see org.exoplatform.ide.editor.extension.php.client.codeassistant.ui.PhpTokenWidgetBase#getTokenValue()
    */
   @Override
   public String getTokenValue()
   {
    
      return token.getName() + getParameters();

   }

   private Image getImage()
   {
      return new Image(PhpClientBundle.INSTANCE.publicMethod());
   }

}
