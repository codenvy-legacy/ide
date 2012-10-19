/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.welcome;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasWidgets;

import org.exoplatform.ide.part.AbstractPartPresenter;

/**
 * Simple Welcome Page
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class WelcomePage extends AbstractPartPresenter
{

   private static WelcomePageUiBinder uiBinder = GWT.create(WelcomePageUiBinder.class);
   private Element element;

   interface WelcomePageUiBinder extends UiBinder<Element, WelcomePage>
   {
   }

   public WelcomePage()
   {
      element = uiBinder.createAndBindUi(this);
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void go(HasWidgets container)
   {
      HTML h = new HTML();
      h.getElement().appendChild(element);
      h.setSize("100%", "100%");
      container.add(h);
   }

   /**
    * @see org.exoplatform.ide.part.PartPresenter#getTitle()
    */
   @Override
   public String getTitle()
   {
      return "Welcome";
   }

}
