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
package org.exoplatform.ide.client.module.netvibes;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.codeassistant.events.RegisterAutocompleteEvent;
import org.exoplatform.ide.client.framework.configuration.IDEConfiguration;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.documentation.RegisterDocumentationEvent;
import org.exoplatform.ide.client.framework.module.IDEModule;
import org.exoplatform.ide.client.module.netvibes.codeassistant.autocomplete.JsAutocompleteImageBundle;
import org.exoplatform.ide.client.module.netvibes.codeassistant.autocomplete.NetvibesTokenCollector;
import org.exoplatform.ide.client.module.netvibes.codeassistant.autocomplete.NetvibesTokenWidgetFactory;
import org.exoplatform.ide.client.module.netvibes.controls.DeployUwaWidgetControl;
import org.exoplatform.ide.client.module.netvibes.service.deploy.DeployWidgetServiceImpl;
import org.exoplatform.ide.client.module.netvibes.ui.DeployUwaWidgetPresenter;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public class NetvibesModule implements IDEModule, InitializeServicesHandler
{
   private HandlerManager eventBus;

   /**
    * IDE application configuration.
    */
   private IDEConfiguration configuration;

   /**
    * @param eventBus
    */
   public NetvibesModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.fireEvent(new RegisterControlEvent(new NewItemControl("File/New/New Netvibes Widget", "Netvibes Widget",
         "Create Netvibes Widget file", Images.UWA_WIGET, MimeType.UWA_WIDGET)));
      eventBus.fireEvent(new RegisterControlEvent(new DeployUwaWidgetControl(), DockTarget.TOOLBAR, true));

      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      
      new DeployUwaWidgetPresenter(eventBus);
      
      JsAutocompleteImageBundle.INSTANCE.css().ensureInjected();
      
      eventBus.fireEvent(new RegisterDocumentationEvent(MimeType.UWA_WIDGET, "http://dev.netvibes.com/doc/uwa/documentation"));
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      configuration = event.getApplicationConfiguration();
      new DeployWidgetServiceImpl(eventBus, configuration.getContext(), event.getLoader());
      
      NetvibesTokenWidgetFactory factory = new NetvibesTokenWidgetFactory(event.getApplicationConfiguration().getContext());
      NetvibesTokenCollector collector = new NetvibesTokenCollector(eventBus);
      eventBus.fireEvent(new RegisterAutocompleteEvent(MimeType.UWA_WIDGET, factory, collector));
   }

}
