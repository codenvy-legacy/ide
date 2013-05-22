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
import com.codenvy.ide.actions.NewProjectAction;
import com.codenvy.ide.actions.SaveAction;
import com.codenvy.ide.api.expressions.ExpressionManager;
import com.codenvy.ide.api.paas.PaaSAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.Constraints;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.action.IdeActions;
import com.codenvy.ide.api.ui.keybinding.KeyBindingAgent;
import com.codenvy.ide.api.ui.keybinding.KeyBuilder;
import com.codenvy.ide.command.OpenProjectCommand;
import com.codenvy.ide.command.SaveAllCommand;
import com.codenvy.ide.command.SaveCommand;
import com.codenvy.ide.command.ShowNewFolderWizardCommand;
import com.codenvy.ide.command.ShowNewProjectWizardCommand;
import com.codenvy.ide.command.ShowNewResourceWizardCommand;
import com.codenvy.ide.command.ShowPreferenceCommand;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.menu.MainMenuPresenter;
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
    private MainMenuPresenter                  menu;
    @Inject
    private SaveCommand                        saveCommand;
    @Inject
    private SaveAllCommand                     saveAllCommand;
    @Inject
    private ShowNewResourceWizardCommand       newFileCommand;
    @Inject
    private ShowNewFolderWizardCommand         newFolderCommand;
    @Inject
    private ShowNewProjectWizardCommand        newProjectCommand;
    @Inject
    private WizardAgentImpl                    wizard;
    @Inject
    private Provider<NewFolderPagePresenter>   newFolderProvider;
    @Inject
    private Provider<NewTextFilePagePresenter> newTextFileProvider;
    @Inject
    private Resources                          resources;
    @Inject
    private KeyBindingAgent                    keyBinding;
    @Inject
    private ShowPreferenceCommand              showPreferencesCommand;
    @Inject
    private OpenProjectCommand                 openProjectCommand;
    @Inject
    private ExpressionManager                  expressionManager;
    @Inject
    private EventBus                           eventBus;
    @Inject
    private PaaSAgent                          paasAgent;
    @Inject
    private ActionManager                      actionManager;
    @Inject
    private NewProjectAction                   newProjectAction;
    @Inject
    private SaveAction                         saveAction;

    /** Instantiates {@link StandardComponentInitializer} an creates standard content */
    @Inject
    public StandardComponentInitializer() {
    }

    public void initialize() {
        // TODO change icon
        wizard.registerNewResourceWizard("General", "Folder", resources.folder(), newFolderProvider);
        wizard.registerNewResourceWizard("General", "Text file", resources.file(), newTextFileProvider);

        menu.addMenuItem("File/New/Project", newProjectCommand);
        menu.addMenuItem("File/New/Folder", newFolderCommand);
        menu.addMenuItem("File/New/Other", newFileCommand);

        menu.addMenuItem("File/Open Project", openProjectCommand);

        menu.addMenuItem("File/Save", saveCommand);
        menu.addMenuItem("File/Save All", saveAllCommand);

        menu.addMenuItem("Window/Preferences", showPreferencesCommand);

        keyBinding.getGlobal().addKeyBinding(new KeyBuilder().action().charCode('s').build(), saveCommand);
        keyBinding.getGlobal().addKeyBinding(new KeyBuilder().action().charCode('S').build(), saveAllCommand);

        actionManager.registerAction("newProject", newProjectAction);
        DefaultActionGroup toolbarGroup = new DefaultActionGroup(actionManager);
        toolbarGroup.addSeparator();
        actionManager.registerAction(IdeActions.GROUP_MAIN_TOOLBAR, toolbarGroup);

        DefaultActionGroup newGroup = new DefaultActionGroup("New", true, actionManager);
        newGroup.getTemplatePresentation().setIcon(resources.file());
        newGroup.addAction(newProjectAction, Constraints.FIRST);
        toolbarGroup.add(newGroup);
        toolbarGroup.addSeparator();

        DefaultActionGroup saveGroup = new DefaultActionGroup(actionManager);
        actionManager.registerAction("saveGroup", saveGroup);
        actionManager.registerAction("save", saveAction);
        saveGroup.add(saveAction);

        // add items to Toolbar
//        toolbar.addDropDownItem("General/New", resources.file(), "Create new resources");
//        toolbar.addItem("General/New/Project", newProjectCommand);
//        toolbar.addItem("General/New/File", newFileCommand);
//        toolbar.addItem("General/New/Folder", newFolderCommand);
//
//        toolbar.addItem("General/Save", saveCommand);
//        toolbar.addItem("General/Save All", saveAllCommand);

//        // TODO test toggle items
//        ToggleItemExpression toggleState = new ToggleItemExpression(expressionManager, true);
//        ToggleItemCommand command = new ToggleItemCommand(resources, eventBus, null, null, toggleState);
//        menu.addMenuItem("File/Checked item", command);
//        toolbar.addToggleItem("Test/Checked item", command);
//        toolbar.addDropDownItem("Test/New", resources.file(), "Test item");
//        toolbar.addToggleItem("Test/New/Checked item", command);

        paasAgent.registerPaaS("None", "None", null, JsonCollections.<String>createArray("", "java", "War"), null, null);
    }
}