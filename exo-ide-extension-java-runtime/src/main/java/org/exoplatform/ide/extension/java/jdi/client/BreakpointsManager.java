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
package org.exoplatform.ide.extension.java.jdi.client;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import org.exoplatform.ide.editor.marking.EditorLineNumberContextMenuEvent;
import org.exoplatform.ide.editor.marking.EditorLineNumberContextMenuHandler;
import org.exoplatform.ide.editor.marking.EditorLineNumberDoubleClickEvent;
import org.exoplatform.ide.editor.marking.EditorLineNumberDoubleClickHandler;
import org.exoplatform.ide.editor.marking.Markable;
import org.exoplatform.ide.editor.marking.Marker;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler;
import org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolverFactory;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Location;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 4:36:51 PM Mar 26, 2012 evgen $
 * 
 */
public class BreakpointsManager implements EditorActiveFileChangedHandler, EditorLineNumberDoubleClickHandler,
   DebuggerConnectedHandler, DebuggerDisconnectedHandler, EditorFileOpenedHandler, BreakPointsUpdatedHandler,
   EditorLineNumberContextMenuHandler
{

   private HandlerManager eventBus;

   private Markable markable;

   private DebuggerClientService service;

   private DebuggerInfo debuggerInfo;

   private final DebuggerAutoBeanFactory autoBeanFactory;

   private FileModel file;

   private Map<String, Set<EditorBreakPoint>> breakPoints;

   private Map<String, FileModel> fileWithBreakPoints;

   private final FqnResolverFactory resolverFactory;

   private HandlerRegistration lineNumberDobleClickHandler;

   private HandlerRegistration lineNumberContextMenuHandler;

   /**
    * @param eventBus
    */
   public BreakpointsManager(HandlerManager eventBus, DebuggerClientService service,
      DebuggerAutoBeanFactory autoBeanFactory, FqnResolverFactory resolverFactory)
   {
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

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (event.getFile() == null)
         return;

      if (resolverFactory.isResolverExist(event.getFile().getMimeType()))
      {
         if (event.getEditor() instanceof Markable)
         {
            if (lineNumberDobleClickHandler != null)
               lineNumberDobleClickHandler.removeHandler();
            markable = (Markable)event.getEditor();
            lineNumberDobleClickHandler = markable.addLineNumberDoubleClickHandler(this);
            file = event.getFile();
            if (debuggerInfo == null)
            {
               if (breakPoints.containsKey(event.getFile().getId()))
               {
                  for (EditorBreakPoint b : breakPoints.get(event.getFile().getId()))
                  {
                     markable.unmarkProblem(b);
                  }
                  breakPoints.get(event.getFile().getId()).clear();
               }
            }
         }
      }
   }

   private void addProblem(BreakPoint breakPoint)
   {
      EditorBreakPoint problem = new EditorBreakPoint(breakPoint, "Breakpoint");
      markable.markProblem(problem);
      if (!breakPoints.containsKey(file.getId()))
         breakPoints.put(file.getId(), new HashSet<EditorBreakPoint>());
      breakPoints.get(file.getId()).add(problem);

      fileWithBreakPoints.put(resolverFactory.getResolver(file.getMimeType()).resolveFqn(file), file);

      eventBus.fireEvent(new BreakPointsUpdatedEvent(breakPoints));
   }

   public void markCurrentBreakPoint(Marker problem)
   {
      if (problem != null)
         markable.markProblem(problem);
   }

   public void unmarkCurrentBreakPoint(Marker problem)
   {
      if (problem != null)
         markable.unmarkProblem(problem);
   }

   /**
    * @see org.exoplatform.ide.editor.problem.LineNumberDoubleClickHandler#onLineNumberDoubleClick(org.exoplatform.ide.editor.problem.LineNumberDoubleClickEvent)
    */
   @Override
   public void onEditorLineNumberDoubleClick(final EditorLineNumberDoubleClickEvent event)
   {
      if (debuggerInfo == null)
         return;

      EditorBreakPoint breakPoint = isBreakpointExist(event.getLineNumber());
      if (breakPoint != null)
         removeBreakpoint(breakPoint);
      else
         addBreakpoint(event.getLineNumber());
   }

   /**
    * @param lineNumber
    * @return
    */
   private EditorBreakPoint isBreakpointExist(int lineNumber)
   {
      if (!breakPoints.containsKey(file.getId()))
         return null;

      for (EditorBreakPoint p : breakPoints.get(file.getId()))
      {
         if (p.getLineNumber() == lineNumber)
            return p;
      }
      return null;
   }

   private void removeBreakpoint(final EditorBreakPoint breakPoint)
   {
      try
      {
         markable.unmarkProblem(breakPoint);
         service.deleteBreakPoint(debuggerInfo.getId(), breakPoint.getBreakPoint(),
            new AsyncRequestCallback<BreakPoint>()
            {

               @Override
               protected void onSuccess(BreakPoint result)
               {
                  breakPoints.get(file.getId()).remove(breakPoint);
                  eventBus.fireEvent(new BreakPointsUpdatedEvent(breakPoints));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  if (exception instanceof ServerException)
                  {
                     ServerException e = (ServerException)exception;
                     if (e.isErrorMessageProvided())
                     {
                        eventBus.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
                        return;
                     }
                  }

                  eventBus.fireEvent(new OutputEvent("Can't delete breakpoint at " + breakPoint.getLineNumber(),
                     Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @param event
    */
   private void addBreakpoint(final int lineNumber)
   {
      AutoBean<BreakPoint> autoBean = autoBeanFactory.create(BreakPoint.class);
      AutoBean<Location> autoBeanlocation = autoBeanFactory.create(Location.class);
      final BreakPoint point = autoBean.as();

      final Location location = autoBeanlocation.as();
      location.setLineNumber(lineNumber);
      location.setClassName(resolverFactory.getResolver(file.getMimeType()).resolveFqn(file));
      point.setLocation(location);
      point.setEnabled(true);
      try
      {
         service.addBreakPoint(debuggerInfo.getId(), point, new AsyncRequestCallback<BreakPoint>()
         {

            @Override
            protected void onSuccess(BreakPoint result)
            {
               addProblem(point);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               if (exception instanceof ServerException)
               {
                  ServerException e = (ServerException)exception;
                  if (e.isErrorMessageProvided())
                  {
                     eventBus.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
                     return;
                  }
               }
               eventBus.fireEvent(new OutputEvent("Can't add breakpoint at " + lineNumber, Type.WARNING));
            }
         });
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedHandler#onDebuggerConnected(org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent)
    */
   @Override
   public void onDebuggerConnected(DebuggerConnectedEvent event)
   {
      debuggerInfo = event.getDebuggerInfo();
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedHandler#onDebuggerDisconnected(org.exoplatform.ide.extension.java.jdi.client.events.DebuggerDisconnectedEvent)
    */
   @Override
   public void onDebuggerDisconnected(DebuggerDisconnectedEvent event)
   {
      debuggerInfo = null;
      if (breakPoints.containsKey(file.getId()))
      {
         for (EditorBreakPoint p : breakPoints.get(file.getId()))
         {
            markable.unmarkProblem(p);
         }
         breakPoints.get(file.getId()).clear();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      if (breakPoints.containsKey(event.getFile().getId()))
      {
         if (debuggerInfo == null)
         {
            breakPoints.get(event.getFile().getId()).clear();
            return;
         }
         if (event.getEditor() instanceof Markable)
         {
            Markable m = (Markable)event.getEditor();
            for (EditorBreakPoint p : breakPoints.get(event.getFile().getId()))
            {
               m.markProblem(p);
            }
         }
      }
   }

   public Map<String, FileModel> getFileWithBreakPoints()
   {
      return fileWithBreakPoints;
   }

   /**
    * @see org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedHandler#onBreakPointsUpdated(org.exoplatform.ide.extension.java.jdi.client.events.BreakPointsUpdatedEvent)
    */
   @Override
   public void onBreakPointsUpdated(BreakPointsUpdatedEvent event)
   {
      if (event.getBreakPoints().isEmpty())
      {
         if (breakPoints.containsKey(file.getId()))
         {
            for (EditorBreakPoint p : breakPoints.get(file.getId()))
            {
               markable.unmarkProblem(p);
            }
            breakPoints.get(file.getId()).clear();
         }
      }
   }

   /**
    * @see org.exoplatform.ide.editor.problem.LineNumberContextMenuHandler#onLineNumberContextMenu(org.exoplatform.ide.editor.problem.LineNumberContextMenuEvent)
    */
   @Override
   public void onEditorLineNumberContextMenu(EditorLineNumberContextMenuEvent event)
   {
      if (debuggerInfo == null)
         return;

      EditorBreakPoint breakPoint = isBreakpointExist(event.getLineNumber());
      if (breakPoint != null)
      {
         IDE.fireEvent(new ShowContextMenuEvent(event.getX(), event.getY(), breakPoint));
      }
   }

}
