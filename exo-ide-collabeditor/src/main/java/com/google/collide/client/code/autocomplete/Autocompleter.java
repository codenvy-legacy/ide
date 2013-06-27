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

    //  /**
    //   * Flag that specifies if proposals are filtered case-insensitively.
    //   *
    //   * <p>Once, this constant should become configuration option.
    //   */
    //  public static final boolean CASE_INSENSITIVE = true;
    //
    //  /**
    //   * Constant which limits number of results returned by
    //   * {@link LimitedContextFilePrefixIndex}.
    //   */
    //  private static final int LOCAL_PREFIX_INDEX_LIMIT = 50;
    //
    //  private static final XmlCodeAnalyzer XML_CODE_ANALYZER = new XmlCodeAnalyzer();
    //
    //  private final SkipListStringBag localPrefixIndexStorage;
    //  private final ParsingTask localPrefixIndexUpdater;
    //  private final PyIndexUpdater pyIndexUpdater;
    //  private final JsIndexUpdater jsIndexUpdater;
    //
    //  private final HtmlAutocompleter htmlAutocompleter;
    //  private final CssAutocompleter cssAutocompleter;
    //  private final CodeGraphAutocompleter jsAutocompleter;
    //  private final CodeGraphAutocompleter pyAutocompleter;

    /** Key that triggered autocomplete box opening. */
    private SignalEventEssence boxTrigger;

    private LanguageSpecificAutocompleter autocompleters;

    public JsonStringMap<ContentAssistProcessor> processors = JsonCollections.createMap();
    private boolean defferedAction;

    //  /**
    //   * Proxy that distributes notifications to all code analyzers.
    //   */
    //  private final CodeAnalyzer distributingCodeAnalyzer = new CodeAnalyzer() {
    //    @Override
    //    public void onBeforeParse() {
    //      XML_CODE_ANALYZER.onBeforeParse();
    //      localPrefixIndexUpdater.onBeforeParse();
    //      pyIndexUpdater.onBeforeParse();
    //      jsIndexUpdater.onBeforeParse();
    //    }
    //
    //    @Override
    //    public void onParseLine(
    //        TaggableLine previousLine, TaggableLine line, @Nonnull JsonArray<Token> tokens) {
    //      LanguageSpecificAutocompleter languageAutocompleter = getLanguageSpecificAutocompleter();
    //      if (htmlAutocompleter == languageAutocompleter) {
    //        htmlAutocompleter.updateModeAnchors(line, tokens);
    //        XML_CODE_ANALYZER.onParseLine(previousLine, line, tokens);
    //        localPrefixIndexUpdater.onParseLine(previousLine, line, tokens);
    //        jsIndexUpdater.onParseLine(previousLine, line, tokens);
    //      } else if (pyAutocompleter == languageAutocompleter) {
    //        localPrefixIndexUpdater.onParseLine(previousLine, line, tokens);
    //        pyIndexUpdater.onParseLine(previousLine, line, tokens);
    //      } else if (jsAutocompleter == languageAutocompleter) {
    //        localPrefixIndexUpdater.onParseLine(previousLine, line, tokens);
    //        jsIndexUpdater.onParseLine(previousLine, line, tokens);
    //      }
    //    }
    //
    //    @Override
    //    public void onAfterParse() {
    //      XML_CODE_ANALYZER.onAfterParse();
    //      localPrefixIndexUpdater.onAfterParse();
    //      pyIndexUpdater.onAfterParse();
    //      jsIndexUpdater.onAfterParse();
    //    }
    //
    //    @Override
    //    public void onLinesDeleted(JsonArray<TaggableLine> deletedLines) {
    //      XML_CODE_ANALYZER.onLinesDeleted(deletedLines);
    //      localPrefixIndexUpdater.onLinesDeleted(deletedLines);
    //      pyIndexUpdater.onLinesDeleted(deletedLines);
    //      jsIndexUpdater.onLinesDeleted(deletedLines);
    //    }
    //  };

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
                //            if (AutocompleteProposals.NO_OP == proposal)
                //            {
                //               return;
                //            }
                //            // This is called on UI click - so surely we want popup to disappear.
                //            // TODO: It's a quick-fix; uncomment when autocompletions
                //            //               become completer state free.
                //            //dismissAutocompleteBox();
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

    /**
     */
    private final AutocompleterCallback callback = new AutocompleterCallback() {

        @Override
        public void rescheduleCompletionRequest() {
            scheduleRequestAutocomplete();
        }
    };

    //  private final OnSelectCommand onSelectCommand = new OnSelectCommand();

    public static Autocompleter create(Editor editor, AutocompleteBox popup,
                                       org.exoplatform.ide.editor.client.api.Editor exoEditor) {
        return new Autocompleter(editor, popup, exoEditor);
    }

    //  public static Autocompleter create(
    //      Editor editor, CubeClient cubeClient, final AutocompleteBox popup) {
    //    SkipListStringBag localPrefixIndexStorage = new SkipListStringBag();
    //    LimitedContextFilePrefixIndex limitedContextFilePrefixIndex = new LimitedContextFilePrefixIndex(
    //        LOCAL_PREFIX_INDEX_LIMIT, localPrefixIndexStorage);
    //    CssAutocompleter cssAutocompleter = CssAutocompleter.create();
    //    CodeGraphAutocompleter jsAutocompleter = JsAutocompleter.create(
    //        cubeClient, limitedContextFilePrefixIndex);
    //    HtmlAutocompleter htmlAutocompleter = HtmlAutocompleter.create(
    //        cssAutocompleter, jsAutocompleter);
    //    CodeGraphAutocompleter pyAutocompleter = PyAutocompleter.create(
    //        cubeClient, limitedContextFilePrefixIndex);
    //    PyIndexUpdater pyIndexUpdater = new PyIndexUpdater();
    //    JsIndexUpdater jsIndexUpdater = new JsIndexUpdater();
    //    return new Autocompleter(editor, popup, localPrefixIndexStorage, htmlAutocompleter,
    //        cssAutocompleter, jsAutocompleter, pyAutocompleter, pyIndexUpdater, jsIndexUpdater);
    //  }

    //  Autocompleter(Editor editor, final AutocompleteBox popup,
    //      SkipListStringBag localPrefixIndexStorage, HtmlAutocompleter htmlAutocompleter,
    //      CssAutocompleter cssAutocompleter, CodeGraphAutocompleter jsAutocompleter,
    //      CodeGraphAutocompleter pyAutocompleter, PyIndexUpdater pyIndexUpdater,
    //      JsIndexUpdater jsIndexUpdater) {
    //    this.editor = editor;
    //    this.localPrefixIndexStorage = localPrefixIndexStorage;
    //    this.pyIndexUpdater = pyIndexUpdater;
    //    this.jsIndexUpdater = jsIndexUpdater;
    //    this.localPrefixIndexUpdater = new ParsingTask(localPrefixIndexStorage);
    //
    //    this.cssAutocompleter = cssAutocompleter;
    //    this.jsAutocompleter = jsAutocompleter;
    //    this.htmlAutocompleter = htmlAutocompleter;
    //    this.pyAutocompleter = pyAutocompleter;
    //
    //    this.popup = popup;
    //    popup.setDelegate(new AutocompleteBox.Events() {
    //
    //      @Override
    //      public void onSelect(ProposalWithContext proposal) {
    //        if (AutocompleteProposals.NO_OP == proposal) {
    //          return;
    //        }
    //        // This is called on UI click - so surely we want popup to disappear.
    //        // TODO: It's a quick-fix; uncomment when autocompletions
    //        //               become completer state free.
    //        //dismissAutocompleteBox();
    //        onSelectCommand.scheduleAutocompletion(proposal);
    //      }
    //
    //      @Override
    //      public void onCancel() {
    //        dismissAutocompleteBox();
    //      }
    //    });
    //  }

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

