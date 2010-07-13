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
package org.exoplatform.ideall.client.plugin.groovy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public interface GroovyPluginImageBundle extends ClientBundle
{

   public static final GroovyPluginImageBundle INSTANCE = GWT.create(GroovyPluginImageBundle.class);
   
   @Source("/public/images/plugin/groovy/bundled/set_autoload.png")
   ImageResource setAutoLoad();

   @Source("/public/images/plugin/groovy/bundled/set_autoload_Disabled.png")
   ImageResource setAutoLoadDisabled();

   @Source("/public/images/plugin/groovy/bundled/unset_autoload.png")
   ImageResource unsetAutoLoad();

   @Source("/public/images/plugin/groovy/bundled/unset_autoload_Disabled.png")
   ImageResource unsetAutoLoadDisabled();

   @Source("/public/images/plugin/groovy/bundled/validate.png")
   ImageResource validateGroovy();

   @Source("/public/images/plugin/groovy/bundled/validate_Disabled.png")
   ImageResource validateGroovyDisabled();

   @Source("/public/images/plugin/groovy/bundled/deploy.png")
   ImageResource deployGroovy();

   @Source("/public/images/plugin/groovy/bundled/deploy_Disabled.png")
   ImageResource deployGroovyDisabled();

   @Source("/public/images/plugin/groovy/bundled/undeploy.png")
   ImageResource undeployGroovy();

   @Source("/public/images/plugin/groovy/bundled/undeploy_Disabled.png")
   ImageResource undeployGroovyDisabled();

   @Source("/public/images/plugin/groovy/bundled/output.png")
   ImageResource groovyOutput();

   @Source("/public/images/plugin/groovy/bundled/output_Disabled.png")
   ImageResource groovyOutputDisabled();
}
