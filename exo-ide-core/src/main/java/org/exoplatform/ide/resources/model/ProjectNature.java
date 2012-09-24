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

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.json.JsonStringSet;

/**
 * Project Nature concept is a composition of controller and a tag. When Nature tag is 
 * added to the Project, then {@link ProjectNature#configure()} is triggered.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public interface ProjectNature
{
   public static final String PRIMARY_NATURE_CATEGORY = "natures.primary";
   public static final String LANG_NATURE_CATEGORY = "natures.lang";
   public static final String PAAS_NATURE_CATEGORY = "natures.paas";

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
    * Returns the identifiers of the natures required by this nature.
    * 
    * @return an array of nature ids that this nature requires,
    *    possibly an empty array.
    */
   JsonStringSet getRequiredNatureIds();

   /**
    * Returns the identifiers of the Nature Categories that this nature exclusively belongs to.
    * No any other Nature of this category can exist on the project
    * 
    * @return a set of nature categories that this nature belongs to,
    *    possibly an empty array.
    */
   JsonStringSet getNatureCategories();

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
   void configure(Project project, AsyncCallback<Project> callback);

   /** 
    * De-configures this nature for its project.  This is called by the workspace 
    * when natures are removed from the project using 
    * <code>Project.setDescription</code> and should not be called directly by 
    * clients.  The nature extension id is removed from the list of natures before 
    * this method is called, and need not be removed here.
    * 
    * @exception Exception if this method fails. 
    */
   void deconfigure(Project project, AsyncCallback<Project> callback);

}
