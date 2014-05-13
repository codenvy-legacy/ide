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
        acceptFactoryHandler.processFactory();
        
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
