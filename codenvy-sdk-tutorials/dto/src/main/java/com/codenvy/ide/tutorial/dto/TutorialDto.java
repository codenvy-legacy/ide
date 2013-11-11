package com.codenvy.ide.tutorial.dto;

import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.action.DefaultActionGroup;
import com.codenvy.ide.tutorial.dto.actions.CreateGistAction;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** Extension used to demonstrate Codenvy DTO feature. */
@Singleton
@Extension(title = "DTO tutorial extension", version = "1.0.0")
public class TutorialDto {

    @Inject
    public TutorialDto(ActionManager actionManager, TutorialDtoLocalizationConstant localizationConstants,
                       CreateGistAction createGistAction) {
        // register a new action
        actionManager.registerAction(localizationConstants.createGistActionlId(), createGistAction);
        DefaultActionGroup saveActionGroup = (DefaultActionGroup)actionManager.getAction("saveGroup");
        saveActionGroup.add(createGistAction);
    }
}
