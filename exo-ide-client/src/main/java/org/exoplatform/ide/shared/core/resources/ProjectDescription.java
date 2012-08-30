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

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Aug 16, 2012  
 */
public interface ProjectDescription
{
//
//   /**
//    * Returns the list of build commands to run when building the described project.
//    * The commands are listed in the order in which they are to be run.
//    *
//    * @return the list of build commands for the described project 
//    */
//   ICommand[] getBuildSpec();
//
//   /**
//    * Returns the name of the described project.
//    *
//    * @return the name of the described project
//    */
//   String getName();
//
//   /**
//    * Sets the name of the described project.
//    * <p>
//    * Setting the name on a description and then setting the 
//    * description on the project has no effect; the new name is ignored.
//    * </p>
//    * <p>
//    * Creating a new project with a description name which doesn't
//    * match the project handle name results in the description name
//    * being ignored; the project will be created using the name
//    * in the handle.
//    * </p>
//    *
//    * @param projectName the name of the described project
//    */
//   void setName(String projectName);
//
//   /** 
//    * Returns the list of natures associated with the described project.
//    * Returns an empty array if there are no natures on this description.
//    *
//    * @return the list of natures for the described project
//    */
//   String[] getNatureIds(); // TODO: REPLACE WITH NATURE CLASSES! No reflection in GWT
//
//   /** 
//    * Returns whether the project nature specified by the given
//    * nature extension id has been added to the described project. 
//    *
//    * @param natureId the nature extension identifier
//    * @return <code>true</code> if the described project has the given nature 
//    */
//   boolean hasNature(String natureId);
//
//   /** 
//    * Sets the list of natures associated with the described project.
//    * A project created with this description will have these natures
//    * added to it in the given order.
//    * <p>
//    * Users must call {@link IProject#setDescription(IProjectDescription, int, IProgressMonitor)} 
//    * before changes made to this description take effect.
//    * </p>
//    *
//    * @param natures the list of natures
//    */
//   void setNatureIds(String[] natures);
//
//   /**
//    * Returns a new build command.
//    * <p>
//    * Note that the new command does not become part of this project
//    * description's build spec until it is installed via the <code>setBuildSpec</code>
//    * method.
//    * </p>
//    *
//    * @return a new command
//    */
//   ICommand newCommand();
//
//   /**
//    * Sets the list of build command to run when building the described project.
//    * <p>
//    * Users must call {@link IProject#setDescription(IProjectDescription, int, IProgressMonitor)} 
//    * before changes made to this description take effect.
//    * </p>
//    *
//    * @param buildSpec the array of build commands to run
//    */
//   void setBuildSpec(ICommand[] buildSpec);

}
