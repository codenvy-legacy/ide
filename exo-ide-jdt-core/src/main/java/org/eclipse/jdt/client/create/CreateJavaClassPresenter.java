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
package org.eclipse.jdt.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.eclipse.jdt.client.event.CreateJavaClassEvent;
import org.eclipse.jdt.client.event.CreateJavaClassHandler;
import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.eclipse.jdt.client.packaging.model.Package;
import org.eclipse.jdt.client.packaging.model.SourceDirectory;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.FileUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CreateJavaClassPresenter implements CreateJavaClassHandler, ViewClosedHandler, 
        ItemsSelectedHandler, EditorActiveFileChangedHandler, TreeRefreshedHandler {

    private static final String TYPE_CONTENT = "\n{\n}";

    public static final String DEFAULT_SOURCE_FOLDER = "src/main/java";

    private static final String DEFAULT_PACKAGE = "(default package)";

    public interface Display extends IsView {
        HasValue<String> sourceFolderField();

        void setSourceFolders(Collection<String> sourceFolders);

        HasValue<String> packageField();

        void setPackages(Collection<String> packages);

        HasValue<String> classNameField();

        void focusInClassNameField();

        HasValue<String> classTypeField();

        void setClassTypes(Collection<String> types);

        HasClickHandlers createButton();

        void enableCreateButton(boolean enabled);

        HasClickHandlers cancelButton();

    }

    private enum JavaTypes {
        CLASS("Class"), INTERFACE("Interface"), ENUM("Enum"), ANNOTATION("Annotation");

        private String value;

        private JavaTypes(String value) {
            this.value = value;
        }

        /** @see java.lang.Enum#toString() */
        @Override
        public String toString() {
            return value;
        }
    }

    /** Default Maven 'sourceDirectory' value */

    private Display display;

    private Item selectedItem;

    private final VirtualFileSystem vfs;

    /** @param eventBus */
    public CreateJavaClassPresenter(VirtualFileSystem vfs) {
        super();
        this.vfs = vfs;

        IDE.addHandler(CreateJavaClassEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() == 1) {
            selectedItem = event.getSelectedItems().get(0);
        } else {
            selectedItem = null;
        }
    }


    /** @see org.eclipse.jdt.client.event.CreateJavaClassHandler#onCreateJavaClass(org.eclipse.jdt.client.event.CreateJavaClassEvent) */
    @Override
    public void onCreateJavaClass(CreateJavaClassEvent event) {
        if (selectedItem == null || display != null) {
            return;
        }

        display = GWT.create(Display.class);
        IDE.getInstance().openView(display.asView());
        bindDisplay();

        //readProjectTreeAndBindDisplay("Reading project structure...");
    }

    /**
     *
     */
    private void bindDisplay() {
        display.cancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.createButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doCreate();
            }
        });

        display.classNameField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                if (event.getValue() != null && !event.getValue().isEmpty()) {
                    display.enableCreateButton(true);
                } else {
                    display.enableCreateButton(false);
                }
            }
        });

        ((HasKeyPressHandlers)display.classNameField()).addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (event.getNativeEvent().getKeyCode() == KeyCodes.KEY_ENTER) {
                    doCreate();
                }
            }
        });

        display.sourceFolderField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                List<String> packages = getPackageNamesInSourceDirectory(event.getValue());
                display.setPackages(packages);
            }
        });


        display.enableCreateButton(false);
      
      /*
       * Set class types
       */
        List<String> types = new ArrayList<String>();
        for (JavaTypes t : JavaTypes.values()) {
            types.add(t.toString());

        }
        display.setClassTypes(types);
      
      /*
       * Set source folders
       */
        JavaProject javaProject = (JavaProject)((ItemContext)selectedItem).getProject();
        List<String> sourceFolders = new ArrayList<String>();
        for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories()) {
            sourceFolders.add(sourceDirectory.getSourceDirectoryName());
        }
        display.setSourceFolders(sourceFolders);

