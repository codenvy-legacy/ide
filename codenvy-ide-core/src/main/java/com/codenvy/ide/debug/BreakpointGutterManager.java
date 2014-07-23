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
package com.codenvy.ide.debug;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.renderer.DebugLineRenderer;
import com.codenvy.ide.texteditor.renderer.LineNumberRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The manager provides to manege breakpoints and shows it into gutter.
 *
 * @author Evgen Vidolob
 */
@Singleton
public class BreakpointGutterManager {
    private StringMap<Array<Breakpoint>> breakpoints;
    private EditorAgent                  editorAgent;
    private DebuggerManager              debuggerManager;
    private ConsolePart                  console;
    private Breakpoint                   currentBreakpoint;

    /**
     * Create manager.
     *
     * @param editorAgent
     * @param debuggerManager
     * @param console
     */
    @Inject
    protected BreakpointGutterManager(EditorAgent editorAgent, DebuggerManager debuggerManager, ConsolePart console, EventBus eventBus) {
        this.editorAgent = editorAgent;
        this.breakpoints = Collections.createStringMap();
        this.debuggerManager = debuggerManager;
        this.console = console;
    }

    /**
     * Change state of the breakpoint in active editor at the specified line.
     *
     * @param lineNumber
     *         active editor's line number where breakpoint is
     */
    public void changeBreakPointState(final int lineNumber) {
        final Debugger debugger = debuggerManager.getDebugger();
        if (debugger == null) {
            return;
        }

        final ItemReference activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        final LineNumberRenderer renderer = getRendererForFile(activeFile);
        final Array<Breakpoint> breakPoints = this.breakpoints.get(activeFile.getPath());
        if (breakPoints != null && !breakPoints.isEmpty()) {
            for (int i = 0; i < breakPoints.size(); i++) {
                Breakpoint breakpoint = breakPoints.get(i);

                if (breakpoint.getLineNumber() == lineNumber) {
                    final int index = i;

                    debugger.deleteBreakpoint(activeFile, lineNumber, new AsyncCallback<Void>() {
                        @Override
                        public void onSuccess(Void result) {
                            breakPoints.remove(index);
                            renderer.fillOrUpdateLines(lineNumber, lineNumber);
                        }

                        @Override
                        public void onFailure(Throwable exception) {
                            if (exception instanceof ServerException) {
                                ServerException e = (ServerException)exception;
                                if (e.isErrorMessageProvided()) {
                                    console.print(e.getMessage());
                                    return;
                                }
                            }
                            console.print("Can't delete breakpoint at line " + (lineNumber + 1));
                        }
                    });
                    return;
                }
            }
        }

        debugger.addBreakpoint(activeFile, lineNumber, new AsyncCallback<Breakpoint>() {
            @Override
            public void onSuccess(Breakpoint result) {
                if (breakPoints != null) {
                    breakPoints.add(result);
                } else {
                    BreakpointGutterManager.this.breakpoints.put(activeFile.getPath(), Collections.createArray(result));
                }
                renderer.fillOrUpdateLines(lineNumber, lineNumber);
            }

            @Override
            public void onFailure(Throwable exception) {
                if (exception instanceof ServerException) {
                    ServerException e = (ServerException)exception;
                    if (e.isErrorMessageProvided()) {
                        console.print(e.getMessage());
                        return;
                    }
                }
                console.print("Can't add breakpoint at line " + (lineNumber + 1));
            }
        });
    }

    /** Remove all breakpoints. */
    public void removeAllBreakPoints() {
        breakpoints.iterate(new StringMap.IterationCallback<Array<Breakpoint>>() {
            @Override
            public void onIteration(String key, Array<Breakpoint> value) {
                Array<Breakpoint> removedBreakpoints = breakpoints.remove(key);
                for (Breakpoint breakpoint : removedBreakpoints.asIterable()) {
                    LineNumberRenderer renderer = getRendererForFile(breakpoint.getFile());
                    if (renderer != null) {
                        renderer.fillOrUpdateLines(breakpoint.getLineNumber(), breakpoint.getLineNumber());
                    }
                }
            }
        });
    }

