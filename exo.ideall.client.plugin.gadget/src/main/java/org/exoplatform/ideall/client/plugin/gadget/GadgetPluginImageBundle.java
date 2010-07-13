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
package org.exoplatform.ideall.client.plugin.gadget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public interface GadgetPluginImageBundle extends ClientBundle
{

   public static final GadgetPluginImageBundle INSTANCE = GWT.create(GadgetPluginImageBundle.class);

   @Source("../public/images/plugin/gadget/bundled/deploy_gadget.png")
   ImageResource deployGadget();

   @Source("../public/images/plugin/gadgetbundled/deploy_gadget_Disabled.png")
   ImageResource deployGadgetDisabled();

   @Source("../public/images/plugin/gadgetbundled/undeploy_gadget.png")
   ImageResource undeployGadget();

   @Source("../public/images/plugin/gadgetbundled/undeploy_gadget_Disabled.png")
   ImageResource undeployGadgetDisabled();
}
