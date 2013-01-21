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
package org.exoplatform.ide.part.console;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import org.exoplatform.ide.part.AbstractPartPresenter;

/**
 * Template for the Console View Part. Used for demo and currently does nothing.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
public class ConsolePartPresenter extends AbstractPartPresenter
{
   private static final String TITLE = "Console";

   /**
    * Construct empty Part
    */
   @Inject
   public ConsolePartPresenter()
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
      return "Displays console output";
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
