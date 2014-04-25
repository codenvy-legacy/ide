package com.codenvy.ide.actions;

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.ui.action.Action;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.texteditor.api.TextEditorOperations;
import com.google.inject.Inject;

/**
 * Formatter Action
 *
 * @author Roman Nikitenko
 */

public class FormatterAction extends Action {

    private final EditorAgent          editorAgent;
    private final AnalyticsEventLogger eventLogger;

    private EditorPartPresenter editor;


    @Inject
    public FormatterAction(EditorAgent editorAgent, CoreLocalizationConstant localization,
                           AnalyticsEventLogger eventLogger) {
        super(localization.formatName(), localization.formatDescription(), null);
        this.editorAgent = editorAgent;
        this.eventLogger = eventLogger;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Format file");
        editor = editorAgent.getActiveEditor();
        if (editor instanceof CodenvyTextEditor) {
            ((CodenvyTextEditor)editor).getView().doOperation(TextEditorOperations.FORMAT);
        }
    }

    @Override
    public void update(ActionEvent e) {
        editor = editorAgent.getActiveEditor();
        boolean isCanDoOperation = false;

        if (editor instanceof CodenvyTextEditor) {
            isCanDoOperation = ((CodenvyTextEditor)editor).getView().canDoOperation(TextEditorOperations.FORMAT);
        }
        e.getPresentation().setEnabled(isCanDoOperation);
    }
}
