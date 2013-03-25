/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.java.client.stackview;

import com.codenvy.ide.api.ui.part.AbstractPartPresenter;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * Dummy part for demo purposes. Can later be used as template for debug stack view
 * 
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class StackViewPartPresenter extends AbstractPartPresenter
{
   private static final String TITLE = "Stack";

   @Inject
   public StackViewPartPresenter()
   {
   }
   
   /**
    * {@inheritDoc}
    */
   @Override
   public String getTitle()
   {
      return TITLE;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public ImageResource getTitleImage()
   {
      return null;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public String getTitleToolTip()
   {
      return "Displays Java Call Stack";
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void go(AcceptsOneWidget container)
   {
      Label label = new Label();
      label.setText("To be implemented");
      container.setWidget(label);
   }
}
