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
package org.exoplatform.ide.wizard.genericproject;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * Provides creating new generic project.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 *
 */
public class GenericProjectPagePresenter extends AbstractWizardPagePresenter implements
   GenericProjectPageView.ActionDelegate
{
   private GenericProjectPageView view;

   private WizardPagePresenter previous;

   private WizardPagePresenter next;

   /**
    * Create presenter
    * 
    * @param caption
    * @param image
    * @param view
    */
   public GenericProjectPagePresenter(String caption, ImageResource image, GenericProjectPageView view)
   {
      super(caption, image);
      this.view = view;
      view.setCheckProjNameDelegate(this);
   }

   /**
    * {@inheritDoc}
    */
   public WizardPagePresenter flipToNext()
   {
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
      return isCompleted();
   }

   /**
    * {@inheritDoc}
    */
   public boolean hasNext()
   {
      return false;
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
      return !view.getProjectName().isEmpty();
   }

   /**
    * {@inheritDoc}
    */
   public String getNotice()
   {
      return isCompleted() ? null : "Please, enter a project name";
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
   public void checkProjectName()
   {
      delegate.updateControls();
   }
}