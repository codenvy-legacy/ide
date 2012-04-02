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
package org.exoplatform.ide.extension.java.jdi;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import com.google.gwt.event.shared.HandlerManager;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.googlecode.gwt.test.GwtTestWithMockito;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.editor.ckeditor.CKEditor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.problem.LineNumberDoubleClickEvent;
import org.exoplatform.ide.editor.problem.Problem;
import org.exoplatform.ide.extension.java.jdi.client.BreakpointsManager;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerAutoBeanFactory;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerClientService;
import org.exoplatform.ide.extension.java.jdi.client.events.DebuggerConnectedEvent;
import org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolver;
import org.exoplatform.ide.extension.java.jdi.client.fqn.FqnResolverFactory;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Location;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Property;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;

import java.util.Collections;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 10:34:14 AM Mar 27, 2012 evgen $
 * 
 */
public class BreakpointManagerTest extends GwtTestWithMockito
{

   @Mock
   private CodeMirror codeMirror;

   @Mock
   private CKEditor ckEditor;

   @Mock
   private DebuggerClientService service;

   @Mock
   private DebuggerAutoBeanFactory autoBeanFactory;

   @Mock
   private AutoBean<BreakPoint> autoBean;

   @Mock
   private BreakPoint breakPoint;

   @Mock
   private AutoBean<Location> locationAutoBean;

   @Mock
   private Location location;

   @Mock
   private DebuggerInfo debuggerInfo;

   @Mock
   private FqnResolverFactory fqnResolverFactory;
   
   @Mock
   private FqnResolver fqnResolver;

   private BreakpointsManager manager;

   @Spy
   private HandlerManager eventBus = new HandlerManager(null);

   @Before
   public void init()
   {
      manager = new BreakpointsManager(eventBus, service, autoBeanFactory, fqnResolverFactory);
   }

   /**
    * @see com.octo.gwt.test.GwtModuleRunnerAdapter#getModuleName()
    */
   @Override
   public String getModuleName()
   {
      return "org.exoplatform.ide.extension.java.jdi.DebuggerExtension";
   }

   @Test
   public void notAddHandlerIfNoFile() throws Exception
   {
      eventBus.fireEvent(new EditorActiveFileChangedEvent(null, null));
      verifyZeroInteractions(codeMirror);
   }

   @Test
   public void shouldNotAddHandlerIfeditorNotMarkable() throws Exception
   {
      FileModel file = new FileModel("", MimeType.APPLICATION_JAVA, "", new FolderModel());
      eventBus.fireEvent(new EditorActiveFileChangedEvent(file, ckEditor));
      verifyZeroInteractions(codeMirror);
   }

   @Test
   public void shouldAddHandlerIfFileIsJava() throws Exception
   {
      FileModel file = new FileModel("", MimeType.APPLICATION_JAVA, "", new FolderModel());
      when(fqnResolverFactory.getResolver(anyString())).thenReturn(fqnResolver);
      when(fqnResolverFactory.isResolverExist(anyString())).thenReturn(true);
      when(fqnResolver.resolveFqn(file)).thenReturn("dsfsdf");
      eventBus.fireEvent(new EditorActiveFileChangedEvent(file, codeMirror));
      verify(codeMirror).addLineNumberDoubleClickHandler(manager);
   }

   @Test
   public void shouldCallServiceIfLineNumberDClicked() throws Exception
   {
      initManager();

      manager.onLineNumberDoubleClick(new LineNumberDoubleClickEvent(2));
      verify(service, only()).addBreakPoint(anyString(), Mockito.<BreakPoint> any(),
         Mockito.<AsyncRequestCallback<BreakPoint>> any());
   }

   @Test
   public void shouldCalculateValidFQNForClass() throws Exception
   {
      initManager();

      manager.onLineNumberDoubleClick(new LineNumberDoubleClickEvent(2));
      ArgumentCaptor<String> fqnCaptor = ArgumentCaptor.forClass(String.class);
      verify(location).setClassName(fqnCaptor.capture());
      Assert.assertEquals("my.test.TestClass", fqnCaptor.getValue());

   }

