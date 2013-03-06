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
package com.codenvy.ide.api.ui.menu;

import com.codenvy.ide.extension.SDK;
import com.codenvy.ide.menu.ExtendedCommand;
import com.codenvy.ide.toolbar.ToggleCommand;

/**
 * Public interface of the Main Menu is represented by {@link MainMenuAgent}. It allows 3rd party code
 * to contribute their
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@SDK(title = "ide.api.ui.menu")
public interface MainMenuAgent
{
   /**
    * Adds new item to the Main Menu, that invokes given command on click.
    * ExtendedCommand interface allows you to add Icon and define Expression
    * that controls visible and enabled states of the item. It is safe to
    * provide Null as an expression. This is thought to be always visible
    * and enabled.
    *
    * @param path
    * @param command
    */
   public void addMenuItem(String path, ExtendedCommand command);

   /**
    * Adds new toggle item to the Main Menu, that invokes given command on click.
    * ToggleCommand interface allows to add Icon and define Expression
    * that controls visible, enabled and selected states of the item. It is safe to provide
    * Null as an expression. This is thought to be always visible and enabled. Selected expression
    * don't have to be Null.
    * 
    * @param path
    * @param command
    */
   public void addMenuItem(String path, ToggleCommand command);
}