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
package org.exoplatform.ide.extension.python.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public interface PythonExtensionClientBundle extends ClientBundle {
    PythonExtensionClientBundle INSTANCE = GWT.<PythonExtensionClientBundle>create(PythonExtensionClientBundle.class);

    @Source("org/exoplatform/ide/extension/python/images/stopApp.png")
    ImageResource stopApp();

    @Source("org/exoplatform/ide/extension/python/images/stopApp_Disabled.png")
    ImageResource stopAppDisabled();

    @Source("org/exoplatform/ide/extension/python/images/runApp.png")
    ImageResource runApp();

    @Source("org/exoplatform/ide/extension/python/images/runApp_Disabled.png")
    ImageResource runAppDisabled();

    @Source("org/exoplatform/ide/extension/python/images/logs.png")
    ImageResource logs();

    @Source("org/exoplatform/ide/extension/python/images/logs_Disabled.png")
    ImageResource logsDisabled();
}
