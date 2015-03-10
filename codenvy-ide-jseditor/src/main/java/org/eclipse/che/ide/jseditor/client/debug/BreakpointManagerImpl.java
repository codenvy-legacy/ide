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
package org.eclipse.che.ide.jseditor.client.debug;

import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.parts.ConsolePart;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.commons.exception.ServerException;
import org.eclipse.che.ide.debug.Breakpoint;
import org.eclipse.che.ide.debug.BreakpointManager;
import org.eclipse.che.ide.debug.BreakpointRenderer;
import org.eclipse.che.ide.debug.BreakpointRenderer.LineChangeAction;
import org.eclipse.che.ide.debug.Debugger;
import org.eclipse.che.ide.debug.DebuggerManager;
import org.eclipse.che.ide.debug.HasBreakpointRenderer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

/** Implementation of {@link BreakpointManager} for jseditor. */
public class BreakpointManagerImpl implements BreakpointManager, LineChangeAction {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(BreakpointManagerImpl.class.getName());

    private final Map<String, List<Breakpoint>> breakpoints;
    private final EditorAgent                   editorAgent;
    private final DebuggerManager               debuggerManager;
    private final ConsolePart                   console;

    private Breakpoint                          currentBreakpoint;

    @Inject
    public BreakpointManagerImpl(final EditorAgent editorAgent,
                                 final DebuggerManager debuggerManager,
                                 final ConsolePart console,
                                 final EventBus eventBus) {
        this.editorAgent = editorAgent;
        this.breakpoints = new HashMap<>();
        this.debuggerManager = debuggerManager;
        this.console = console;
    }

    @Override
    public void changeBreakPointState(final int lineNumber) {
        final Debugger debugger = debuggerManager.getDebugger();
        if (debugger == null) {
            return;
        }

        final VirtualFile activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        final BreakpointRenderer breakpointRenderer = getBreakpointRendererForFile(activeFile);
        if (breakpointRenderer == null) {
            return;
        }

        final List<Breakpoint> breakpointsForPath = this.breakpoints.get(activeFile.getPath());
        if (breakpointsForPath != null) {
            for (final Breakpoint breakpoint: breakpointsForPath) {

                if (breakpoint.getLineNumber() == lineNumber) {
                    LOG.fine("Attempt to remove breakpoint on line " + lineNumber);
                    debugger.deleteBreakpoint(activeFile, lineNumber, new AsyncCallback<Void>() {
                        @Override
                        public void onSuccess(final Void result) {
                            breakpointsForPath.remove(breakpoint);
                            breakpointRenderer.removeBreakpointMark(lineNumber);
                        }

                        @Override
                        public void onFailure(final Throwable exception) {
                            if (exception instanceof ServerException) {
                                final ServerException e = (ServerException)exception;
                                if (e.isErrorMessageProvided()) {
                                    BreakpointManagerImpl.this.console.print(e.getMessage());
                                    return;
                                }
                            }
                            BreakpointManagerImpl.this.console.print("Can't delete breakpoint at line " + (lineNumber + 1));
                        }
                    });
                    LOG.fine("Breakpoint removed.");
                    if (breakpointsForPath.isEmpty()) {
                        breakpoints.remove(activeFile.getPath());
                    }
                    return;
                }
            }
        }

        debugger.addBreakpoint(activeFile, lineNumber, new AsyncCallback<Breakpoint>() {
            @Override
            public void onSuccess(final Breakpoint result) {
                if (breakpointsForPath != null) {
                    breakpointsForPath.add(result);
                } else {
                    final List<Breakpoint> newList = new ArrayList<>();
                    newList.add(result);
                    BreakpointManagerImpl.this.breakpoints.put(activeFile.getPath(), newList);
                }
                breakpointRenderer.addBreakpointMark(lineNumber, BreakpointManagerImpl.this);
            }

            @Override
            public void onFailure(final Throwable exception) {
                if (exception instanceof ServerException) {
                    final ServerException e = (ServerException)exception;
                    if (e.isErrorMessageProvided()) {
                        BreakpointManagerImpl.this.console.print(e.getMessage());
                        return;
                    }
                }
                BreakpointManagerImpl.this.console.print("Can't add breakpoint at line " + (lineNumber + 1));
            }
        });
    }