//      LanguageSpecificAutocompleter autocompleter = getLanguageSpecificAutocompleter(contentType);
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
        //    localPrefixIndexStorage.clear();
    }

    /** Setups for the document to be auto-completed. */
    public void reset(DocumentParser parser) {
        Preconditions.checkNotNull(parser);

        stop();

//      LanguageSpecificAutocompleter autocompleter = getAutocompleter(parser.getSyntaxType());
        autocompleters.attach(parser);
    }

//   protected LanguageSpecificAutocompleter getLanguageSpecificAutocompleter(String contentType)
//   {
//      Preconditions.checkNotNull(contentAssistProcessor);
//      if (autocompleters.containsKey(contentType))
//         return autocompleters.get(contentType);
//      return NoneAutocompleter.getInstance(); //contentAssistProcessor.getLanguageSpecificAutocompleter();
//   }

    //  AutocompleteController getController() {
    //    return autocompleteController;
    //  }

    //  SyntaxType getMode() {
    //    return (autocompleteController == null)
    //        ? SyntaxType.NONE : autocompleteController.getLanguageSpecificAutocompleter().getMode();
    //  }

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

    //  /**
    //   * Fetch changes from controller for selected proposal, hide popup;
    //   * apply changes.
    //   *
    //   * @param proposal proposal item selected by user
    //   */
    //  void reallyFinishAutocompletion(CompletionProposal proposal) {
    //    applyChanges(contentAssistProcessor.finish(proposal));
    //  }

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
        //    if (autocompleteController != null) {
        //      autocompleteController.pause();
        //    }
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

        //    if (AutocompleteProposals.PARSING == proposals && popup.isShowing()) {
        //      // Do nothing to avoid flickering.
        //    } else if (!proposals.isEmpty()) {
        //      popup.positionAndShow(proposals);
        //    } else {
        //      dismissAutocompleteBox();
        //    }
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

    //   @VisibleForTesting
    //   protected LanguageSpecificAutocompleter getAutocompleter(SyntaxType mode)
    //   {
    //      //    switch (mode) {
    //      //      case HTML:
    //      //        return htmlAutocompleter;
    //      //      case JS:
    //      //        return jsAutocompleter;
    //      //      case CSS:
    //      //        return cssAutocompleter;
    //      //      case PY:
    //      //        return pyAutocompleter;
    //      //      default:
    //      //        return NoneAutocompleter.getInstance();
    //      //    }
    //      if (autocompleters.containsKey(mode.name()))
    //         return autocompleters.get(mode.name());
    //      return NoneAutocompleter.getInstance();
    //   }

    public void cleanup() {
        stop();
    }

    //  public CodeAnalyzer getCodeAnalyzer() {
    //    return distributingCodeAnalyzer;
    //  }

    /** Refreshes proposals list after cursor has been processed by parser. */
    public void onCursorLineParsed() {
        refresh();
    }

    public void onDocumentParsingFinished() {
        refresh();
    }

    /**
     * @param mode
     * @param autocompleter
     */
    public void addAutocompleter(LanguageSpecificAutocompleter autocompleter) {
        this.autocompleters = autocompleter;
//      autocompleters.put(contentType, autocompleter);
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#install(org.exoplatform.ide.editor.client.api.Editor) */
    @Override
    public void install(org.exoplatform.ide.editor.client.api.Editor textViewer) {
        // TODO Auto-generated method stub

    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#uninstall() */
    @Override
    public void uninstall() {
        // TODO Auto-generated method stub

    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#showPossibleCompletions() */
    @Override
    public String showPossibleCompletions() {
        // TODO Auto-generated method stub
        return null;
    }

    /** @see org.exoplatform.ide.editor.client.api.contentassist.ContentAssistant#showContextInformation() */
    @Override
    public String showContextInformation() {
        // TODO Auto-generated method stub
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
