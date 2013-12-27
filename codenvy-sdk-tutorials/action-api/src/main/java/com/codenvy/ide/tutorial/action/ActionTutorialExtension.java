package com.codenvy.ide.tutorial.action;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.tutorial.action.action.ChangeItemAction;
import com.codenvy.ide.tutorial.action.action.EnableAction;
import com.codenvy.ide.tutorial.action.action.VisibleAction;
import com.codenvy.ide.tutorial.action.part.TutorialHowToPresenter;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_CONTEXT_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_MENU;
import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_MAIN_TOOLBAR;
import static com.codenvy.ide.api.ui.workspace.PartStackType.EDITING;

/** Extension used to demonstrate the Action feature. */
@Singleton
@Extension(title = "Action tutorial", version = "1.0.0")
public class ActionTutorialExtension {
    public static boolean SHOW_ITEM = true;

    @Inject
    public ActionTutorialExtension(ActionManager actionManager, ActionTutorialResources resources, ChangeItemAction changeItemAction,
                                   VisibleAction visibleAction, EnableAction enableAction, WorkspaceAgent workspaceAgent,
                                   TutorialHowToPresenter howToPresenter) {
        workspaceAgent.openPart(howToPresenter, EDITING);

        // Get main groups of Main menu, Toolbar and Context menu
        DefaultActionGroup mainMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_MENU);
        DefaultActionGroup toolbar = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_TOOLBAR);
        DefaultActionGroup contextMenu = (DefaultActionGroup)actionManager.getAction(GROUP_MAIN_CONTEXT_MENU);

        // Create main group
        DefaultActionGroup actionGroup = new DefaultActionGroup("Actions", false, actionManager);
        actionManager.registerAction("ActionsGroup", actionGroup);

        // Create drop down group
        DefaultActionGroup popupGroup = new DefaultActionGroup("Drop down group", true, actionManager);
        popupGroup.getTemplatePresentation().setIcon(resources.item());
        actionManager.registerAction("DropDownGroup", popupGroup);
        actionGroup.add(popupGroup);

        // Add separator
        actionGroup.addSeparator();

        // Create general(not drop down) group
        DefaultActionGroup notPopupGroup = new DefaultActionGroup("General group", false, actionManager);
        actionManager.registerAction("GeneralGroup", notPopupGroup);
        actionGroup.add(notPopupGroup);

        popupGroup.add(changeItemAction);
        popupGroup.add(visibleAction);
        popupGroup.add(enableAction);

        notPopupGroup.add(changeItemAction);
        notPopupGroup.add(visibleAction);
        notPopupGroup.add(enableAction);

        // Add actions in MainMenu, Toolbar and Context menu
        mainMenu.add(actionGroup);
        toolbar.add(actionGroup);
        contextMenu.add(actionGroup);
    }
}