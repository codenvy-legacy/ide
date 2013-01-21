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
package org.exoplatform.ide.part.projectexplorer;

import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.view.View;

/**
 * Interface of project tree view.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ProjectExplorerView extends View<ProjectExplorerView.ActionDelegate>
{
   /**
    * Sets items into tree.
    * 
    * @param resource The root resource item
    */
   void setItems(Resource resource);

   /**
    * Needs for delegate some function into ProjectTree view. 
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions in response to some node action.
       * 
       * @param resource node
       */
      void onNodeAction(Resource resource);
   }
}