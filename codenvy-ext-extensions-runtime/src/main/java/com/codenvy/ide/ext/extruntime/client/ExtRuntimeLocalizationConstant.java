/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants contained in resource bundle: 'ExtRuntimeLocalizationConstant.properties'.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeLocalizationConstant.java Jul 3, 2013 12:40:17 PM azatsarynnyy $
 */
public interface ExtRuntimeLocalizationConstant extends Messages {
    /* Actions */
    @Key("control.launchExtension.id")
    String launchExtensionActionlId();

    @Key("control.launchExtension.text")
    String launchExtensionActionText();

    @Key("control.launchExtension.description")
    String launchExtensionActionDescription();

    @Key("control.stopExtension.id")
    String stopExtensionActionlId();

    @Key("control.stopExtension.text")
    String stopExtensionActionText();

    @Key("control.stopExtension.description")
    String stopExtensionActionDescription();

    /* Messages */
    @Key("appStarting")
    String applicationStarting();

    @Key("appStarted.uris")
    String applicationStartedOnUrls(String name, String uris);

    @Key("startAppFailed")
    String startApplicationFailed();

    @Key("appStopped")
    String applicationStopped(String name);

    @Key("stopAppFailed")
    String stopApplicationFailed();
}
