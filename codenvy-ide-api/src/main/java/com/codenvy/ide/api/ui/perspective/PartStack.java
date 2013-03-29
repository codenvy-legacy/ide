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
package com.codenvy.ide.api.ui.perspective;

import com.codenvy.ide.api.mvp.Presenter;

import com.google.gwt.user.client.ui.AcceptsOneWidget;

/**
 * Part Stack is tabbed layout element, containing Parts.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public interface PartStack extends Presenter
{

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(AcceptsOneWidget container);

   /**
    * Change the focused state of the PartStack to desired value 
    * 
    * @param focused
    */
   public void setFocus(boolean focused);

   /**
    * Add part to the PartStack. Newly added part will be activated. If the Part
    * has already been added to this PartStack, then it will be activated only. 
    * 
    * @param part
    */
   public void addPart(PartPresenter part);

   /**
    * Ask if PartStack contains given Part. 
    * 
    * @param part
    * @return
    */
   public boolean containsPart(PartPresenter part);

   /**
    * Number of parts in the PartStack
    * 
    * @return
    */
   public int getNumberOfParts();

   /**
    * Get active Part. Active is the part that is currently displayed on the screen
    * 
    * @return
    */
   public PartPresenter getActivePart();

   /**
    * Activate given part (force show it on the screen). If part wasn't previously added
    * to the PartStack or has been removed, that method has no effect.
    * 
    * @param part
    */
   public void setActivePart(PartPresenter part);

}