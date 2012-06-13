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
package org.exoplatform.ide.extension.googleappengine.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 16, 2012 5:43:05 PM anya $
 * 
 */
public interface GAEClientBundle extends ClientBundle
{
   GAEClientBundle INSTANCE = GWT.<GAEClientBundle> create(GAEClientBundle.class);

   @Source("org/exoplatform/ide/extension/googleappengine/images/gae.png")
   ImageResource googleAppEngine();
   
   @Source("org/exoplatform/ide/extension/googleappengine/images/gae_logo.png")
   ImageResource googleAppEngineLogo();

   @Source("org/exoplatform/ide/extension/googleappengine/images/gae_Disabled.png")
   ImageResource googleAppEngineDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/create_app.png")
   ImageResource createApplicationConrtol();

   @Source("org/exoplatform/ide/extension/googleappengine/images/create_app_Disabled.png")
   ImageResource createApplicationConrtolDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/general.png")
   ImageResource general();

   @Source("org/exoplatform/ide/extension/googleappengine/images/get_logs.png")
   ImageResource getLogs();

   @Source("org/exoplatform/ide/extension/googleappengine/images/get_logs_Disabled.png")
   ImageResource getLogsDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/logs.png")
   ImageResource logs();

   @Source("org/exoplatform/ide/extension/googleappengine/images/rollback_application.png")
   ImageResource rollbackApplication();

   @Source("org/exoplatform/ide/extension/googleappengine/images/rollback_application_Disabled.png")
   ImageResource rollbackApplicationDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_application.png")
   ImageResource updateApplication();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_application_Disabled.png")
   ImageResource updateApplicationDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/crons.png")
   ImageResource crons();

   @Source("org/exoplatform/ide/extension/googleappengine/images/backends.png")
   ImageResource backends();

   @Source("org/exoplatform/ide/extension/googleappengine/images/dos.png")
   ImageResource updateDos();

   @Source("org/exoplatform/ide/extension/googleappengine/images/dos_Disabled.png")
   ImageResource updateDosDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_indexes.png")
   ImageResource updateIndexes();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_indexes_Disabled.png")
   ImageResource updateIndexesDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/vacuum_indexes.png")
   ImageResource vacuumIndexes();

   @Source("org/exoplatform/ide/extension/googleappengine/images/vacuum_indexes_Disabled.png")
   ImageResource vacuumIndexesDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_pagespeed.png")
   ImageResource updatePagespeed();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_pagespeed_Disabled.png")
   ImageResource updatePagespeedDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_queues.png")
   ImageResource updateQueues();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_queues_Disabled.png")
   ImageResource updateQueuesDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update.png")
   ImageResource update();

   @Source("org/exoplatform/ide/extension/googleappengine/images/update_Disabled.png")
   ImageResource updateDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/start.png")
   ImageResource start();

   @Source("org/exoplatform/ide/extension/googleappengine/images/start_Disabled.png")
   ImageResource startDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/stop.png")
   ImageResource stop();

   @Source("org/exoplatform/ide/extension/googleappengine/images/stop_Disabled.png")
   ImageResource stopDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/remove.png")
   ImageResource remove();

   @Source("org/exoplatform/ide/extension/googleappengine/images/remove_Disabled.png")
   ImageResource removeDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/configure.png")
   ImageResource configure();

   @Source("org/exoplatform/ide/extension/googleappengine/images/configure_Disabled.png")
   ImageResource configureDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/rollback.png")
   ImageResource rollback();

   @Source("org/exoplatform/ide/extension/googleappengine/images/rollback_Disabled.png")
   ImageResource rollbackDisabled();

   @Source("org/exoplatform/ide/extension/googleappengine/images/rollback_all.png")
   ImageResource rollbackAll();

   @Source("org/exoplatform/ide/extension/googleappengine/images/rollback_all_Disabled.png")
   ImageResource rollbackAllDisabled();
   
   @Source("org/exoplatform/ide/extension/googleappengine/images/resource_limits.png")
   ImageResource resourceLimits();

   @Source("org/exoplatform/ide/extension/googleappengine/images/resource_limits_Disabled.png")
   ImageResource resourceLimitsDisabled();
}
