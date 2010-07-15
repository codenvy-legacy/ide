/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.module.development;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.ideall.client.framework.module.AbstractIDEModule;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.module.development.control.ShowOutlineControl;
import org.exoplatform.ideall.client.module.development.control.ShowPreviewCommand;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class DevelopmentModule extends AbstractIDEModule
{

   public DevelopmentModule(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, context);
      new DevelopmentModuleEventHandler(eventBus, context);
   }

   public void initializeModule()
   {
      addControl(new ShowOutlineControl(), true);
      addControl(new ShowPreviewCommand(), true, true);
   }

   public void initializeServices(Loader loader)
   {
   }

}
