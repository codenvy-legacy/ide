package com.codenvy.ide.ext.helloworld.client;

/**
 * Codenvy API imports. In this extension we'll need
 * to talk to Parts and Action API. Gin and Singleton
 * imports are obligatory as well for any extension
 */
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.ext.helloworld.client.action.HelloWorldAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @Singleton is required in case the instance is triggered several times this extension will be initialized several times as well.
 * @Extension lets us know this is an extension and code injected in it will be executed when launched
 */
@Singleton
@Extension(title = "Hello world", version = "1.0.0")
public class HelloWorldExtension

/**
 * All menu items are actions. Here we register a new action in actionManager - HelloWorldID. Then, we get it in the DefaultActionGroup
 * which is general class for all items on the toolbar, context menus etc.
 */
{
    @Inject
    public HelloWorldExtension(ActionManager actionManager, HelloWorldAction action) {
        actionManager.registerAction("HelloWorldID", action);

        DefaultActionGroup contextMenu = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_TOOLBAR);

        /**
         * Finally, this action is added to toolbar
         */
        contextMenu.add(action, Constraints.LAST);

    }
}
