/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.plugin.gadget;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.framework.control.NewItemControl;
import org.exoplatform.ideall.client.framework.model.AbstractApplicationContext;
import org.exoplatform.ideall.client.framework.plugin.AbstractIDEModule;
import org.exoplatform.ideall.client.plugin.gadget.controls.DeployGadgetCommand;
import org.exoplatform.ideall.client.plugin.gadget.controls.UndeployGadgetCommand;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GadgetPlugin extends AbstractIDEModule
{
   
   public GadgetPlugin(HandlerManager eventBus, AbstractApplicationContext context)
   {
      super(eventBus, context);
      new GadgetPluginEventHandler(eventBus, context);
   }

   /**
    * @see org.exoplatform.ideall.client.framework.plugin.IDEPlugin#initializePlugin(com.google.gwt.event.shared.HandlerManager, org.exoplatform.ideall.client.framework.model.AbstractApplicationContext)
    */
   public void initializePlugin(HandlerManager eventBus, AbstractApplicationContext context)
   {      
      addControl(new NewItemControl("File/New/New Google Gadget", "Google Gadget", "Create New Google 4Gadget",
         Images.GOOGLE_GADGET, MimeType.GOOGLE_GADGET));
      addControl(new DeployGadgetCommand(), true, true);
      addControl(new UndeployGadgetCommand(), true, true);
      
      new GadgetPluginEventHandler(eventBus, context);
   }

}
