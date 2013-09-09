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
package org.exoplatform.ide.extension.java.jdi.client;

import com.codenvy.ide.commons.shared.ListenerRegistrar.Remover;
import com.google.collide.client.editor.gutter.Gutter.ClickListener;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.contextmenu.ShowContextMenuEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberContextMenuEvent;
import org.exoplatform.ide.editor.client.marking.EditorLineNumberContextMenuHandler;
import org.exoplatform.ide.editor.java.Breakpoint;
import org.exoplatform.ide.editor.java.BreakpointGutterManager;
import org.exoplatform.ide.editor.java.JavaEditor;
import org.exoplatform.ide.extension.java.jdi.client.events.*;
import org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolverFactory;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Location;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:36:51 PM Mar 26, 2012 evgen $
 */
public class BreakpointsManager implements EditorActiveFileChangedHandler, DebuggerConnectedHandler,
                                           DebuggerDisconnectedHandler, EditorFileOpenedHandler, BreakPointsUpdatedHandler,
                                           EditorLineNumberContextMenuHandler,
                                           ClickListener {

    private HandlerManager eventBus;

    private DebuggerClientService service;

    private DebuggerInfo debuggerInfo;

    private final DebuggerAutoBeanFactory autoBeanFactory;

    private FileModel file;

    private Map<String, Set<EditorBreakPoint>> breakPoints;

    private Map<String, FileModel> fileWithBreakPoints;

    private final FqnResolverFactory resolverFactory;

    private Remover breakpointClickRemover;

    private BreakpointGutterManager gutterManager;

    private HandlerRegistration lineNumberContextMenuHandler;

    /** @param eventBus */
    public BreakpointsManager(HandlerManager eventBus, DebuggerClientService service,
                              DebuggerAutoBeanFactory autoBeanFactory, FqnResolverFactory resolverFactory) {
        this.eventBus = eventBus;
        this.service = service;
        this.autoBeanFactory = autoBeanFactory;
        this.resolverFactory = resolverFactory;
        breakPoints = new HashMap<String, Set<EditorBreakPoint>>();
        fileWithBreakPoints = new HashMap<String, FileModel>();
        eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
        eventBus.addHandler(DebuggerConnectedEvent.TYPE, this);
        eventBus.addHandler(DebuggerDisconnectedEvent.TYPE, this);
        eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
        eventBus.addHandler(EditorLineNumberContextMenuEvent.TYPE, this);
        eventBus.addHandler(BreakPointsUpdatedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
        if (event.getFile() == null)
            return;

        if (resolverFactory.isResolverExist(event.getFile().getMimeType())) {
            if (event.getEditor() instanceof JavaEditor) {
                if (breakpointClickRemover != null)
                    breakpointClickRemover.remove();
                JavaEditor editor = (JavaEditor)event.getEditor();
                gutterManager = editor.getBreakPointManager();
                breakpointClickRemover = gutterManager.addLineClickListener(this);
                file = event.getFile();
                if (debuggerInfo == null) {
                    if (breakPoints.containsKey(event.getFile().getId())) {
                        for (EditorBreakPoint b : breakPoints.get(event.getFile().getId())) {
                            gutterManager.removeBreakpoint(b.getLineNumber());
                        }
                        breakPoints.get(event.getFile().getId()).clear();
                    }
                }
            }
        }
    }

    private void addProblem(BreakPoint breakPoint) {
        EditorBreakPoint problem = new EditorBreakPoint(breakPoint, "Breakpoint");
        gutterManager.setBreakpoint(problem);
        if (!breakPoints.containsKey(file.getId()))
            breakPoints.put(file.getId(), new HashSet<EditorBreakPoint>());
        breakPoints.get(file.getId()).add(problem);

        fileWithBreakPoints.put(resolverFactory.getResolver(file.getMimeType()).resolveFqn(file), file);

        eventBus.fireEvent(new BreakPointsUpdatedEvent(breakPoints));
    }

    public void markCurrentBreakPoint(Breakpoint bp) {
        if (bp != null)
            gutterManager.setCurrentDebugLine(bp);
    }

    public void unmarkCurrentBreakPoint(Breakpoint bp) {
        if (bp != null)
            gutterManager.removeCurrentDebugLine(bp);
    }

    /**
     * @param lineNumber
     * @return
     */
    private EditorBreakPoint isBreakpointExist(int lineNumber) {
        if (!breakPoints.containsKey(file.getId()))
            return null;

        for (EditorBreakPoint p : breakPoints.get(file.getId())) {
            if (p.getLineNumber() == lineNumber)
                return p;
        }
        return null;
    }

    private void removeBreakpoint(final EditorBreakPoint breakPoint) {
        try {
            gutterManager.removeBreakpoint(breakPoint.getLineNumber());
            service.deleteBreakPoint(debuggerInfo.getId(), breakPoint.getBreakPoint(),
                                     new AsyncRequestCallback<BreakPoint>() {

                                         @Override
                                         protected void onSuccess(BreakPoint result) {
                                             breakPoints.get(file.getId()).remove(breakPoint);
                                             eventBus.fireEvent(new BreakPointsUpdatedEvent(breakPoints));
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             if (exception instanceof ServerException) {
                                                 ServerException e = (ServerException)exception;
                                                 if (e.isErrorMessageProvided()) {
                                                     eventBus.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
                                                     return;
                                                 }
                                             }

                                             eventBus.fireEvent(new OutputEvent("Can't delete breakpoint at " + breakPoint.getLineNumber(),
                                                                                Type.ERROR));
                                         }
                                     });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /** @param event */
    private void addBreakpoint(final int lineNumber) {
        AutoBean<BreakPoint> autoBean = autoBeanFactory.create(BreakPoint.class);
        AutoBean<Location> autoBeanlocation = autoBeanFactory.create(Location.class);
        final BreakPoint point = autoBean.as();

        final Location location = autoBeanlocation.as();
        location.setLineNumber(lineNumber);
        location.setClassName(resolverFactory.getResolver(file.getMimeType()).resolveFqn(file));
        point.setLocation(location);
        point.setEnabled(true);
        try {
            service.addBreakPoint(debuggerInfo.getId(), point, new AsyncRequestCallback<BreakPoint>() {

                @Override
                protected void onSuccess(BreakPoint result) {
                    addProblem(point);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException e = (ServerException)exception;
                        if (e.isErrorMessageProvided()) {
                            eventBus.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
                            return;
                        }
                    }
                    eventBus.fireEvent(new OutputEvent("Can't add breakpoint at " + lineNumber, Type.WARNING));
                }
            });
        } catch (RequestException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler#onDebuggerConnected(org.exoplatform.ide
     * .extension.java.jdi.client.events.DebuggerConnectedEvent) */
    @Override
    public void onDebuggerConnected(DebuggerConnectedEvent event) {
        debuggerInfo = event.getDebuggerInfo();
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler#onDebuggerDisconnected(org.exoplatform.ide
     * .extension.java.jdi.client.events.DebuggerDisconnectedEvent) */
    @Override
    public void onDebuggerDisconnected(DebuggerDisconnectedEvent event) {
        debuggerInfo = null;
        if (file != null && breakPoints.containsKey(file.getId())) {
            for (EditorBreakPoint p : breakPoints.get(file.getId())) {
                gutterManager.removeBreakpoint(p.getLineNumber());
            }
            breakPoints.get(file.getId()).clear();
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client
     * .framework.editor.event.EditorFileOpenedEvent) */
    @Override
    public void onEditorFileOpened(EditorFileOpenedEvent event) {
        if (breakPoints.containsKey(event.getFile().getId())) {
            if (debuggerInfo == null) {
                breakPoints.get(event.getFile().getId()).clear();
                return;
            }
            if (event.getEditor() instanceof JavaEditor) {
                JavaEditor m = (JavaEditor)event.getEditor();
                for (EditorBreakPoint p : breakPoints.get(event.getFile().getId())) {
                    m.getBreakPointManager().setBreakpoint(p);
                }
            }
        }
    }

    public Map<String, FileModel> getFileWithBreakPoints() {
        return fileWithBreakPoints;
    }

    /** @see org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedHandler#onBreakPointsUpdated(org.exoplatform.ide
     * .extension.java.jdi.client.events.BreakPointsUpdatedEvent) */
    @Override
    public void onBreakPointsUpdated(BreakPointsUpdatedEvent event) {
        if (event.getBreakPoints().isEmpty()) {
            if (breakPoints.containsKey(file.getId())) {
                for (EditorBreakPoint p : breakPoints.get(file.getId())) {
                    gutterManager.removeBreakpoint(p.getLineNumber());
                }
                breakPoints.get(file.getId()).clear();
            }
        }
    }

    /** @see org.exoplatform.ide.editor.problem.LineNumberContextMenuHandler#onLineNumberContextMenu(org.exoplatform.ide.editor.problem
     * .LineNumberContextMenuEvent) */
    @Override
    public void onEditorLineNumberContextMenu(EditorLineNumberContextMenuEvent event) {
        if (debuggerInfo == null)
            return;

        EditorBreakPoint breakPoint = isBreakpointExist(event.getLineNumber());
        if (breakPoint != null) {
            IDE.fireEvent(new ShowContextMenuEvent(event.getX(), event.getY(), breakPoint));
        }
    }

    /** @see com.google.collide.client.editor.gutter.Gutter.ClickListener#onClick(int) */
    @Override
    public void onClick(int y) {
        if (debuggerInfo == null)
            return;

        EditorBreakPoint breakPoint = isBreakpointExist(y);
        if (breakPoint != null)
            removeBreakpoint(breakPoint);
        else
            addBreakpoint(y);

    }

}
