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
package org.exoplatform.ide.extension.chromattic.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * CloudFoundry client resources (images).
 * 
 * @author <a href="dnochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id:
 * 
 */
public interface ChromatticClientBundle extends ClientBundle
{
   ChromatticClientBundle INSTANCE = GWT.<ChromatticClientBundle> create(ChromatticClientBundle.class);

   /*
    * Buttons
    */
   @Source("org/exoplatform/ide/extension/chromattic/images/buttons/ok.png")
   ImageResource okButton();

   @Source("org/exoplatform/ide/extension/chromattic/images/buttons/ok_Disabled.png")
   ImageResource okButtonDisabled();

   @Source("org/exoplatform/ide/extension/chromattic/images/buttons/cancel.png")
   ImageResource cancelButton();

   @Source("org/exoplatform/ide/extension/chromattic/images/buttons/cancel_Disabled.png")
   ImageResource cancelButtonDisabled();

   /*
    * Controls
    */
   @Source("org/exoplatform/ide/extension/chromattic/images/controls/preview-node-type.png")
   ImageResource previewNodeTypeControl();

   @Source("org/exoplatform/ide/extension/chromattic/images/controls/preview-node-type_Disabled.png")
   ImageResource previewNodeTypeControlDisabled();

   @Source("org/exoplatform/ide/extension/chromattic/images/controls/deploy-node-type.png")
   ImageResource deployNodeTypeControl();

   @Source("org/exoplatform/ide/extension/chromattic/images/controls/deploy-node-type_Disabled.png")
   ImageResource deployNodeTypeControlDisabled();

}
