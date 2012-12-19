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
import org.exoplatform.ide.core.editor.EditorAgent;
import org.exoplatform.ide.java.client.JavaClientBundle;
import org.exoplatform.ide.java.client.core.JavaConventions;
import org.exoplatform.ide.java.client.core.JavaCore;
import org.exoplatform.ide.java.client.projectmodel.CompilationUnit;
import org.exoplatform.ide.java.client.projectmodel.JavaProject;
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
public class NewJavaClassPagePresenter extends AbstractWizardPagePresenter
   implements NewJavaClassPageView.ActionDelegate
{

   private enum JavaTypes
   {
      CLASS("Class"), INTERFACE("Interface"), ENUM("Enum"), ANNOTATION("Annotation");

      private String value;

      private JavaTypes(String value)
      {
         this.value = value;
      }

      /**
       * @see java.lang.Enum#toString()
       */
      @Override
      public String toString()
      {
         return value;
      }
   }

   private static final String TYPE_CONTENT = "\n{\n}";

   private static final String DEFAULT_PACKAGE = "(default package)";

   public static final String CAPTION = "Create new Java class.";

   private final Project activeProject;

   private NewJavaClassPageView view;

   private EditorAgent editorAgent;

   private JsonArray<Folder> parents;

   private Folder parent;

   private String errorMessage;

   private boolean isTypeNameValid;

   @Inject
   public NewJavaClassPagePresenter(NewJavaClassPageView view, ResourceProvider provider, EditorAgent editorAgent)
   {
      super(CAPTION, JavaClientBundle.INSTANCE.newClassWizz());
      this.view = view;
      this.editorAgent = editorAgent;
      activeProject = provider.getActiveProject();
      view.setDelegate(this);
      init();
   }

   private void init()
   {
      JsonArray<String> classTypes = JsonCollections.createArray();
      for (JavaTypes t : JavaTypes.values())
      {
         classTypes.add(t.toString());
      }
      view.setClassTypes(classTypes);
      JavaProject javaProject = (JavaProject)activeProject;
      JsonArray<String> parentNames = JsonCollections.createArray();
      parents = JsonCollections.createArray();
      for (SourceFolder sf : javaProject.getSourceFolders().asIterable())
      {
         parentNames.add(sf.getName() + " " + DEFAULT_PACKAGE);
         parents.add(sf);
         for (Resource r : sf.getChildren().asIterable())
         {
            if (r instanceof org.exoplatform.ide.java.client.projectmodel.Package)
            {
               parentNames.add(r.getName());
               parents.add((Folder)r);
            }
         }
      }
      view.setParents(parentNames);
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
      return isTypeNameValid;
   }

   @Override
   public String getNotice()
   {
      if (!isTypeNameValid)
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
      return null;
   }

   @Override
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }

   @Override
   public void doFinish()
   {
      doCreate();
      super.doFinish();

   }

   private void doCreate()
   {
      if (view.getClassName() == null || view.getClassName().isEmpty())
      {
         return;
      }

      try
      {
         switch (JavaTypes.valueOf(view.getClassType().toUpperCase()))
         {
            case CLASS:
               createClass(view.getClassName());
               break;

            case INTERFACE:
               createInterface(view.getClassName());
               break;

            case ENUM:
               createEnum(view.getClassName());
               break;

            case ANNOTATION:
               createAnnotation(view.getClassName());
               break;
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private String createCassName()
   {
      return view.getClassName() + ".java";
   }

   private void createAnnotation(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public @interface ").append(name).append(TYPE_CONTENT);
      createClassFile(content.toString());
   }

   private void createEnum(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public enum ").append(name).append(TYPE_CONTENT);
      createClassFile(content.toString());

   }

   private void createInterface(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public interface ").append(name).append(TYPE_CONTENT);
      createClassFile(content.toString());
   }

   private void createClass(String name)
   {
      StringBuilder content = new StringBuilder(getPackage());
      content.append("public class ").append(name).append(TYPE_CONTENT);
      createClassFile(content.toString());
   }

   private String getPackage()
   {
      if (parent instanceof SourceFolder)
      {
         return "\n";
      }

      String packageName = parent.getName();
      return "package " + packageName + ";\n\n";
   }

   private void createClassFile(final String fileContent)
   {
      ((JavaProject)activeProject).createCompilationUnit(parent, createCassName(), fileContent, new AsyncCallback<CompilationUnit>()
      {
         @Override
         public void onFailure(Throwable caught)
         {
            Log.error(NewJavaClassPagePresenter.class, caught);
         }

         @Override
         public void onSuccess(CompilationUnit result)
         {
            editorAgent.openEditor(result);
         }
      });
   }

   @Override
   public void parentChanged(int index)
   {
      parent = parents.get(index);
   }

   @Override
   public void checkTypeName()
   {
      validate(createCassName());
      delegate.updateControls();
   }

   private void validate(String value)
   {
      IStatus status =
         JavaConventions.validateCompilationUnitName(value, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
            JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
      switch (status.getSeverity())
      {
         case IStatus.WARNING:
            errorMessage = status.getMessage();
            isTypeNameValid = true;
            break;
         case IStatus.OK:
            isTypeNameValid = true;
            errorMessage = null;
            break;

         default:
            isTypeNameValid = false;
            errorMessage = status.getMessage();
            break;
      }
   }
}
