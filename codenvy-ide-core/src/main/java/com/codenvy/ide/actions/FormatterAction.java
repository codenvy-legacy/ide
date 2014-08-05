/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.texteditor.api.HandlesTextOperations;
import com.codenvy.ide.texteditor.api.HasHandlesOperationsView;
import com.codenvy.ide.texteditor.api.TextEditorOperations;
import com.google.inject.Inject;

/**
 * Formatter Action
 * 
 * @author Roman Nikitenko
 */

public class FormatterAction extends Action {

    private final ResourceProvider     resourceProvider;
    private final EditorAgent          editorAgent;
    private final AnalyticsEventLogger eventLogger;

    @Inject
    public FormatterAction(ResourceProvider resourceProvider, EditorAgent editorAgent, CoreLocalizationConstant localization,
                           AnalyticsEventLogger eventLogger, Resources resources) {
        super(localization.formatName(), localization.formatDescription(), null, resources.format());
        this.resourceProvider = resourceProvider;
        this.editorAgent = editorAgent;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Format file");
        final EditorPartPresenter editor = editorAgent.getActiveEditor();
        if (editor instanceof HasHandlesOperationsView) {
            final HandlesTextOperations handlesOperationsView = ((HasHandlesOperationsView)editor).getView();
            if (handlesOperationsView != null && handlesOperationsView.canDoOperation(TextEditorOperations.FORMAT)) {
                handlesOperationsView.doOperation(TextEditorOperations.FORMAT);
            }
        }
    }

    @Override
    public void update(ActionEvent e) {
        final EditorPartPresenter editor = editorAgent.getActiveEditor();
        boolean isCanDoOperation = false;

        if (editor instanceof HasHandlesOperationsView) {
            final HandlesTextOperations handlesOperationsView = ((HasHandlesOperationsView)editor).getView();
            if (handlesOperationsView != null) {
                isCanDoOperation = handlesOperationsView.canDoOperation(TextEditorOperations.FORMAT);
            }
        }
        e.getPresentation().setEnabled(isCanDoOperation);
        e.getPresentation().setVisible(resourceProvider.getActiveProject() != null);
    }
}
