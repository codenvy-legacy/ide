/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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

    @Source("com/codenvy/ide/factory/images/buttons/facebook.png")
    ImageResource facebook();

    @Source("com/codenvy/ide/factory/images/buttons/gplus.png")
    ImageResource gplus();

    @Source("com/codenvy/ide/factory/images/buttons/twitter.png")
    ImageResource twitter();

    @Source("com/codenvy/ide/factory/images/buttons/mail.png")
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
