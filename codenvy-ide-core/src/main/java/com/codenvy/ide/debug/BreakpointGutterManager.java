/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.debug;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.texteditor.renderer.DebugLineRenderer;
import com.codenvy.ide.texteditor.renderer.LineNumberRenderer;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The manager provides to manege breakpoints and shows it into gutter.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class BreakpointGutterManager {
    private StringMap<Array<Breakpoint>> breakPoints;
    private EditorAgent                  editorAgent;
    private DebuggerManager              debuggerManager;
    private ConsolePart                  console;
    private LineNumberRenderer           renderer;
    private DebugLineRenderer            debugLineRenderer;
    private Breakpoint                   markedBreakPoint;

    /**
     * Create manager.
     *
     * @param editorAgent
     * @param debuggerManager
     * @param console
     */
    @Inject
    protected BreakpointGutterManager(EditorAgent editorAgent, DebuggerManager debuggerManager, ConsolePart console) {
        this.editorAgent = editorAgent;
        this.breakPoints = Collections.createStringMap();
        this.debuggerManager = debuggerManager;
        this.console = console;
    }

    /**
     * Set render for place where breakpoints are shown.
     *
     * @param renderer
     */
    public void setBreakPointRenderer(LineNumberRenderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Set render for place where debug step are shown.
     *
     * @param debugLineRenderer
     */
    public void setDebugLineRenderer(DebugLineRenderer debugLineRenderer) {
        this.debugLineRenderer = debugLineRenderer;
    }

    /**
     * Change state of breakpoint.
     *
     * @param lineNumber
     *         line number where breakpoint is
     */
    public void changeBreakPoint(final int lineNumber) {
        final File activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        final Array<Breakpoint> breakPoints = this.breakPoints.get(activeFile.getId());
        final Debugger debugger = debuggerManager.getDebugger();

        if (debugger != null) {
            try {
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
                                    console.print("Can't delete breakpoint at " + (lineNumber + 1));
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
                            BreakpointGutterManager.this.breakPoints.put(activeFile.getId(), Collections.<Breakpoint>createArray(result));
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
                        console.print("Can't add breakpoint at " + (lineNumber + 1));
                    }
                });
            } catch (RequestException e) {
                Log.error(BreakpointGutterManager.class, e);
            }
        }
    }

    /** Remove all breakpoints. */
    public void removeAllBreakPoints() {
        EditorPartPresenter activeEditor = editorAgent.getActiveEditor();
        File activeFile = null;
        final String activeFileId;
        if (activeEditor != null) {
            activeFile = activeEditor.getEditorInput().getFile();
        }
        activeFileId = activeFile != null ? activeFile.getId() : null;

        breakPoints.iterate(new StringMap.IterationCallback<Array<Breakpoint>>() {
            @Override
            public void onIteration(String key, Array<Breakpoint> value) {
                breakPoints.remove(key);
                if (key.equals(activeFileId)) {
                    for (int i = 0; i < value.size(); i++) {
                        Breakpoint breakpoint = value.get(i);
                        int lineNumber = breakpoint.getLineNumber();
                        renderer.fillOrUpdateLines(lineNumber, lineNumber);
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
        File activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        Array<Breakpoint> breakPoints = this.breakPoints.get(activeFile.getId());
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
    public Array<Breakpoint> getBreakPoints() {
        final Array<Breakpoint> points = Collections.<Breakpoint>createArray();

        breakPoints.iterate(new StringMap.IterationCallback<Array<Breakpoint>>() {
            @Override
            public void onIteration(String key, Array<Breakpoint> value) {
                points.addAll(value);
            }
        });

        return points;
    }

    /**
     * Mark current line.
     *
     * @param lineNumber
     *         line which need to mark
     */
    public void markCurrentBreakPoint(int lineNumber) {
        int oldLIneNumber = 0;
        if (markedBreakPoint != null) {
            oldLIneNumber = markedBreakPoint.getLineNumber();
        }

        File activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        markedBreakPoint = new Breakpoint(Breakpoint.Type.BREAKPOINT, lineNumber, activeFile.getPath());

        renderer.fillOrUpdateLines(oldLIneNumber, oldLIneNumber);
        renderer.fillOrUpdateLines(lineNumber, lineNumber);
        debugLineRenderer.showLine(lineNumber);
    }

    /** Unmark current line. */
    public void unmarkCurrentBreakPoint() {
        if (markedBreakPoint != null) {
            int oldLIneNumber = markedBreakPoint.getLineNumber();
            markedBreakPoint = null;

            renderer.fillOrUpdateLines(oldLIneNumber, oldLIneNumber);
            debugLineRenderer.disableLine();
        }
    }

    /**
     * Check whether current line is marked.
     *
     * @param lineNumber
     *         line which need to check
     * @return <code>true</code> if the line is marked, and <code>false</code> otherwise
     */
    public boolean isMarkedLine(int lineNumber) {
        if (markedBreakPoint != null) {
            File activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
            boolean isFileWithMarkBreakPoint = activeFile.getPath().equals(markedBreakPoint.getPath());
            boolean isCurrentLine = lineNumber == markedBreakPoint.getLineNumber();
            return isFileWithMarkBreakPoint && isCurrentLine;
        }
        return false;
    }
}