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
package com.codenvy.ide.ext.github.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface GitHubLocalizationConstant extends Messages {
    // BUTTONS

    @Key("button.cancel")
    String buttonCancel();

    @Key("button.ok")
    String buttonOk();

    @Key("button.finish")
    String finishButton();

    // MESSAGES

    // Unmarshaller Errors
    @Key("merge.unmarshal.failed")
    String mergeUnmarshallerFailed();

    @Key("welcome.import.title")
    String welcomeImportTitle();

    @Key("welcome.import.text")
    String welcomeImportText();

    @Key("github.ssh.key.update.failed")
    String gitHubSshKeyUpdateFailed();

    /*
     * ImportFromGitHub
     */
    @Key("import.github.title")
    String importFromGithubTitle();

    @Key("import.github.account")
    String importFromGithubAccount();

    @Key("import.github.selectProjectType")
    String importFromGithubSelectProjectType();

    @Key("import.github.login")
    String importFromGithubLogin();

    @Key("import.github.login.failed")
    String importFromGithubLoginFailed();

    @Key("git.read.only")
    String gitReadOnly();

    @Key("user.not.found")
    String userNotFound();

    @Key("login.oauth.title")
    String loginOAuthTitle();

    @Key("login.oauth.label")
    String loginOAuthLabel();

    /*
     * Project
     */
    @Key("project.name")
    String projectName();

    /*
     * SamplesListGrid
     */
    @Key("samplesListGrid.column.name")
    String samplesListRepositoryColumn();

    @Key("samplesListGrid.column.description")
    String samplesListDescriptionColumn();

    @Key("samplesListGrid.column.type")
    String samplesListTypeColumn();

    @Key("github.sshkey.title")
    String githubSshKeyTitle();

    @Key("github.sshkey.label")
    String githubSshKeyLabel();
}