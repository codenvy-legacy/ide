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
package org.exoplatform.ide.editor.java.client.codeassistant.ui;

import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.editor.api.codeassitant.Token;
import org.exoplatform.ide.editor.api.codeassitant.TokenProperties;
import org.exoplatform.ide.editor.java.client.JavaClientBundle;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 19, 2010 5:00:40 PM evgen $
 * 
 */
public class JavaClassTokenWidget extends JavaTokenWidgetBase
{

   private Grid grid;

   /**
    * @param token
    * @param number
    */
   public JavaClassTokenWidget(Token token, String restContext, String projectId)
   {
      super(token, restContext, projectId);
      grid = new Grid(1, 3);
      grid.setStyleName(JavaClientBundle.INSTANCE.css().item());
      grid.setWidth("100%");

      Image i = getImage();
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      Label nameLabel = new Label(token.getName(), false);
      nameLabel.getElement().setInnerHTML(getModifiers() + nameLabel.getElement().getInnerHTML());

      grid.setWidget(0, 1, nameLabel);

      String pack = token.getProperty(TokenProperties.FQN).isStringProperty().stringValue();
      if (pack.contains("."))
         pack = pack.substring(0, pack.lastIndexOf("."));
      Label l = new Label("-" + pack, false);
      l.setStyleName(JavaClientBundle.INSTANCE.css().fqnStyle());
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

         case INTERFACE :

            return new Image(JavaClientBundle.INSTANCE.interfaceItem());

         case ANNOTATION :
            return new Image(JavaClientBundle.INSTANCE.annotationItem());

         case ENUM :
            return new Image(JavaClientBundle.INSTANCE.enumItem());

         case CLASS :
         default :
            return new Image(JavaClientBundle.INSTANCE.classItem());

      }

   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenValue()
    */
   @Override
   public String getTokenValue()
   {
      return token.getName();
   }

   /**
    * @see org.exoplatform.ide.client.framework.codeassistant.TokenWidget#getTokenDecription()
    */
   @Override
   public Widget getTokenDecription()
   {
      return new Frame(docContext + token.getProperty(TokenProperties.FQN).isStringProperty().stringValue()
         + "&projectid=" + projectId + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId() + "&isclass=true");
   }

}
