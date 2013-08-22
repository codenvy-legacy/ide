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
package com.codenvy.ide.factory.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Codenvy Factory client resources.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: FactoryClientBundle.java Jun 11, 2013 2:50:47 PM azatsarynnyy $
 */
public interface FactoryClientBundle extends ClientBundle {
    FactoryClientBundle INSTANCE = GWT.<FactoryClientBundle> create(FactoryClientBundle.class);

    @Source("com/codenvy/ide/factory/images/buttons/ok.png")
    ImageResource ok();

    @Source("com/codenvy/ide/factory/images/buttons/ok_Disabled.png")
    ImageResource okDisabled();

    @Source("com/codenvy/ide/factory/images/buttons/cancel.png")
    ImageResource cancel();

    @Source("com/codenvy/ide/factory/images/buttons/cancel_Disabled.png")
    ImageResource cancelDisabled();

    @Source("com/codenvy/ide/factory/images/controls/share.png")
    ImageResource share();

    @Source("com/codenvy/ide/factory/images/controls/share_Disabled.png")
    ImageResource shareDisabled();

    @Source("com/codenvy/ide/factory/images/GitHub-Mark-32px.png")
    ImageResource gitHub();

    @Source("com/codenvy/ide/factory/images/link.png")
    ImageResource link();

    @Source("com/codenvy/ide/factory/images/world.png")
    ImageResource world();

    //@Source("com/codenvy/ide/factory/images/buttons/facebook.png")
    @Source("com/codenvy/ide/factory/images/btn-facebook.png")
    ImageResource facebook();

    //@Source("com/codenvy/ide/factory/images/buttons/gplus.png")
    @Source("com/codenvy/ide/factory/images/btn-gplus.png")
    ImageResource gplus();

    //@Source("com/codenvy/ide/factory/images/buttons/twitter.png")
    @Source("com/codenvy/ide/factory/images/btn-twitter.png")
    ImageResource twitter();

    //@Source("com/codenvy/ide/factory/images/buttons/mail.png")
    @Source("com/codenvy/ide/factory/images/btn-mail.png")
    ImageResource mail();
    
    @Source("com/codenvy/ide/factory/images/create-account.png")
    ImageResource createAccount();
    
    @Source("com/codenvy/ide/factory/images/create-account-hover.png")
    ImageResource createAccountHover();
    
    @Source("com/codenvy/ide/factory/images/login.png")
    ImageResource login();
    
    @Source("com/codenvy/ide/factory/images/login-hover.png")
    ImageResource loginHover();

    @Source("com/codenvy/ide/factory/images/copy-to-my-workspace.png")
    ImageResource copyToMyWorkspace();
    
    @Source("com/codenvy/ide/factory/images/copy-to-my-workspace-hover.png")
    ImageResource copyToMyWorkspaceHover();
    
}
