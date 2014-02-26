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

package com.codenvy.ide.texteditor;

import elemental.html.Element;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.collections.StringMap.IterationCallback;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.mvp.CompositeView;
import com.codenvy.ide.mvp.UiComponent;
import com.codenvy.ide.text.BadLocationException;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentCommand;
import com.codenvy.ide.text.DocumentImpl;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.RegionImpl;
import com.codenvy.ide.text.TextUtilities;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.text.store.DocumentModel;
import com.codenvy.ide.text.store.LineInfo;
import com.codenvy.ide.text.store.TextStoreMutator;
import com.codenvy.ide.texteditor.api.AutoEditStrategy;
import com.codenvy.ide.texteditor.api.BeforeTextListener;
import com.codenvy.ide.texteditor.api.ContentFormatter;
import com.codenvy.ide.texteditor.api.KeyListener;
import com.codenvy.ide.texteditor.api.NativeKeyUpListener;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorOperations;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.TextInputListener;
import com.codenvy.ide.texteditor.api.TextListener;
import com.codenvy.ide.texteditor.api.UndoManager;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistAssistant;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;
import com.codenvy.ide.texteditor.codeassistant.CodeAssistantImpl;
import com.codenvy.ide.texteditor.codeassistant.QuickAssistAssistantImpl;
import com.codenvy.ide.texteditor.documentparser.DocumentParser;
import com.codenvy.ide.texteditor.gutter.Gutter;
import com.codenvy.ide.texteditor.gutter.LeftGutterManager;
import com.codenvy.ide.texteditor.input.ActionExecutor;
import com.codenvy.ide.texteditor.input.CommonActions;
import com.codenvy.ide.texteditor.input.InputController;
import com.codenvy.ide.texteditor.input.InputScheme;
import com.codenvy.ide.texteditor.input.RootActionExecutor;
import com.codenvy.ide.texteditor.linedimensions.LineDimensionsCalculator;
import com.codenvy.ide.texteditor.linedimensions.LineDimensionsUtils;
import com.codenvy.ide.texteditor.parenmatch.ParenMatchHighlighter;
import com.codenvy.ide.texteditor.renderer.AnnotationRenderer;
import com.codenvy.ide.texteditor.renderer.CurrentLineHighlighter;
import com.codenvy.ide.texteditor.renderer.DebugLineRenderer;
import com.codenvy.ide.texteditor.renderer.LineRenderer;
import com.codenvy.ide.texteditor.renderer.RenderTimeExecutor;
import com.codenvy.ide.texteditor.renderer.Renderer;
import com.codenvy.ide.texteditor.selection.CursorView;
import com.codenvy.ide.texteditor.selection.LocalCursorController;
import com.codenvy.ide.texteditor.selection.SelectionLineRenderer;
import com.codenvy.ide.texteditor.selection.SelectionManager;
import com.codenvy.ide.texteditor.selection.SelectionModel;
import com.codenvy.ide.texteditor.syntaxhighlighter.SyntaxHighlighter;
import com.codenvy.ide.util.CssUtils;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar;
import com.codenvy.ide.util.dom.Elements;
import com.codenvy.ide.util.dom.FontDimensionsCalculator;
import com.codenvy.ide.util.dom.FontDimensionsCalculator.FontDimensions;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.codenvy.ide.util.input.SignalEvent;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

import java.util.Iterator;


/**
 * The Display for the text editor presenter.
 * This is default implementation for {@link TextEditorPartView}
 * This class composes many of the other classes that together form the editor.
 * For example, the area where the text is displayed, the {@link Buffer}, is a
 * nested presenter. Other components are not presenters, such as the input
 * mechanism which is handled by the {@link InputController}.
 * <p/>
 * If an added element wants native browser selection, you must not inherit the
 * "user-select" CSS property. See
 * {@link CssUtils#setUserSelect(Element, boolean)}.
 */
public class TextEditorViewImpl extends UiComponent<TextEditorViewImpl.View> implements TextEditorPartView {

