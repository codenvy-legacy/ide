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
package com.codenvy.ide.wizard.warproject;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.paas.AbstractPaasWizardPagePresenter;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.wizard.WizardPagePresenter;
import com.codenvy.ide.wizard.newgenericproject.NewGenericProjectPagePresenter;
import com.codenvy.ide.wizard.newgenericproject.NewGenericProjectPageView;
import com.codenvy.ide.wizard.newgenericproject.NewGenericProjectWizardResource;
import com.codenvy.ide.wizard.newproject.CreateProjectHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

/**
 * Provides creating new war project.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public class NewWarProjectPagePresenter extends NewGenericProjectPagePresenter
{

   /**
    * Create page presenter.
    * 
    * @param resources
    * @param view
    * @param resourceProvider
    */
   @Inject
   protected NewWarProjectPagePresenter(NewGenericProjectWizardResource resources, NewGenericProjectPageView view,
      ResourceProvider resourceProvider)
   {
      super(resources, view, resourceProvider);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getCaption()
   {
      return "New war project wizard";
   }

   /**
    * {@inheritDoc}
    */
   public WizardPagePresenter flipToNext()
   {
      AbstractPaasWizardPagePresenter paasWizardPage = getPaaSWizardPage();
      CreateProjectHandler createProjectHandler = getCreateProjectHandler();
      createProjectHandler.setProjectName(view.getProjectName());
      paasWizardPage.setCreateProjectHandler(createProjectHandler);
      paasWizardPage.setPrevious(this);
      paasWizardPage.setUpdateDelegate(delegate);

      return paasWizardPage;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void doFinish()
   {
      CreateProjectHandler createProjectHandler = getCreateProjectHandler();
      createProjectHandler.setProjectName(view.getProjectName());
      createProjectHandler.create(new AsyncCallback<Project>()
      {
         @Override
         public void onSuccess(Project result)
         {
            // do nothing
         }

         @Override
         public void onFailure(Throwable caught)
         {
            // do nothing
         }
      });
   }
}