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
package org.exoplatform.ide.core;

import org.exoplatform.ide.command.SaveCommand;
import org.exoplatform.ide.menu.MainMenuPresenter;

import com.google.inject.Inject;

import com.google.gwt.core.client.Callback;

/**
 * Initializer for standard component i.e. some basic menu commands (Save, Save As etc) 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class StandartComponentInitializer implements Component
{

   private final MainMenuPresenter menuPresenter;

   private final SaveCommand saveCommand;

   /**
    * 
    */
   @Inject
   public StandartComponentInitializer(MainMenuPresenter menuPresenter, SaveCommand saveCommand)
   {
      this.menuPresenter = menuPresenter;
      this.saveCommand = saveCommand;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void start(Callback<Component, ComponentException> callback)
   {
      menuPresenter.addMenuItem("File/Save", saveCommand);
      callback.onSuccess(this);
   }

}
