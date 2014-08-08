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

package com.codenvy.ide.texteditor.renderer;

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.texteditor.FocusManager;
import com.codenvy.ide.debug.BreakpointGutterManager;
import com.codenvy.ide.text.store.DocumentModel;
import com.codenvy.ide.text.store.Line;
import com.codenvy.ide.texteditor.Buffer;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.ViewportModel;
import com.codenvy.ide.texteditor.gutter.Gutter;
import com.codenvy.ide.texteditor.renderer.ChangeTracker.ChangeType;
import com.codenvy.ide.texteditor.selection.SelectionModel;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerRegistrar;

import java.util.EnumSet;

/**
 * A class that is the entry point for the rendering of the editor.
 * <p/>
 * The lifecycle of this class is tied to the current document. If the document
 * is replaced, a new instance of this class is created for the new document.
 */
public class Renderer {

    public static Renderer create(DocumentModel document, ViewportModel viewport, Buffer buffer, Gutter leftGutter,
                                  SelectionModel selection, FocusManager focusManager,
                                  TextEditorViewImpl editor, Resources res, RenderTimeExecutor renderTimeExecutor,
                                  BreakpointGutterManager breakpointGutterManager) {
        return new Renderer(document, viewport, buffer, leftGutter, selection, focusManager, editor, res,
                            renderTimeExecutor, breakpointGutterManager);
    }

    /** Listener that is notified when the rendering is finished. */
    public interface CompletionListener {
        void onRenderCompleted();
    }

    /**
     * Listener that is notified on creation and garbage collection of a
     * rendered line.
     */
    public interface LineLifecycleListener {
        void onRenderedLineCreated(Line line, int lineNumber);

        void onRenderedLineGarbageCollected(Line line);

        void onRenderedLineShifted(Line line, int lineNumber);
    }

    private final ChangeTracker changeTracker;

    private final ListenerManager<CompletionListener> completionListenerManager;

    private final ListenerManager<LineLifecycleListener> lineLifecycleListenerManager;

    private final LineNumberRenderer lineNumberRenderer;

    private final ListenerManager.Dispatcher<CompletionListener> renderCompletedDispatcher =
            new ListenerManager.Dispatcher<CompletionListener>() {
                @Override
                public void dispatch(CompletionListener listener) {
                    listener.onRenderCompleted();
                }
            };

    private final ViewportRenderer viewportRenderer;

    private final ViewportModel viewport;

    private final RenderTimeExecutor renderTimeExecutor;

    private Renderer(DocumentModel document, ViewportModel viewport, Buffer buffer, Gutter leftGutter,
                     SelectionModel selection, FocusManager focusManager, TextEditorViewImpl editor,
                     Resources res, RenderTimeExecutor renderTimeExecutor, BreakpointGutterManager breakpointGutterManager) {
        this.viewport = viewport;
        this.renderTimeExecutor = renderTimeExecutor;
        this.completionListenerManager = ListenerManager.create();
        this.lineLifecycleListenerManager = ListenerManager.create();
        this.changeTracker = new ChangeTracker(this, buffer, document, viewport, selection, focusManager);
        this.viewportRenderer =
                new ViewportRenderer(document, buffer, viewport, editor.getView(), lineLifecycleListenerManager);
        this.lineNumberRenderer = new LineNumberRenderer(buffer, res, leftGutter, viewport, selection, editor, breakpointGutterManager);

    }

    public void addLineRenderer(LineRenderer lineRenderer) {
        viewportRenderer.addLineRenderer(lineRenderer);
    }

    public ListenerRegistrar<CompletionListener> getCompletionListenerRegistrar() {
        return completionListenerManager;
    }

    public ListenerRegistrar<LineLifecycleListener> getLineLifecycleListenerRegistrar() {
        return lineLifecycleListenerManager;
    }

    public void removeLineRenderer(LineRenderer lineRenderer) {
        viewportRenderer.removeLineRenderer(lineRenderer);
    }

    public void renderAll() {
        viewportRenderer.render();
        renderTimeExecutor.executeQueuedCommands();
        handleRenderCompleted();
    }

    public void renderChanges() {
        EnumSet<ChangeType> changes = changeTracker.getChanges();

        int viewportTopmostContentChangedLine =
                Math.max(viewport.getTopLineNumber(), changeTracker.getTopmostContentChangedLineNumber());

        if (changes.contains(ChangeType.VIEWPORT_LINE_NUMBER)) {

            lineNumberRenderer.render();

            viewportRenderer.renderViewportLineNumbersChanged(changeTracker.getViewportLineNumberChangedEdges());
        }

        if (changes.contains(ChangeType.VIEWPORT_CONTENT)) {

            viewportRenderer.renderViewportContentChange(viewportTopmostContentChangedLine,
                                                         changeTracker.getViewportRemovedLines());

            if (changeTracker.hadContentChangeThatUpdatesFollowingLines()) {

                lineNumberRenderer.renderLineAndFollowing(viewportTopmostContentChangedLine);
            }
        }

        if (changes.contains(ChangeType.VIEWPORT_SHIFT)) {

            viewportRenderer.renderViewportShift(false);
            lineNumberRenderer.render();
        }

        if (changes.contains(ChangeType.DIRTY_LINE)) {

            viewportRenderer.renderDirtyLines(changeTracker.getDirtyLines());
        }

        renderTimeExecutor.executeQueuedCommands();

        handleRenderCompleted();

    }

    private void handleRenderCompleted() {
        viewportRenderer.handleRenderCompleted();
        completionListenerManager.dispatch(renderCompletedDispatcher);
    }

    public void requestRenderLine(Line line) {
        changeTracker.requestRenderLine(line);
    }

    public void teardown() {
        changeTracker.teardown();
        viewportRenderer.teardown();
        lineNumberRenderer.teardown();
    }

    public LineNumberRenderer getLineNumberRenderer() {
        return lineNumberRenderer;
    }
}