    public static final int ANIMATION_DURATION = 100;
    private static      int idCounter          = 0;
    private final Buffer                                      buffer;
    private final EditorTextStoreMutator                      editorDocumentMutator;
    private final FontDimensionsCalculator                    editorFontDimensionsCalculator;
    private final com.codenvy.ide.texteditor.api.FocusManager focusManager;
    private final MouseHoverManager                           mouseHoverManager;
    private final int                               id                            = idCounter++;
    private final FontDimensionsCalculator.Callback fontDimensionsChangedCallback = new FontDimensionsCalculator.Callback() {
        @Override
        public void onFontDimensionsChanged(FontDimensions fontDimensions) {
            handleFontDimensionsChanged();
        }
    };
    private final Array<Gutter>                     gutters                       = Collections.createArray();
    private final InputController   input;
    private final LeftGutterManager leftGutterManager;
    private final ListenerManager<ReadOnlyListener>  readOnlyListenerManager  = ListenerManager.create();
    private final ListenerManager<TextInputListener> textInputListenerManager = ListenerManager.create();
    private final EditorActivityManager              editorActivityManager;
    private final RenderTimeExecutor                 renderTimeExecutor;
    private final com.codenvy.ide.Resources          resources;
    private final UserActivityManager                userActivityManager;
    private final OverviewRuler                      overviewRuller;
    private       DocumentModel                      textStore;
    private       UndoManager                        editorUndoManager;
    private       LocalCursorController              localCursorController;
    private       Renderer                           renderer;
    private       SelectionManager                   selectionManager;
    private       ViewportModel                      viewport;
    private       boolean                            isReadOnly;
    private       Document                           document;
    private       SyntaxHighlighter                  syntaxHighlighter;
    private       Parser                             parser;
    private       CodeAssistantImpl                  codeAssistant;
    private       VerticalRuler                      verticalRuler;
    private       QuickAssistAssistant               quickAssistAssistant;
    private       BreakpointGutterManager            breakpointGutterManager;
    private       DtoFactory                         dtoFactory;
    private       StringMap<Array<AutoEditStrategy>> autoEditStrategies;
    private       String                             documentPartitioning;
    private       ContentFormatter                   contentFormatter;

    public TextEditorViewImpl(com.codenvy.ide.Resources resources, UserActivityManager userActivityManager,
                              BreakpointGutterManager breakpointGutterManager, DtoFactory dtoFactory) {
        this.resources = resources;
        this.userActivityManager = userActivityManager;
        this.breakpointGutterManager = breakpointGutterManager;
        this.dtoFactory = dtoFactory;
        editorFontDimensionsCalculator = FontDimensionsCalculator.get(resources.workspaceEditorCss().editorFont());
        renderTimeExecutor = new RenderTimeExecutor();
        LineDimensionsCalculator lineDimensions = LineDimensionsCalculator.create(editorFontDimensionsCalculator);

        buffer = Buffer.create(resources, editorFontDimensionsCalculator.getFontDimensions(), lineDimensions,
                               renderTimeExecutor);
        input = new InputController();
        View view = new View(resources, buffer.getView().getElement(), input.getInputElement());
        setView(view);

        focusManager = new FocusManagerImpl(buffer, input.getInputElement());

        Gutter overviewGutter = createGutter(true, Gutter.Position.RIGHT, resources.workspaceEditorCss().leftGutterNotification());
        overviewRuller = new OverviewRuler(overviewGutter, this);

        Gutter leftGutter = createGutter(false, Gutter.Position.LEFT, resources.workspaceEditorCss().leftGutter());
        leftGutterManager = new LeftGutterManager(leftGutter, buffer);
        Gutter leftNotificationGutter = createGutter(false, Gutter.Position.LEFT,
                                                     resources.workspaceEditorCss().leftGutterNotification());
        verticalRuler = new VerticalRuler(leftNotificationGutter, this);

        editorDocumentMutator = new EditorTextStoreMutator(this);
        mouseHoverManager = new MouseHoverManager(this);

        editorActivityManager = new EditorActivityManager(userActivityManager, buffer.getScrollListenerRegistrar(),
                                                          getKeyListenerRegistrar());

        input.initializeFromEditor(this, editorDocumentMutator);

        setAnimationEnabled(true);
        addBoxShadowOnScrollHandler();
        editorFontDimensionsCalculator.addCallback(fontDimensionsChangedCallback);
    }

