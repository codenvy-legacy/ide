/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.api.ui.toolbar;

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.extension.SDK;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.toolbar.ToggleCommand;

/**
 * Public interface of the Toolbar is represented by {@link ToolbarAgent}. It allows 3rd party code
 * to contribute their
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@SDK(title = "ide.api.ui.toolbar")
public interface ToolbarAgent
{
   /**
    * Adds new item to Toolbar, that invokes given command on click.
    * ExtendedCommand interface allows to add Icon and define Expression
    * that controls visible and enabled states of the item. It is safe to provide
    * Null as an expression. This is thought to be always visible and enabled.
    * 
    * @param path
    * @param command
    * @throws IllegalStateException throws when item isn't exist
    */
   public void addItem(String path, ExtendedCommand command) throws IllegalStateException;

   /**
    * Adds new toggle item to Toolbar, that invokes given command on click.
    * ExtendedCommand interface allows to add Icon and define Expression
    * that controls visible, enabled and selected states of the item. It is safe to provide
    * Null as an expression. This is thought to be always visible and enabled. Selected expression
    * don't have to be Null.
    * 
    * @param path
    * @param command
    * @throws IllegalStateException throws when item isn't exist
    */
   public void addToggleItem(String path, ToggleCommand command) throws IllegalStateException;

   /**
    * Adds new dropdown item to Toolbar, that shows menu with items on click. 
    * Provided Icon and ToolTip for item. This item will always visible 
    * (if parent item is visible also) and always remains enabled.
    * 
    * @param path
    * @param icon
    * @param tooltip
    * @throws IllegalStateException throws when item isn't exist
    */
   public void addDropDownItem(String path, Image icon, String tooltip) throws IllegalStateException;

   /**
    * Adds new dropdown item to Toolbar, that shows menu with items on click.
    * Provided Icon and ToolTip for item. Expressions that controls visible and 
    * enabled states of the item. It is safe to provide Null as an expression. 
    * This is thought to be always visible and enabled.
    * 
    * @param path
    * @param icon
    * @param tooltip
    * @param visibleWhen
    * @param enabledWhen
    * @throws IllegalStateException throws when item isn't exist
    */
   public void addDropDownItem(String path, Image icon, String tooltip, Expression visibleWhen, Expression enabledWhen)
      throws IllegalStateException;

   /**
    * Copy item from MainMenu with all its states and behavior and add it to Toolbar.
    * 
    * @param toolbarPath
    * @param mainMenuPath
    * @throws IllegalStateException throws when item isn't exist
    */
   public void copyMainMenuItem(String toolbarPath, String mainMenuPath) throws IllegalStateException;
}