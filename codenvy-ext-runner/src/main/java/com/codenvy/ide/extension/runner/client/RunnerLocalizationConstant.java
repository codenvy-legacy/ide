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
package com.codenvy.ide.extension.runner.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants contained in resource bundle:
 * 'RunnerLocalizationConstant.properties'.
 *
 * @author Artem Zatsarynnyy
 */
public interface RunnerLocalizationConstant extends Messages {
    /* Buttons */
    @Key("button.run")
    String buttonRun();

    @Key("button.cancel")
    String buttonCancel();

    /* Actions */
    @Key("control.runApp.id")
    String runAppActionId();

    @Key("control.runApp.text")
    String runAppActionText();

    @Key("control.runApp.description")
    String runAppActionDescription();

    @Key("control.customRunApp.id")
    String customRunAppActionId();

    @Key("control.customRunApp.text")
    String customRunAppActionText();

    @Key("control.customRunApp.description")
    String customRunAppActionDescription();

    @Key("control.getAppLogs.id")
    String getAppLogsActionId();

    @Key("control.getAppLogs.text")
    String getAppLogsActionText();

    @Key("control.getAppLogs.description")
    String getAppLogsActionDescription();

    @Key("control.stopApp.id")
    String stopAppActionId();

    @Key("control.stopApp.text")
    String stopAppActionText();

    @Key("control.stopApp.description")
    String stopAppActionDescription();

    @Key("control.updateExtension.id")
    String updateExtensionActionId();

    @Key("control.updateExtension.text")
    String updateExtensionText();

    @Key("control.updateExtension.description")
    String updateExtensionDescription();

    /* Messages */
    @Key("appStarting")
    String applicationStarting(String name);

    @Key("appStarted.url")
    String applicationStartedOnUrl(String name, String uris);

    @Key("startAppFailed")
    String startApplicationFailed(String name);

    @Key("getAppLogsFailed")
    String getApplicationLogsFailed();

    @Key("appStopped")
    String applicationStopped(String name);

    @Key("stopAppFailed")
    String stopApplicationFailed(String name);

    @Key("appUpdating")
    String applicationUpdating(String name);

    @Key("appUpdated")
    String applicationUpdated(String name);

    @Key("updateAppFailed")
    String updateApplicationFailed(String name);

    /* RunConfigurationView */
    @Key("view.runConfiguration.title")
    String runConfigurationViewTitle();
}