    /**
     * Check whether breakpoint in this line exist.
     *
     * @param lineNumber
     *         line where breakpoint is
     * @return <code>true</code> if the breakpoint exist, and <code>false</code> otherwise
     */
    public boolean isBreakPointExist(int lineNumber) {
        ItemReference activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        Array<Breakpoint> breakPoints = this.breakpoints.get(activeFile.getPath());
        if (breakPoints != null) {
            for (int i = 0; i < breakPoints.size(); i++) {
                Breakpoint breakpoint = breakPoints.get(i);
                if (breakpoint.getLineNumber() == lineNumber) {
                    return true;
                }
            }
        }
        return false;
    }

    /** @return all breakpoints. */
    public Array<Breakpoint> getBreakpoints() {
        final Array<Breakpoint> breakpoints = Collections.createArray();
        this.breakpoints.iterate(new StringMap.IterationCallback<Array<Breakpoint>>() {
            @Override
            public void onIteration(String key, Array<Breakpoint> value) {
                breakpoints.addAll(value);
            }
        });
        return breakpoints;
    }

    /**
     * Mark current line.
     *
     * @param lineNumber
     *         line which need to mark
     */
    public void markCurrentBreakpoint(int lineNumber) {
        int oldLineNumber = 0;
        if (currentBreakpoint != null) {
            oldLineNumber = currentBreakpoint.getLineNumber();
        }

        ItemReference activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        LineNumberRenderer renderer = getRendererForFile(activeFile);
        currentBreakpoint = new Breakpoint(Breakpoint.Type.CURRENT, lineNumber, activeFile.getPath(), activeFile);

        renderer.fillOrUpdateLines(oldLineNumber, oldLineNumber);
        renderer.fillOrUpdateLines(lineNumber, lineNumber);
        DebugLineRenderer debugLineRenderer = getDebugLineRenderer();
        if (debugLineRenderer != null)
            debugLineRenderer.showLine(lineNumber);
    }


    private DebugLineRenderer getDebugLineRenderer() {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
        if (activeEditor instanceof CodenvyTextEditor) {
            TextEditorPartView view = ((CodenvyTextEditor)activeEditor).getView();
            if (view instanceof TextEditorViewImpl) {
                return ((TextEditorViewImpl)view).getDebugLineRenderer();
            }
        }
        return null;
    }

    /** Unmark current line. */
    public void unmarkCurrentBreakpoint() {
        if (currentBreakpoint != null) {
            final int oldLineNumber = currentBreakpoint.getLineNumber();
            LineNumberRenderer r = getRendererForFile(currentBreakpoint.getFile());
            currentBreakpoint = null;
            r.fillOrUpdateLines(oldLineNumber, oldLineNumber);
            DebugLineRenderer debugLineRenderer = getDebugLineRenderer();
            if (debugLineRenderer != null)
                debugLineRenderer.disableLine();
        }
    }

    /**
     * Check whether line is marked.
     *
     * @param lineNumber
     *         line which need to check
     * @return <code>true</code> if the line is marked, and <code>false</code> otherwise
     */
    public boolean isMarkedLine(int lineNumber) {
        if (currentBreakpoint != null) {
            ItemReference activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
            boolean isFileWithMarkBreakPoint = activeFile.getPath().equals(currentBreakpoint.getPath());
            boolean isCurrentLine = lineNumber == currentBreakpoint.getLineNumber();
            return isFileWithMarkBreakPoint && isCurrentLine;
        }
        return false;
    }

    private LineNumberRenderer getRendererForFile(ItemReference file) {
        StringMap<EditorPartPresenter> openedEditors = editorAgent.getOpenedEditors();
        for (String key : openedEditors.getKeys().asIterable()) {
            EditorPartPresenter editor = openedEditors.get(key);
            if (file.getPath().equals(editor.getEditorInput().getFile().getPath())) {
                if (editor instanceof CodenvyTextEditor) {
                    TextEditorPartView view = ((CodenvyTextEditor)editor).getView();
                    if (view instanceof TextEditorViewImpl) {
                        return ((TextEditorViewImpl)view).getRenderer().getLineNumberRenderer();
                    }
                }
            }
        }
        return null;
    }
}