//      for (ResourceDirectory resourceDirectory : currentProjectItem.getResourceDirectories())
//      {
//         sourceFolders.add(resourceDirectory.getName());
//      }

        showCurrentPackage();
        display.focusInClassNameField();
    }
   
   /*
   private void readProjectTreeAndBindDisplay(final String loaderMessage)
   {
      IDELoader.show(loaderMessage);

      try
      {
         ProjectTreeUnmarshaller unmarshaller = new ProjectTreeUnmarshaller(project);
         AsyncRequestCallback<ProjectModel> callback = new AsyncRequestCallback<ProjectModel>(unmarshaller)
         {
            @Override
            protected void onSuccess(ProjectModel result)
            {
               IDELoader.hide();

               ProjectTreeParser treeParser = new ProjectTreeParser(project, new Project(project));
               treeParser.parseProjectStructure(new ProjectTreeParser.ParsingCompleteListener()
               {
                  @Override
                  public void onParseComplete(Project resultItem)
                  {
                     currentProjectItem = resultItem;
                     IDE.getInstance().openView(display.asView());
                     bindDisplay();
                  }
               });
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDELoader.hide();
               IDE.fireEvent(new ExceptionThrownEvent("Error loading project structure"));
               exception.printStackTrace();
            }
         };

         VirtualFileSystem.getInstance().getProjectTree(project, callback);
      }
      catch (Exception e)
      {
         IDELoader.hide();
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
   */

    private List<String> getPackageNamesInSourceDirectory(String sourceDirectoryName) {
        List<String> packages = new ArrayList<String>();

        JavaProject javaProject = (JavaProject)((ItemContext)selectedItem).getProject();
        for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories()) {
            if (sourceDirectory.getSourceDirectoryName().equals(sourceDirectoryName)) {
                for (org.eclipse.jdt.client.packaging.model.Package _package : sourceDirectory.getPackages()) {
                    if (_package.getPackageName().isEmpty()) {
                        packages.add(DEFAULT_PACKAGE);
                    } else {
                        packages.add(_package.getPackageName());
                    }
                }

            }
        }

        return packages;

