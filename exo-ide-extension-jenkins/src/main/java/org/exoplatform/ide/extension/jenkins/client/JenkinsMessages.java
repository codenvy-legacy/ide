/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Jenkins extension messages constants.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface JenkinsMessages extends Messages {

    @Key("control.build.javaApp.id")
    String buildJavaAppId();

    @Key("control.build.javaApp.title")
    String buildJavaAppTitle();

    @Key("control.build.javaApp.prompt")
    String buildJavaAppPrompt();

    @Key("view.build.javaApp.title")
    String buildJavaAppViewTitle();

    @Key("view.build.javaApp.button.cancel")
    String buildJavaAppButtonCancel();

    @Key("view.build.javaApp.button.build")
    String buildJavaAppButtonBuild();

    @Key("view.build.javaApp.git.url")
    String buildJavaAppGitRepositoryUrl();

    @Key("controller.no.remote.repository")
    String noRemoteRepository();

    @Key("controller.no.git.repository")
    String noGitRepository();

    @Key("controller.no.git.repository.title")
    String noGitRepositoryTitle();

    @Key("controller.start.build")
    String buildStarted(String projectName);

    @Key("control.status.start")
    String statusControlStart();

    @Key("controller.build.result.title")
    String buildResultTitle();

    @Key("controller.build.result.message")
    String buildResultMessage(String projectName, String buildResult);
}