    @Override
    public void removeAllBreakpoints() {
        LOG.fine("Remove all breakpoints");
        for (final Entry<String, List<Breakpoint>> entry: this.breakpoints.entrySet()) {
            final String path = entry.getKey();
            final List<Breakpoint> pathBreakpoints = entry.getValue();
            
            removeBreakpointsForPath(path, pathBreakpoints);
        }
        this.breakpoints.clear();
    }

    private void removeBreakpointsForPath(final String path, final List<Breakpoint> pathBreakpoints) {
        LOG.fine("\tRemove all breakpoints for path " + path);
        EditorPartPresenter editor = null;
        for (final Breakpoint breakpoint: pathBreakpoints) {
            EditorPartPresenter editorForBreakpoint;
            if (editor == null) {
                editorForBreakpoint = getEditorForFile(breakpoint.getFile());
                if (editorForBreakpoint == null) {
                    // we won't be able to have an editor for any other breakpoint
                    return;
                }
                editor = editorForBreakpoint;
            } else {
                editorForBreakpoint = editor;
            }
            final BreakpointRenderer breakpointRenderer = getBreakpointRendererForEditor(editorForBreakpoint);
            if (breakpointRenderer == null) {
                // the renderer will also be null for the other breakpoints for the same path
                return;
            }
            breakpointRenderer.removeBreakpointMark(breakpoint.getLineNumber());
        }
    }

    @Override
    @Deprecated
    public boolean isBreakPointExist(int lineNumber) {
        return breakpointExists(lineNumber);
    }