//      List<String> packages = new ArrayList<String>();
//      packages.add(0, DEFAULT_PACKAGE);
//
//      List<Package> packageItems = new ArrayList<Package>();
//      for (ResourceDirectory resourceDirectoryItem : currentProjectItem.getResourceDirectories())
//      {
//         if (sourceFolder.equals(resourceDirectoryItem.getName()))
//         {
//            packageItems.addAll(resourceDirectoryItem.getPackages());
//         }
//      }
//
//      for (Package pi : packageItems)
//      {
//         String[] parts = pi.getPackageName().split("\\.");
//         String packageName = "";
//         for (String part : parts)
//         {
//            packageName += (packageName.isEmpty() ? "" : ".") + part;
//
//            if (!packages.contains(packageName))
//            {
//               packages.add(packageName);
//            }
//         }
//      }
//
//      return packages;
    }

    private void showCurrentPackage() {
//      ResourceDirectory resourceDirectory = null;
//      for (ResourceDirectory rd : currentProjectItem.getResourceDirectories())
//      {
//         if (selectedItem.getPath().startsWith(rd.getFolder().getPath()))
//         {
//            resourceDirectory = rd;
//            break;
//         }
//      }

        JavaProject javaProject = (JavaProject)((ItemContext)selectedItem).getProject();
        SourceDirectory sourceDirectory = null;
        for (SourceDirectory sd : javaProject.getSourceDirectories()) {
            if (selectedItem.getPath().startsWith(sd.getPath())) {
                sourceDirectory = sd;
                break;
            }
        }

        if (sourceDirectory != null) {
            display.sourceFolderField().setValue(sourceDirectory.getName());

            List<String> packages = getPackageNamesInSourceDirectory(sourceDirectory.getName());

            if (packages.size() > 0) {
                display.setPackages(packages);
            }

            //String packageName = ((ItemContext)selectedItem).getParent().getPath().substring(sourceDirectory.getPath().length());

            String selectedFolderName =
                    selectedItem instanceof FolderModel ? selectedItem.getPath() : ((FileModel)selectedItem).getParent().getPath();
            String packageName = selectedFolderName.substring(sourceDirectory.getPath().length());

            packageName = packageName.replaceAll("/", "\\.");
            if (packageName.startsWith(".")) {
                packageName = packageName.substring(1);
            }

            display.packageField().setValue(packageName);
        }

        //      if (resourceDirectory != null)
//      {
//         display.sourceFolderField().setValue(resourceDirectory.getName());
//      }

//      List<String> packages = getPackageNamesInSourceDirectory(sourceDirectory.getName());
//      if (packages.size() > 0)
//      {
//         display.setPackages(packages);
//      }
//
//      if (resourceDirectory != null)
//      {
//         String packageName = parentFolder.getPath().substring(resourceDirectory.getFolder().getPath().length());
//         packageName = packageName.replaceAll("/", "\\.");
//         if (packageName.startsWith("."))
//         {
//            packageName = packageName.substring(1);
//         }
//
//         display.packageField().setValue(packageName);
//      }
    }

    /**
     *
     */
    private void doCreate() {
        if (display.classNameField().getValue() == null || display.classNameField().getValue().isEmpty()) {
            return;
        }
        try {
            switch (JavaTypes.valueOf(display.classTypeField().getValue().toUpperCase())) {
                case CLASS:
                    createClass(display.classNameField().getValue());
                    break;

                case INTERFACE:
                    createInterface(display.classNameField().getValue());
                    break;

                case ENUM:
                    createEnum(display.classNameField().getValue());
                    break;

                case ANNOTATION:
                    createAnnotation(display.classNameField().getValue());
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAnnotation(String name) {
        StringBuilder content = new StringBuilder(getPackage());
        content.append("public @interface ").append(name).append(TYPE_CONTENT);
        createClassFile(name, content.toString());
    }

    private void createEnum(String name) {
        StringBuilder content = new StringBuilder(getPackage());
        content.append("public enum ").append(name).append(TYPE_CONTENT);
        createClassFile(name, content.toString());

    }

    private void createInterface(String name) {
        StringBuilder content = new StringBuilder(getPackage());
        content.append("public interface ").append(name).append(TYPE_CONTENT);
        createClassFile(name, content.toString());
    }

    private void createClass(String name) {
        StringBuilder content = new StringBuilder(getPackage());
        content.append("public class ").append(name).append(TYPE_CONTENT);
        createClassFile(name, content.toString());
    }

    private FileModel createdClass;

    private void createClassFile(final String fileName, final String fileContent) {
        createdClass = null;
        JavaProject javaProject = (JavaProject)((ItemContext)selectedItem).getProject();

        String sourceFolderName = display.sourceFolderField().getValue();
        String packageName = display.packageField().getValue();
        if (packageName.equals(DEFAULT_PACKAGE)) {
            packageName = "";
        }

//
//      if (path == null)
//      {
//         return;
//      }
//
//      path = project.getPath() + "/" + path;


        for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories()) {
            if (sourceDirectory.getSourceDirectoryName().equals(sourceFolderName)) {
                for (final Package _package : sourceDirectory.getPackages()) {
                    if (_package.getPackageName().equals(packageName)) {
                        final FileModel newFile = new FileModel(fileName + ".java", MimeType.APPLICATION_JAVA, fileContent, _package);

                        if (_package.getLinks().isEmpty()) {
                            try {
                                VirtualFileSystem.getInstance()
                                                 .getItemById(_package.getId(),
                                                              new AsyncRequestCallback<ItemWrapper>(
                                                                                                    new ItemUnmarshaller(
                                                                                                                         new ItemWrapper(
                                                                                                                                         _package))) {

                                                                  @Override
                                                                  protected void onSuccess(ItemWrapper result) {
                                                                      _package.setLinks(result.getItem().getLinks());
                                                                      createJavaFile(_package, newFile);
                                                                  }

                                                                  @Override
                                                                  protected void onFailure(Throwable exception) {
                                                                      IDE.fireEvent(new ExceptionThrownEvent(exception));
                                                                  }
                                                              });
                            } catch (RequestException e) {
                                IDE.fireEvent(new ExceptionThrownEvent(e));
                            }
                        } else {
                            createJavaFile(_package, newFile);
                        }

                        return;
                    }
                }
            }
        }

      /*

      if (!DEFAULT_PACKAGE.equals(packageName))
      {
         String packagePath = packageName.replaceAll("\\.", "/");
         path += "/" + packagePath;
      }

      try
      {
         VirtualFileSystem.getInstance().getItemByPath(path,
            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper(new FolderModel())))
            {
               @Override
               protected void onSuccess(ItemWrapper result)
               {
                  classParentFolder = (FolderModel)result.getItem();
                  FileModel newFile = new FileModel(fileName + ".java", MimeType.APPLICATION_JAVA, fileContent, classParentFolder);
                  try
                  {
                     vfs.createFile(classParentFolder, new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile))
                     {
                        @Override
                        protected void onSuccess(FileModel result)
                        {
                           IDE.getInstance().closeView(display.asView().getId());
                           result.setProject(project);
                           fileOpenedHandler = IDE.addHandler(EditorActiveFileChangedEvent.TYPE, CreateJavaClassPresenter.this);
                           IDE.fireEvent(new OpenFileEvent(result));
                        }

                        @Override
                        protected void onFailure(Throwable exception)
                        {
                           IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                     });
                  }
                  catch (RequestException e)
                  {
                     IDE.fireEvent(new ExceptionThrownEvent(e));
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
      */
    }
    
    private void createJavaFile(final Package _package, FileModel newFile) {
        try {
            vfs.createFile(_package, new AsyncRequestCallback<FileModel>(new FileUnmarshaller(newFile)) {
                @Override
                protected void onSuccess(FileModel result) {
                    createdClass = result;
                    IDE.getInstance().closeView(display.asView().getId());
                    IDE.addHandler(TreeRefreshedEvent.TYPE, CreateJavaClassPresenter.this);
                    IDE.fireEvent(new RefreshBrowserEvent(_package.getProject(), result));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private String getPackage() {
        if (DEFAULT_PACKAGE.equals(display.packageField().getValue())) {
            return "";
        }

        String packageName = display.packageField().getValue();
        return "package " + packageName + ";\n\n";
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform
     * .ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
    @Override
    public void onEditorActiveFileChanged(final EditorActiveFileChangedEvent event) {
//      fileOpenedHandler.removeHandler();
//      Scheduler.get().scheduleDeferred(new ScheduledCommand()
//      {
//         @Override
//         public void execute()
//         {
//            IDE.fireEvent(new RefreshBrowserEvent(classParentFolder, event.getFile()));
//         }
//      });
    }

    @Override
    public void onTreeRefreshed(TreeRefreshedEvent event) {
        IDE.removeHandler(TreeRefreshedEvent.TYPE, this);
        if (createdClass != null) {
            Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                @Override
                public void execute() {
                    try {
                        JavaProject javaProject = (JavaProject)((ItemContext)selectedItem).getProject();
                        FileModel file = (FileModel)javaProject.getResource(createdClass.getPath());
                        createdClass = null;
                        IDE.fireEvent(new OpenFileEvent(file));

                    } catch (Exception e) {
                        IDE.fireEvent(new ExceptionThrownEvent(e.getMessage()));
                    }
                }
            });
        }

    }

}
