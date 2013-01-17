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
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import org.exoplatform.ide.Resources;
import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.api.ui.part.PartAgent.PartStackType;
import org.exoplatform.ide.client.PageResources;
import org.exoplatform.ide.client.event.FileEvent;
import org.exoplatform.ide.client.event.FileEvent.FileOperation;
import org.exoplatform.ide.client.event.FileEventHandler;
import org.exoplatform.ide.client.extensionsPart.ExtensionsPage;
import org.exoplatform.ide.client.projectExplorer.ProjectExplorerPresenter;
import org.exoplatform.ide.client.welcome.WelcomePage;
import org.exoplatform.ide.command.EditorActiveExpression;
import org.exoplatform.ide.command.NoProjectOpenedExpression;
import org.exoplatform.ide.command.ProjectOpenedExpression;
import org.exoplatform.ide.core.editor.EditorAgent;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.core.expressions.ExpressionManager;
import org.exoplatform.ide.java.client.projectmodel.JavaProject;
import org.exoplatform.ide.java.client.projectmodel.JavaProjectDesctiprion;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.menu.ExtendedCommand;
import org.exoplatform.ide.menu.MainMenuPresenter;
import org.exoplatform.ide.outline.OutlinePartPresenter;
import org.exoplatform.ide.part.PartAgentPresenter;
import org.exoplatform.ide.presenter.Presenter;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.ProjectDescription;
import org.exoplatform.ide.resources.model.Property;
import org.exoplatform.ide.rest.MimeType;
import org.exoplatform.ide.toolbar.ToolbarPresenter;
import org.exoplatform.ide.ui.list.SimpleList;
import org.exoplatform.ide.ui.list.SimpleList.View;
import org.exoplatform.ide.util.dom.Elements;
import org.exoplatform.ide.util.loging.Log;

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
public class WorkspacePresenter implements Presenter, WorkspaceView.ActionDelegate
{

   WorkspaceView view;

   EventBus eventBus;

   ProjectExplorerPresenter projectExplorerPresenter;

   private final MainMenuPresenter menuPresenter;

   private final ToolbarPresenter toolbarPresenter;

   private final PartAgentPresenter partAgent;

   private final EditorAgent editorAgent;

   protected final Resources resources;

