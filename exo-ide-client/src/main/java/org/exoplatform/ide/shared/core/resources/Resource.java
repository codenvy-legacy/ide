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
public interface Resource
{

   /**
    * Removes the local history of this resource and its descendents.
    * <p>
    * This operation is long-running; progress and cancellation are provided
    * by the given progress monitor. 
    * </p>
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting and cancellation are not desired
    */
   void clearHistory(ProgressMonitor monitor) throws Exception;

   /**
    * Makes a copy of this resource at the given path. 
    * <p>

    * This operation changes resources; these changes will be reported
    * in a subsequent resource change event that will include 
    * an indication that the resource copy has been added to its new parent.
    * </p>
    * <p>
    * This operation is long-running; progress and cancellation are provided
    * by the given progress monitor. 
    * </p>
    *
    * @param destination the destination path 
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception Exception if this resource could not be copied.
    */
   void copy(String destination, ProgressMonitor monitor) throws Exception;

   /**
    * Creates and returns the marker with the specified type on this resource.
    * Marker type ids are the id of an extension installed in the
    * <code>org.eclipse.core.resources.markers</code> extension
    * point. The specified type string must not be <code>null</code>.
    *
    * @param type the type of the marker to create
    * @return the handle of the new marker
    * @exception Exception if this method fails.
    */
   //Marker createMarker(String type) throws Exception; // TODO: ?

   /**
    * Deletes this resource from the workspace.
    * <p>
    * This method changes resources; these changes will be reported
    * in a subsequent resource change event.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor. 
    * </p>
    * 
    * @param force a flag controlling whether resources that are not
    *    in sync with the local file system will be tolerated
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception Exception if this method fails.
    */
   void delete(ProgressMonitor monitor) throws Exception;

   /**
    * Compares two objects for equality;
    * for resources, equality is defined in terms of their handles:
    * same resource type, equal full paths, and identical workspaces. 
    * Resources are not equal to objects other than resources.
    *
    * @param other the other object
    * @return an indication of whether the objects are equals
    */
   boolean equals(Object other);

   /**
    * Returns whether this resource exists in the workspace.
    *
    * @return <code>true</code> if the resource exists, otherwise
    *    <code>false</code>
    */
   boolean exists();

   /**
    * Returns all markers of the specified type on this resource,
    * and, optionally, on its children. If <code>includeSubtypes</code>
    * is <code>false</code>, only markers whose type exactly matches 
    * the given type are returned.  Returns an empty array if there 
    * are no matching markers.
    *
    * @param type the type of marker to consider, or <code>null</code> to indicate all types
    * @param includeSubtypes whether or not to consider sub-types of the given type
    * @param depth how far to recurse (see <code>IResource.DEPTH_* </code>)
    * @return an array of markers
    * @exception Exception 
    */
   //Marker[] findMarkers(String type, boolean includeSubtypes, int depth) throws Exception; // ?

   /**
    * Returns the file extension portion of this resource's name,
    * or <code>null</code> if it does not have one.
    * <p>
    * The file extension portion is defined as the string
    * following the last period (".") character in the name.
    * If there is no period in the name, the path has no
    * file extension portion. If the name ends in a period,
    * the file extension portion is the empty string.
    * </p>
    *
    * @return a string file extension
    */
   String getFileExtension();

   /**
    * Returns the full, absolute path of this resource relative to the
    * workspace.
    *
    * @return the absolute path of this resource
    */
   String getFullPath();

   /**
    * Returns the name of this resource. 
    * The name of a resource is synonymous with the last segment
    * of its full (or project-relative) path for all resources other than the 
    * workspace root.  The workspace root's name is the empty string.
    *
    * @return the name of the resource
    */
   String getName();

   /**
    * Returns the path variable manager for this resource.
    * 
    * @return the path variable manager
    */
   PathVariableManager getPathVariableManager();

   /**
    * Returns the resource which is the parent of this resource,
    * or <code>null</code> if it has no parent (that is, this
    * resource is the workspace root).
    *
    * @return the parent resource of this resource,
    *    or <code>null</code> if it has no parent
    */
   Container getParent();

   /**
    * Returns the project which contains this resource.
    * Returns itself for projects and <code>null</code>
    * for the workspace root.
    *
    * @return the project handle
    */
   Project getProject();

   /**
    * Returns the workspace which manages this resource.
    *
    * @return the workspace
    */
   Workspace getWorkspace();

   /**
    * Returns whether this resource is accessible.  For files and folders,
    * this is equivalent to existing; for projects, 
    * this is equivalent to existing and being open.  The workspace root
    * is always accessible.
    *
    * @return <code>true</code> if this resource is accessible, and
    *   <code>false</code> otherwise
    */
   boolean isAccessible();

   /**
    * Returns whether this resource is hidden in the resource tree. Returns 
    * <code>false</code> if this resource does not exist.
    *
    * @return <code>true</code> if this resource is hidden , and
    *   <code>false</code> otherwise
    */
   boolean isHidden();

   /**
    * Returns whether this resource is a virtual resource. Returns <code>true</code>
    * for folders that have been marked virtual using the {@link #VIRTUAL} update
    * flag.  Returns <code>false</code> in all other cases, including 
    * the case where this resource does not exist.  The workspace root, projects
    * and files currently cannot be made virtual.
    * 
    * @return <code>true</code> if this resource is virtual, and
    *         <code>false</code> otherwise
    */
   boolean isVirtual();

   /**
    * Moves this resource so that it is located at the given path.  
    * <p>
    * This method changes resources; these changes will be reported
    * in a subsequent resource change event that will include 
    * an indication that the resource has been removed from its parent
    * and that a corresponding resource has been added to its new parent.
    * Additional information provided with resource delta shows that these
    * additions and removals are related.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor. 
    * </p>
    *
    * @param destination the destination path 
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception Exception if this resource could not be moved. 
    */
   void move(String destination, ProgressMonitor monitor) throws Exception;

   /**
    * Returns whether this resource subtree is marked as derived. Returns
    * <code>false</code> if this resource does not exist.
    * 
    *
    * @return <code>true</code> if this resource is marked as derived, and
    *   <code>false</code> otherwise
    */
   boolean isDerived();

   /**
    * Sets whether this resource subtree is marked as derived.
    * <p>
    * A <b>derived</b> resource is a regular file or folder that is
    * created in the course of translating, compiling, copying, or otherwise 
    * processing other files. Derived resources are not original data, and can be
    * recreated from other resources. It is commonplace to exclude derived 
    * resources from version and configuration management because they would
    * otherwise clutter the team repository with version of these ever-changing
    * files as each user regenerates them.
    * </p>
    */
   void setDerived(boolean isDerived) throws Exception;

   /**
    * Returns a non-negative modification stamp, or <code>NULL_STAMP</code> if 
    * the resource does not exist or is not local or is not accessible.
    *
    * @return the modification stamp, or <code>NULL_STAMP</code> if this resource either does
    *    not exist or exists as a closed project
    */
   long getModificationStamp();

   /**
    * Reverts this resource's modification stamp.  This is intended to be used by 
    * a client that is rolling back or undoing a previous change to this resource.  
    */
   void revertModificationStamp(long value) throws Exception;

   /** 
    * @return <code>true</code> if object locked and <code>false</code> otherwise 
    */
   boolean isLocked(); // TODO
}
