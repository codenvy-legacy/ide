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
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.client.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.texteditor.HandlesTextOperations;
import com.codenvy.ide.api.texteditor.HasHandlesOperationsView;
import com.codenvy.ide.api.texteditor.TextEditorOperations;
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
    private       EditorPartPresenter  editor;

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
        } else if (editor instanceof HasHandlesOperationsView) {
            handlesOperations = ((HasHandlesOperationsView)editor).getView();
        }
        if (handlesOperations != null && handlesOperations.canDoOperation(TextEditorOperations.FORMAT)) {
            handlesOperations.doOperation(TextEditorOperations.FORMAT);
        }
    }

    @Override
    public void update(ActionEvent e) {
        final EditorPartPresenter editor = editorAgent.getActiveEditor();
        boolean isCanDoOperation = false;

        HandlesTextOperations handlesOperations = null;
        if (editor instanceof HandlesTextOperations) {
            handlesOperations = (HandlesTextOperations) editor;
        } else if (editor instanceof HasHandlesOperationsView) {
            handlesOperations = ((HasHandlesOperationsView)editor).getView();
        }

        if (handlesOperations != null) {
                isCanDoOperation = handlesOperations.canDoOperation(TextEditorOperations.FORMAT);
        }

        e.getPresentation().setEnabled(isCanDoOperation);
        e.getPresentation().setVisible(appContext.getCurrentProject() != null);
    }
}
