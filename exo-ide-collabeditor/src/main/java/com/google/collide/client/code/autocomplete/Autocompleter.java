// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.client.code.autocomplete;

import com.codenvy.ide.client.util.ScheduledCommandExecutor;
import com.codenvy.ide.client.util.SignalEvent.KeySignalType;
import com.codenvy.ide.client.util.UserAgent;
import com.codenvy.ide.client.util.logging.Log;
import com.codenvy.ide.json.shared.JsonCollections;
import com.codenvy.ide.json.shared.JsonStringMap;
import com.google.collide.client.code.autocomplete.LanguageSpecificAutocompleter.ExplicitAction;
import com.google.collide.client.documentparser.DocumentParser;
import com.google.collide.client.editor.Editor;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant;
import org.exoplatform.ide.editor.client.api.contentassist.Point;
import org.exoplatform.ide.editor.shared.text.BadLocationException;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * Class to implement all the autocompletion support that is not specific to a
 * given language (e.g., css).
 */
public class Autocompleter implements ContentAssistant {

    /** Key that triggered autocomplete box opening. */
    private SignalEventEssence boxTrigger;

    private LanguageSpecificAutocompleter autocompleters;

    public JsonStringMap<ContentAssistProcessor> processors = JsonCollections.createMap();

    private boolean defferedAction;

    private class OnSelectCommand extends ScheduledCommandExecutor {

        private CompletionProposal selectedProposal;

        @Override
        protected void execute() {
            Preconditions.checkNotNull(selectedProposal);
            applyChanges(selectedProposal);
            selectedProposal = null;
        }

        public void scheduleAutocompletion(CompletionProposal selectedProposal) {
            Preconditions.checkNotNull(selectedProposal);
            this.selectedProposal = selectedProposal;
            scheduleDeferred();
        }
    }

    private final Editor editor;

    private boolean isAutocompleteInsertion = false;

    private final AutocompleteBox popup;

    private ContentAssistProcessor contentAssistProcessor;

    private final OnSelectCommand onSelectCommand = new OnSelectCommand();

    private final org.exoplatform.ide.editor.client.api.Editor exoEditor;

    /**
     * @param editor
     * @param exoEditor
     */
    Autocompleter(Editor editor, AutocompleteBox popup, org.exoplatform.ide.editor.client.api.Editor exoEditor) {
        this.editor = editor;
        this.popup = popup;
        this.exoEditor = exoEditor;

        popup.setDelegate(new AutocompleteBox.Events() {
            @Override
            public void onSelect(CompletionProposal proposal) {
                onSelectCommand.scheduleAutocompletion(proposal);
            }

            @Override
            public void onCancel() {
                dismissAutocompleteBox();
            }
        });
    }

    /**
     * Refreshes autocomplete popup contents (if it is displayed).
     * <p/>
     * <p>This method should be called when the code is modified.
     */
    public void refresh() {
        if (contentAssistProcessor == null) {
            return;
        }

        if (isAutocompleteInsertion) {
            return;
        }

        if (popup.isShowing()) {
            scheduleRequestAutocomplete();
        }
    }

    public static Autocompleter create(Editor editor, AutocompleteBox popup,
                                       org.exoplatform.ide.editor.client.api.Editor exoEditor) {
        return new Autocompleter(editor, popup, exoEditor);
    }

    /**
     * Asks popup and language-specific autocompleter to process key press
     * and schedules corresponding autocompletion requests, if required.
     *
     * @return {@code true} if event shouldn't be further processed / bubbled
     */
    public boolean processKeyPress(SignalEventEssence trigger) {
        if(editor.isReadOnly()){
            return false;
        }
        if (popup.isShowing() && popup.consumeKeySignal(trigger)) {
            return true;
        }
        defferedAction = false;
        String contentType;
        try {
            contentType = exoEditor.getDocument().getContentType(getOffset(exoEditor.getDocument()));
            contentAssistProcessor = getContentAssistProcessor(contentType);
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
            return false;
        }
        if (isActionSpace(trigger)) {
            boxTrigger = trigger;
            scheduleRequestAutocomplete();
            return true;
        }

        try {
            ExplicitAction action = autocompleters.getExplicitAction(editor.getSelection(), trigger, popup.isShowing());
    
            switch (action.getType()) {
                case EXPLICIT_COMPLETE:
                    boxTrigger = null;
                    performExplicitCompletion(action.getExplicitAutocompletion());
                    return true;
    
                case DEFERRED_COMPLETE:
                    boxTrigger = trigger;
                    defferedAction = true;
                    scheduleRequestAutocomplete();
                    return false;
    
                case CLOSE_POPUP:
                    dismissAutocompleteBox();
                    return false;
    
                default:
                    return false;
            }
        } catch (Exception e) {
            Log.error(getClass(), e);
            return false;
        }
        
    }

