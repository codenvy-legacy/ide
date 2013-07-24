/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.core;

import com.codenvy.ide.Resources;
import com.codenvy.ide.actions.NewFolderAction;
import com.codenvy.ide.actions.NewProjectAction;
import com.codenvy.ide.actions.NewResourceAction;
import com.codenvy.ide.actions.OpenProjectAction;
import com.codenvy.ide.actions.SaveAction;
import com.codenvy.ide.actions.SaveAllAction;
import com.codenvy.ide.actions.ShowPreferencesAction;
import com.codenvy.ide.actions.UpdateExtensionAction;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.KeyBuilder;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.toolbar.ToolbarPresenter;
import com.codenvy.ide.wizard.WizardAgentImpl;
import com.codenvy.ide.wizard.newfile.NewTextFilePagePresenter;
import com.codenvy.ide.wizard.newfolder.NewFolderPagePresenter;
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
    private PaaSAgent paasAgent;

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
    private ToolbarPresenter toolbarPresenter;

    /** Instantiates {@link StandardComponentInitializer} an creates standard content */
    @Inject
    public StandardComponentInitializer() {
    }

    public void initialize() {
        // TODO change icon
        wizard.registerNewResourceWizard("General", "Folder", resources.folder(), newFolderProvider);
        wizard.registerNewResourceWizard("General", "Text file", resources.file(), newTextFileProvider);


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

        actionManager.registerAction("updateExtension", updateExtensionAction);
        DefaultActionGroup runMenuActionGroup = (DefaultActionGroup)actionManager.getAction(IdeActions.GROUP_RUN_MAIN_MENU);
        runMenuActionGroup.add(updateExtensionAction);

        toolbarPresenter.bindMainGroup(toolbarGroup);
        paasAgent.registerPaaS("None", "None", null, JsonCollections.<String>createArray("", "java", "War"), null, null);
    }
}