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
package com.codenvy.ide.extension.android.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AndroidExtensionClientBundle extends ClientBundle {
    AndroidExtensionClientBundle INSTANCE = GWT.<AndroidExtensionClientBundle>create(AndroidExtensionClientBundle.class);

    @Source("com/codenvy/ide/extension/android/images/runApp.png")
    ImageResource runApp();

    @Source("com/codenvy/ide/extension/android/images/runApp_Disabled.png")
    ImageResource runAppDisabled();

    @Source("com/codenvy/ide/extension/android/images/manymo48.png")
    ImageResource manymoPaas();

    @Source("com/codenvy/ide/extension/android/images/manymo48_Disabled.png")
    ImageResource manymoPaasDisabled();
}
