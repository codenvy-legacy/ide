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
package org.exoplatform.ide.java.client.wizard;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.java.client.JavaClientBundle;
import org.exoplatform.ide.java.client.core.JavaConventions;
import org.exoplatform.ide.java.client.core.JavaCore;
import org.exoplatform.ide.java.client.projectmodel.JavaProject;
import org.exoplatform.ide.java.client.projectmodel.Package;
import org.exoplatform.ide.java.client.projectmodel.SourceFolder;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.runtime.IStatus;
import org.exoplatform.ide.util.loging.Log;
import org.exoplatform.ide.wizard.AbstractWizardPagePresenter;
import org.exoplatform.ide.wizard.WizardPagePresenter;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class NewPackagePagePresenter extends AbstractWizardPagePresenter implements NewPackagePageView.ActionDelegate
{

   private NewPackagePageView view;

   private Project project;

   private boolean notJavaProject;

   private JsonArray<String> parentNames;

   private JsonArray<Folder> parents;

   private Folder parent;

   private boolean isPackageNameValid;

   private String errorMessage;

   @Inject
   public NewPackagePagePresenter(NewPackagePageView view, ResourceProvider resourceProvider)
   {
      super("New Java Package", JavaClientBundle.INSTANCE.packageItem());
      this.view = view;
      view.setDelegate(this);
      project = resourceProvider.getActiveProject();
      init();
   }

   private void init()
   {
      if (project instanceof JavaProject)
      {
         JavaProject javaProject = (JavaProject)project;
         parentNames = JsonCollections.createArray();
         parents = JsonCollections.createArray();
         for (SourceFolder sf : javaProject.getSourceFolders().asIterable())
         {
            parentNames.add(sf.getName());
            parents.add(sf);
            for (Resource r : sf.getChildren().asIterable())
            {
               if (r instanceof Package)
               {
                  parentNames.add(r.getName());
                  parents.add((Folder)r);
               }
            }
         }
         view.setParents(parentNames);
      }
      else
      {
         notJavaProject = true;
      }
      parent = parents.get(0);
   }

   @Override
   public WizardPagePresenter flipToNext()
   {
      return null;
   }

   @Override
   public boolean canFinish()
   {
      return isCompleted();
   }

   @Override
   public boolean hasNext()
   {
      return false;
   }

   @Override
   public boolean isCompleted()
   {
      return !notJavaProject && isPackageNameValid;
   }

   @Override
   public String getNotice()
   {
      if (notJavaProject)
      {
         return project.getName() + " is not Java project";
      }
      if (!isPackageNameValid)
      {
         return errorMessage;
      }
      else
      {
         if (errorMessage != null)
         {
            return errorMessage;
         }
      }
      return "Create a new Java package.";
   }

   @Override
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
      checkPackageName();
   }

   @Override
   public void parentChanged(int index)
   {
      parent = parents.get(index);
   }

   @Override
   public void checkPackageName()
   {
      validate(view.getPackageName());
      delegate.updateControls();
   }

   @Override
   public void doFinish()
   {
      SourceFolder parentSourceFolder;
      String parentName;
      if (parent instanceof SourceFolder)
      {
         parentSourceFolder = (SourceFolder)parent;
         parentName = "";
      }
      else
      {
         parentSourceFolder = (SourceFolder)parent.getParent();
         parentName = parent.getName() + '.';
      }

      ((JavaProject)project).createPackage(parentSourceFolder, parentName + view.getPackageName(), new AsyncCallback<Package>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Log.error(NewPackagePagePresenter.class, caught);
         }

         @Override
         public void onSuccess(Package result)
         {
         }
      });
      super.doFinish();
   }

   private void validate(String value)
   {
      IStatus status =
         JavaConventions.validatePackageName(value, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
            JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
      switch (status.getSeverity())
      {
         case IStatus.WARNING:
            errorMessage = status.getMessage();
            isPackageNameValid = true;
            break;
         case IStatus.OK:
            isPackageNameValid = true;
            errorMessage = null;
            break;

         default:
            isPackageNameValid = false;
            errorMessage = status.getMessage();
            break;
      }
   }
}
