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
package org.exoplatform.ide.client.module.groovy.codeassistant.ui;

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.module.groovy.GroovyPluginImageBundle;

import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 13, 2010 4:06:10 PM evgen $
 *
 */
public class GroovyVariableWidget extends GroovyTokenWidgetBase
{

   private Grid grid;
   
   /**
    * @param token
    * @param restContext
    */
   public GroovyVariableWidget(TokenExt token, String restContext)
   {
      super(token, restContext);
      grid = new Grid(1, 2);
      grid.setStyleName(GroovyPluginImageBundle.INSTANCE.css().item());
      Image i = new Image(GroovyPluginImageBundle.INSTANCE.variable());
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      String name = token.getName() + ":" + token.getProperty(TokenExtProperties.TYPE);
      Label nameLabel = new Label(name, false);
      grid.setWidget(0, 1, nameLabel);
      
      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 1, "100%");

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
