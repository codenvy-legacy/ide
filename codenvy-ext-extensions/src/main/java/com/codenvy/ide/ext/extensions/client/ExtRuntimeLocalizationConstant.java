/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.extensions.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants contained in resource bundle:
 * 'ExtRuntimeLocalizationConstant.properties'.
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

    @Key("control.getExtensionLogs.id")
    String getExtensionLogsActionId();

    @Key("control.getExtensionLogs.text")
    String getExtensionLogsActionText();

    @Key("control.getExtensionLogs.description")
    String getExtensionLogsActionDescription();

    @Key("control.stopExtension.id")
    String stopExtensionActionId();

    @Key("control.stopExtension.text")
    String stopExtensionActionText();

    @Key("control.stopExtension.description")
    String stopExtensionActionDescription();

    @Key("control.buildBundle.id")
    String buildBundleActionId();

    @Key("control.buildBundle.text")
    String buildBundleActionText();

    @Key("control.buildBundle.description")
    String buildBundleActionDescription();

    /* Messages */
    @Key("appBuilding")
    String applicationBuilding(String name);

    @Key("appBuilt")
    String applicationBuilt(String name);

    @Key("getBundle")
    String getBundle(String path);

    @Key("buildAppFailed")
    String buildApplicationFailed(String name);

    @Key("appStarting")
    String applicationStarting(String name);

    @Key("appStarted.uris")
    String applicationStartedOnUrls(String name, String uris);

    @Key("startAppFailed")
    String startApplicationFailed(String name);

    @Key("getAppLogsFailed")
    String getApplicationLogsFailed();

    @Key("appStopped")
    String applicationStopped(String name);

    @Key("stopAppFailed")
    String stopApplicationFailed(String name);
}
