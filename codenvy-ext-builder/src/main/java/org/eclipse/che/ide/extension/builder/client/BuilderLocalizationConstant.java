/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.extension.builder.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Localization constants. Interface to represent the constants contained in resource bundle:
 * 'BuilderLocalizationConstant.properties'.
 *
 * @author Artem Zatsarynnyy
 */
public interface BuilderLocalizationConstant extends Messages {
    /* Actions */
    @Key("control.buildProject.id")
    String buildProjectControlId();

    @Key("control.buildProject.text")
    String buildProjectControlTitle();

    @Key("control.buildProject.description")
    String buildProjectControlDescription();

    @Key("control.clearBuilderConsole.id")
    String clearConsoleControlId();

    @Key("control.clearBuilderConsole.text")
    String clearConsoleControlTitle();

    @Key("control.browseTargetFolder.text")
    String browseTargetFolderActionTitle();

    @Key("control.browseTargetFolder.description")
    String browseTargetFolderActionDescription();

    @Key("control.clearBuilderConsole.description")
    String clearConsoleControlDescription();

    /* Messages */
    @Key("messages.buildInProgress")
    String buildInProgress(String project);

    @Key("messages.buildFailed")
    String buildFailed();

    @Key("messages.buildCanceled")
    String buildCanceled(String project);

    @Key("messages.promptSaveFiles")
    String messagePromptSaveFiles();

    /* Titles */
    @Key("titles.promptSaveFiles")
    String titlePromptSaveFiles();

    /* BuildController */
    @Key("build.started")
    String buildStarted(String project);

    @Key("build.finished")
    String buildFinished(String project);

    @Key("build.artifact.not-ready")
    String artifactNotReady();

    /* BuilderConsoleView */
    @Key("view.builderConsole.title")
    String builderConsoleViewTitle();

    String fullBuildLogConsoleLink();
}
