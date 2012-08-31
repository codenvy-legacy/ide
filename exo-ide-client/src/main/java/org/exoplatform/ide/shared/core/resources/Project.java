/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero GeneralLicense
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU GeneralLicense for more details.
 *
 * You should have received a copy of the GNU GeneralLicense
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.shared.core.resources;

import org.exoplatform.ide.client.core.runtime.ProgressMonitor;

import java.util.Map;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 15, 2012  
 */
public interface Project
{
   /**
    * Opens this project.  No action is taken if the project is already open.
    * <p>
    * This method changes resources; these changes will be reported
    * in a subsequent resource change event that includes
    * an indication that the project has been opened and its resources
    * have been added to the tree.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor.
    * </p>
    *
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception CoreException if this method fails. Reasons include:
    * <ul>
    * <li> Resource changes are disallowed during certain types of resource change 
    *       event notification. See <code>IResourceChangeEvent</code> for more details.</li>
    * </ul>
    * @exception OperationCanceledException if the operation is canceled. 
    * Cancelation can occur even if no progress monitor is provided.
    */
   void open(ProgressMonitor monitor) throws Exception;

   /**
    * Closes this project.  The project need not be open.  Closing
    * a closed project does nothing.
    * <p>
    * This method changes resources; these changes will be reported
    * in a subsequent resource change event that includes
    * an indication that this project has been closed and its members
    * have been removed.  
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor.
    * </p>
    *
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception Exception if this method fails.
    */
   void close(ProgressMonitor monitor) throws Exception;

   /** 
    * Builds this project. Does nothing if the project is closed.
    * <p>
    * This method may change resources; these changes will be reported
    * in a subsequent resource change event.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor.
    * </p>
    *
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception CoreException if the build fails.
    */
   void build(ProgressMonitor monitor) throws Exception;

   /**
    * Invokes the <code>build</code> method of the specified builder 
    * for this project. Does nothing if this project is closed.  If this project
    * has multiple builders on its build spec matching the given name, only
    * the first matching builder will be run. The build is run for the project's
    * active build configuration.
    * <p>
    * This method may change resources; these changes will be reported
    * in a subsequent resource change event.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor.
    * </p>
    *
    * @param builderName the name of the builder
    * @param args a table of builder-specific arguments keyed by argument name
    *    (key type: <code>String</code>, value type: <code>String</code>);
    *    <code>null</code> is equivalent to an empty map
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception Exception if the build fails.
    */
   void build(String builderName, Map<String, String> args, ProgressMonitor monitor) throws Exception; // TODO

   /**
    * Creates a new project resource in the workspace using the given project
    * description. Upon successful completion, the project will exist but be closed.
    * <p>
    * This method changes resources; these changes will be reported
    * in a subsequent resource change event, including an indication 
    * that the project has been added to the workspace.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor. 
    * </p>
    *
    * @param description the project description
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception Exception if this method fails.
    *
    */
   void create(ProjectDescription description, ProgressMonitor monitor) throws Exception;

   /**
    * Returns the description for this project.
    * The returned value is a copy and cannot be used to modify 
    * this project.  The returned value is suitable for use in creating, 
    * copying and moving other projects.
    *
    * @return the description for this project
    * @exception Exception if this method fails.
    */
   ProjectDescription getDescription() throws Exception;

   /**
    * Returns whether this project is open.
    * <p>
    * A project must be opened before it can be manipulated.
    * A closed project is passive and has a minimal memory
    * footprint; a closed project has no members.
    * </p>
    *
    * @return <code>true</code> if this project is open, <code>false</code> if
    *    this project is closed or does not exist
    */
   boolean isOpen();

   /**
    * Changes this project resource to match the given project
    * description. This project should exist and be open.
    * <p>
    * This method changes resources; these changes will be reported
    * in a subsequent resource change event, including an indication 
    * that the project's content has changed.
    * </p>
    * <p>
    * This method is long-running; progress and cancellation are provided
    * by the given progress monitor. 
    * </p>
    *
    * @param description the project description
    * @param monitor a progress monitor, or <code>null</code> if progress
    *    reporting is not desired
    * @exception Exception if this method fails. .
    *
    */
   void setDescription(ProjectDescription description, ProgressMonitor monitor) throws Exception;

   // TODO : getReferencingProjects
   // TODO : getReferencedProjects

   // TODO : getBuildConfigs
   // TODO : hasBuildConfigs
}
