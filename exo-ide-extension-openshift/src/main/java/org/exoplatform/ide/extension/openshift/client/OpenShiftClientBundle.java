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
package org.exoplatform.ide.extension.openshift.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * OpenShift client resources (images).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  May 26, 2011 10:45:11 AM anya $
 *
 */
public interface OpenShiftClientBundle extends ClientBundle
{
   OpenShiftClientBundle INSTANCE = GWT.<OpenShiftClientBundle> create(OpenShiftClientBundle.class);

   @Source("org/exoplatform/ide/extension/openshift/images/buttons/ok.png")
   ImageResource okButton();

   @Source("org/exoplatform/ide/extension/openshift/images/buttons/ok_Disabled.png")
   ImageResource okButtonDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/buttons/properties.png")
   ImageResource propertiesButton();

   @Source("org/exoplatform/ide/extension/openshift/images/buttons/properties_Disabled.png")
   ImageResource propertiesButtonDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/buttons/cancel.png")
   ImageResource cancelButton();

   @Source("org/exoplatform/ide/extension/openshift/images/buttons/cancel_Disabled.png")
   ImageResource cancelButtonDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/createApp.png")
   ImageResource createApplicationControl();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/createApp_Disabled.png")
   ImageResource createApplicationControlDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/destroyApp.png")
   ImageResource destroyApplicationControl();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/destroyApp_Disabled.png")
   ImageResource destroyApplicationControlDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/appInfo.png")
   ImageResource applicationInfoControl();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/appInfo_Disabled.png")
   ImageResource applicationInfoControlDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/preview.png")
   ImageResource previewControl();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/preview_Disabled.png")
   ImageResource previewControlDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/userInfo.png")
   ImageResource userInfoControl();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/userInfo_Disabled.png")
   ImageResource userInfoControlDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/createDomain.png")
   ImageResource createDomainControl();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/createDomain_Disabled.png")
   ImageResource createDomainControlDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/updateKey.png")
   ImageResource updateKeyControl();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/updateKey_Disabled.png")
   ImageResource updateKeyControlDisabled();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/openshift.png")
   ImageResource openShiftControl();

   @Source("org/exoplatform/ide/extension/openshift/images/controls/openshift_Disabled.png")
   ImageResource openShiftControlDisabled();
}