    /**
     * Hook called on receipt of a <code>EditorDocumentMutator</code>. The event has
     * been translated into a <code>DocumentCommand</code> which can now be
     * manipulated by interested parties. By default, the hook forwards the command
     * to the installed instances of <code>AutoEditStrategy</code>.
     *
     * @param command
     *         the document command representing the verify event
     */
    public void customizeDocumentCommand(DocumentCommand command) {
//        if (isIgnoringAutoEditStrategies())
//            return;

        Document document = getDocument();

//        if (fTabsToSpacesConverter != null)
//            fTabsToSpacesConverter.customizeDocumentCommand(document, command);
        Array<AutoEditStrategy> strategies = null;
        try {
            String contentType = TextUtilities.getContentType(document, getDocumentPartitioning(), command.offset, true);
            strategies = autoEditStrategies.get(contentType);
        } catch (BadLocationException e) {
            Log.debug(TextEditorViewImpl.class, e);
        }


        if (strategies == null)
            return;

        switch (strategies.size()) {
            // optimization
            case 0:
                break;

            case 1:
                strategies.asIterable().iterator().next().customizeDocumentCommand(document, command);
                break;

            // make iterator robust against adding/removing strategies from within strategies
            default:
                strategies = Collections.createArray(strategies.asIterable());
                for (final Iterator<AutoEditStrategy> iterator = strategies.asIterable().iterator(); iterator.hasNext(); )
                    iterator.next().customizeDocumentCommand(document, command);

                break;
        }
    }

    private void handleFontDimensionsChanged() {
        buffer.repositionAnchoredElementsWithColumn();
        if (renderer != null) {
         /*
          * TODO: think about a scheme where we don't have to render
          * the whole viewport (currently we do because of the right-side gap
          * fillers)
          */
            renderer.renderAll();
        }
    }

    /**
     * Adds a scroll handler to the buffer scrollableElement so that a drop shadow
     * can be added and removed when scrolled.
     */
    private void addBoxShadowOnScrollHandler() {
        if (true) {
            // TODO: investigate why this kills performance
            return;
        }

        //      this.buffer.getScrollListenerRegistrar().add(new ScrollListener()
        //      {
        //
        //         @Override
        //         public void onScroll(Buffer buffer, int scrollTop)
        //         {
        //            if (scrollTop < 20)
        //            {
        //               getElement().removeClassName(getView().css.scrolled());
        //            }
        //            else
        //            {
        //               getElement().addClassName(getView().css.scrolled());
        //            }
        //         }
        //      });
    }

    public void addLineRenderer(LineRenderer lineRenderer) {
      /*
       * TODO: Because the line renderer is document-scoped, line
       * renderers have to re-add themselves whenever the document changes. This
       * is unexpected.
       */
        renderer.addLineRenderer(lineRenderer);
    }

    public Gutter createGutter(boolean overviewMode, Gutter.Position position, String cssClassName) {
        Gutter gutter = Gutter.create(overviewMode, position, cssClassName, buffer);
        if (viewport != null && renderer != null) {
            gutter.handleDocumentChanged(viewport, renderer);
        }

        gutters.add(gutter);

        gutter.getGutterElement().addClassName(getView().css.gutter());
        getView().addGutter(gutter.getGutterElement());
        return gutter;
    }

    public void removeGutter(Gutter gutter) {
        getView().removeGutter(gutter.getGutterElement());
        gutters.remove(gutter);
    }