    @Override
    public boolean breakpointExists(final int lineNumber) {
        if (editorAgent.getActiveEditor() == null) {
            return false;
        }

        final VirtualFile activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        final List<Breakpoint> breakPoints = this.breakpoints.get(activeFile.getPath());
        if (breakPoints != null) {
            for (final Breakpoint breakpoint : breakPoints) {
                if (breakpoint.getLineNumber() == lineNumber) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<Breakpoint> getBreakpointList() {
        final List<Breakpoint> result = new ArrayList<>();
        for (final List<Breakpoint> fileBreakpoints: this.breakpoints.values()) {
            result.addAll(fileBreakpoints);
        }
        return result;
    }

    @Override
    @Deprecated
    public Array<Breakpoint> getBreakpoints() {
        return org.eclipse.che.ide.collections.Collections.createArray(getBreakpointList());
    }

    @Override
    public void markCurrentBreakpoint(int lineNumber) {
        unmarkCurrentBreakpoint();
        LOG.fine("Mark current breakpoint on line " + lineNumber);

        final VirtualFile activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
        this.currentBreakpoint = new Breakpoint(Breakpoint.Type.CURRENT, lineNumber, activeFile.getPath(), activeFile);

        final BreakpointRenderer breakpointRenderer = getBreakpointRendererForFile(activeFile);
        if (breakpointRenderer != null) {
            if (breakpointExists(lineNumber)) {
                breakpointRenderer.setBreakpointActive(lineNumber, true);
            }
            breakpointRenderer.setLineActive(lineNumber, true);
        }
    }

    @Override
    public void unmarkCurrentBreakpoint() {
        if (this.currentBreakpoint != null) {
            final int oldLineNumber = this.currentBreakpoint.getLineNumber();
            LOG.fine("Unmark current breakpoint on line " + oldLineNumber);
            final BreakpointRenderer breakpointRenderer = getBreakpointRendererForFile(this.currentBreakpoint.getFile());
            this.currentBreakpoint = null;
            if (breakpointRenderer != null) {
                if (breakpointExists(oldLineNumber)) {
                    breakpointRenderer.setBreakpointActive(oldLineNumber, false);
                }
                breakpointRenderer.setLineActive(oldLineNumber, false);
            }
        }
    }

    @Override
    public boolean isCurrentBreakpoint(int lineNumber) {
        if (this.currentBreakpoint != null) {
            final VirtualFile activeFile = editorAgent.getActiveEditor().getEditorInput().getFile();
            boolean isFileWithMarkBreakPoint = activeFile.getPath().equals(this.currentBreakpoint.getPath());
            boolean isCurrentLine = lineNumber == this.currentBreakpoint.getLineNumber();
            return isFileWithMarkBreakPoint && isCurrentLine;
        }
        return false;
    }

    @Override
    @Deprecated
    public boolean isMarkedLine(int lineNumber) {
        return isCurrentBreakpoint(lineNumber);
    }

    private EditorPartPresenter getEditorForFile(VirtualFile fileNode) {
        final StringMap<EditorPartPresenter> openedEditors = editorAgent.getOpenedEditors();
        for (final String key : openedEditors.getKeys().asIterable()) {
            final EditorPartPresenter editor = openedEditors.get(key);
            if (fileNode.getPath().equals(editor.getEditorInput().getFile().getPath())) {
                return editor;
            }
        }
        return null;
    }

    private BreakpointRenderer getBreakpointRendererForFile(VirtualFile fileNode) {
        final EditorPartPresenter editor = getEditorForFile(fileNode);
        if (editor != null) {
            return getBreakpointRendererForEditor(editor);
        } else {
            return null;
        }
    }

    private BreakpointRenderer getBreakpointRendererForEditor(final EditorPartPresenter editor) {
        if (editor instanceof HasBreakpointRenderer) {
            final BreakpointRenderer renderer = ((HasBreakpointRenderer)editor).getBreakpointRenderer();
            if (renderer != null && renderer.isReady()) {
                return renderer;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    @Override
    public void onLineChange(final VirtualFile file, final int firstLine, final int linesAdded, final int linesRemoved) {
        final List<Breakpoint> fileBreakpoints = this.breakpoints.get(file.getPath());

        final int delta = linesAdded - linesRemoved;

        if (fileBreakpoints != null) {
            LOG.fine("Change in file with breakpoints " + file.getPath());

            final List<Breakpoint> toRemove = new ArrayList<>();
            final List<Breakpoint> toAdd = new ArrayList<>();

            for (final Breakpoint breakpoint: fileBreakpoints) {
                final int lineNumber = breakpoint.getLineNumber();
                if (lineNumber < firstLine) {
                    // we're before any change
                    continue;
                }
                if (lineNumber < firstLine + linesRemoved) {
                    // the line was removed
                    LOG.fine("Removing breakpoint " + breakpoint + " (removed line).");
                    toRemove.add(breakpoint);
                } else {
                    // all other lines are shifted by linesAdded - linesRemoved
                    LOG.fine("Moving breakpoint " + breakpoint + " delta=" + delta);
                    toRemove.add(breakpoint);
                    toAdd.add(new Breakpoint(breakpoint.getType(), breakpoint.getLineNumber() + delta,
                                             breakpoint.getPath(), breakpoint.getFile(), breakpoint.getMessage()));
                }
            }
            for (final Breakpoint breakpoint: toRemove) {
                changeBreakPointState(breakpoint.getLineNumber());
            }
            for (final Breakpoint breakpoint: toAdd) {
                changeBreakPointState(breakpoint.getLineNumber());
            }
        }
        if (this.currentBreakpoint != null && this.currentBreakpoint.getLineNumber() > firstLine) {
            // whatever happens, that can't be handled well
            // so i'll just unmark the current line without marking the new one as it doesn't make sense (the
            // current position is not synchronized with the debugger)
            unmarkCurrentBreakpoint();
        }
    }
}
