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

    @Key("control.editCustomEnvironments.id")
    String editCustomEnvironmentsActionId();

    @Key("control.editCustomEnvironments.text")
    String editCustomEnvironmentsActionText();

    @Key("control.editCustomEnvironments.description")
    String editCustomEnvironmentsActionDescription();

    @Key("control.getAppLogs.id")
    String getAppLogsActionId();

    @Key("control.getAppLogs.text")
    String getAppLogsActionText();

    @Key("control.getAppLogs.description")
    String getAppLogsActionDescription();

    @Key("control.shutdown.id")
    String shutdownActionId();

    @Key("control.shutdown.text")
    String shutdownActionText();

    @Key("control.shutdown.description")
    String shutdownActionDescription();

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

    @Key("control.runWith.title")
    String runWithActionTitle();

    @Key("control.runWith.description")
    String runWithActionDescription();

    @Key("control.environment.id")
    String environmentActionId(String name);

    @Key("control.environment.text")
    String environmentActionText(String name);

    @Key("control.environment.description")
    String environmentActionDescription(String name);

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

    @Key("messages.failedRememberOptions")
    String messagesFailedRememberOptions();

    @Key("messages.totalLessRequiredMemory")
    String messagesTotalLessRequiredMemory(int totalRAM, int requestedRAM);

    @Key("messages.totalLessOverrideMemory")
    String messagesTotalLessOverrideMemory(int overrideRAM, int totalRAM);

    @Key("messages.totalLessDefaultMemory")
    String messagesTotalLessDefaultMemory(int defaultRAM, int totalRAM);

    @Key("messages.totalLessCustomRunMemory")
    String messagesTotalLessCustomRunMemory(int customRunRAM, int totalRAM);

    @Key("messages.availableLessOverrideMemory")
    String messagesAvailableLessOverrideMemory(int overrideRAM, int totalRAM, int usedRAM);

    @Key("messages.availableLessDefaultMemory")
    String messagesAvailableLessDefaultMemory(int defaultRAM, int totalRAM, int usedRAM);

    @Key("messages.availableLessRequiredMemory")
    String messagesAvailableLessRequiredMemory(int totalRAM, int usedRAM, int requestedRAM);

    @Key("messages.overrideLessRequiredMemory")
    String messagesOverrideLessRequiredMemory(int overrideRAM, int requestedRAM);

    @Key("messages.overrideMemory")
    String messagesOverrideMemory();

    @Key("messages.defaultMemory")
    String messagesDefaultMemory(int requiredRam);

    @Key("appWillBeStopped")
    String appWillBeStopped(String appName);

    @Key("enteredValueNotCorrect")
    String enteredValueNotCorrect();

    @Key("runnerNotReady")
    String runnerNotReady();

    @Key("retrievingImagesFailed")
    String retrievingImagesFailed(String message);

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

    @Key("view.customRun.rememberRunMemoryLabel")
    String customRunViewRememberRunMemoryLabel();

    /* CustomEnvironmentsView */
    @Key("view.customEnvironments.title")
    String customEnvironmentsViewTitle();

    @Key("view.customEnvironments.addNewEnv.title")
    String customEnvironmentsViewAddNewEnvTitle();

    @Key("view.customEnvironments.addNewEnv.message")
    String customEnvironmentsViewAddNewEnvMessage();

    @Key("view.customEnvironments.removeEnv.title")
    String customEnvironmentsViewRemoveEnvTitle();

    @Key("view.customEnvironments.removeEnv.message")
    String customEnvironmentsViewRemoveEnvMessage(String name);

    @Key("view.selectRunnerPage.memoryRecommendedLabel")
    String selectRunnerPageViewMemoryRecommendedLabel();

    @Key("view.selectRunnerPage.environmentType.title")
    String selectRunnerPageEnvironmentTypeTitle();
    
    @Key("view.selectRunnerPage.environmentType.description")
    String selectRunnerPageEnvironmentTypeDescription();
    
    @Key("view.selectRunnerPage.environmentType.field")
    String selectRunnerPageEnvironmentTypeField();
    
    @Key("view.selectRunnerPage.environmentTarget.field")
    String selectRunnerPageEnvironmentTargetField();
    
    @Key("view.selectRunnerPage.environmentSubTechnology.field")
    String selectRunnerPageEnvironmentSubTechnologyField();
    
    @Key("view.selectRunnerPage.environment.title")
    String selectRunnerPageEnvironmentTitle();
    
    @Key("view.selectRunnerPage.environment.field")
    String selectRunnerPageEnvironmentField();

    @Key("view.selectRunnerPage.consoleTarget.field")
    String selectRunnerPageConsoleTargetField();
    
    @Key("view.selectRunnerPage.webappTarget.field")
    String selectRunnerPageWebappTargetField();
    
    @Key("view.selectRunnerPage.mobileTarget.field")
    String selectRunnerPageMobileTargetField();
    
    @Key("view.selectRunnerPage.standaloneTarget.field")
    String selectRunnerPageStandaloneTargetField();
}
