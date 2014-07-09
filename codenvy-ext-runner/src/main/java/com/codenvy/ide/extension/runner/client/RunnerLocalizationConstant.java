/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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

    @Key("control.clearRunnerConsole.id")
    String clearConsoleControlId();

    @Key("control.clearRunnerConsole.text")
    String clearConsoleControlTitle();

    @Key("control.clearRunnerConsole.description")
    String clearConsoleControlDescription();

    @Key("control.viewRecipe.id")
    String viewRecipeActionId();

    @Key("control.viewRecipe.text")
    String viewRecipeText();

    @Key("control.viewRecipe.description")
    String viewRecipeDescription();

    /* Messages */
    @Key("appStarting")
    String applicationStarting(String name);

    @Key("appStarted")
    String applicationStarted(String name);

    @Key("startAppFailed")
    String startApplicationFailed(String name);

    @Key("getAppLogsFailed")
    String getApplicationLogsFailed();

    @Key("appStopped")
    String applicationStopped(String name);

    @Key("stopAppFailed")
    String stopApplicationFailed(String name);

    @Key("appFailed")
    String applicationFailed(String name);

    @Key("appCanceled")
    String applicationCanceled(String name);

    @Key("appUpdating")
    String applicationUpdating(String name);

    @Key("appUpdated")
    String applicationUpdated(String name);

    @Key("updateAppFailed")
    String updateApplicationFailed(String name);

    @Key("gettingEnvironmentsFailed")
    String gettingEnvironmentsFailed();

    @Key("messages.promptSaveFiles")
    String messagePromptSaveFiles();

    @Key("appWillBeStopped")
    String appWillBeStopped(String appName);

    @Key("enteredValueNotCorrect")
    String enteredValueNotCorrect();

    /* Titles */
    @Key("titles.promptSaveFiles")
    String titlePromptSaveFiles();

    @Key("titles.warning")
    String titlesWarning();

    /* RunConfigurationView */
    @Key("view.runConfiguration.title")
    String runConfigurationViewTitle();

    @Key("view.runConfiguration.environmentLabel")
    String runConfigurationViewEnvironmentLabel();

    @Key("view.runConfiguration.memorySizeLabel")
    String runConfigurationViewMemorySizeLabel();
}
