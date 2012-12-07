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
package org.exoplatform.ide.java.client.wizard;

import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.exoplatform.ide.java.client.JavaClientBundle;
import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
@Singleton
public class NewJavaProjectPagePresenter extends AbstractWizardPagePresenter
{


   private WizardPagePresenter previous;

   private JavaProjectPageView view;

   @Inject
   public NewJavaProjectPagePresenter(JavaClientBundle resources, JavaProjectPageView view)
   {
      super("Java Project", resources.javaProject());
      this.view = view;
   }

   @Override
   public WizardPagePresenter flipToNext()
   {
      //TODO
      return null;
   }

   @Override
   public WizardPagePresenter flipToPrevious()
   {
      return previous;
   }

   @Override
   public void setPrevious(WizardPagePresenter previous)
   {
      this.previous = previous;
   }

   @Override
   public boolean canFinish()
   {
      //TODO
      return false;
   }

   @Override
   public boolean hasNext()
   {
      //TODO
      return false;
   }

   @Override
   public boolean hasPrevious()
   {
      return previous != null;
   }

   @Override
   public boolean isCompleted()
   {
      //TODO
      return false;
   }

   @Override
   public String getNotice()
   {
      //TODO
      return null;
   }

   @Override
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }
}
