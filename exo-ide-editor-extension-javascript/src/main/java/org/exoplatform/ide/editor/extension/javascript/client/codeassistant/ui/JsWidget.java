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
package org.exoplatform.ide.editor.extension.javascript.client.codeassistant.ui;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.api.codeassitant.TokenType;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JsWodget Feb 24, 2011 4:22:18 PM evgen $
 *
 */
public class JsWidget extends JSBaseWidget
{

   /**
    * @param token
    */
   public JsWidget(Token token)
   {
      super(token);
      grid = new Grid(1, 3);
      grid.setStyleName(CodeAssistantClientBundle.INSTANCE.css().item());
      grid.setWidth("100%");

      Image i = getImage();
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      String name = token.getName();
      if (token.getType() == TokenType.FUNCTION)
      {
         name += "()";
      }
      if (token.hasProperty(TokenProperties.SHORT_HINT))
      {
         name += token.getProperty(TokenProperties.SHORT_HINT).isStringProperty().stringValue();
      }

      Label nameLabel = new Label(name, false);

      grid.setWidget(0, 1, nameLabel);
      String pack = "";
      if (token.hasProperty(TokenProperties.FQN))
      {
         pack = "-" + token.getProperty(TokenProperties.FQN).isStringProperty().stringValue();
      }

      Label l = new Label(pack, false);
      l.setStyleName(CodeAssistantClientBundle.INSTANCE.css().fqnStyle());
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
            return new Image(CodeAssistantClientBundle.INSTANCE.methodItem());
         case VARIABLE :
            return new Image(CodeAssistantClientBundle.INSTANCE.varItem());
         case PROPERTY :
            return new Image(CodeAssistantClientBundle.INSTANCE.property());
         case FUNCTION :
            return new Image(CodeAssistantClientBundle.INSTANCE.functionItem());
         default :
            return new Image(CodeAssistantClientBundle.INSTANCE.template());

      }

   }
}
