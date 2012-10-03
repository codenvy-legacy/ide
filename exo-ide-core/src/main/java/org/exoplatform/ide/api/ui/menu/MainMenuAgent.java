/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.api.ui.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.menu.ExtendedCommand;

/**
 * Public interface of the Main Menu is represented by {@link MainMenuAgent}. It allows 3rd party code 
 * to contribute their 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public interface MainMenuAgent
{

   /**
    * Adds new item to the Main Menu, that invokes given command on click. 
    * This item will always me visible (if parent menu is visible also) 
    * and always remains enabled. Use this with attention.
    * 
    * @param path
    * @param command
    */
   public void addMenuItem(String path, Command command);

   /**
    * Adds new item to the Main Menu, that invokes given command on click. 
    * ExtendedCommand interface allows you to add Icon and define Expression 
    * that controls visible and enabled states of the item. This is the 
    * recommended way of adding the items. It is safe to provide Null as an 
    * expression. This is thought to be always visible and enabled.
    * 
    * @param path
    * @param command
    */
   public void addMenuItem(String path, ExtendedCommand command);

   /**
    * Adds new item to the Main Menu, that invokes given command on click. 
    * Provided Icon and Expressions are used to maintain the Item. Please 
    * use ExtendedCommand instead of defining expressions by hand.
    * 
    * @param path
    * @param command
    * @param visibilityExpression Value of this Core Expression is used to set Visible state of the menu item
    * @param enabledExpression Value of this Core Expression is used to set Enabled state of the menu item
    */
   public void addMenuItem(String path, Image icon, Command command, Expression visibileWhen, Expression enabledWhen);

}