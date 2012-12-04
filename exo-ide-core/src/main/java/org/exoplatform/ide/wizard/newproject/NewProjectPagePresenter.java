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
package org.exoplatform.ide.wizard.newproject;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * Provides selecting kind of project which user wish to create.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 *
 */
public class NewProjectPagePresenter extends AbstractWizardPagePresenter implements NewProjectPageView.ActionDelegate
{
   private WizardPagePresenter previous;

   private WizardPagePresenter next;

   private NewProjectPageView view;

   private NewProjectWizardAgentImpl container;

   /**
    * Create presenter
    * 
    * @param caption
    * @param container
    * @param resources
    */
   public NewProjectPagePresenter(String caption, NewProjectWizardAgentImpl container,
      NewProjectWizardResource resources)
   {
      super(caption, resources.newProjectIcon());
      this.container = container;
      view = new NewProjectPageViewImpl(container.getWizards(), resources);
      view.setBtnPressedDelegate(this);
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
   public WizardPagePresenter flipToPrevious()
   {
      return previous;
   }

   /**
    * {@inheritDoc}
    */
   public void setPrevious(WizardPagePresenter previous)
   {
      this.previous = previous;
   }

   /**
    * {@inheritDoc}
    */
   public boolean canFinish()
   {
      return false;
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
   public boolean hasPrevious()
   {
      return previous != null;
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
   public String getNotice()
   {
      return isCompleted() ? null : "Please, choose technology";
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
   public void onButtonPressed(int id)
   {
      next = container.getWizards().get(id).getWizardPage();
      delegate.updateControls();
   }
}