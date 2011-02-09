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
package org.exoplatform.ide.extension.groovy.client.codeassistant.ui;

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.extension.groovy.client.GroovyPluginImageBundle;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 16, 2010 11:33:46 AM evgen $
 *
 */
public class GroovyKeyWordWidget extends GroovyTokenWidgetBase
{

   private Grid grid;

   /**
    * @param token
    * @param restContext
    */
   public GroovyKeyWordWidget(TokenExt token)
   {
      super(token, "");
      grid = new Grid(1, 2);
      grid.setStyleName(GroovyPluginImageBundle.INSTANCE.css().item());
      grid.setWidth("100%");

      Image i = new Image(GroovyPluginImageBundle.INSTANCE.blankImage());
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      Label nameLabel = new Label(token.getName(), false);
//      nameLabel.setStyleName(GroovyPluginImageBundle.INSTANCE.css().keywordStyle());
      grid.setWidget(0, 1, nameLabel);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);

      initWidget(grid);

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
    * @see org.exoplatform.ide.client.module.groovy.codeassistant.ui.GroovyTokenWidgetBase#getTokenDecription()
    */
   @Override
   public String getTokenDecription()
   {
      return null;
   }
}
