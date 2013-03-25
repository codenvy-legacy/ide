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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.mvp.View;

import com.codenvy.ide.api.ui.menu.ToggleCommand;

import com.codenvy.ide.api.ui.menu.ExtendedCommand;


import com.google.gwt.resources.client.ImageResource;


/**
 * The view of {@link ToolbarPresenter}. 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ToolbarView extends View<ToolbarView.ActionDelegate>
{
   /**
    * Needs for delegate some function into Toolbar view.
    */
   public interface ActionDelegate
   {
   }

   /**
    * Sets Item by given path visible or invisible.
    * 
    * @param path toolbarItem path
    * @param visible state
    * @throws IllegalStateException throws when item isn't exist 
    */
   public void setVisible(String path, boolean visible) throws IllegalStateException;

   /**
    * Sets Item by given path enabled or disabled.
    * 
    * @param path toolbarItem path
    * @param enabled
    * @throws IllegalStateException throws when item isn't exist
    */
   public void setEnabled(String path, boolean enabled) throws IllegalStateException;

   /**
    * Sets Item by given path selected or unselected.
    * 
    * @param path
    * @param selected
    * @throws IllegalStateException throws when item isn't exist
    */
   public void setSelected(String path, boolean selected) throws IllegalStateException;

   /**
    * Adds toolbar item with the following path, command, visible and enabled states.
    * 
    * @param path
    * @param command
    * @param visible
    * @param enabled
    * @throws IllegalStateException throws when item isn't exist
    */
   public void addItem(String path, ExtendedCommand command, boolean visible, boolean enabled)
      throws IllegalStateException;
   
   /**
    * Adds toggle toolbar item with the following path, command with expression, visible, enabled and selected states.
    * 
    * @param path
    * @param command
    * @param visible
    * @param enabled
    * @param selected
    * @throws IllegalStateException throws when item isn't exist
    */
   public void addToggleItem(String path, ToggleCommand command, boolean visible, boolean enabled, boolean selected)
      throws IllegalStateException;

   /**
    * Adds dropdown item with the following path, icon, tooltip, visible and enabled states.
    * 
    * @param path
    * @param icon
    * @param tooltip
    * @param visible
    * @param enabled
    * @throws IllegalStateException throws when item isn't exist
    */
   public void addDropDownItem(String path, ImageResource icon, String tooltip, boolean visible, boolean enabled)
      throws IllegalStateException;

   /**
    * Copy item from MainMenu and add it to Toolbar.
    * 
    * @param toolbarPath
    * @param mainMenuPath
    * @throws IllegalStateException throws when item isn't exist
    */
   public void copyMainMenuItem(String toolbarPath, String mainMenuPath) throws IllegalStateException;
}