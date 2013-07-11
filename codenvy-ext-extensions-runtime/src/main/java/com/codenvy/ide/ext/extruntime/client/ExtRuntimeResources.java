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
package com.codenvy.ide.ext.extruntime.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Client resources.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeResources.java Jul 3, 2013 12:37:19 PM azatsarynnyy $
 */
public interface ExtRuntimeResources extends ClientBundle {
    public interface ExtensionRuntimeCSS extends CssResource {
        String login();

        String loginFont();

        String loginErrorFont();

        String project();

        String labelH();

        String link();

        String textinput();

        String appInfo();

        String event();
    }

    @Source({"ExtensionRuntime.css", "com/codenvy/ide/api/ui/style.css"})
    ExtensionRuntimeCSS extensionRuntimeCss();

    @Source("com/codenvy/ide/ext/extruntime/images/controls/launchApp.png")
    ImageResource launchApp();

    @Source("com/codenvy/ide/ext/extruntime/images/controls/stopApp.png")
    ImageResource stopApp();

    @Source("com/codenvy/ide/ext/extruntime/images/codenvyExtensionProject.png")
    ImageResource codenvyExtensionProject();

    @Source("com/codenvy/ide/ext/extruntime/images/newCodenvyExtensionProject.png")
    ImageResource newCodenvyExtensionProject();
}
