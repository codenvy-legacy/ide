/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
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
package org.exoplatform.ide.perspective;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.exoplatform.ide.view.View;

/**
 * 
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public interface PerspectiveView<T> extends View<T>
{
   /**
    * Returns central panel.
    * 
    * @return
    */
   AcceptsOneWidget getEditorPanel();

   /**
    * Returns left panel.
    * 
    * @return
    */
   AcceptsOneWidget getNavigationPanel();

   /**
    * Returns bottom panel.
    * 
    * @return
    */
   AcceptsOneWidget getInformationPanel();

   /**
    * Returns right panel.
    * 
    * @return
    */
   AcceptsOneWidget getToolPanel();

   /**
    * Handle View events 
    */
   public interface ActionDelegate
   {
   }
}
