/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Anchor;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.factory.client.accept.AcceptFactoryHandler;
import com.codenvy.ide.factory.client.action.ShareFactoryAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_WINDOW;

/**
 * @author Vladyslav Zhukovskii
 */
@Singleton
@Extension(title = "Factory", version = "3.0.0")
public class FactoryExtension {
    
    public static final String SHARE_GROUP_MAIN_MENU        = "Share";
    
    @Inject
    public FactoryExtension(AcceptFactoryHandler acceptFactoryHandler, ActionManager actionManager, ShareFactoryAction shareFactoryAction, FactoryResources resources) {
        //Entry point to start up factory acceptance
        acceptFactoryHandler.process();
        
        resources.factoryCSS().ensureInjected();
        
        DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);

        DefaultActionGroup share = new DefaultActionGroup(SHARE_GROUP_MAIN_MENU, true, actionManager);
        actionManager.registerAction(SHARE_GROUP_MAIN_MENU, share);
        Constraints afterWindow = new Constraints(Anchor.AFTER, GROUP_WINDOW);
        mainMenu.add(share, afterWindow);

        actionManager.registerAction("ShareFactory", shareFactoryAction);
        share.add(shareFactoryAction);
    }
}
