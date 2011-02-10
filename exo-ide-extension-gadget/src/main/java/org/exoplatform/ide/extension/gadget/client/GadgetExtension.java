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
package org.exoplatform.ide.extension.gadget.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.extension.gadget.client.controls.DeployGadgetCommand;
import org.exoplatform.ide.extension.gadget.client.controls.UndeployGadgetCommand;
import org.exoplatform.ide.extension.gadget.client.service.GadgetServiceImpl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GadgetExtension extends Extension implements InitializeServicesHandler
{

   private HandlerManager eventBus;

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   @Override
   public void initialize(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New Google Gadget", "Google Gadget",
         "Create New Google Gadget", Images.GOOGLE_GADGET, MimeType.GOOGLE_GADGET).setDelimiterBefore(true)));

      eventBus.fireEvent(new RegisterControlEvent(new DeployGadgetCommand(), DockTarget.TOOLBAR, true));
      eventBus.fireEvent(new RegisterControlEvent(new UndeployGadgetCommand(), DockTarget.TOOLBAR, true));

      //      addControl(new NewItemControl("File/New/New Google Gadget", "Google Gadget", "Create New Google 4Gadget",
      //         Images.GOOGLE_GADGET, MimeType.GOOGLE_GADGET));
      //      addControl(new DeployGadgetCommand(eventBus), true, true);
      //      addControl(new UndeployGadgetCommand(eventBus), true, true);

      new GadgetPluginEventHandler(eventBus);
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);

   }

   public void onInitializeServices(InitializeServicesEvent event)
   {
      new GadgetServiceImpl(eventBus, event.getLoader(), event.getApplicationConfiguration().getContext(), event
         .getApplicationConfiguration().getGadgetServer(), event.getApplicationConfiguration().getPublicContext());
   }

}
