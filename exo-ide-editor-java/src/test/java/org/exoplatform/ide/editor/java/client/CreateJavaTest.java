///*
// * Copyright (C) 2012 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.editor.java.client;
//
//import static org.fest.assertions.Assertions.assertThat;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//import com.google.gwt.event.dom.client.ClickHandler;
//import com.google.gwt.event.dom.client.HasClickHandlers;
//import com.google.gwt.event.logical.shared.ValueChangeEvent;
//import com.google.gwt.event.logical.shared.ValueChangeHandler;
//import com.google.gwt.event.shared.HandlerManager;
//import com.google.gwt.user.client.ui.HasValue;
//import com.googlecode.gwt.test.GwtTestWithMockito;
//import com.googlecode.gwt.test.utils.GwtReflectionUtils;
//
//import org.eclipse.jdt.client.create.CreateJavaPresenter;
//import org.eclipse.jdt.client.create.CreateJavaPresenter.Display;
//import org.eclipse.jdt.client.event.CreateJavaClassEvent;
//import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
//import org.exoplatform.ide.client.framework.event.OpenFileEvent;
//import org.exoplatform.ide.client.framework.module.IDE;
//import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
//import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
//import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
//import org.exoplatform.ide.vfs.client.VirtualFileSystem;
//import org.exoplatform.ide.vfs.client.model.FileModel;
//import org.exoplatform.ide.vfs.client.model.FolderModel;
//import org.exoplatform.ide.vfs.client.model.ProjectModel;
//import org.exoplatform.ide.vfs.shared.Item;
//import org.exoplatform.ide.vfs.shared.Property;
//import org.junit.Before;
//import org.junit.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.Spy;
//
//import java.util.Arrays;
//import java.util.Collection;
//import java.util.Collections;
//
///**
// * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
// * @version $Id:
// *
// */
//public class CreateJavaTest extends GwtTestWithMockito
//{
//
//   @Spy
//   private HandlerManager eventBus = new HandlerManager(null);
//
//   @Mock
//   private IDE ide;
//
//   @Mock
//   private Display display;
//
//   @Mock
//   private HasClickHandlers clickHandlers;
//   
//   @Mock
//   private HasClickHandlers creatClickHandlers;
//
//   @Mock
//   private HasValue<String> nameField;
//   
//   @Mock
//   private HasValue<String> typeSelect;
//   
//   @Mock
//   private VirtualFileSystem vfs;
//
//   private CreateJavaPresenter presenter;
//   
//   private ProjectModel project;
//   
//   private FolderModel selectedFolder;
//   
//
//   @Before
//   public void init()
//   {
//      presenter = new CreateJavaPresenter(eventBus, vfs, ide);
//      when(display.getCreateButton()).thenReturn(creatClickHandlers);
//      when(display.getCancelButton()).thenReturn(clickHandlers);
//      when(display.getNameField()).thenReturn(nameField);
//      when(display.getTypeSelect()).thenReturn(typeSelect);
//      eventBus.fireEvent(new CreateJavaClassEvent());
//      FolderModel projectParent = new FolderModel();
//      projectParent.setPath("");
//      project = new ProjectModel("MyProject", projectParent, "", Collections.<Property> emptyList());
//      eventBus.fireEvent(new ProjectOpenedEvent(project));
//      selectedFolder = new FolderModel();
//      selectedFolder.setPath("/MyProject/src/main/java/my/test");
//      selectedFolder.setProject(project);
//      eventBus.fireEvent(new ItemsSelectedEvent(Arrays.asList((Item)selectedFolder), null));
//   }
//
//   @Test
//   public void shouldHadleEvents() throws Exception
//   {
//      verify(eventBus).addHandler(CreateJavaClassEvent.TYPE, presenter);
//      verify(eventBus).addHandler(ViewClosedEvent.TYPE, presenter);
//      verify(eventBus).addHandler(ProjectOpenedEvent.TYPE, presenter);
//      verify(eventBus).addHandler(ProjectClosedEvent.TYPE, presenter);
//      verify(eventBus).addHandler(ItemsSelectedEvent.TYPE, presenter);
//   }
//
//   @Test
//   public void shouldOpenForm() throws Exception
//   {
//      verify(ide).openView(display.asView());
//   }
//
//   @Test
//   @SuppressWarnings({"rawtypes", "unchecked"})
//   public void shouldBindDisplay() throws Exception
//   {
//      verify(clickHandlers).addClickHandler(Mockito.<ClickHandler> anyObject());
//      verify(creatClickHandlers).addClickHandler(Mockito.<ClickHandler> anyObject());
//      verify(nameField).addValueChangeHandler(Mockito.<ValueChangeHandler> anyObject());
//      verify(display).setCreateButtonEnabled(false);
//   }
//   
//   @Test
//   @SuppressWarnings({"rawtypes", "unchecked"})
//   public void shouldDisableCreateButtonIfNameFildIsEmpty() throws Exception
//   {
//      ArgumentCaptor<ValueChangeHandler> valueChangeCaptor = ArgumentCaptor.forClass(ValueChangeHandler.class);
//      verify(nameField).addValueChangeHandler(valueChangeCaptor.capture());
//      ValueChangeEvent valueChangeEvent = Mockito.mock(ValueChangeEvent.class);
//      addMockedObject(ValueChangeEvent.class, valueChangeEvent);
//      when(valueChangeEvent.getValue()).thenReturn("");
//      valueChangeCaptor.getValue().onValueChange(valueChangeEvent);
//      verify(display, times(2)).setCreateButtonEnabled(false);
//   }
//   
//   @Test
//   @SuppressWarnings({"rawtypes", "unchecked"})
//   public void shouldEnableCreateButtonIfNameFildNotEmpty() throws Exception
//   {
//      ArgumentCaptor<ValueChangeHandler> valueChangeCaptor = ArgumentCaptor.forClass(ValueChangeHandler.class);
//      verify(nameField).addValueChangeHandler(valueChangeCaptor.capture());
//      ValueChangeEvent valueChangeEvent = Mockito.mock(ValueChangeEvent.class);
//      addMockedObject(ValueChangeEvent.class, valueChangeEvent);
//      when(valueChangeEvent.getValue()).thenReturn("My");
//      valueChangeCaptor.getValue().onValueChange(valueChangeEvent);
//      verify(display, times(1)).setCreateButtonEnabled(true);
//   }
//   
//   @Test
//   @SuppressWarnings({"rawtypes", "unchecked"})
//   public void shouldSetJavaTypes() throws Exception
//   {
//      ArgumentCaptor<Collection> typesCaptor = ArgumentCaptor.forClass(Collection.class);
//      verify(display).setTypes(typesCaptor.capture());
//      Collection<String> types = typesCaptor.getValue();
//      assertThat(types).containsOnly("Class", "Interface", "Enum", "Annotation");
//   }
//   
//   @Test
//   @SuppressWarnings({"rawtypes", "unchecked"})
//   public void shouldCreateFile() throws Exception
//   {
//      String className = "MyClass";
//      when(nameField.getValue()).thenReturn(className);
//      when(typeSelect.getValue()).thenReturn("Class");
//      ArgumentCaptor<AsyncRequestCallback> asyncCaptor = ArgumentCaptor.forClass(AsyncRequestCallback.class);
//      ArgumentCaptor<ClickHandler> clickHandlerCaptor = ArgumentCaptor.forClass(ClickHandler.class);
//      verify(creatClickHandlers).addClickHandler(clickHandlerCaptor.capture());
//      clickHandlerCaptor.getValue().onClick(null);
//      
//      verify(vfs).createFile(Mockito.<FolderModel> anyObject(), asyncCaptor.capture());
//      GwtReflectionUtils.callPrivateMethod(asyncCaptor.getValue(), "onSuccess", asyncCaptor.getValue().getPayload());
//         
//      ArgumentCaptor<OpenFileEvent> openFileCaptor = ArgumentCaptor.forClass(OpenFileEvent.class);
//      verify(eventBus, Mockito.atLeastOnce()).fireEvent(openFileCaptor.capture());
//      FileModel payload = (FileModel)asyncCaptor.getValue().getPayload();
//      assertThat(payload.getName()).endsWith(".java");
//   }
//   
//   @Test
//   public void shouldCreateClass() throws Exception
//   {
//      String className = "MyClass";
//      String classContent = createJavaType("Class", className);
//      assertThat(classContent).contains("package my.test;").contains("public class " + className);
//   }
//   
//   @Test
//   public void shouldCreateInterface() throws Exception
//   {
//      String className = "MyInterface";
//      String classContent = createJavaType("Interface", className);
//      assertThat(classContent).contains("package my.test;").contains("public interface " + className);
//   }
//   
//   @Test
//   public void shouldCreateEnum() throws Exception
//   {
//      String className = "MyEnum";
//      String classContent = createJavaType("Enum", className);
//      assertThat(classContent).contains("package my.test;").contains("public enum " + className);
//   }
//   
//   @Test
//   public void shouldCreateAnnotation() throws Exception
//   {
//      String className = "MyAnnotation";
//      String classContent = createJavaType("Annotation", className);
//      assertThat(classContent).contains("package my.test;").contains("public @interface " + className);
//   }
//   
//   @Test
//   public void shouldCreateTypeWithDefaultPackage() throws Exception
//   {
//      selectedFolder.setPath("/MyProject/src/main/java/");
//      eventBus.fireEvent(new ItemsSelectedEvent(Arrays.asList((Item)selectedFolder), null));
//      String className = "DefaultPackage";
//      String classContent = createJavaType("Class", className);
//      assertThat(classContent).doesNotContain("package").contains("public class " + className);
//   }
//   
//   
//   @SuppressWarnings({"rawtypes", "unchecked"})
//   private String createJavaType(String type, String name) throws Exception
//   {
//      when(nameField.getValue()).thenReturn(name);
//      when(typeSelect.getValue()).thenReturn(type);
//      ArgumentCaptor<AsyncRequestCallback> asyncCaptor = ArgumentCaptor.forClass(AsyncRequestCallback.class);
//      ArgumentCaptor<ClickHandler> clickHandlerCaptor = ArgumentCaptor.forClass(ClickHandler.class);
//      verify(creatClickHandlers).addClickHandler(clickHandlerCaptor.capture());
//      clickHandlerCaptor.getValue().onClick(null);
//      
//      verify(vfs).createFile(Mockito.<FolderModel> anyObject(), asyncCaptor.capture());
//      FileModel payload = (FileModel)asyncCaptor.getValue().getPayload();
//      return payload.getContent();
//   }
//
//   /**
//    * @see com.googlecode.gwt.test.GwtModuleRunnerAdapter#getModuleName()
//    */
//   @Override
//   public String getModuleName()
//   {
//      return "org.exoplatform.ide.editor.java.JavaEditorExtension";
//   }
//
//}
