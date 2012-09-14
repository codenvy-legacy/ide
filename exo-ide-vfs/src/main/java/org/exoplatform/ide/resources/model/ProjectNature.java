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
package org.exoplatform.ide.resources.model;

/**
 * Project Nature concept is a composition of controller and a tag. When Nature tag is 
 * added to the Project, then {@link ProjectNature#configure()} is triggered.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ProjectNature
{

   public interface ProjectNatureDescriptor
   {
      /**
       * Returns the unique identifier of this nature.

       * @return the unique nature identifier
       */
      String getNatureId();

      /**
       * Returns a displayable label for this nature.
       * Returns the empty string if no label for this nature
       * is specified in the plug-in manifest file.
       *
       * @return a displayable string label for this nature,
       *    possibly the empty string
       */
      String getLabel();

      /**
       * Returns the unique identifiers of the natures required by this nature.
       * 
       * @return an array of nature ids that this nature requires,
       *    possibly an empty array.
       */
      String[] getRequiredNatureIds();

      /**
       * Returns the identifiers of the nature sets that this nature belongs to.
       * 
       * @return an array of nature set ids that this nature belongs to,
       *    possibly an empty array.
       */
      String[] getNatureSetIds();

   }

   /** 
    * Configures this nature for its project. This is called by the workspace 
    * when natures are added to the project using <code>Project.setDescription</code>
    * and should not be called directly by clients.  The nature extension 
    * id is added to the list of natures before this method is called,
    * and need not be added here.
    * 
    * Exceptions thrown by this method will be propagated back to the caller
    * of <code>Project.setDescription</code>, but the nature will remain in
    * the project description.
    *
    * @exception Exception if this method fails.
    */
   void configure() throws Exception;

   /** 
    * De-configures this nature for its project.  This is called by the workspace 
    * when natures are removed from the project using 
    * <code>Project.setDescription</code> and should not be called directly by 
    * clients.  The nature extension id is removed from the list of natures before 
    * this method is called, and need not be removed here.
    * 
    * @exception Exception if this method fails. 
    */
   void deconfigure() throws Exception;

   /** 
    * Returns the project to which this project nature applies.
    *
    * @return the project handle
    */
   Project getProject();

   /**
    * Sets the project to which this nature applies.
    * Used when instantiating this project nature runtime.
    * This is called by <code>IProject.create()</code> or
    * <code>IProject.setDescription()</code>
    * and should not be called directly by clients.
    *
    * @param project the project to which this nature applies
    */
   void setProject(Project project);

}
