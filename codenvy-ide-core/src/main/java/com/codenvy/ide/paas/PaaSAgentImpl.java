/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.paas;

import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;


/**
 * The implementation of {@link PaaSAgent}.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class PaaSAgentImpl implements PaaSAgent
{
   private final JsonArray<PaaS> registeredPaaS;

   /**
    * Create agent.
    */
   @Inject
   public PaaSAgentImpl()
   {
      this.registeredPaaS = JsonCollections.createArray();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void registerPaaS(String id, String title, ImageResource image, boolean providesTemplate,
      JsonArray<String> requiredTypes, AbstractPaasWizardPagePresenter wizardPage,
      PreferencesPagePresenter preferencePage)
   {
      PaaS paas = new PaaS(id, title, image, providesTemplate, requiredTypes, wizardPage);
      registeredPaaS.add(paas);

      // TODO preference page
   }

   /**
    * Returns all available PaaSes.
    * 
    * @return
    */
   public JsonArray<PaaS> getPaaSes()
   {
      return registeredPaaS;
   }
}