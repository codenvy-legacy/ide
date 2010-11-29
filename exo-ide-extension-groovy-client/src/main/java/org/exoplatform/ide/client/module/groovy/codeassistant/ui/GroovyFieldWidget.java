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

import java.util.HashMap;

import org.exoplatform.ide.client.framework.codeassistant.TokenExt;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtProperties;
import org.exoplatform.ide.client.framework.codeassistant.TokenExtType;
import org.exoplatform.ide.client.module.groovy.GroovyPluginImageBundle;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 29, 2010 10:25:25 AM evgen $
 *
 */
public class GroovyFieldWidget extends GroovyTokenWidgetBase
{
   
   private Grid grid;

   /**
    * @param token
    */
   public GroovyFieldWidget(TokenExt token, HashMap<TokenExtType, ImageResource> images)
   {
      super(token);
      grid = new Grid(1, 3);
      grid.setStyleName(GroovyPluginImageBundle.INSTANCE.css().item());

      Image i = new Image(images.get(token.getType()));
      i.setHeight("16px");
      grid.setWidget(0, 0, i);

      String name = token.getName() +":" + token.getProperty(TokenExtProperties.TYPE);
      name += ":" +token.getProperty(TokenExtProperties.RETURNTYPE);

      grid.setWidget(0, 1, new Label(name, false));

      String pack = token.getProperty(TokenExtProperties.DECLARINGCLASS);
      Label l = new Label("-"+pack);
      l.setStyleName(GroovyPluginImageBundle.INSTANCE.css().fqnStyle());
      
      grid.setWidget(0, 2, l);

      grid.getCellFormatter().setWidth(0, 0, "16px");
      grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_LEFT);
      grid.getCellFormatter().setWidth(0, 2, "100%");

      initWidget(grid);
      setWidth("100%");
   }

}
