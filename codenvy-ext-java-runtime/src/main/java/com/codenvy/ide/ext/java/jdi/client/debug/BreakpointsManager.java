/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.ext.java.jdi.client.debug;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.commons.shared.ListenerRegistrar.Remover;
import com.codenvy.ide.ext.java.client.editor.Breakpoint;
import com.codenvy.ide.ext.java.jdi.client.fqn.FqnResolverFactory;
import com.codenvy.ide.ext.java.jdi.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.java.jdi.shared.BreakPoint;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:36:51 PM Mar 26, 2012 evgen $
 */
@Singleton
public class BreakpointsManager {
    private       EventBus                                   eventBus;
    private       DebuggerClientService                      service;
    private       ConsolePart                                console;
    private       DebuggerInfo                               debuggerInfo;
    private       File                                       file;
    private       JsonStringMap<JsonArray<EditorBreakPoint>> breakPoints;
    private       JsonStringMap<File>                        fileWithBreakPoints;
    private final FqnResolverFactory                         resolverFactory;
    private       Remover                                    breakpointClickRemover;
    //    private       BreakpointGutterManager                    gutterManager;
    private       HandlerRegistration                        lineNumberContextMenuHandler;


    @Inject
    protected BreakpointsManager(EventBus eventBus, DebuggerClientService service, FqnResolverFactory resolverFactory,
                                 ConsolePart console) {
        this.eventBus = eventBus;
        this.service = service;
        this.resolverFactory = resolverFactory;
        this.breakPoints = JsonCollections.createStringMap();
        this.fileWithBreakPoints = JsonCollections.createStringMap();
        this.console = console;
    }

    /**
     *
     */
//    @Override
//    public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event) {
//        if (event.getFile() == null)
//            return;
//
//        if (resolverFactory.isResolverExist(event.getFile().getMimeType())) {
//            if (event.getEditor() instanceof JavaEditor) {
//                if (breakpointClickRemover != null)
//                    breakpointClickRemover.remove();
//                JavaEditor editor = (JavaEditor)event.getEditor();
//                gutterManager = editor.getBreakPointManager();
//                breakpointClickRemover = gutterManager.addLineClickListener(this);
//                file = event.getFile();
//                if (debuggerInfo == null) {
//                    if (breakPoints.containsKey(event.getFile().getId())) {
//                        for (EditorBreakPoint b : breakPoints.get(event.getFile().getId())) {
//                            gutterManager.removeBreakpoint(b.getLineNumber());
//                        }
//                        breakPoints.get(event.getFile().getId()).clear();
//                    }
//                }
//            }
//        }
//    }
    private void addProblem(BreakPoint breakPoint) {
        EditorBreakPoint problem = new EditorBreakPoint(breakPoint, "Breakpoint");
        // TODO
        // gutterManager.setBreakpoint(problem);
        if (!breakPoints.containsKey(file.getId())) {
            breakPoints.put(file.getId(), JsonCollections.<EditorBreakPoint>createArray());
        }
        breakPoints.get(file.getId()).add(problem);
        fileWithBreakPoints.put(resolverFactory.getResolver(file.getMimeType()).resolveFqn(file), file);

        breakPointsUpdated(breakPoints);
    }

    public void markCurrentBreakPoint(Breakpoint bp) {
        if (bp != null) {
            // TODO
            // gutterManager.setCurrentDebugLine(bp);
        }
    }

    public void unmarkCurrentBreakPoint(Breakpoint bp) {
        if (bp != null) {
            // TODO
            // gutterManager.removeCurrentDebugLine(bp);
        }
    }

    private EditorBreakPoint isBreakpointExist(int lineNumber) {
        if (!breakPoints.containsKey(file.getId())) {
            return null;
        }

        JsonArray<EditorBreakPoint> points = breakPoints.get(file.getId());
        for (int i = 0; i < points.size(); i++) {
            EditorBreakPoint point = points.get(i);
            if (point.getLineNumber() == lineNumber) {
                return point;
            }
        }
        return null;
    }

