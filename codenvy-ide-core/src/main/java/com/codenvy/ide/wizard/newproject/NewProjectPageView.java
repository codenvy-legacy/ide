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
package com.codenvy.ide.wizard.newproject;

import com.codenvy.ide.view.View;

/**
 * Interface of new project page view.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface NewProjectPageView extends View<NewProjectPageView.ActionDelegate>
{
   /**
    * Needs for delegate some function into NewProjectPage view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having selected project type
       * 
       * @param id button's id
       */
      void onProjectTypeSelected(int id);

      /**
       * Performs any actions appropriate in response to the user 
       * having selected paas
       * 
       * @param id button's id
       */
      void onPaaSSelected(int id);
   }
}