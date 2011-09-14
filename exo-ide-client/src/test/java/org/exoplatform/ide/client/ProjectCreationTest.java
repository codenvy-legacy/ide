/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client;

import org.easymock.EasyMock;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.command.Control;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.outline.ui.OutlineItemCreator;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.project.CreateProjectPresenter;
import org.exoplatform.ide.client.project.CreateProjectPresenter.Display;
import org.exoplatform.ide.client.project.CreateProjectPresenter.ErrorMessage;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.junit.GWTMockUtilities;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public class ProjectCreationTest extends TestCase
{
   
   private MockVirtualFileSystem vfs = new MockVirtualFileSystem("dev-monit");
   
   private HandlerManager eventBus;
   
   private CreateProjectPresenter.Display display;
   
   private String projectName;
   
   private String error;
   
   
   public void setError(String error)
   {
      this.error = error;
   }
   
   public String getError()
   {
      return error;
   }
   
   
   @SuppressWarnings("static-access")
   protected void setUp() {
      new MockIde();
      eventBus = IDE.getInstance().EVENT_BUS;
      GWTMockUtilities.disarm(); 
//      IsView view = EasyMock.createStrictMock(IsView.class);
//      mockDisplay = EasyMock.createStrictMock(Display.class);
//      EasyMock.replay(view,mockDisplay);
      
      display = new MockDisplay();
      eventBus.addHandler(ExceptionThrownEvent.TYPE, new MockExceptionThrownHandler());
   }
   
   
   public void testCreateProject()
   {
      List<Item> selectedItems = new ArrayList<Item>();
      selectedItems.add(new FolderModel());
      CreateProjectPresenter presenter = new CreateProjectPresenter(eventBus, vfs, display, selectedItems);
      presenter.setProjectName("test");
      presenter.setErrorMessage(new MockErrorMessages());
      List<String> list = new ArrayList<String>();
      list.add("Java Project");
      presenter.setProjectTypes(list);
      presenter.doCreateProject();
      assertEquals(projectName, "test");
   }
   
   
   public void testCreateProjectFailifSelectToFolder() throws InterruptedException
   {
      List<Item> selectedItems = new ArrayList<Item>();
      selectedItems.add(new FolderModel());
      selectedItems.add(new FolderModel());
      CreateProjectPresenter presenter = new CreateProjectPresenter(eventBus, vfs, display, selectedItems);
      presenter.setProjectName("test");
      presenter.setErrorMessage(new MockErrorMessages());
      List<String> list = new ArrayList<String>();
      list.add("Java Project");
      presenter.setProjectTypes(list);
      presenter.doCreateProject();
      assertNotNull(getError());
   }
   
   public void testCreateProjectFailEmptyProjectName() throws InterruptedException
   {
      List<Item> selectedItems = new ArrayList<Item>();
      selectedItems.add(new FolderModel());
      CreateProjectPresenter presenter = new CreateProjectPresenter(eventBus, vfs, display, selectedItems);
      presenter.setProjectName(null);
      presenter.setErrorMessage(new MockErrorMessages());
      List<String> list = new ArrayList<String>();
      list.add("Java Project");
      presenter.setProjectTypes(list);
      presenter.doCreateProject();
      assertNotNull(getError());
   }
   
   public void testCreateProjectFailIfParenIfFile() throws InterruptedException
   {
      List<Item> selectedItems = new ArrayList<Item>();
      selectedItems.add(new FileModel());
      CreateProjectPresenter presenter = new CreateProjectPresenter(eventBus, vfs, display, selectedItems);
      presenter.setProjectName(null);
      presenter.setErrorMessage(new MockErrorMessages());
      List<String> list = new ArrayList<String>();
      list.add("Java Project");
      presenter.setProjectTypes(list);
      presenter.doCreateProject();
      assertNotNull(getError());
   }
   
   
   private class MockVirtualFileSystem extends VirtualFileSystem
   {
      public MockVirtualFileSystem(String workspaceURL)
      {
         super(workspaceURL);
      }
      
      @Override
      public void createProject(Folder parent, AsyncRequestCallback<ProjectModel> callback) throws RequestException
      {
         projectName = callback.getPayload().getName();
      }
   }
   
   @Override
   protected void tearDown() throws Exception
   {
      super.tearDown();
      eventBus = null;
      GWTMockUtilities.restore();
   }
   
   
   //Mock Classes
   //TODO: need improve for use easymock framework.
   
   private class MockErrorMessages implements ErrorMessage
   {

      @Override
      public String cantCreateProjectIfMultiselectionParent()
      {
         return "Can't create project you must select only one parent folder";
      }

      @Override
      public String cantCreateProjectIfProjectNameNotSet()
      {
         return "Project name can't be empty or null";
      }
      
   }
   
  
   private class MockExceptionThrownHandler implements ExceptionThrownHandler
   {

      @Override
      public void onError(ExceptionThrownEvent event)
      {
         setError(event.getErrorMessage());
      }
      
   }
   
   private class MockDisplay implements Display
   {
      
      private String projectName; 

      @Override
      public View asView()
      {
         return null;
      }

      @Override
      public HasClickHandlers getCreateButton()
      {
         return new HasClickHandlers()
         {
            
            @Override
            public void fireEvent(GwtEvent<?> event)
            {
            }
            
            @Override
            public HandlerRegistration addClickHandler(ClickHandler handler)
            {
               return null;
            }
         };
      }

      @Override
      public HasClickHandlers getCancelButton()
      {
         return new HasClickHandlers()
         {
            
            @Override
            public void fireEvent(GwtEvent<?> event)
            {
            }
            
            @Override
            public HandlerRegistration addClickHandler(ClickHandler handler)
            {
               return null;
            }
         };
      }

      @Override
      public void setProjectType(List<String> types)
      {
      }

      @Override
      public void setProjectName(String name)
      {
         projectName = name;
      }

      @Override
      public HasValue<String> getProjectName()
      {
         return new HasValue<String>()
         {

            @Override
            public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
            {
               return null;
            }

            @Override
            public void fireEvent(GwtEvent<?> event)
            {
            }

            @Override
            public String getValue()
            {
               return projectName;
            }

            @Override
            public void setValue(String value)
            {
            }

            @Override
            public void setValue(String value, boolean fireEvents)
            {
            }
         };
      }

      @Override
      public HasValue<String> getProjectType()
      {
         return new HasValue<String>()
         {

            @Override
            public HandlerRegistration addValueChangeHandler(ValueChangeHandler<String> handler)
            {
               return null;
            }

            @Override
            public void fireEvent(GwtEvent<?> event)
            {
            }

            @Override
            public String getValue()
            {
               return null;
            }

            @Override
            public void setValue(String value)
            {
            }

            @Override
            public void setValue(String value, boolean fireEvents)
            {
            }
         };
      }

      @Override
      public Widget asWidget()
      {
         return null;
      }
      
   }
   
   
   private class MockIde extends IDE 
   {
      
      @Override
      public void addControl(Control<?> control, DockTarget dockTarget, boolean rightDocking)
      {
      }

      @Override
      public void addControl(Control<?> control)
      {
      }

      @Override
      public void openView(View view)
      {
      }

      @Override
      public void closeView(String viewId)
      {
      }

      @Override
      public void addEditor(EditorProducer editorProducer)
      {
      }

      @Override
      public EditorProducer getEditor(String mimeType) throws EditorNotFoundException
      {
         return null;
      }

      @Override
      public void addOutlineItemCreator(String mimeType, OutlineItemCreator outlineItemCreator)
      {
      }

      @Override
      public OutlineItemCreator getOutlineItemCreator(String mimeType)
      {
         return null;
      }
      
   }

}