    private void removeBreakpoint(final EditorBreakPoint breakPoint) {
        try {
            // TODO
            // gutterManager.removeBreakpoint(breakPoint.getLineNumber());
            service.deleteBreakPoint(debuggerInfo.getId(), breakPoint.getBreakPoint(), new AsyncRequestCallback<BreakPoint>() {
                @Override
                protected void onSuccess(BreakPoint result) {
                    breakPoints.get(file.getId()).remove(breakPoint);
                    breakPointsUpdated(breakPoints);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    if (exception instanceof ServerException) {
                        ServerException e = (ServerException)exception;
                        if (e.isErrorMessageProvided()) {
                            console.print(e.getMessage());
                            return;
                        }
                    }
                    console.print("Can't delete breakpoint at " + breakPoint.getLineNumber());
                }
            });
        } catch (RequestException e) {
            Log.error(BreakpointsManager.class, e);
        }
    }


    private void addBreakpoint(final int lineNumber) {
        DtoClientImpls.LocationImpl location = DtoClientImpls.LocationImpl.make();
        location.setLineNumber(lineNumber);
        location.setClassName(resolverFactory.getResolver(file.getMimeType()).resolveFqn(file));

        final DtoClientImpls.BreakPointImpl point = DtoClientImpls.BreakPointImpl.make();
        point.setLocation(location);
        point.setIsEnabled(true);

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
                            console.print(e.getMessage());
                            return;
                        }
                    }
                    console.print("Can't add breakpoint at " + lineNumber);
                }
            });
        } catch (RequestException e) {
            Log.error(BreakpointsManager.class, e);
        }
    }

    public void debuggerConnected(DebuggerInfo debuggerInfo) {
        this.debuggerInfo = debuggerInfo;
    }

    public void debuggerDisconnected() {
        debuggerInfo = null;
        if (file != null && breakPoints.containsKey(file.getId())) {
            JsonArray<EditorBreakPoint> points = breakPoints.get(file.getId());
            for (int i = 0; i < points.size(); i++) {
                EditorBreakPoint point = points.get(i);
                // TODO
                // gutterManager.removeBreakpoint(p.getLineNumber());
            }
            breakPoints.get(file.getId()).clear();
        }
    }


    public void editorFileOpened(File file) {
        if (breakPoints.containsKey(file.getId())) {
            if (debuggerInfo == null) {
                breakPoints.get(file.getId()).clear();
                return;
            }
            // TODO
//            if (event.getEditor() instanceof JavaEditor) {
//                JavaEditor m = (JavaEditor)event.getEditor();
//                for (EditorBreakPoint p : breakPoints.get(event.getFile().getId())) {
//                    m.getBreakPointManager().setBreakpoint(p);
//                }
//            }
        }
    }

    public JsonStringMap<File> getFileWithBreakPoints() {
        return fileWithBreakPoints;
    }

    public void breakPointsUpdated(JsonStringMap<JsonArray<EditorBreakPoint>> breakPoints) {
        if (breakPoints.isEmpty()) {
            if (this.breakPoints.containsKey(file.getId())) {
                JsonArray<EditorBreakPoint> points = this.breakPoints.get(file.getId());
                for (int i = 0; i < points.size(); i++) {
                    EditorBreakPoint point = points.get(i);
                    // gutterManager.removeBreakpoint(p.getLineNumber());
                }
                breakPoints.get(file.getId()).clear();
            }
        }
    }

//    public void onEditorLineNumberContextMenu(EditorLineNumberContextMenuEvent event) {
//        if (debuggerInfo == null)
//            return;
//
//        EditorBreakPoint breakPoint = isBreakpointExist(event.getLineNumber());
//        if (breakPoint != null) {
//            IDE.fireEvent(new ShowContextMenuEvent(event.getX(), event.getY(), breakPoint));
//        }
//    }


//    @Override
//    public void onClick(int y) {
//        if (debuggerInfo == null)
//            return;
//
//        EditorBreakPoint breakPoint = isBreakpointExist(y);
//        if (breakPoint != null)
//            removeBreakpoint(breakPoint);
//        else
//            addBreakpoint(y);
//    }
}