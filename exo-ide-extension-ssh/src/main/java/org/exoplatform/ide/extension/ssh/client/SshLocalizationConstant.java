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
package org.exoplatform.ide.extension.ssh.client;

import com.google.gwt.i18n.client.Constants;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public interface SshLocalizationConstant extends Constants {

    @DefaultStringValue("Cancel")
    @Key("cancelButton")
    String cancelButton();

    @DefaultStringValue("Close")
    @Key("closeButton")
    String closeButton();

    @DefaultStringValue("Upload")
    @Key("uploadButton")
    String uploadButton();

    @DefaultStringValue("Browse...")
    @Key("browseButton")
    String browseButton();

    @DefaultStringValue("Host")
    @Key("hostFieldTitle")
    String hostFieldTitle();

    @DefaultStringValue("File name")
    @Key("fileNameFieldTitle")
    String fileNameFieldTitle();

    @Key("host.validation.error")
    String hostValidationError();

    //
    @DefaultStringValue("Upload")
    @Key("key.manager.uploadButton")
    String managerUploadButton();

    @DefaultStringValue("Generate")
    @Key("key.manager.generateButton")
    String managerGenerateButton();


    @Key("key.manager.title")
    String sshManagerTitle();
}
