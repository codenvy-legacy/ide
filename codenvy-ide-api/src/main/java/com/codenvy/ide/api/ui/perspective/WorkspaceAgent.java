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


import com.codenvy.ide.api.ui.part.PartPresenter;
import com.codenvy.ide.api.ui.perspective.PerspectivePresenter.PartStackType;
import com.codenvy.ide.extension.SDK;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Provider;


/**
 * Handles IDE Perspective, allows to open/close/switch beetween perspectives, 
 * manages opened Parts.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
@SDK(title = "ide.api.ui.workspace")
public interface WorkspaceAgent
{

   /**
    * Register a new perspective.
    * 
    * TODO: associate perspectives by groups to prompt when some actions performed:
    * like start Java Debug, open Java project and etc.
    * 
    * @param title
    * @param icon
    * @param pespectiveProvider
    */
   public void registerPerspective(String title, ImageResource icon,
      Provider<? extends PerspectivePresenter> pespectiveProvider);

   /**
    * Open new or show already opened Perspective
    * 
    * @param title
    */
   public void openPerspective(String title);

   /**
    * Closed opened perspective
    * 
    * @param title
    */
   public void closePerspective(String title);

   /**
    * Activate given part
    * 
    * @param part
    */
   public void setActivePart(PartPresenter part);

   /**
    * Opens given Part
    * 
    * @param part
    * @param type
    * 
    */
   public void showPart(PartPresenter part, PartStackType type);

}