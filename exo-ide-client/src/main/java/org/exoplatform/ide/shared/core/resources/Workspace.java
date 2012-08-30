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
package org.exoplatform.ide.shared.core.resources;

import org.exoplatform.ide.client.core.runtime.ProgressMonitor;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
 */
public interface Workspace
{
   /**
    * Adds the given listener for resource change events to this workspace. Has
    * no effect if an identical listener is already registered.
    * 
    * @param listener the listener
    */
   void addResourceChangeListener(ResourceChangeListener listener);

   /**
    * Removes the given resource change listener from this workspace. Has no
    * effect if an identical listener is not registered.
    * 
    * @param listener the listener
    */
   void removeResourceChangeListener(ResourceChangeListener listener);

   /**
    * Copies the given sibling resources so that they are located as members of
    * the resource at the given path; the names of the copies are the same as
    * the corresponding originals.
    * <p>
    * This method is long-running; progress and cancellation are provided by
    * the given progress monitor.
    * </p>
    * @param resources
    * @param destination
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting and cancellation are not desired
    * @throws Exception
    */
   void copy(Resource[] resources, String destination, ProgressMonitor monitor) throws Exception;// TODO exception

   /**
    * Deletes the given resources.
    * 
    * @param resources
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting and cancellation are not desired
    * @throws Exception
    */
   void delete(Resource[] resources, ProgressMonitor monitor) throws Exception; // TODO

   /**
    * Moves the given sibling resources so that they are located as members of
    * the resource at the given path; the names of the new members are the
    * same.
    * </p>
    * <p>
    * This method changes resources; these changes will be reported in a
    * subsequent resource change event that will include an indication that the
    * resources have been removed from their parent and that corresponding
    * resources have been added to the new parent. Additional information
    * provided with resource delta shows that these additions and removals are
    * pairwise related.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided by
    * the given progress monitor.
    * </p>
    * @param resources the list of resources
    * @param destination is a dst path
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting and cancellation are not desired
    * @throws Exception
    */
   void move(Resource[] resources, String destination, ProgressMonitor monitor) throws Exception;// TODO

   /**
    * Builds all projects in this workspace. Projects are built in the order
    * specified in this workspace's description. Projects not mentioned in the
    * order or for which the order cannot be determined are built in an
    * undefined order after all other projects have been built. If no order is
    * specified, the workspace computes an order determined by project
    * references.
    * <p>
    * This method may change resources; these changes will be reported in a
    * subsequent resource change event.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided by
    * the given progress monitor.
    * </p>
    * Cancelation can occur even if no progress monitor is provided.
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting and cancellation are not desired
    * @return
    * @throws Exception
    */
   void build(ProgressMonitor monitor) throws Exception; // TODO EXCEPTION CLASS

   /**
    * Returns the root resource of this workspace.
    * 
    * @return the workspace root
    */
   WorkspaceRoot getRoot();

   /**
    * Returns the path variable manager for this workspace.
    * 
    * @return the path variable manager
    */
   PathVariableManager getPathVariableManager();

   /**
    * Runs the given action as an atomic workspace operation.
    */
   // void run(IWorkspaceRunnable action, IProgressMonitor monitor) throws CoreException; // TODO : need or not

   //Status save(boolean full, IProgressMonitor monitor) throws CoreException; // TODO : need or not
}
