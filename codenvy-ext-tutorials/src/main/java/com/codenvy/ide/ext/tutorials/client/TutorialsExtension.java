/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
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
package com.codenvy.ide.ext.tutorials.client;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.template.TemplateAgent;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.api.ui.wizard.template.AbstractTemplatePage;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.tutorials.client.action.ShowTutorialGuideAction;
import com.codenvy.ide.ext.tutorials.client.template.CreateActionTutorialPage;
import com.codenvy.ide.ext.tutorials.client.template.CreateEditorTutorialPage;
import com.codenvy.ide.ext.tutorials.client.template.CreateGinTutorialPage;
import com.codenvy.ide.ext.tutorials.client.template.CreateNewProjectWizardTutorialPage;
import com.codenvy.ide.ext.tutorials.client.template.CreateNewResourceWizardTutorialPage;
import com.codenvy.ide.ext.tutorials.client.template.CreateNotificationTutorialPage;
import com.codenvy.ide.ext.tutorials.client.template.CreatePartsTutorialPage;
import com.codenvy.ide.ext.tutorials.client.template.CreateWizardTutorialPage;
import com.codenvy.ide.ext.tutorials.client.template.CreateWysiwygTutorialPage;
import com.codenvy.ide.resources.ProjectTypeAgent;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_WINDOW;
import static com.codenvy.ide.ext.java.client.projectmodel.JavaProject.PRIMARY_NATURE;

/**
 * Entry point for an extension that adds support to work with tutorial projects.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TutorialsExtension.java Sep 13, 2013 4:14:56 PM azatsarynnyy $
 */
@Singleton
@Extension(title = "Codenvy tutorial projects support.", version = "3.0.0")
public class TutorialsExtension {
    /** Default name of the tutorial project type. */
    public static final String TUTORIAL_PROJECT_TYPE           = "CodenvyTutorial";
    /** Default name of the file that contains tutorial description. */
    public static final String DEFAULT_README_FILE_NAME        = "guide.html";
    public static final String ACTION_TUTORIAL_ID              = "ActionTutorial";
    public static final String NOTIFICATION_TUTORIAL_ID        = "NotificationTutorial";
    public static final String WIZARD_TUTORIAL_ID              = "WizardTutorial";
    public static final String NEW_PROJECT_WIZARD_TUTORIAL_ID  = "NewProjectWizardTutorial";
    public static final String NEW_RESOURCE_WIZARD_TUTORIAL_ID = "NewResourceWizardTutorial";
    public static final String PARTS_TUTORIAL_ID               = "PartsTutorial";
    public static final String EDITOR_TUTORIAL_ID              = "EditorTutorial";
    public static final String GIN_TUTORIAL_ID                 = "GinTutorial";
    public static final String WYSIWIG_EDITOR_TUTORIAL_ID                 = "WysiwygEditorTutorial";

    @Inject
    public TutorialsExtension(TemplateAgent templateAgent,
                              Provider<CreateActionTutorialPage> createActionTutorialPage,
                              Provider<CreateNotificationTutorialPage> createNotificationTutorialPage,
                              Provider<CreateWizardTutorialPage> createWizardTutorialPageProvider,
                              Provider<CreateNewProjectWizardTutorialPage> createNewProjectWizardTutorialPageProvider,
                              Provider<CreateNewResourceWizardTutorialPage> createNewResourceWizardTutorialPageProvider,
                              Provider<CreatePartsTutorialPage> createPartsTutorialPageProvider,
                              Provider<CreateEditorTutorialPage> createEditorTutorialPageProvider,
                              Provider<CreateGinTutorialPage> createGinTutorialPageProvider,
                              Provider<CreateWysiwygTutorialPage> createWysiwygTutorialPageProvider,
                              ProjectTypeAgent projectTypeAgent,
                              TutorialsResources resources,
                              TutorialsLocalizationConstant localizationConstants,
                              ActionManager actionManager,
                              ShowTutorialGuideAction showAction) {
        resources.tutorialsCss().ensureInjected();

        // register actions
        DefaultActionGroup windowMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_WINDOW);

        actionManager.registerAction(localizationConstants.showTutorialGuideActionId(), showAction);
        windowMenuActionGroup.add(showAction);

        // register project type
        projectTypeAgent.register(TUTORIAL_PROJECT_TYPE,
                                  "Codenvy tutorial",
                                  resources.codenvyTutorialProject(),
                                  PRIMARY_NATURE,
                                  Collections.<String>createArray(TUTORIAL_PROJECT_TYPE));

        // register templates
        templateAgent.register(NOTIFICATION_TUTORIAL_ID,
                               "Notification API tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createNotificationTutorialPage));
        templateAgent.register(ACTION_TUTORIAL_ID,
                               "Action API tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createActionTutorialPage));
        templateAgent.register(WIZARD_TUTORIAL_ID,
                               "Wizard API tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createWizardTutorialPageProvider));
        templateAgent.register(NEW_PROJECT_WIZARD_TUTORIAL_ID,
                               "New project wizard tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(
                                       createNewProjectWizardTutorialPageProvider));
        templateAgent.register(NEW_RESOURCE_WIZARD_TUTORIAL_ID,
                               "New resource wizard tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(
                                       createNewResourceWizardTutorialPageProvider));
        templateAgent.register(PARTS_TUTORIAL_ID,
                               "Part API tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createPartsTutorialPageProvider));
        templateAgent.register(EDITOR_TUTORIAL_ID,
                               "Editor API tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createEditorTutorialPageProvider));
        templateAgent.register(GIN_TUTORIAL_ID,
                               "GIN (GWT Injection) tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createGinTutorialPageProvider));

        templateAgent.register(WYSIWIG_EDITOR_TUTORIAL_ID,
                               "WYSIWYG Editor tutorial.",
                               resources.codenvyTutorialTemplate(),
                               PRIMARY_NATURE,
                               Collections.createArray(TUTORIAL_PROJECT_TYPE),
                               Collections.<Provider<? extends AbstractTemplatePage>>createArray(createWysiwygTutorialPageProvider));
    }
}
