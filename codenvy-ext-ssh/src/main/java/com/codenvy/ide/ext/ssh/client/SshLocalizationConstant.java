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
    
}