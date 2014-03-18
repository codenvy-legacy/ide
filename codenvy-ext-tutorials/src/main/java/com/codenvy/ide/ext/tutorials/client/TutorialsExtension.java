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
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.ext.tutorials.client.action.ShowTutorialGuideAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import static com.codenvy.ide.api.ui.action.IdeActions.GROUP_WINDOW;

/**
 * Entry point for an extension that adds support to work with tutorial projects.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
@Extension(title = "Codenvy tutorial projects", version = "3.0.0")
public class TutorialsExtension {
    /** Default name of the file that contains tutorial description. */
    public static final String DEFAULT_README_FILE_NAME = "guide.html";

    @Inject
    public TutorialsExtension(TutorialsResources resources,
                              TutorialsLocalizationConstant localizationConstants,
                              ActionManager actionManager,
                              ShowTutorialGuideAction showAction) {
        resources.tutorialsCss().ensureInjected();

        Map<String, String> icons = new HashMap<>(1);
        icons.put("codenvy_tutorial.projecttype.big.icon", "codenvy-tutorial/codenvy.jpg");

        // register actions
        DefaultActionGroup windowMenuActionGroup = (DefaultActionGroup)actionManager.getAction(GROUP_WINDOW);

        actionManager.registerAction(localizationConstants.showTutorialGuideActionId(), showAction);
        windowMenuActionGroup.add(showAction);
    }
}
