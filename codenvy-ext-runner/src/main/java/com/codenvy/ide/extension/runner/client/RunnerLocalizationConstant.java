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

    @Key("button.add")
    String buttonAdd();

    @Key("button.remove")
    String buttonRemove();

    @Key("button.close")
    String buttonClose();

    @Key("button.edit")
    String buttonEdit();

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

    @Key("control.editImages.id")
    String editImagesActionId();

    @Key("control.editImages.text")
    String editImagesActionText();

    @Key("control.editImages.description")
    String editImagesActionDescription();

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

    @Key("control.customImages.title")
    String customImagesActionTitle();

    @Key("control.image.id")
    String imageActionId(int id);

    @Key("control.image.text")
    String imageActionText(String id);

    @Key("control.image.description")
    String imageActionDescription(String id);

    /* Messages */
    @Key("another.project.running.now")
    String anotherProjectRunningNow();

    @Key("project.running.now")
    String projectRunningNow(String project);

    @Key("appStarting")
    String applicationStarting(String name);

    @Key("appStarted")
    String applicationStarted(String name);

    @Key("startAppFailed")
    String startApplicationFailed(String name);

    @Key("getResourcesFailed")
    String getResourcesFailed();

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

    @Key("gettingEnvironmentsFailed")
    String gettingEnvironmentsFailed();

    @Key("messages.promptSaveFiles")
    String messagePromptSaveFiles();

    @Key("messages.failedSaveFiles")
    String messageFailedSaveFiles();

    @Key("messages.totalLessRequiredMemory")
    String messagesTotalLessRequiredMemory(int totalRAM, int requestedRAM);

    @Key("messages.totalLessOverrideMemory")
    String messagesTotalLessOverrideMemory(int overrideRAM, int totalRAM);

    @Key("messages.totalLessCustomRunMemory")
    String messagesTotalLessCustomRunMemory(int customRunRAM, int totalRAM);

    @Key("messages.availableLessOverrideMemory")
    String messagesAvailableLessOverrideMemory(int overrideRAM, int totalRAM, int usedRAM);

    @Key("messages.availableLessRequiredMemory")
    String messagesAvailableLessRequiredMemory(int totalRAM, int usedRAM, int requestedRAM);

    @Key("messages.overrideLessRequiredMemory")
    String messagesOverrideLessRequiredMemory(int overrideRAM, int requestedRAM);

    @Key("messages.overrideMemory")
    String messagesOverrideMemory();

    @Key("appWillBeStopped")
    String appWillBeStopped(String appName);

    @Key("enteredValueNotCorrect")
    String enteredValueNotCorrect();

    @Key("runnerNotReady")
    String runnerNotReady();

    @Key("retrievingImagesFailed")
    String retrievingImagesFailed();

    /* Titles */
    @Key("titles.promptSaveFiles")
    String titlePromptSaveFiles();

    @Key("titles.warning")
    String titlesWarning();

    @Key("runner.pannel.starts-after-launch")
    String startsAfterLaunch();

    @Key("titles.ram-manager")
    @DefaultMessage("RAM Manager")
    String titlesRamManager();

    @Key("ram-manager.ram-size-must-multiple")
    @DefaultMessage("RAM size must be a positive value that is a multiple of {0} MB")
    String ramSizeMustBeMultipleOf(String multiple);

    /* RunConfigurationView */
    @Key("view.runConfiguration.title")
    String runConfigurationViewTitle();

    @Key("view.runConfiguration.environmentLabel")
    String runConfigurationViewEnvironmentLabel();

    @Key("view.runConfiguration.memoryRunnerLabel")
    String runConfigurationViewMemoryRunnerLabel();

    @Key("view.runConfiguration.memoryTotalLabel")
    String runConfigurationViewMemoryTotalLabel();

    @Key("view.runConfiguration.memoryAvailableLabel")
    String runConfigurationViewMemoryAvailableLabel();

    @Key("view.runConfiguration.skipBuildLabel")
    String runConfigurationViewSkipBuildLabel();

    /* EditImagesView */
    @Key("view.editImages.title")
    String editImagesViewTitle();
}
