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
package org.exoplatform.ide.client.editor;

import com.google.inject.Inject;

import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;

import org.exoplatform.ide.client.presenter.Presenter;

/**
 * 
 * Dummy Editor implementation. 
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 27, 2012  
 */
public class EditorPresenter implements Presenter
{
   // TODO: rework. It should be generic type accepting file-resource model
   // Review Eclipse Workspace Resource model
   // Editor should accept a model, not the content

   /**
    * Editor view interface description
    */
   public interface Display extends IsWidget
   {
      HasText getTextArea();
   }

   Display display;

   @Inject
   public EditorPresenter(Display display)
   {
      this.display = display;
   }

   /**
   * {@inheritDoc}
   */
   public void go(HasWidgets container)
   {
      container.add(display.asWidget());
   }

   /**
    * Set the content of the file.
    * To be removed 
    * 
    * @param text
    */
   public void setText(String text)
   {
      display.getTextArea().setText(text);
   }

}
