/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.client;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.java.client.control.CreateJavaProjectControl;
import org.exoplatform.ide.extension.java.client.create.CreateJavaProjectPresenter;
import org.exoplatform.ide.extension.java.shared.ProjectType;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Java extention for IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: JavaExtension.java Jun 21, 2011 12:29:16 PM vereshchaka $
 *
 */
public class JavaExtension extends Extension implements InitializeServicesHandler
{
   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   public static final JavaLocalizationConstant LOCALIZATION_CONSTANT = GWT.create(JavaLocalizationConstant.class);

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new JavaClientServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      eventBus = IDE.EVENT_BUS;
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);
      
      IDE.getInstance().addControl(new CreateJavaProjectControl(ProjectType.WEB), DockTarget.NONE, false);
      IDE.getInstance().addControl(new CreateJavaProjectControl(ProjectType.SPRING), DockTarget.NONE, false);
      new CreateJavaProjectPresenter(eventBus);
   }

}
