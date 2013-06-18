/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.debug;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
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
    private JsonStringMap<JsonArray<Breakpoint>> breakPoints;
    private EditorAgent                          editorAgent;
    private DebuggerManager                      debuggerManager;
    private ConsolePart                          console;
    private LineNumberRenderer                   renderer;
    private DebugLineRenderer                    debugLineRenderer;
    private Breakpoint                           markedBreakPoint;

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
        this.breakPoints = JsonCollections.createStringMap();
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
        final JsonArray<Breakpoint> breakPoints = this.breakPoints.get(activeFile.getId());
        final Debugger debugger = debuggerManager.getDebugger();

        if (debugger != null) {
            try {
                if (breakPoints != null && !breakPoints.isEmpty()) {
                    for (int i = 0; i < breakPoints.size(); i++) {
                        Breakpoint breakpoint = breakPoints.get(i);

                        if (breakpoint.getLineNumber() == lineNumber) {
                            final int index = i;

                            debugger.deleteBreakPoint(activeFile, lineNumber, new AsyncCallback<Void>() {
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

                debugger.addBreakPoint(activeFile, lineNumber, new AsyncCallback<Breakpoint>() {
                    @Override
                    public void onSuccess(Breakpoint result) {
                        if (breakPoints != null) {
                            breakPoints.add(result);
                        } else {
                            BreakpointGutterManager.this.breakPoints.put(activeFile.getId(),
                                                                         JsonCollections.<Breakpoint>createArray(result));
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
        final File activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        final String activeFileId = activeFile.getId();

        breakPoints.iterate(new JsonStringMap.IterationCallback<JsonArray<Breakpoint>>() {
            @Override
            public void onIteration(String key, JsonArray<Breakpoint> value) {
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
        JsonArray<Breakpoint> breakPoints = this.breakPoints.get(activeFile.getId());
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
    public JsonArray<Breakpoint> getBreakPoints() {
        final JsonArray<Breakpoint> points = JsonCollections.createArray();

        breakPoints.iterate(new JsonStringMap.IterationCallback<JsonArray<Breakpoint>>() {
            @Override
            public void onIteration(String key, JsonArray<Breakpoint> value) {
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