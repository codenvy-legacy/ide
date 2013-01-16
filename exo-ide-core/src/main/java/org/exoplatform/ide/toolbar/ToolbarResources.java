/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.toolbar;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;

/**
 * Resources of toolbar.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ToolbarResources extends ClientBundle
{
   public interface ToolbarCSS extends CssResource
   {
      String checkedToolbarItem();

      String uncheckedToolbarItem();

      String toolbarItem();

      String disable();

      String enable();

      String menuHorizontal();
      
      String menuVertical();
   }

   @Source({"org/exoplatform/ide/toolbar/Toolbar.css", "org/exoplatform/ide/common/constants.css"})
   ToolbarCSS toolbarCSS();

   @Source("org/exoplatform/ide/toolbar/check.png")
   DataResource checkIcon();

   @Source("org/exoplatform/ide/toolbar/empty.png")
   DataResource emptyIcon();
}