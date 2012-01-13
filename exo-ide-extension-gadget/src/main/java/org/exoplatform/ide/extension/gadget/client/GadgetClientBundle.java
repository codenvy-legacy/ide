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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public interface GadgetClientBundle extends ClientBundle
{

   public static final GadgetClientBundle INSTANCE = GWT.create(GadgetClientBundle.class);

   @Source("bundled/deploy_gadget.png")
   ImageResource deployGadget();

   @Source("bundled/deploy_gadget_Disabled.png")
   ImageResource deployGadgetDisabled();

   @Source("bundled/undeploy_gadget.png")
   ImageResource undeployGadget();

   @Source("bundled/undeploy_gadget_Disabled.png")
   ImageResource undeployGadgetDisabled();

   @Source("bundled/preview.png")
   ImageResource preview();

   @Source("bundled/preview_Disabled.png")
   ImageResource previewDisabled();

}
