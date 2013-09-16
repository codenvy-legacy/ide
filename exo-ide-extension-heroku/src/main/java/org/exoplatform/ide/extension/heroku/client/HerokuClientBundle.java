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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Heroku client resources (images).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 10:45:11 AM anya $
 */
public interface HerokuClientBundle extends ClientBundle {
    HerokuClientBundle INSTANCE = GWT.<HerokuClientBundle>create(HerokuClientBundle.class);

    @Source("org/exoplatform/ide/extension/heroku/images/controls/heroku_32.png")
    ImageResource herokuLogo();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/ok.png")
    ImageResource okButton();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/ok_Disabled.png")
    ImageResource okButtonDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/help.png")
    ImageResource helpButton();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/help_Disabled.png")
    ImageResource helpButtonDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/run.png")
    ImageResource runButton();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/run_Disabled.png")
    ImageResource runButtonDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/cancel.png")
    ImageResource cancelButton();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/cancel_Disabled.png")
    ImageResource cancelButtonDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/properties.png")
    ImageResource propertiesButton();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/properties_Disabled.png")
    ImageResource propertiesButtonDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/edit.png")
    ImageResource editButton();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/edit_Disabled.png")
    ImageResource editButtonDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/heroku.png")
    ImageResource heroku();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/heroku_Disabled.png")
    ImageResource herokuDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/heroku_48.png")
    ImageResource heroku48();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/heroku_48_Disabled.png")
    ImageResource heroku48Disabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/apps-list.png")
    ImageResource applicationsList();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/apps-list_Disabled.png")
    ImageResource applicationsListDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/addKeys.png")
    ImageResource addKeys();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/addKeys_Disabled.png")
    ImageResource addKeysDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/clearKeys.png")
    ImageResource clearKeys();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/clearKeys_Disabled.png")
    ImageResource clearKeysDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/changeStack.png")
    ImageResource changeStack();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/changeStack_Disabled.png")
    ImageResource changeStackDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/createApp.png")
    ImageResource createApplication();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/createApp_Disabled.png")
    ImageResource createApplicationDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/destroyApp.png")
    ImageResource destroyApplication();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/destroyApp_Disabled.png")
    ImageResource destroyApplicationDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/rake.png")
    ImageResource rake();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/rake_Disabled.png")
    ImageResource rakeDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/renameApp.png")
    ImageResource renameApplication();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/renameApp_Disabled.png")
    ImageResource renameApplicationDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/appInfo.png")
    ImageResource applicationInfo();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/appInfo_Disabled.png")
    ImageResource applicationInfoDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/switchAccount.png")
    ImageResource switchAccount();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/switchAccount_Disabled.png")
    ImageResource switchAccountDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/getLogs.png")
    ImageResource getLogs();

    @Source("org/exoplatform/ide/extension/heroku/images/buttons/getLogs_Disabled.png")
    ImageResource getLogsDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/logs.png")
    ImageResource logs();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/logs_Disabled.png")
    ImageResource logsDisabled();
}