   @Test
   public void shouldAddMarkToEditor() throws Exception
   {
      initManager();

      manager.onLineNumberDoubleClick(new LineNumberDoubleClickEvent(2));
      ArgumentCaptor<AsyncRequestCallback> asyncCaptor = ArgumentCaptor.forClass(AsyncRequestCallback.class);
      verify(service).addBreakPoint(anyString(), Mockito.<BreakPoint> any(), asyncCaptor.capture());
      when(breakPoint.getLocation()).thenReturn(location);
      when(location.getLineNumber()).thenReturn(2);
      GwtReflectionUtils.callPrivateMethod(asyncCaptor.getValue(), "onSuccess", breakPoint);
      ArgumentCaptor<Problem> problemCaptor = ArgumentCaptor.forClass(Problem.class);
      verify(codeMirror).markProblem(problemCaptor.capture());
      Assert.assertEquals(2, problemCaptor.getValue().getLineNumber());
   }

   @Test
   public void shouldFireOutputEventIfError() throws Exception
   {
      initManager();

      manager.onLineNumberDoubleClick(new LineNumberDoubleClickEvent(2));
      ArgumentCaptor<AsyncRequestCallback> asyncCaptor = ArgumentCaptor.forClass(AsyncRequestCallback.class);
      verify(service).addBreakPoint(anyString(), Mockito.<BreakPoint> any(), asyncCaptor.capture());
      GwtReflectionUtils.callPrivateMethod(asyncCaptor.getValue(), "onFailure", new Exception());
      ArgumentCaptor<OutputEvent> eventCaptor = ArgumentCaptor.forClass(OutputEvent.class);
      verify(eventBus, times(3)).fireEvent(eventCaptor.capture());
      Assert.assertTrue(eventCaptor.getValue().getMessage().contains("2"));
   }

   @Test
   public void shouldRemoveMarkIfAlreadyExist() throws Exception
   {
      initManager();

      manager.onLineNumberDoubleClick(new LineNumberDoubleClickEvent(2));
      ArgumentCaptor<AsyncRequestCallback> asyncCaptor = ArgumentCaptor.forClass(AsyncRequestCallback.class);
      verify(service).addBreakPoint(anyString(), Mockito.<BreakPoint> any(), asyncCaptor.capture());
      when(breakPoint.getLocation()).thenReturn(location);
      when(location.getLineNumber()).thenReturn(2);
      GwtReflectionUtils.callPrivateMethod(asyncCaptor.getValue(), "onSuccess", breakPoint);

      manager.onLineNumberDoubleClick(new LineNumberDoubleClickEvent(2));
      ArgumentCaptor<Problem> problemCaptor = ArgumentCaptor.forClass(Problem.class);

      verify(codeMirror).unmarkProblem(problemCaptor.capture());
      Assert.assertEquals(2, problemCaptor.getValue().getLineNumber());
   }

   /**
    * 
    */
   private void initManager()
   {
      FolderModel parent = new FolderModel();
      parent.setPath("/MyProject/src/main/java/my/test");
      FileModel file = new FileModel("TestClass.java", MimeType.APPLICATION_JAVA, "", parent);
      FolderModel projectParent = new FolderModel();
      projectParent.setPath("/");
      file.setProject(new ProjectModel("MyProject", projectParent, "", Collections.<Property> emptyList()));
      file.setId("fileId" + hashCode());
      
      when(fqnResolverFactory.getResolver(anyString())).thenReturn(fqnResolver);
      when(fqnResolverFactory.isResolverExist(anyString())).thenReturn(true);
      
      when(fqnResolver.resolveFqn(file)).thenReturn("my.test.TestClass");
      eventBus.fireEvent(new EditorActiveFileChangedEvent(file, codeMirror));
      eventBus.fireEvent(new DebuggerConnectedEvent(debuggerInfo));

      when(autoBeanFactory.create(BreakPoint.class)).thenReturn(autoBean);
      when(autoBean.as()).thenReturn(breakPoint);
      when(autoBeanFactory.create(Location.class)).thenReturn(locationAutoBean);
      when(locationAutoBean.as()).thenReturn(location);
      when(debuggerInfo.getId()).thenReturn("debugId");

   }

}
