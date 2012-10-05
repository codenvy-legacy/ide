/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.workspace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.client.editor.EditorAgent;
import org.exoplatform.ide.client.event.FileEvent;
import org.exoplatform.ide.client.event.FileEvent.FileOperation;
import org.exoplatform.ide.client.event.FileEventHandler;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter;
import org.exoplatform.ide.client.welcome.WelcomePage;
import org.exoplatform.ide.core.expressions.AbstractExpression;
import org.exoplatform.ide.core.expressions.ExpressionManager;
import org.exoplatform.ide.core.expressions.ProjectConstraintExpression;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.part.PartAgent;
import org.exoplatform.ide.part.PartAgent.PartStackType;
import org.exoplatform.ide.part.PartStackPresenter;
import org.exoplatform.ide.presenter.Presenter;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Property;

import java.util.Date;

/**
 * Root Presenter that implements Workspace logic. Descendant Presenters are injected via
 * constructor and exposed to coresponding UI containers.
 * 
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 24, 2012  
 */
public class WorkspacePeresenter implements Presenter
{

   public interface Display extends IsWidget
   {
      HasWidgets getCenterPanel();

      void clearCenterPanel();

      HasWidgets getLeftPanel();

      HasWidgets getMenuPanel();
   }

   Display display;

   EventBus eventBus;

   ProjectExplorerPresenter projectExpolorerPresenter;

   private final MainMenuPresenter menuPresenter;

   private final PartAgent partAgent;

   private final EditorAgent editorAgent;

   @Inject
   protected WorkspacePeresenter(Display display, final ProjectExplorerPresenter projectExpolorerPresenter,
      EventBus eventBus, MainMenuPresenter menuPresenter, EditorAgent editorAgent,
      final ResourceProvider resourceManager, final ExpressionManager expressionManager, PartAgent partAgent)

   {
      super();
      this.display = display;
      this.projectExpolorerPresenter = projectExpolorerPresenter;
      this.eventBus = eventBus;
      this.menuPresenter = menuPresenter;
      this.editorAgent = editorAgent;
      this.partAgent = partAgent;

      // FOR DEMO
      menuPresenter.addMenuItem("File/New/new File", null);
      menuPresenter.addMenuItem("File/New/new Project", null);

      NoProjectOpenedExpression noProjectOpenedExpression = new NoProjectOpenedExpression();
      expressionManager.registerExpression(noProjectOpenedExpression);
      menuPresenter.addMenuItem("File/Create Demo Content", null, new CreadDemoContentCommand(resourceManager), null,
         noProjectOpenedExpression);

      ProjectOpenedExpression projectOpenedExpression = new ProjectOpenedExpression();
      expressionManager.registerExpression(projectOpenedExpression);
      menuPresenter.addMenuItem("Project", null, null, projectOpenedExpression, null);
      menuPresenter.addMenuItem("Project/Some Project Operation", null, null, projectOpenedExpression,
         noProjectOpenedExpression);
      bind();

      //XXX DEMO

      partAgent.addPart(new WelcomePage(), PartStackType.EDITING);
      partAgent.addPart(projectExpolorerPresenter,PartStackType.NAVIGATION);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(HasWidgets container)
   {
      container.clear();
      // Expose Project Explorer into Tools Panel
      menuPresenter.go(display.getMenuPanel());

      PartStackPresenter navigationStack = partAgent.getPartStack(PartStackType.NAVIGATION);
      navigationStack.go(display.getLeftPanel());

      PartStackPresenter editoStack = partAgent.getPartStack(PartStackType.EDITING);
      editoStack.go(display.getCenterPanel());

      container.add(display.asWidget());
   }

   protected void bind()
   {
      eventBus.addHandler(FileEvent.TYPE, new FileEventHandler()
      {

         @Override
         public void onFileOperation(final FileEvent event)
         {
            if (event.getOperationType() == FileOperation.OPEN)
            {
//               // Set up the callback object.
//               AsyncCallback<File> callback = new AsyncCallback<File>()
//               {
//                  @Override
//                  public void onFailure(Throwable caught)
//                  {
//                     GWT.log("error" + caught);
//                  }
//
//                  @Override
//                  public void onSuccess(File file)
//                  {
//                     openFile(file);
//                  }
//               };
//
//               Project project = event.getFile().getProject();
//               project.getContent(event.getFile(), callback);
               editorAgent.openEditor(event.getFile());

               //fileSystemService.getFileContent(event.getFileName(), callback);
            }
            else if (event.getOperationType() == FileOperation.CLOSE)
            {
               // close associated editor. OR it can be closed itself TODO
            }
         }
      });
   }

   protected void openFile(File file)
   {
      // Calling display.getCenterPanel.cler() violates the Law of Demeter
//      editorPresenter.openFile(file);
//      partAgent.addPart(editorPresenter, PartStackType.EDITING);
      //editorPresenter.go(display.getCenterPanel());
   }

   // FOR DEMO:
   private final class ProjectOpenedExpression extends AbstractExpression implements ProjectConstraintExpression
   {
      public ProjectOpenedExpression()
      {
         super(false);
      }

      @Override
      public boolean onProjectChanged(Project project)
      {
         value = project != null;
         return value;
      }

   }

   // FOR DEMO:
   private final class NoProjectOpenedExpression extends AbstractExpression implements ProjectConstraintExpression
   {
      public NoProjectOpenedExpression()
      {
         super(true);
      }

      @Override
      public boolean onProjectChanged(Project project)
      {
         value = project == null;
         return value;
      }

   }

   // FOR DEMO:
   private final class CreadDemoContentCommand implements Command
   {
      private final ResourceProvider resourceManager;

      private CreadDemoContentCommand(ResourceProvider resourceManager)
      {
         this.resourceManager = resourceManager;
      }

      @SuppressWarnings("rawtypes")
      @Override
      public void execute()
      {
         // DUMMY CREATE DEMO CONTENT
         resourceManager.createProject("Test Project " + (new Date().getTime()),
            JsonCollections.<Property> createArray(), new AsyncCallback<Project>()
            {

               @Override
               public void onSuccess(final Project project)
               {
                  project.createFolder(project, "Test Folder", new AsyncCallback<Folder>()
                  {

                     @Override
                     public void onSuccess(Folder result)
                     {
                        project.createFile(result, "TestFileOnFs.txt", "This is file content of the file from VFS",
                           "text/text-pain", new AsyncCallback<File>()
                           {

                              @Override
                              public void onSuccess(File result)
                              {
                                 // ok
                              }

                              @Override
                              public void onFailure(Throwable caught)
                              {
                                 GWT.log("Error creating demo folder" + caught);
                              }
                           });
                        project.createFile(result, "TestJava.java", "public class TestJava\n{\n\n}",
                           "application/java", new AsyncCallback<File>()
                           {

                              @Override
                              public void onSuccess(File result)
                              {
                                 // ok
                              }

                              @Override
                              public void onFailure(Throwable caught)
                              {
                                 GWT.log("Error creating demo folder" + caught);
                              }
                           });

                     }

                     @Override
                     public void onFailure(Throwable caught)
                     {
                        GWT.log("Error creating demo folder" + caught);
                     }
                  });

               }

               @Override
               public void onFailure(Throwable caught)
               {
                  GWT.log("Error creating demo content" + caught);
               }
            });
      }
   }

}
