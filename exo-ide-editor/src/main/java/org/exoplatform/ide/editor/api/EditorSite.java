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
package org.exoplatform.ide.editor.api;

import org.exoplatform.ide.ui.ActionBars;
import org.exoplatform.ide.ui.MenuManager;


/**
 * The primary interface between an editor part and the workbench.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public interface EditorSite
{
   /**
    * Returns the action bars for this part site. Editors of the same type (and
    * in the same window) share the same action bars. Contributions to the
    * action bars are done by the <code>IEditorActionBarContributor</code>.
    * 
    * @return the action bars
    * @since 2.1
    */
    public ActionBars getActionBars();
    
    /**
     * <p>
     * Registers a pop-up menu with the default id for extension. The default id
     * is defined as the part id.
     * </p>
     * <p>
     * By default, context menus include object contributions based on the
     * editor input for the current editor. It is possible to override this
     * behaviour by calling this method with <code>includeEditorInput</code>
     * as <code>false</code>. This might be desirable for editors that
     * present a localized view of an editor input (e.g., a node in a model
     * editor).
     * </p>
     * 
     * @param menuManager
     *            the menu manager; must not be <code>null</code>.
     * @param selectionProvider
     *            the selection provider; must not be <code>null</code>.
     * @param includeEditorInput
     *            Whether the editor input should be included when adding object
     *            contributions to this context menu.
     * @since 3.1
     */
    public void registerContextMenu(MenuManager menuManager,
            SelectionProvider selectionProvider, boolean includeEditorInput);

    /**
     * <p>
     * Registers a pop-up menu with a particular id for extension. This method
     * should only be called if the target part has more than one context menu
     * to register.
     * </p>
     * <p>
     * By default, context menus include object contributions based on the
     * editor input for the current editor. It is possible to override this
     * behaviour by calling this method with <code>includeEditorInput</code>
     * as <code>false</code>. This might be desirable for editors that
     * present a localized view of an editor input (e.g., a node in a model
     * editor).
     * </p>
     * <p>
     * For a detailed description of context menu registration see
     * {@link IWorkbenchPartSite#registerContextMenu(MenuManager, ISelectionProvider)}
     * </p>
     * 
     * @param menuId
     *            the menu id; must not be <code>null</code>.
     * @param menuManager
     *            the menu manager; must not be <code>null</code>.
     * @param selectionProvider
     *            the selection provider; must not be <code>null</code>.
     * @param includeEditorInput
     *            Whether the editor input should be included when adding object
     *            contributions to this context menu.
     * @since 3.1
     */
    public void registerContextMenu(String menuId, MenuManager menuManager,
            SelectionProvider selectionProvider, boolean includeEditorInput);
}