   @Inject
   protected WorkspacePresenter(WorkspaceView view, final ProjectExplorerPresenter projectExplorerPresenter,
      EventBus eventBus, MainMenuPresenter menuPresenter, ToolbarPresenter toolbarPresenter, EditorAgent editorAgent,
      Resources resources, final ResourceProvider resourceProvider, final ExpressionManager expressionManager,
      PartAgentPresenter partAgent, ExtensionsPage extensionsPage, PageResources pageResources,
      OutlinePartPresenter outlinePresenter, NoProjectOpenedExpression noProjectOpenedExpression,
      EditorActiveExpression editorActiveExpression, ProjectOpenedExpression projectOpenedExpression)
   {
      super();
      this.view = view;
      view.setDelegate(this);
      this.projectExplorerPresenter = projectExplorerPresenter;
      this.eventBus = eventBus;
      this.menuPresenter = menuPresenter;
      this.toolbarPresenter = toolbarPresenter;
      this.editorAgent = editorAgent;
      this.partAgent = partAgent;
      this.resources = resources;

      // FOR DEMO

      // CREATE STATIC MENU CONTENT
      menuPresenter.addMenuItem("File/Open Project", new OpenProjectCommand(resourceProvider));

      // CREATE DYNAMIC MENU CONTENT
      menuPresenter.addMenuItem("File/Create Demo Content", new CreateDemoContentCommand(resourceProvider));

      bind();

      //XXX DEMO

      partAgent.addPart(extensionsPage, PartStackType.EDITING);
      partAgent.addPart(new WelcomePage(pageResources), PartStackType.EDITING);
      partAgent.addPart(projectExplorerPresenter, PartStackType.NAVIGATION);
      partAgent.addPart(outlinePresenter, PartStackType.TOOLING);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(AcceptsOneWidget container)
   {
      // Expose Project Explorer into Tools Panel
      menuPresenter.go(view.getMenuPanel());
      toolbarPresenter.go(view.getToolbarPanel());

      partAgent.go(PartStackType.NAVIGATION, view.getLeftPanel());
      partAgent.go(PartStackType.EDITING, view.getCenterPanel());
      partAgent.go(PartStackType.TOOLING, view.getRightPanel());

      container.setWidget(view);
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

   // FOR DEMO:
   private final class CreateDemoContentCommand implements ExtendedCommand
   {
      private final ResourceProvider resourceManager;

      private CreateDemoContentCommand(ResourceProvider resourceManager)
      {
         this.resourceManager = resourceManager;
      }

      @Override
      public void execute()
      {
         // DUMMY CREATE DEMO CONTENT
         resourceManager.createProject("Test Project " + (new Date().getTime()), JsonCollections
            .<Property>createArray(
               //
               new Property(ProjectDescription.PROPERTY_PRIMARY_NATURE, JavaProject.PRIMARY_NATURE),//
               new Property(JavaProjectDesctiprion.PROPERTY_SOURCE_FOLDERS, JsonCollections.createArray(
                  "src/main/java", "src/main/resources", "src/test/java", "src/test/resources"))//
            ), new AsyncCallback<Project>()
         {

            @Override
            public void onSuccess(final Project project)
            {
               project.createFolder(project, "src", new AsyncCallback<Folder>()
               {

                  @Override
                  public void onFailure(Throwable caught)
                  {
                     Log.error(getClass(), caught);
                  }

                  @Override
                  public void onSuccess(Folder result)
                  {
                     project.createFolder(result, "main", new AsyncCallback<Folder>()
                     {

                        @Override
                        public void onFailure(Throwable caught)
                        {
                           Log.error(getClass(), caught);
                        }

                        @Override
                        public void onSuccess(Folder result)
                        {
                           project.createFolder(result, "java/org/exoplatform/ide", new AsyncCallback<Folder>()
                           {

                              @Override
                              public void onFailure(Throwable caught)
                              {
                                 Log.error(getClass(), caught);
                              }

                              @Override
                              public void onSuccess(Folder result)
                              {
                                 project.createFile(result, "Test.java",
                                    "package org.exoplatform.ide;\n public class Test\n{\n}",
                                    MimeType.APPLICATION_JAVA, new AsyncCallback<File>()
                                 {

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                       Log.error(getClass(), caught);
                                    }

                                    @Override
                                    public void onSuccess(File result)
                                    {
                                    }
                                 });
                                 project.createFolder(result, "void", new AsyncCallback<Folder>()
                                 {

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                       Log.error(getClass(), caught);
                                    }

                                    @Override
                                    public void onSuccess(Folder result)
                                    {
                                    }
                                 });
                              }
                           });
                           project.createFolder(result, "resources/org/exoplatform/ide", new AsyncCallback<Folder>()
                           {

                              @Override
                              public void onFailure(Throwable caught)
                              {
                                 Log.error(getClass(), caught);
                              }

                              @Override
                              public void onSuccess(Folder result)
                              {
                                 project.createFile(result, "styles.css", ".test{\n\n}", "text/css",
                                    new AsyncCallback<File>()
                                    {

                                       @Override
                                       public void onSuccess(File result)
                                       {
                                          // ok
                                       }

                                       @Override
                                       public void onFailure(Throwable caught)
                                       {
                                          Log.error(getClass(), caught);
                                       }
                                    });

                              }
                           });

                           project.createFolder(result, "webapp", new AsyncCallback<Folder>()
                           {

                              @Override
                              public void onFailure(Throwable caught)
                              {
                                 Log.error(getClass(), caught);
                              }

                              @Override
                              public void onSuccess(Folder result)
                              {
                              }
                           });
                        }
                     });
                     project.createFolder(result, "test", new AsyncCallback<Folder>()
                     {

                        @Override
                        public void onFailure(Throwable caught)
                        {
                           Log.error(getClass(), caught);
                        }

                        @Override
                        public void onSuccess(Folder result)
                        {
                           project.createFolder(result, "java/org/exoplatform/ide", new AsyncCallback<Folder>()
                           {

                              @Override
                              public void onFailure(Throwable caught)
                              {
                                 Log.error(getClass(), caught);
                              }

                              @Override
                              public void onSuccess(Folder result)
                              {
                                 project.createFile(result, "TestClass.java",
                                    "package org.exoplatform.ide;\n public class TestClass\n{\n}",
                                    MimeType.APPLICATION_JAVA, new AsyncCallback<File>()
                                 {

                                    @Override
                                    public void onFailure(Throwable caught)
                                    {
                                    }

                                    @Override
                                    public void onSuccess(File result)
                                    {
                                    }
                                 });
                              }
                           });

                           project.createFolder(result, "resources/org/exoplatform/ide", new AsyncCallback<Folder>()
                           {

                              @Override
                              public void onFailure(Throwable caught)
                              {
                                 Log.error(getClass(), caught);
                              }

                              @Override
                              public void onSuccess(Folder result)
                              {
                                 project.createFile(result, "TestFileOnFs.txt",
                                    "This is file content of the file from VFS", "text/text-pain",
                                    new AsyncCallback<File>()
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
                           });
                        }
                     });

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

      @Override
      public Image getIcon()
      {
         return null;
      }

      @Override
      public String getToolTip()
      {
         return "Create Demo content";
      }

      @Override
      public Expression inContext()
      {
         return null;
      }

      @Override
      public Expression canExecute()
      {
         return null;
      }
   }

   /**
    * Opens new project.
    * TODO : Extract dialog as framework UI component
    */
   private final class OpenProjectCommand implements ExtendedCommand
   {
      private final ResourceProvider resourceProvider;

      private SimpleList<String> list;

      private final Image icon;

      private SimpleList.ListItemRenderer<String> listItemRenderer = new SimpleList.ListItemRenderer<String>()
      {
         @Override
         public void render(Element itemElement, String itemData)
         {
            TableCellElement label = Elements.createTDElement();
            label.setInnerHTML(itemData);
            itemElement.appendChild(label);
         }

         @Override
         public Element createElement()
         {
            return Elements.createTRElement();
         }
      };

      private SimpleList.ListEventDelegate<String> listDelegate = new SimpleList.ListEventDelegate<String>()
      {
         @Override
         public void onListItemClicked(Element itemElement, String itemData)
         {
            Log.info(this.getClass(), "onListItemClicked ", itemElement);
            list.getSelectionModel().setSelectedItem(itemData);
         }

         @Override
         public void onListItemDoubleClicked(Element listItemBase, String itemData)
         {
            Log.info(this.getClass(), "onListItemDoubleClicked ", itemData);
            //                     Assert.isNotNull(delegate);
            //                     delegate.onSelect(itemData);
         }
      };

      /**
       *
       */
      @Inject
      public OpenProjectCommand(ResourceProvider resourceProvider)
      {
         this.resourceProvider = resourceProvider;
         // TODO change image
         this.icon = new Image(resources.folderOpen());

         TableElement tableElement = Elements.createTableElement();
         tableElement.setAttribute("style", "width: 100%");
         list = SimpleList.create((View)tableElement, resources.defaultSimpleListCss(), listItemRenderer, listDelegate);
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public void execute()
      {

         resourceProvider.listProjects(new AsyncCallback<JsonArray<String>>()
         {
            @Override
            public void onSuccess(JsonArray<String> result)
            {
               final PopupPanel dialogBox = createDialog(result);
               dialogBox.center();
               dialogBox.show();
            }

            @Override
            public void onFailure(Throwable caught)
            {
               Log.error(OpenProjectCommand.class, "can't list projects", caught);
            }
         });

      }

      /**
       * @return
       */
      public PopupPanel createDialog(JsonArray<String> projects)
      {

         final DialogBox dialogBox = new DialogBox();
         dialogBox.setText("Open the project");

         ScrollPanel listPanel = new ScrollPanel();
         listPanel.setStyleName(resources.coreCss().simpleListContainer());
         listPanel.add(list);
         dialogBox.setTitle("Select a project");
         dialogBox.setText("Select a project, please");

         DockLayoutPanel content = new DockLayoutPanel(Unit.PX);
         content.setSize("300px", "300px");
         FlowPanel bottomPanel = new FlowPanel();
         content.addSouth(bottomPanel, 24);
         content.add(listPanel);

         dialogBox.setWidget(content);

         Button closeButton = new Button("cancel", new ClickHandler()
         {
            @Override
            public void onClick(ClickEvent event)
            {
               dialogBox.hide();
            }
         });
         Button okButton = new Button("ok", new ClickHandler()
         {
            @Override
            public void onClick(ClickEvent event)
            {
               Log.info(this.getClass(), "onClick = ", list.getSelectionModel().getSelectedItem());
               if (list.getSelectionModel().getSelectedItem() != null)
               {
                  String selectedItem = list.getSelectionModel().getSelectedItem();
                  resourceProvider.getProject(selectedItem, new AsyncCallback<Project>()
                  {
                     @Override
                     public void onSuccess(Project result)
                     {
                        dialogBox.hide();
                     }

                     @Override
                     public void onFailure(Throwable caught)
                     {
                        Log.error(OpenProjectCommand.class, "can't open projects", caught);
                     }
                  });
               }
            }
         });
         bottomPanel.add(closeButton);
         bottomPanel.add(okButton);
         list.render(projects);
         return dialogBox;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Image getIcon()
      {
         return icon;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public String getToolTip()
      {
         return "Open project";
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Expression inContext()
      {
         return null;
      }

      /**
       * {@inheritDoc}
       */
      @Override
      public Expression canExecute()
      {
         return null;
      }
   }

}
