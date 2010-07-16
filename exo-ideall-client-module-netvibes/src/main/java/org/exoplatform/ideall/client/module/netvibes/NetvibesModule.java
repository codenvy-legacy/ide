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
package org.exoplatform.ideall.client.module.netvibes;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.framework.control.NewItemControl;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.module.AbstractIDEModule;

import com.google.gwt.event.shared.HandlerManager;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class NetvibesModule extends AbstractIDEModule
{

   public NetvibesModule(HandlerManager eventBus, AbstractApplicationContext context)
   {
      super(eventBus, context);
   }

   /**
    * @see org.exoplatform.ideall.client.framework.module.IDEModule#initializeModule()
    */
   public void initializeModule()
   {
      addControl(new NewItemControl("File/New/New Netvibes Widget", "Netvibes Widget", "Create Netvibes Widget file",Images.UWA_WIGET, MimeType.UWA_WIDGET));
   }

   /**
    * @see org.exoplatform.ideall.client.framework.module.IDEModule#initializeServices(org.exoplatform.gwtframework.commons.loader.Loader)
    */
   public void initializeServices(Loader loader)
   {
      // TODO Auto-generated method stub
   }
}
