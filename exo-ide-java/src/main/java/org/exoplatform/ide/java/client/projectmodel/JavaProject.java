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
package org.exoplatform.ide.java.client.projectmodel;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import org.exoplatform.ide.core.event.ResourceChangedEvent;
import org.exoplatform.ide.java.client.core.JavaConventions;
import org.exoplatform.ide.java.client.core.JavaCore;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Link;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.rest.AsyncRequest;
import org.exoplatform.ide.rest.AsyncRequestCallback;
import org.exoplatform.ide.rest.HTTPHeader;
import org.exoplatform.ide.rest.MimeType;
import org.exoplatform.ide.runtime.IStatus;

/**
 * A Java project represents a view of a project resource in terms of Java
 * elements such as package , compilation units.
 * A project may contain several source folders, which contain packages.
 * JavaProject overrides <code>createFolder</code> and <code>createFile</code>,
 * implementations try to create package or compilation unit if it's possible, 
 * else fall back to super implementations of this methods.   
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class JavaProject extends Project
{

   /**
    * Primary nature for Java-scpecific project
    */
   public static final String PRIMARY_NATURE = "java";

   /** Java-scpecific project description */
   private JavaProjectDesctiprion description;

   /**
    * @param eventBus
    */
   protected JavaProject(EventBus eventBus)
   {
      super(eventBus);
      this.description = new JavaProjectDesctiprion(this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public JavaProjectDesctiprion getDescription()
   {
      return description;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   protected void refreshTree(Folder root, final AsyncCallback<Folder> callback)
   {
      try
      {
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<Folder> internalCallback =
            new AsyncRequestCallback<Folder>(new JavaModelUnmarshaller(root, (JavaProject)root.getProject()))
            {
               @Override
               protected void onSuccess(Folder refreshedRoot)
               {
                  callback.onSuccess(refreshedRoot);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         String url = vfsInfo.getUrlTemplates().get(Link.REL_TREE).getHref();
         url = URL.decode(url).replace("[id]", root.getId());
         AsyncRequest.build(RequestBuilder.GET, URL.encode(url)).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * Create new Java package. 
    * This method check package name, and if name not valid call <code>onFailure</code> callback method with 
    * JavaModelException 
    * @param parent the source folder where create package
    * @param name the name of new package
    * @param callback
    */
   public void createPackage(final SourceFolder parent, String name, final AsyncCallback<Package> callback)
   {
      try
      {
         checkItemValid(parent);
         if (!checkPackageName(name))
         {
            callback.onFailure(new JavaModelException("Package name not valid"));
            return;
         }
         Folder folderParent = findFolderParent(parent, name);
         String packagePartName;
         if (folderParent == null)
         {
            folderParent = parent;
            packagePartName = name;
         }
         else
         {
            packagePartName = name.substring(parent.getName().length() + 1);
         }
         final Folder packageParent = folderParent;
         final String path = packagePartName.replaceAll("\\.", "/");
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<Package> internalCallback =
            new AsyncRequestCallback<Package>(new PackageUnmarshaller(new Package()))
            {
               @Override
               protected void onSuccess(final Package pack)
               {

                  if (path.contains("/"))
                  {
                     // refresh tree, cause additional hierarchy folders my have been created
                     refreshTree(packageParent, new AsyncCallback<Folder>()
                     {
                        @Override
                        public void onSuccess(Folder result)
                        {
                           Package newPackage = (Package)parent.findResourceById(pack.getId());
                           eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newPackage));
                           callback.onSuccess(newPackage);
                        }

                        @Override
                        public void onFailure(Throwable exception)
                        {
                           callback.onFailure(exception);
                        }
                     });
                  }
                  else
                  {
                     // add to the list of items
                     parent.addChild(pack);
                     // set proper parent project
                     pack.setProject(JavaProject.this);
                     eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(pack));
                     callback.onSuccess(pack);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         String url = folderParent.getLinkByRelation(Link.REL_CREATE_FOLDER).getHref();
         String urlString = URL.decode(url).replace("[name]", path);
         urlString = URL.encode(urlString);
         loader.setMessage("Creating new package...");
         AsyncRequest.build(RequestBuilder.POST, urlString).loader(loader).send(internalCallback);

      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * Create new Compilation Unit (Java file).
    * Compilation unit may created only in Packages or SourceFolder's.
    * @param parent the parent, must be instance of Package or SourceFolder.
    * @param name the name of new compilation unit
    * @param content the content of compilation unit
    * @param callback
    */
   public void createCompilationUnit(final Folder parent, String name, String content,
                                     final AsyncCallback<CompilationUnit> callback)
   {
      try
      {
         checkItemValid(parent);
         checkCompilationUnitParent(parent);
         checkCompilationUnitName(name);
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<CompilationUnit> internalCallback =
            new AsyncRequestCallback<CompilationUnit>(new CompilationUnitUnmarshaller(new CompilationUnit()))
            {
               @Override
               protected void onSuccess(CompilationUnit newCU)
               {
                  // add to the list of items
                  parent.addChild(newCU);
                  // set proper parent project
                  newCU.setProject(JavaProject.this);
                  eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(newCU));
                  callback.onSuccess(newCU);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         String url = parent.getLinkByRelation(Link.REL_CREATE_FILE).getHref();
         url = URL.decode(url).replace("[name]", name);
         url = URL.encode(url);
         loader.setMessage("Creating new compilation unit...");
         AsyncRequest.build(RequestBuilder.POST, url).data(content).header(HTTPHeader.CONTENT_TYPE, mimeType)
            .loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * Find folder where package must be created, if such folder not exist then return null.
    * @param parent
    * @param name
    * @return
    */
   protected Folder findFolderParent(SourceFolder parent, String name)
   {
      Folder result = null;
      int longestMatch = 0;
      String[] newPackages = name.split("\\.");
      for (Resource r : parent.getChildren().asIterable())
      {
         if (r instanceof Package)
         {
            if (name.startsWith(r.getName()) && r.getName().length() > longestMatch)
            {
               //additional check for situation if parent package partial match:
               // "com.exo.ide.cli" - exist, and we try to create "com.exo.ide.client" package,
               //in this case parent folder for this package must be "com.exo.ide" not com.exo.ide.cli
               String packName = r.getName();
               String[] split = packName.split("\\.");
               String lastPackage = split[split.length - 1];
               if (newPackages.length > split.length && newPackages[split.length - 1].equals(lastPackage))
               {
                  longestMatch = r.getName().length();
                  result = (Folder)r;
               }
            }
         }
      }
      return result;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void createFolder(Folder parent, String name, AsyncCallback<Folder> callback)
   {
      if (parent instanceof JavaProject && description.getSourceFolders().contains(name))
      {
         createSourceFolder((JavaProject)parent, name, callback);
         return;
      }
      else if (checkPackageName(name))
      {
         if (parent instanceof SourceFolder)
         {
            createFolderAsPackage(parent, name, callback);
            return;
         }
         else if (parent instanceof Package)
         {
            createFolderAsPackage(parent.getParent(), name, callback);
            return;
         }
      }

      super.createFolder(parent, name, callback);
   }

   private void createSourceFolder(final JavaProject parent, String name, final AsyncCallback<Folder> callback)
   {
      try
      {
         // create internal wrapping Request Callback with proper Unmarshaller
         AsyncRequestCallback<SourceFolder> internalCallback =
            new AsyncRequestCallback<SourceFolder>(new SourceFolderUnmarshaller(new SourceFolder(), parent.getPath()))
            {
               @Override
               protected void onSuccess(SourceFolder srcFolder)
               {
                  // add to the list of items
                  parent.addChild(srcFolder);
                  // set proper parent project
                  srcFolder.setProject(JavaProject.this);
                  eventBus.fireEvent(ResourceChangedEvent.createResourceCreatedEvent(srcFolder));
                  callback.onSuccess(srcFolder);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  callback.onFailure(exception);
               }
            };

         String url = parent.getLinkByRelation(Link.REL_CREATE_FOLDER).getHref();
         String urlString = URL.decode(url).replace("[name]", name);
         urlString = URL.encode(urlString);
         loader.setMessage("Creating new source folder...");
         AsyncRequest.build(RequestBuilder.POST, urlString).loader(loader).send(internalCallback);
      }
      catch (Exception e)
      {
         callback.onFailure(e);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void createFile(Folder parent, String name, String content, String mimeType,
                          final AsyncCallback<File> callback)
   {
      if (parent instanceof SourceFolder || parent instanceof Package)
      {

         if (MimeType.APPLICATION_JAVA.equals(mimeType))
         {
            createCompilationUnit(parent, name, content, new AsyncCallback<CompilationUnit>()
            {

               @Override
               public void onSuccess(CompilationUnit result)
               {
                  callback.onSuccess(result);
               }

               @Override
               public void onFailure(Throwable caught)
               {
                  callback.onFailure(caught);
               }
            });
            return;
         }

      }
      super.createFile(parent, name, content, mimeType, callback);
   }

   /**
    *
    * @param parent
    * @param name
    * @param callback
    */
   private void createFolderAsPackage(Folder parent, String name, final AsyncCallback<Folder> callback)
   {
      createPackage((SourceFolder)parent, name.replaceAll("/", "."), new AsyncCallback<Package>()
      {

         @Override
         public void onFailure(Throwable caught)
         {
            callback.onFailure(caught);
         }

         @Override
         public void onSuccess(Package result)
         {
            callback.onSuccess(result);
         }
      });
   }

   /**
    * Check is parent instance of Package or Source folder 
    * @param parent
    * @throws JavaModelException
    */
   protected void checkCompilationUnitParent(Folder parent) throws JavaModelException
   {
      if (!(parent instanceof Package) && !(parent instanceof SourceFolder))
      {
         throw new JavaModelException("CompilationUnit must be child of 'Package' or 'SourceFolder'");
      }
   }

   /**
    * Check package name.
    *  <p>
    * The syntax of a package name corresponds to PackageName as
    * defined by PackageDeclaration (JLS2 7.4). For example, <code>"java.lang"</code>.
    * <p>
    * @param name
    */
   protected boolean checkPackageName(String name)
   {
      //TODO infer COMPILER_SOURCE and COMPILER_COMPLIANCE to project properties
      IStatus status =
         JavaConventions.validatePackageName(name, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
            JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
      return status.getSeverity() != IStatus.ERROR;

   }

   /**
    * Check the given compilation unit name.
    * <p>
    * A compilation unit name must obey the following rules:
    * <ul>
    * <li> it must not be null
    * <li> it must be suffixed by a dot ('.') followed by one of the java like extension
    * <li> its prefix must be a valid identifier
    * </ul>
    * </p>
    * @param name
    * @throws JavaModelException
    */
   private void checkCompilationUnitName(String name) throws JavaModelException
   {
      //TODO infer COMPILER_SOURCE and COMPILER_COMPLIANCE to project properties
      IStatus status =
         JavaConventions.validateCompilationUnitName(name, JavaCore.getOption(JavaCore.COMPILER_SOURCE),
            JavaCore.getOption(JavaCore.COMPILER_COMPLIANCE));
      if (status.getSeverity() == IStatus.ERROR)
      {
         throw new JavaModelException(status.getMessage());
      }
   }
}