    private static boolean isActionSpace(SignalEventEssence trigger) {
        if (UserAgent.isMac()) {
            return (trigger.metaKey) && (trigger.keyCode == ' ') && (!trigger.altKey) && (!trigger.metaKey) && (!trigger.shiftKey) &&
                   (trigger.type == KeySignalType.INPUT);
        } else {
            return (trigger.ctrlKey) && (trigger.keyCode == ' ') && (!trigger.altKey) && (!trigger.metaKey) && (!trigger.shiftKey) &&
                   (trigger.type == KeySignalType.INPUT);
        }
    }

    /** Hides popup and prevents further activity. */
    private void stop() {
        dismissAutocompleteBox();
        if (this.contentAssistProcessor != null) {
            this.contentAssistProcessor = null;
        }
    }

    /** Setups for the document to be auto-completed. */
    public void reset(DocumentParser parser) {
        Preconditions.checkNotNull(parser);

        stop();
        autocompleters.attach(parser);
    }

    /** Applies textual and UI changes specified with {@link AutocompleteResult}. */
    private void applyChanges(CompletionProposal result) {
        dismissAutocompleteBox();

        isAutocompleteInsertion = true;
        try {
            IDocument document = exoEditor.getDocument();
            result.apply(document);

            Point selection = result.getSelection(document);
            if (selection != null) {
                int row = document.getLineOfOffset(selection.x);
                int lineOffset = document.getLineOffset(row);
                exoEditor.setCursorPosition(row + 1, selection.x - lineOffset + 1);
                exoEditor.selectRange(row + 1, selection.x - lineOffset, row + 1, selection.x - lineOffset + selection.y);
            }
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        } finally {
            isAutocompleteInsertion = false;
        }
    }

    /**
     * Dismisses the autocomplete box.
     * <p/>
     * <p>This is called when the user hits escape or types until
     * there are no more autocompletions or navigates away
     * from the autocompletion box position.
     */
    public void dismissAutocompleteBox() {
        popup.dismiss();
        boxTrigger = null;
    }

    /**
     * Schedules an asynchronous call to compute and display / perform
     * appropriate autocompletion proposals.
     */
    private void scheduleRequestAutocomplete() {
        final SignalEventEssence trigger = boxTrigger;
        final ContentAssistProcessor processor = contentAssistProcessor;
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                requestAutocomplete(processor, trigger);
            }
        });
    }

    private void performExplicitCompletion(AutocompleteResult completion) {
        Preconditions.checkState(!isAutocompleteInsertion);
        isAutocompleteInsertion = true;
        try {
            completion.apply(editor);
        } finally {
            isAutocompleteInsertion = false;
        }
    }

    @VisibleForTesting
    void requestAutocomplete(ContentAssistProcessor contentAssistProcessor, SignalEventEssence trigger) {
        if (contentAssistProcessor == null) {
            return;
        }
        // TODO: If there is only one proposal that gives us nothing
        //               then there are no proposals!
        IDocument document = exoEditor.getDocument();

        int offset = getOffset(document);
        CompletionProposal[] proposals = contentAssistProcessor.computeCompletionProposals(exoEditor, offset);
        if (proposals != null && proposals.length > 0) {

            if (!defferedAction && proposals.length == 1 && !popup.isShowing() && proposals[0].isAutoInsertable()) {
                onSelectCommand.scheduleAutocompletion(proposals[0]);
                return;
            }
            popup.positionAndShow(proposals);
        } else {
            dismissAutocompleteBox();
        }
    }

    /**
     * @param document
     * @return
     * @throws BadLocationException
     */
    private int getOffset(IDocument document) {
        int lineOffset;
        try {
            lineOffset = document.getLineOffset(editor.getSelection().getCursorLineNumber());
            return lineOffset + editor.getSelection().getCursorColumn();
        } catch (BadLocationException e) {
            Log.error(getClass(), e);
        }
        return 0;
    }

    public void cleanup() {
        stop();
    }

    /**
     * @param autocompleter
     */
    public void addAutocompleter(LanguageSpecificAutocompleter autocompleter) {
        this.autocompleters = autocompleter;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#install(org.exoplatform.ide.editor.client.api.Editor) */
    @Override
    public void install(org.exoplatform.ide.editor.client.api.Editor textViewer) {

    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#uninstall() */
    @Override
    public void uninstall() {

    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#showPossibleCompletions() */
    @Override
    public String showPossibleCompletions() {
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#showContextInformation() */
    @Override
    public String showContextInformation() {
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#getContentAssistProcessor(java.lang.String) */
    @Override
    public ContentAssistProcessor getContentAssistProcessor(String contentType) {
        return processors.get(contentType);
    }

    /**
     * @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#addContentAssitProcessor(java.lang.String,
     *      org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor)
     */
    @Override
    public void addContentAssitProcessor(String contentType, ContentAssistProcessor processor) {
        processors.put(contentType, processor);
    }

}
