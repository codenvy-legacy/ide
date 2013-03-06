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
package com.codenvy.ide.java.client.wizard;

import com.codenvy.ide.view.View;

/**
 * View for new Java project wizard.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public interface NewJavaProjectPageView extends View<NewJavaProjectPageView.ActionDelegate>
{
   /**
    *Action delegate for new Java project wizard
    */
   public interface ActionDelegate
   {
      /**
       * Checks whether project's name is complete or not and updates navigation buttons.
       */
      void checkProjectInput();
   }

   /**
    * Get new Project name
    * @return the new project name.
    */
   String getProjectName();

   /**
    * Get Source folder name
    * @return the source folder name
    */
   String getSourceFolder();
}