    public void setAnimationEnabled(boolean enabled) {
        getView().setAnimationEnabled(enabled);
    }

    public ListenerRegistrar<BeforeTextListener> getBeforeTextListenerRegistrar() {
        return editorDocumentMutator.getBeforeTextListenerRegistrar();
    }

    public Buffer getBuffer() {
        return buffer;
    }

    public Gutter getLeftGutter() {
        return leftGutterManager.getGutter();
    }

    public DocumentModel getTextStore() {
        return textStore;
    }

    public TextStoreMutator getEditorDocumentMutator() {
        return editorDocumentMutator;
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#getElement() */
    @Override
    public com.google.gwt.user.client.Element getElement() {
        return (com.google.gwt.user.client.Element)getView().getElement();
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#getFocusManager() */
    @Override
    public com.codenvy.ide.texteditor.api.FocusManager getFocusManager() {
        return focusManager;
    }

    public MouseHoverManager getMouseHoverManager() {
        return mouseHoverManager;
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#getKeyListenerRegistrar() */
    @Override
    public ListenerRegistrar<KeyListener> getKeyListenerRegistrar() {
        return input.getKeyListenerRegistrar();
    }

    public ListenerRegistrar<NativeKeyUpListener> getNativeKeyUpListenerRegistrar() {
        return input.getNativeKeyUpListenerRegistrar();
    }

    public Renderer getRenderer() {
        return renderer;
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#getSelection() */
    @Override
    public SelectionModel getSelection() {
        return selectionManager.getSelectionModel();
    }

    public LocalCursorController getCursorController() {
        return localCursorController;
    }

    public ListenerRegistrar<TextListener> getTextListenerRegistrar() {
        return editorDocumentMutator.getTextListenerRegistrar();
    }

    // TODO: need a public interface and impl
    public ViewportModel getViewport() {
        return viewport;
    }

    public void removeLineRenderer(LineRenderer lineRenderer) {
        renderer.removeLineRenderer(lineRenderer);
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#getDocument() */
    @Override
    public Document getDocument() {
        return document;
    }

    /** {@inheritDoc} */
    @Override
    public void setDocument(final Document document) {
        this.document = document;
        textStore = ((DocumentImpl)document).getTextStore();

      /*
       * TODO: dig into each component, figure out dependencies,
       * break apart components so we can reduce circular dependencies which
       * require the multiple stages of initialization
       */
        // Core editor components
        buffer.handleDocumentChanged(textStore);
        leftGutterManager.handleDocumentChanged(textStore);

        selectionManager = SelectionManager.create(document, textStore, buffer, focusManager, resources);

        SelectionModel selection = selectionManager.getSelectionModel();
        viewport = ViewportModel.create(textStore, selection, buffer);
        input.handleDocumentChanged(textStore, selection, viewport);
        renderer = Renderer.create(textStore, viewport, buffer, getLeftGutter(), selection, focusManager, this, resources,
                                   renderTimeExecutor, breakpointGutterManager);
        if (editorUndoManager != null) {
            editorUndoManager.connect(this);
        }

        // Delayed core editor component initialization
        viewport.initialize();
        selection.initialize(viewport);
        selectionManager.initialize(renderer);
        buffer.handleComponentsInitialized(viewport, renderer);
        for (int i = 0, n = gutters.size(); i < n; i++) {
            gutters.get(i).handleDocumentChanged(viewport, renderer);
        }

        //    // Non-core editor components
        //    editorUndoManager = EditorUndoManager.create(this, document, selection);
        //    searchModel = SearchModel.create(appContext,
        //        document,
        //        renderer,
        //        viewport,
        //        selection,
        //        editorDocumentMutator);
        localCursorController = LocalCursorController.create(resources, focusManager, selection, buffer, this);
        ParenMatchHighlighter.create(textStore, getViewport(), textStore.getAnchorManager(), getView().getResources(),
                                     getRenderer(), getSelection());
        createSyntaxHighlighter(parser);
        new CurrentLineHighlighter(buffer, selection, resources);
        breakpointGutterManager.setDebugLineRenderer(new DebugLineRenderer(buffer, resources));
        textInputListenerManager.dispatch(new Dispatcher<TextInputListener>() {

            @Override
            public void dispatch(TextInputListener listener) {
                listener.inputDocumentChanged(null, document);
            }
        });

    }

    public void undo() {
        editorUndoManager.undo();
    }

    public void redo() {
        editorUndoManager.redo();
    }

    public void scrollTo(int lineNumber, int column) {
        if (textStore != null) {
            LineInfo lineInfo = textStore.getLineFinder().findLine(lineNumber);
         /*
          * TODO: the cursor will be the last line in the viewport,
          * fix this
          */
            SelectionModel selectionModel = getSelection();
            selectionModel.deselect();
            selectionModel.setCursorPosition(lineInfo, column);
        }
    }

    public void cleanup() {
        editorFontDimensionsCalculator.removeCallback(fontDimensionsChangedCallback);
        editorActivityManager.teardown();
    }

    @Override
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public void setReadOnly(final boolean isReadOnly) {

        if (this.isReadOnly == isReadOnly) {
            return;
        }

        this.isReadOnly = isReadOnly;

        readOnlyListenerManager.dispatch(new Dispatcher<TextEditorViewImpl.ReadOnlyListener>() {
            @Override
            public void dispatch(ReadOnlyListener listener) {
                listener.onReadOnlyChanged(isReadOnly);
            }
        });
    }

    public ListenerRegistrar<ReadOnlyListener> getReadOnlyListenerRegistrar() {
        return readOnlyListenerManager;
    }

    public int getId() {
        return id;
    }

    public InputController getInput() {
        return input;
    }

    public void setLeftGutterVisible(boolean visible) {
        Element gutterElement = leftGutterManager.getGutter().getGutterElement();
        if (visible) {
            getView().addGutter(gutterElement);
        } else {
            getView().removeGutter(gutterElement);
        }
    }

    /** @return the editorUndoManager */
    public UndoManager getUndoManager() {
        return editorUndoManager;
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#setUndoManager(com.codenvy.ide.texteditor.api.UndoManager) */
    @Override
    public void setUndoManager(UndoManager undoManager) {
        this.editorUndoManager = undoManager;
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#configure(com.codenvy.ide.texteditor.api.TextEditorConfiguration) */
    @Override
    public void configure(TextEditorConfiguration configuration) {
        setUndoManager(configuration.getUndoManager(this));
        LineDimensionsUtils.setTabSpaceEquivalence(configuration.getTabWidth(this));
        parser = configuration.getParser(this);
        RootActionExecutor actionExecutor = getInput().getActionExecutor();
        actionExecutor.addDelegate(TextActions.INSTANCE);
        StringMap<CodeAssistProcessor> processors = configuration.getContentAssistantProcessors(this);
        setDocumentPartitioning(configuration.getConfiguredDocumentPartitioning(this));
        Reconciler reconciler = configuration.getReconciler(this);
        contentFormatter = configuration.getContentFormatter(this);
        if (reconciler != null) {
            reconciler.install(this);
        }


        if (processors != null) {
            codeAssistant = new CodeAssistantImpl();
            processors.iterate(new IterationCallback<CodeAssistProcessor>() {
                @Override
                public void onIteration(String key, CodeAssistProcessor value) {
                    codeAssistant.setCodeAssistantProcessor(key, value);
                }
            });
            codeAssistant.install(this);
            actionExecutor.addDelegate(new ActionExecutor() {

                @Override
                public boolean execute(String actionName, InputScheme scheme, SignalEvent event) {
                    if (CommonActions.RUN_CODE_ASSISTANT.equals(actionName)) {
                        codeAssistant.showPossibleCompletions();
                        return true;
                    }
                    return false;
                }
            });
        }

        QuickAssistProcessor assistAssistant = configuration.getQuickAssistAssistant(this);
        if (assistAssistant != null) {
            quickAssistAssistant = new QuickAssistAssistantImpl();
            quickAssistAssistant.setQuickAssistProcessor(assistAssistant);
            quickAssistAssistant.install(this);
        }
        actionExecutor.addDelegate(new ActionExecutor() {

            @Override
            public boolean execute(String actionName, InputScheme scheme, SignalEvent event) {
                if (CommonActions.RUN_QUICK_ASSISTANT.equals(actionName) && quickAssistAssistant != null) {
                    quickAssistAssistant.showPossibleQuickAssists();
                    return true;
                }
                return false;
            }
        });

        String[] contentTypes = configuration.getConfiguredContentTypes(this);
        for (String t : contentTypes) {
            setAutoEditStrategies(configuration.getAutoEditStrategies(this, t), t);
        }
    }

    /**
     * Returns the document partitioning for this viewer.
     *
     * @return the document partitioning for this viewer
     */
    protected String getDocumentPartitioning() {
        return documentPartitioning;
    }

    /**
     * Sets the document partitioning of this viewer. The partitioning is used by this viewer to
     * access partitioning information of the viewers input document.
     */
    private void setDocumentPartitioning(String documentPartitioning) {
        this.documentPartitioning = documentPartitioning;
    }

    /**
     * Sets the given edit strategy as the only strategy for the given content type.
     *
     * @param strategies
     *         the auto edit strategies
     * @param contentType
     *         the content type
     */
    protected final void setAutoEditStrategies(AutoEditStrategy[] strategies, String contentType) {
        if (autoEditStrategies == null)
            autoEditStrategies = Collections.createStringMap();

        Array<AutoEditStrategy> autoEditStrategies = this.autoEditStrategies.get(contentType);

        if (strategies == null) {
            if (autoEditStrategies == null)
                return;

            this.autoEditStrategies.put(contentType, null);

        } else {
            if (autoEditStrategies == null) {
                autoEditStrategies = Collections.createArray();
                this.autoEditStrategies.put(contentType, autoEditStrategies);
            }

            autoEditStrategies.clear();
            autoEditStrategies.addAll(Collections.createArray(strategies));
        }
    }

    /** @param parser */
    private void createSyntaxHighlighter(Parser parser) {
        if (parser == null) {
            return;
        }
        DocumentParser documentParser = DocumentParser.create(textStore, parser, userActivityManager);
        syntaxHighlighter = SyntaxHighlighter.create(textStore, renderer, viewport, selectionManager.getSelectionModel(),
                                                     documentParser, resources.workspaceEditorCss());
        addLineRenderer(syntaxHighlighter.getRenderer());
        //            Autoindenter.create(documentParser, this);
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#canDoOperation(int) */
    @Override
    public boolean canDoOperation(int operation) {
        if (TextEditorOperations.CODEASSIST_PROPOSALS == operation && codeAssistant != null) {
            return true;
        }

        if (TextEditorOperations.QUICK_ASSIST == operation && quickAssistAssistant != null) {
            return true;
        }

        if (TextEditorOperations.FORMAT == operation && contentFormatter != null) {
            return true;
        }

        throw new UnsupportedOperationException("Operation code: " + operation + " is not supported!");
        // TODO implement all code in TextEditorOperations


    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#doOperation(int) */
    @Override
    public void doOperation(int operation) {
        switch (operation) {
            case TextEditorOperations.CODEASSIST_PROPOSALS:
                if (codeAssistant != null) {
                    codeAssistant.showPossibleCompletions();
                }
                break;
            case TextEditorOperations.QUICK_ASSIST:
                if (quickAssistAssistant != null) {
                    quickAssistAssistant.showPossibleQuickAssists();
                }
                break;
            case TextEditorOperations.FORMAT:
                if (contentFormatter != null){
                    int lengthSelectedRange = selectionManager.getSelectionModel().getSelectedRange().getLength();
                    int offset = selectionManager.getSelectionModel().getSelectedRange().getOffset();
                    Region region = null;
                    if (lengthSelectedRange > 0){
                        region = new RegionImpl(offset, lengthSelectedRange);
                    }
                    else {region = new RegionImpl(0, getDocument().getLength());}
                    contentFormatter.format(getDocument(),region);
                }
            default:
                throw new UnsupportedOperationException("Operation code: " + operation + " is not supported!");
        }

        // TODO implement all code in TextEditorOperations
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#addTextInputListener(com.codenvy.ide.texteditor.api.TextInputListener) */
    @Override
    public void addTextInputListener(TextInputListener listener) {
        textInputListenerManager.add(listener);
    }

    /** @see com.codenvy.ide.texteditor.api.TextEditorPartView#removeTextInputListener(com.codenvy.ide.texteditor.api.TextInputListener) */
    @Override
    public void removeTextInputListener(TextInputListener listener) {
        textInputListenerManager.remove(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void setDocument(Document document, AnnotationModel annotationModel) {
        setDocument(document);
        if (annotationModel != null) {
            annotationModel.connect(document);
            verticalRuler.setModel(annotationModel);
            new AnnotationRenderer(this, annotationModel.getAnnotationDecorations(), dtoFactory).setMode(annotationModel);
            overviewRuller.setModel(annotationModel);
        }
    }

    /**
     * Internal API. Set specific quick assistant implementation.
     *
     * @param quickAssistAssistant
     */
    public void setQuickAssistAssistant(QuickAssistAssistant quickAssistAssistant) {
        this.quickAssistAssistant = quickAssistAssistant;
    }

    public void resetHistory() {
        editorUndoManager.reset();
    }

    /** Animation CSS. */
    @CssResource.Shared
    public interface EditorSharedCss extends CssResource {
        String animationEnabled();

        String scrollable();
    }

    /** CssResource for the editor. */
    public interface Css extends EditorSharedCss {
        String leftGutter();

        String leftGutterNotification();

        String editorFont();

        String root();

        String scrolled();

        String gutter();

        String lineRendererError();

        String leftGutterBase();

        String lineWarning();

        String lineError();
    }

    /** ClientBundle for the editor. */
    public interface Resources
            extends Buffer.Resources, CursorView.Resources, SelectionLineRenderer.Resources, ParenMatchHighlighter.Resources {
        @Source({"Editor.css", "com/codenvy/ide/api/ui/style.css"})
        Css workspaceEditorCss();

        @Source("squiggle.gif")
        ImageResource squiggle();

        @Source("squiggle-warning.png")
        ImageResource squiggleWarning();
    }

    /**
     * A listener that is called when the editor becomes or is no longer
     * read-only.
     */
    public interface ReadOnlyListener {
        void onReadOnlyChanged(boolean isReadOnly);
    }

    /**
     * The view for the editor, containing gutters and the buffer. This exposes
     * only the ability to enable or disable animations.
     */
    public static class View extends CompositeView<Void> {
        final         Css       css;
        final         Resources res;
        private final Element   bufferElement;

        private View(Resources res, Element bufferElement, Element inputElement) {

            this.res = res;
            this.bufferElement = bufferElement;
            this.css = res.workspaceEditorCss();

            Element rootElement = Elements.createDivElement(css.root());
            rootElement.appendChild(bufferElement);
            rootElement.appendChild(inputElement);
            setElement(rootElement);
        }

        private void addGutter(Element gutterElement) {
            getElement().insertBefore(gutterElement, bufferElement);
        }

        private void removeGutter(Element gutterElement) {
            getElement().removeChild(gutterElement);
        }

        public void setAnimationEnabled(boolean enabled) {
            // TODO: Re-enable animations when they are stable.
            if (enabled) {
                // getElement().addClassName(css.animationEnabled());
            } else {
                // getElement().removeClassName(css.animationEnabled());
            }
        }

        public Resources getResources() {
            return res;
        }
    }

}
