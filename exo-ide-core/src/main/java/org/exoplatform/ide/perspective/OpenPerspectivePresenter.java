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
package org.exoplatform.ide.perspective;

import org.exoplatform.ide.Resources;

/**
 * Provides opening perspective.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class OpenPerspectivePresenter implements OpenPerspectiveView.ActionDelegate
{
   private OpenPerspectiveView view;

   private WorkspacePresenter workspacePresenter;

   private String selectedPerspective = null;

   /**
    * Create OpenPerspectivePresenter.
    * 
    * @param workspacePresenter
    * @param resources
    */
   public OpenPerspectivePresenter(WorkspacePresenter workspacePresenter, Resources resources)
   {
      this(workspacePresenter, new OpenPerspectiveViewImpl(workspacePresenter.getPerspectives(), resources));
   }

   /**
    * Creates OpenPerspectivePresenter with given instance of view.
    * 
    * For Unit Tests.
    * 
    * @param workspacePresenter
    * @param view
    */
   protected OpenPerspectivePresenter(WorkspacePresenter workspacePresenter, OpenPerspectiveView view)
   {
      this.view = view;
      this.view.setDelegate(this);
      this.workspacePresenter = workspacePresenter;

      updateComponents();
   }

   /**
    * Updates change perspective view components. 
    */
   private void updateComponents()
   {
      view.setOpenButtonEnabled(selectedPerspective != null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onOpenClicked()
   {
      workspacePresenter.openPerspective(selectedPerspective);

      view.close();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void onCancelClicked()
   {
      view.close();
   }

   /**
    * Show dialog.
    */
   public void show()
   {
      view.showDialog();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void selectedPerspective(String perspectiveName)
   {
      this.selectedPerspective = perspectiveName;

      updateComponents();
   }
}