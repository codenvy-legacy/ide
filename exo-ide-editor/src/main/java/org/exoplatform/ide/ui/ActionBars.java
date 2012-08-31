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
package org.exoplatform.ide.ui;


/**
 * Used by a part to access its menu, toolbar, and status line managers.  
 * <p>
 * In a workbench window there are a number of actions which are applicable to
 * all parts.  Some common examples are <code>CUT</code>, <code>COPY</code> and 
 * <code>PASTE</code>. These actions, known as "global actions", are contributed to 
 * the workbench window by the window itself and shared by all parts.  The
 * presentation is owned by the window.  The implementation is delegated to the
 * active part.  
 * </p>
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface ActionBars
{
   /**
    * Returns the menu manager.
    * <p>
    * Note: Clients who add or remove items from the returned menu manager are
    * responsible for calling <code>updateActionBars</code> so that the changes
    * can be propagated throughout the workbench.
    * </p>
    *
    * @return the menu manager
    */
   public MenuManager getMenuManager();
   

   /**
   * Returns the status line manager.
   * <p>
   * Note: Clients who add or remove items from the returned status line
   * manager are responsible for calling <code>updateActionBars</code> so
   * that the changes can be propagated throughout the workbench.
   * </p>
   * 
   * @return the status line manager
   */
   public StatusLineManager getStatusLineManager();

   /**
    * Returns the tool bar manager.
    * <p>
    * Note: Clients who add or remove items from the returned tool bar manager are
    * responsible for calling <code>updateActionBars</code> so that the changes
    * can be propagated throughout the workbench.
    * </p>
    *
    * @return the tool bar manager
    */
   public ToolBarManager getToolBarManager();

   /**
    * Sets the global action handler for the action with the given id.
    * <p>
    * Note: Clients who manipulate the global action list are
    * responsible for calling <code>updateActionBars</code> so that the changes
    * can be propagated throughout the workbench.
    * </p>
    *
    * @param actionId an action id declared in the registry
    * @param handler an action which implements the action id, or
    *   <code>null</code> to clear any existing handler
    * @see IWorkbenchActionConstants
    */
   public void setGlobalActionHandler(String actionId, Action handler);
}
