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

import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.MenuItem;

/**
 * The class is type of menu item. It has additional state (selected/unselected). 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class ToggleItem extends MenuItem implements Selectable
{
   private boolean isSelected;

   private ToolbarResources resources;

   /**
    * Create Menu Item.
    * 
    * @param text
    * @param asHTML
    * @param cmd
    * @param isSelected
    * @param resources
    */
   public ToggleItem(String text, boolean asHTML, ScheduledCommand cmd, boolean isSelected, ToolbarResources resources)
   {
      super(text, asHTML, cmd);

      this.resources = resources;
      setSelected(isSelected);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean isSelected()
   {
      return isSelected;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setSelected(boolean isSelected)
   {
      this.isSelected = isSelected;

      if (isSelected)
      {
         this.removeStyleName(resources.toolbarCSS().uncheckedToolbarItem());
         this.addStyleName(resources.toolbarCSS().checkedToolbarItem());
      }
      else
      {
         this.removeStyleName(resources.toolbarCSS().checkedToolbarItem());
         this.addStyleName(resources.toolbarCSS().uncheckedToolbarItem());
      }
   }
}