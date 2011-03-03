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
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 9:40:49 AM evgen $
 *
 */
public class JavaConstructorWidget extends JavaTokenWidgetBase
{

   private Grid grid;

   private String value;

   /**
    * @param token
    */
   public JavaConstructorWidget(Token token, String restContext)
   {
      super(token, restContext);

      grid = new Grid(1, 3);
      grid.setStyleName(CodeAssistantClientBundle.INSTANCE.css().item());
      grid.setWidth("100%");

      Image i = getImage();
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      value = token.getName() + token.getProperty(TokenProperties.PARAMETER_TYPES).isStringProperty().stringValue();
      Label nameLabel = new Label(value, false);
      nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());
      grid.setWidget(0, 1, nameLabel);

      Label label = new Label("-" + token.getProperty(TokenProperties.DECLARING_CLASS), false);
      label.setStyleName(CodeAssistantClientBundle.INSTANCE.css().fqnStyle());
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
    * Return {@link Image} for specific access modifier
    * @return
    */
   private Image getImage()
   {
      Image i;
      if (ModifierHelper.isPrivate(modifieres))
      {
         i = new Image(CodeAssistantClientBundle.INSTANCE.publicField());
      }
      else if (ModifierHelper.isProtected(modifieres))
      {
         i = new Image(CodeAssistantClientBundle.INSTANCE.protectedField());
      }
      else if (ModifierHelper.isPublic(modifieres))
      {
         i = new Image(CodeAssistantClientBundle.INSTANCE.publicField());
      }
      else
      {
         i = new Image(CodeAssistantClientBundle.INSTANCE.defaultField());
      }
      return i;
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenValue()
    */
   @Override
   public String getTokenValue()
   {
      return value;
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenDecription()
    */
   @Override
   public Widget getTokenDecription()
   {
      return null;
   }

}
