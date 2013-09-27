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

import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.eclipse.jdt.client.packaging.model.SourceDirectory;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaControl extends SimpleControl implements IDEControl, ItemsSelectedHandler {

    /** @param id */
    public JavaControl(String id) {
        super(id);
        setShowInContextMenu(true);
    }

    /** @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client
     * .framework.navigation.event.ItemsSelectedEvent) */
    @Override
    public void onItemsSelected(ItemsSelectedEvent event) {
        if (event.getSelectedItems().size() != 1) {
            setEnabled(false);
            return;
        }

        if (!(event.getSelectedItems().get(0) instanceof FileModel) && !(event.getSelectedItems().get(0) instanceof FolderModel)) {
            setEnabled(false);
            return;
        }

        Item item = event.getSelectedItems().get(0);
        ProjectModel project = ((ItemContext)item).getProject();

        if (project == null) {
            return;
        }

        if (!(project instanceof JavaProject)) {
            return;
        }

        JavaProject javaProject = (JavaProject)project;
        for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories()) {
            if (item.getPath().startsWith(sourceDirectory.getPath())) {
                setEnabled(true);
                return;
            }
        }

        setEnabled(false);


//      if (project != null)
//      {
//         if (event.getView() instanceof ProjectExplorerDisplay)
//         {
//            String sourcePath;
//            List<String> paths = getDefaultPaths();
//
//            if (project.hasProperty("sourceFolder"))
//            {
//               sourcePath = project.getPropertyValue("sourceFolder");
//               paths.add(sourcePath);
//            }
//
//            for (String path : getDefaultPaths())
//            {
//               if (item.getPath().startsWith((project.getPath().endsWith("/") ? project.getPath() : project.getPath() + "/") + path))
//               {
//                  setEnabled(true);
//                  return;
//               }
//            }
//         }
//         else if (event.getView() instanceof PackageExplorerDisplay)
//         {
//            setEnabled(isInResourceDirectory(item));
//            return;
//         }
//      }


//      if (event.getSelectedItems().size() == 1 && event.getSelectedItems().get(0) instanceof ItemContext)
//      {
//         Item item = event.getSelectedItems().get(0);
//         ProjectModel project = ((ItemContext)item).getProject();
//
//         if (project != null)
//         {
//            if (event.getView() instanceof ProjectExplorerDisplay)
//            {
//               String sourcePath;
//               List<String> paths = getDefaultPaths();
//
//               if (project.hasProperty("sourceFolder"))
//               {
//                  sourcePath = project.getPropertyValue("sourceFolder");
//                  paths.add(sourcePath);
//               }
//
//               for (String path : getDefaultPaths())
//               {
//                  if (item.getPath().startsWith((project.getPath().endsWith("/") ? project.getPath() : project.getPath() + "/") + path))
//                  {
//                     setEnabled(true);
//                     return;
//                  }
//               }
//            }
//            else if (event.getView() instanceof PackageExplorerDisplay)
//            {
//               setEnabled(isInResourceDirectory(item));
//               return;
//            }
//         }
//      }

//      setEnabled(false);
    }

//   private boolean isInResourceDirectory(Item item)
//   {
////      System.out.println("JavaControl.isInResourceDirectory()");
////      return false;
//
//      System.out.println("class >>> " + item.getClass().getName());
//      System.out.println("item >>>> " + item.getPath());
//      
//      return false;
//      
////      Project projectItem = PackageExplorerPresenter.getInstance().getProjectItem();
////
////      if (projectItem == null)
////      {
////         return false;
////      }
////
////      for (ResourceDirectory resourceDirectory : projectItem.getResourceDirectories())
////      {
////         if (item.getPath().startsWith(resourceDirectory.getFolder().getPath()))
////         {
////            return true;
////         }
////      }
////
////      return false;
//   }

    /** @see org.exoplatform.ide.client.framework.control.IDEControl#initialize() */
    @Override
    public void initialize() {
        setVisible(true);
        IDE.addHandler(ItemsSelectedEvent.TYPE, this);
    }

//   private List<String> getDefaultPaths()
//   {
//      List<String> sourceDirs = new ArrayList<String>();
//      sourceDirs.add("src/main/java");
//      sourceDirs.add("src/main/resources");
//      sourceDirs.add("src/test/java");
//      sourceDirs.add("src/test/resources");
//
//      return sourceDirs;
//   }

}