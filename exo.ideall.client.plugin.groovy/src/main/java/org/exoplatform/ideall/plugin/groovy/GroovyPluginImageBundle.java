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
package org.exoplatform.ideall.plugin.groovy;

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
   
   @Source("../public/images/bundled/groovy/set_autoload.png")
   ImageResource setAutoLoad();

   @Source("../public/images/bundled/groovy/set_autoload_Disabled.png")
   ImageResource setAutoLoadDisabled();

   @Source("../public/images/bundled/groovy/unset_autoload.png")
   ImageResource unsetAutoLoad();

   @Source("../public/images/bundled/groovy/unset_autoload_Disabled.png")
   ImageResource unsetAutoLoadDisabled();

   @Source("../public/images/bundled/groovy/validate.png")
   ImageResource validateGroovy();

   @Source("../public/images/bundled/groovy/validate_Disabled.png")
   ImageResource validateGroovyDisabled();

   @Source("../public/images/bundled/groovy/deploy.png")
   ImageResource deployGroovy();

   @Source("../public/images/bundled/groovy/deploy_Disabled.png")
   ImageResource deployGroovyDisabled();

   @Source("../public/images/bundled/groovy/undeploy.png")
   ImageResource undeployGroovy();

   @Source("../public/images/bundled/groovy/undeploy_Disabled.png")
   ImageResource undeployGroovyDisabled();

   @Source("../public/images/bundled/groovy/output.png")
   ImageResource groovyOutput();

   @Source("../public/images/bundled/groovy/output_Disabled.png")
   ImageResource groovyOutputDisabled();
}
