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
package com.codenvy.ide.extension.html.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlExtensionClientBundle.java Jun 26, 2013 11:11:11 AM azatsarynnyy $
 */
public interface HtmlExtensionClientBundle extends ClientBundle {
    HtmlExtensionClientBundle INSTANCE = GWT.<HtmlExtensionClientBundle> create(HtmlExtensionClientBundle.class);

    @Source("com/codenvy/ide/extension/html/images/stopApp.png")
    ImageResource stopApp();

    @Source("com/codenvy/ide/extension/html/images/stopApp_Disabled.png")
    ImageResource stopAppDisabled();

    @Source("com/codenvy/ide/extension/html/images/runApp.png")
    ImageResource runApp();

    @Source("com/codenvy/ide/extension/html/images/runApp_Disabled.png")
    ImageResource runAppDisabled();

    @Source("com/codenvy/ide/extension/html/images/logs.png")
    ImageResource logs();

    @Source("com/codenvy/ide/extension/html/images/logs_Disabled.png")
    ImageResource logsDisabled();
}
