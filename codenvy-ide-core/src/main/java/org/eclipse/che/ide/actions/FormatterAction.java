/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.actions;

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.texteditor.HandlesTextOperations;
import org.eclipse.che.ide.api.texteditor.TextEditorOperations;
import com.google.inject.Inject;

/**
 * Formatter Action
 *
 * @author Roman Nikitenko
 */
public class FormatterAction extends Action {

    private final AppContext           appContext;
    private final EditorAgent          editorAgent;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public FormatterAction(AppContext appContext, EditorAgent editorAgent, CoreLocalizationConstant localization,
                           AnalyticsEventLogger eventLogger, Resources resources) {
        super(localization.formatName(), localization.formatDescription(), null, resources.format());
        this.appContext = appContext;
        this.editorAgent = editorAgent;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);
        final EditorPartPresenter editor = editorAgent.getActiveEditor();
        HandlesTextOperations handlesOperations = null;
        if (editor instanceof HandlesTextOperations) {
            handlesOperations = (HandlesTextOperations) editor;
            if (handlesOperations.canDoOperation(TextEditorOperations.FORMAT)) {
                handlesOperations.doOperation(TextEditorOperations.FORMAT);
            }
        }
    }

    @Override
    public void update(ActionEvent e) {
        final EditorPartPresenter editor = editorAgent.getActiveEditor();
        boolean isCanDoOperation = false;

        HandlesTextOperations handlesOperations = null;
        if (editor instanceof HandlesTextOperations) {
            handlesOperations = (HandlesTextOperations) editor;
            isCanDoOperation = handlesOperations.canDoOperation(TextEditorOperations.FORMAT);
        }

        e.getPresentation().setEnabled(isCanDoOperation);
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }
}
