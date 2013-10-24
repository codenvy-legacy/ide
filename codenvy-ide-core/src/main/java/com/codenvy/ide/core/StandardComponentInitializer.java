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
package com.codenvy.ide.core;

import com.codenvy.ide.Resources;
import com.codenvy.ide.actions.*;
import com.codenvy.ide.actions.CloseProjectAction;
import com.codenvy.ide.actions.DeleteResourceAction;
import com.codenvy.ide.actions.NewFolderAction;
import com.codenvy.ide.actions.NewProjectAction;
import com.codenvy.ide.actions.NewResourceAction;
import com.codenvy.ide.actions.OpenProjectAction;
import com.codenvy.ide.actions.SaveAction;
import com.codenvy.ide.actions.SaveAllAction;
import com.codenvy.ide.actions.ShowPreferencesAction;
import com.codenvy.ide.actions.UpdateExtensionAction;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.parts.WelcomePart;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.KeyBuilder;
import com.codenvy.ide.api.ui.wizard.newproject.NewProjectWizard;
import com.codenvy.ide.api.ui.preferences.PreferencesAgent;
import com.codenvy.ide.extension.ExtensionManagerPresenter;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.toolbar.MainToolbar;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.welcome.WelcomeLocalizationConstant;
import com.codenvy.ide.welcome.action.ConnectSupportAction;
import com.codenvy.ide.welcome.action.CreateProjectAction;
import com.codenvy.ide.welcome.action.InviteAction;
import com.codenvy.ide.welcome.action.ShowDocumentationAction;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.newfile.NewTextFilePagePresenter;
import com.codenvy.ide.wizard.newfolder.NewFolderPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.start.NewProjectPagePresenter;
import com.codenvy.ide.wizard.newproject.pages.template.ChooseTemplatePagePresenter;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Initializer for standard component i.e. some basic menu commands (Save, Save As etc)
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class StandardComponentInitializer {

    @Inject
    private WizardAgentImpl wizard;

    @Inject
    private Provider<NewFolderPagePresenter> newFolderProvider;

    @Inject
    private Provider<NewTextFilePagePresenter> newTextFileProvider;

    @Inject
    private Resources resources;

    @Inject
    private KeyBindingAgent keyBinding;

    @Inject
    private EventBus eventBus;

    @Inject
    private ActionManager actionManager;

    @Inject
    private NewProjectAction newProjectAction;

    @Inject
    private SaveAction saveAction;

    @Inject
    private SaveAllAction saveAllAction;

    @Inject
    private NewFolderAction newFolderAction;

    @Inject
    private NewResourceAction newFileAction;

    @Inject
    private OpenProjectAction openProjectAction;

    @Inject
    private ShowPreferencesAction showPreferencesAction;

    @Inject
    private UpdateExtensionAction updateExtensionAction;

    @Inject
    @MainToolbar
    private ToolbarPresenter toolbarPresenter;

    @Inject
    private WelcomePart welcomePart;

    @Inject
    private WelcomeLocalizationConstant welcomeConstant;

    @Inject
    private CreateProjectAction createProjectAction;

    @Inject
    private ShowDocumentationAction showDocumentationAction;

    @Inject
    private InviteAction inviteAction;

    @Inject
    private ConnectSupportAction connectSupportAction;

    @Inject
    private DeleteResourceAction deleteResourceAction;

    @Inject
    private CloseProjectAction closeProjectAction;

    @Inject
    private NewProjectWizard newProjectWizard;

    @Inject
    private Provider<NewProjectPagePresenter> newProjectPageProvider;

    @Inject
    private Provider<ChooseTemplatePagePresenter> chooseTemplatePageProvider;

    @Inject
    private PreferencesAgent preferencesAgent;

    @Inject
    private ExtensionManagerPresenter extensionManagerPresenter;

    /** Instantiates {@link StandardComponentInitializer} an creates standard content */
    @Inject
    public StandardComponentInitializer() {
    }

    public void initialize() {
        // TODO change icon
        wizard.registerNewResourceWizard("General", "Folder", resources.folder(), newFolderProvider);
        wizard.registerNewResourceWizard("General", "Text file", resources.file(), newTextFileProvider);

        preferencesAgent.addPage(extensionManagerPresenter);

        DefaultActionGroup window = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_WINDOW);
        actionManager.registerAction("showPreferences", showPreferencesAction);
        window.add(showPreferencesAction);
        DefaultActionGroup fileGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_FILE);

        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('s').build(), "save");
        keyBinding.getGlobal().addKey(new KeyBuilder().action().charCode('S').build(), "saveAll");

        actionManager.registerAction("newProject", newProjectAction);
        actionManager.registerAction("openProject", openProjectAction);
        DefaultActionGroup toolbarGroup = new DefaultActionGroup(actionManager);
        toolbarGroup.addSeparator();
        actionManager.registerAction(IdeActions.GROUP_MAIN_TOOLBAR, toolbarGroup);

        DefaultActionGroup newGroup = new DefaultActionGroup("New", true, actionManager);
        newGroup.getTemplatePresentation().setIcon(resources.file());
        newGroup.addAction(newProjectAction, Constraints.FIRST);
        toolbarGroup.add(newGroup);
        toolbarGroup.addSeparator();
        fileGroup.add(newGroup);
        fileGroup.add(openProjectAction);
        actionManager.registerAction("newFolder", newFolderAction);
        actionManager.registerAction("newResource", newFileAction);
        newGroup.add(newFileAction);
        newGroup.add(newFolderAction);

        DefaultActionGroup saveGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction("saveGroup", saveGroup);
        actionManager.registerAction("save", saveAction);
        actionManager.registerAction("saveAll", saveAllAction);
        saveGroup.addSeparator();
        saveGroup.add(saveAction);
        saveGroup.add(saveAllAction);
        toolbarGroup.addSeparator();
        toolbarGroup.add(saveGroup);
        toolbarGroup.addSeparator();
        fileGroup.add(saveGroup);

        DefaultActionGroup changeResourceGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction("changeResourceGroup", changeResourceGroup);
        actionManager.registerAction("closeProject", closeProjectAction);
        actionManager.registerAction("deleteItem", deleteResourceAction);
        changeResourceGroup.add(closeProjectAction);
        changeResourceGroup.add(deleteResourceAction);
        changeResourceGroup.addSeparator();
        toolbarGroup.add(changeResourceGroup);

        actionManager.registerAction("updateExtension", updateExtensionAction);
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_RUN_MAIN_MENU);
        runMenuActionGroup.add(updateExtensionAction);

        toolbarPresenter.bindMainGroup(toolbarGroup);

        welcomePart.addItem(createProjectAction);
        welcomePart.addItem(showDocumentationAction);
        welcomePart.addItem(inviteAction);
        welcomePart.addItem(connectSupportAction);

        DefaultActionGroup contextMenuGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_MAIN_CONTEXT_MENU);
        contextMenuGroup.add(newGroup);
        contextMenuGroup.addSeparator();

        DefaultActionGroup resourceOperation = new DefaultActionGroup(actionManager);
        resourceOperation.addSeparator();
        actionManager.registerAction("resourceOperation", resourceOperation);
        resourceOperation.add(deleteResourceAction);
        contextMenuGroup.add(resourceOperation);

        DefaultActionGroup closeProjectGroup = new DefaultActionGroup(actionManager);
        closeProjectGroup.addSeparator();
        actionManager.registerAction("closeProjectGroup", closeProjectGroup);
        closeProjectGroup.add(closeProjectAction);
        contextMenuGroup.add(closeProjectGroup);

        newProjectWizard.addPage(newProjectPageProvider);
        newProjectWizard.addPage(chooseTemplatePageProvider);
    }
}