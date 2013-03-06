/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.perspective;

import com.codenvy.ide.view.View;

/**
 * The view of {@link OpenPerspectivePresenter}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface OpenPerspectiveView extends View<OpenPerspectiveView.ActionDelegate>
{
   /**
    * Needs for delegate some function into ChangePerspective view.
    */
   public interface ActionDelegate
   {
      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Open button.
       */
      void onOpenClicked();

      /**
       * Performs any actions appropriate in response to the user 
       * having pressed the Cancel button.
       */
      void onCancelClicked();

      /**
       * Returns selected perspective.
       */
      void selectedPerspective(String perspectiveName);
   }

   /**
    * Sets whether Open button is enabled.
    * 
    * @param isEnabled <code>true</code> to enable the button, <code>false</code>
    * to disable it
    */
   void setOpenButtonEnabled(boolean isEnabled);

   /**
    * Close dialog.
    */
   void close();

   /**
    * Show dialog.
    */
   void showDialog();
}