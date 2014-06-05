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