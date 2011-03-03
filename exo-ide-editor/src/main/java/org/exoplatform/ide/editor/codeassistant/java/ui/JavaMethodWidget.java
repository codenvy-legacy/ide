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
package org.exoplatform.ide.editor.codeassistant.java.ui;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;
import org.exoplatform.ide.editor.codeassistant.util.ModifierHelper;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 10:06:35 AM evgen $
 *
 */
public class JavaMethodWidget extends JavaTokenWidgetBase
{

   private Grid grid;

   /**
    * @param token
    * @param restContext 
    */
   public JavaMethodWidget(Token token, String restContext)
   {
      super(token, restContext);
      grid = new Grid(1, 3);
      grid.setStyleName(CodeAssistantClientBundle.INSTANCE.css().item());
      //      grid.setWidth("100%");

      Image i = getImage();
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      String name = token.getName() + token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
      name += ":" + token.getProperty(TokenProperties.RETURN_TYPE).isStringProperty().stringValue();

      Label nameLabel = new Label(name, false);
      nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());
      grid.setWidget(0, 1, nameLabel);

      String pack = token.getProperty(TokenProperties.DECLARING_CLASS).isStringProperty().stringValue();
      Label label = new Label("-" + pack, false);
      label.setStyleName(CodeAssistantClientBundle.INSTANCE.css().fqnStyle());
      grid.setWidget(0, 2, label);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 2, "100%");

      initWidget(grid);
      //      setWidth("100%");
   }

   private Image getImage()
   {
     
      if (ModifierHelper.isPrivate(modifieres))
      {
         return new Image(CodeAssistantClientBundle.INSTANCE.privateMethod());
      }
      else if (ModifierHelper.isProtected(modifieres))
      {
         return new Image(CodeAssistantClientBundle.INSTANCE.protectedMethod());
      }
      else if (ModifierHelper.isPublic(modifieres))
      {
         return new Image(CodeAssistantClientBundle.INSTANCE.publicMethod());
      }
      else
      {
         return new Image(CodeAssistantClientBundle.INSTANCE.defaultMethod());
      }
     
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenValue()
    */
   @Override
   public String getTokenValue()
   {
      return token.getName() + token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
   }

}
