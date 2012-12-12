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
package org.exoplatform.ide.wizard.newresource;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardAgentImpl;
import org.exoplatform.ide.wizard.WizardPagePresenter;
import org.exoplatform.ide.wizard.newresource.NewResourcePageView.ActionDelegate;

/**
 * Provides selecting kind of file which user wish to create.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class NewResourcePagePresenter extends AbstractWizardPagePresenter implements ActionDelegate
{
   private NewResourcePageView view;

   private WizardPagePresenter next;

   /**
    * Create presenter.
    * 
    * @param resources
    * @param wizardAgent
    * @param project
    */
   @Inject
   public NewResourcePagePresenter(Resources resources, WizardAgentImpl wizardAgent)
   {
      this("Create a new resource", resources.newResourceIcon(), wizardAgent, new NewResourcePageViewImpl(resources,
         wizardAgent.getNewResourceWizards()));
   }

   /**
    * Create presenter.
    * 
    * For tests
    * 
    * @param caption
    * @param image
    * @param wizardAgent
    * @param project
    * @param view
    */
   protected NewResourcePagePresenter(String caption, ImageResource image, WizardAgentImpl wizardAgent,
      NewResourcePageView view)
   {
      super(caption, image);
      this.view = view;
      view.setDelegate(this);
   }

   /**
    * {@inheritDoc}
    */
   public boolean isCompleted()
   {
      return next != null;
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasNext()
   {
      return next != null;
   }

   /**
    * {@inheritDoc}
    */
   public WizardPagePresenter flipToNext()
   {
      next.setPrevious(this);
      next.setUpdateDelegate(delegate);
      return next;
   }

   /**
    * {@inheritDoc}
    */
   public String getNotice()
   {
      if (next == null)
      {
         return "Please, select resource type.";
      }

      return null;
   }

   /**
    * {@inheritDoc}
    */
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }

   /**
    * {@inheritDoc}
    */
   public void selectedFileType(NewResourceWizardData newResourceWizard)
   {
      next = newResourceWizard.getWizardPage();
      delegate.updateControls();
   }

   /**
    * {@inheritDoc}
    */
   public boolean canFinish()
   {
      return false;
   }
}