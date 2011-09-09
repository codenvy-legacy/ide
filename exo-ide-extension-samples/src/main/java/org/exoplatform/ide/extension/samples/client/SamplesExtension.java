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
package org.exoplatform.ide.extension.samples.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.samples.client.load.ShowSamplesPresenter;
import org.exoplatform.ide.extension.samples.client.location.SelectLocationPresenter;
import org.exoplatform.ide.extension.samples.client.startpage.OpenStartPageEvent;
import org.exoplatform.ide.extension.samples.client.startpage.StartPagePresenter;
import org.exoplatform.ide.extension.samples.client.wizard.definition.WizardDefinitionStepPresenter;
import org.exoplatform.ide.extension.samples.client.wizard.deployment.WizardDeploymentStepPresenter;
import org.exoplatform.ide.extension.samples.client.wizard.finish.WizardFinishStepPresenter;
import org.exoplatform.ide.extension.samples.client.wizard.source.WizardSourceStepPresenter;

/**
 * Samples extention for IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesExtension.java Sep 2, 2011 12:34:36 PM vereshchaka $
 *
 */
public class SamplesExtension extends Extension implements InitializeServicesHandler
{
   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   public static final SamplesLocalizationConstant LOCALIZATION_CONSTANT = GWT
      .create(SamplesLocalizationConstant.class);

   /**
    * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent)
    */
   @Override
   public void onInitializeServices(InitializeServicesEvent event)
   {
      new SamplesClientServiceImpl(eventBus, event.getApplicationConfiguration().getContext(), event.getLoader());
   }

   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      eventBus = IDE.EVENT_BUS;
      eventBus.addHandler(InitializeServicesEvent.TYPE, this);

      new StartPagePresenter(eventBus);
      new SelectLocationPresenter(eventBus);
      new ShowSamplesPresenter(eventBus);
      
      new WizardSourceStepPresenter(eventBus);
      new WizardDefinitionStepPresenter(eventBus);
      new WizardDeploymentStepPresenter(eventBus);
      new WizardFinishStepPresenter(eventBus);
      
      eventBus.fireEvent(new OpenStartPageEvent());
   }

}
