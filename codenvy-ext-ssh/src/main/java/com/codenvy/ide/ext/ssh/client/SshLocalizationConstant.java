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
package com.codenvy.ide.ext.ssh.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface SshLocalizationConstant extends Messages {
    @Key("cancelButton")
    String cancelButton();

    @Key("closeButton")
    String closeButton();

    @Key("uploadButton")
    String uploadButton();

    @Key("browseButton")
    String browseButton();

    @Key("hostFieldTitle")
    String hostFieldTitle();

    @Key("fileNameFieldTitle")
    String fileNameFieldTitle();

    @Key("uploadSshKeyViewTitle")
    String uploadSshKeyViewTitle();

    @Key("host.validation.error")
    String hostValidationError();

    @Key("key.manager.uploadButton")
    String managerUploadButton();

    @Key("key.manager.generateButton")
    String managerGenerateButton();

    @Key("key.manager.title")
    String sshManagerTitle();

    @Key("public.sshkey.field")
    String publicSshKeyField();

    @Key("hostname.field")
    String hostNameField();

    @Key("delete.sshkey.question")
    String deleteSshKeyQuestion(String host);


    @Key("delete.sshkey.title")
    String deleteSshKeyTitle();


    @Key("delete.sshkey.failed")
    String deleteSshKeyFailed();

    @Key("get.sshkey.failed")
    String getSshKeyFailed();

    @Key("login.oauth.title")
    String loginOAuthTitle();

    @Key("login.oauth.label")
    String loginOAuthLabel();

    @Key("sshkeys.provider.not.found")
    String sshKeysProviderNotFound(String host);

    @Key("loader.deleteSshKey.message")
    String loaderDeleteSshKeyMessage(String host);

    @Key("loader.getSshKeys.message")
    String loaderGetSshKeysMessage();

    @Key("loader.getPublicSshKey.message")
    String loaderGetPublicSshKeyMessage(String host);
}