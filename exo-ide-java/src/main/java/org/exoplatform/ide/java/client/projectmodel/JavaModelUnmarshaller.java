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
package org.exoplatform.ide.java.client.projectmodel;

import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONValue;

import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;

import com.google.gwt.http.client.Response;

import org.exoplatform.ide.commons.exception.UnmarshallerException;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringSet;
import org.exoplatform.ide.json.JsonStringSet.IterationCallback;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Folder;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.resources.model.Resource;
import org.exoplatform.ide.rest.Unmarshallable;
import org.exoplatform.ide.util.loging.Log;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaModelUnmarshaller implements Unmarshallable<Folder>
{

   private static final String CHILDREN = "children";

   private static final String TYPE = "itemType";

   private static final String ITEM = "item";

   private static final String ID = "id";

   private static final String PATH = "path";

   private JavaProject project;

   private JsonStringSet sourceFolders;

   private String projectPath;

   /**
    * @param project
    */
   public JavaModelUnmarshaller(JavaProject project)
   {
      super();
      this.project = project;

      sourceFolders = JsonCollections.createStringSet();
      projectPath = project.getPath();
      project.getDescription().getSourceFolders().iterate(new IterationCallback()
      {
         @Override
         public void onIteration(String key)
         {
            sourceFolders.add(projectPath + (key.startsWith("/") ? key : "/" +key));
         }
      });
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void unmarshal(Response response) throws UnmarshallerException
   {
      try
      {
         JSONObject object = JSONParser.parseLenient(response.getText()).isObject();
         parseProjectStructure(object.get(CHILDREN), project, project);

      }
      catch (Exception exc)
      {
         String message = "Can't parse response " + response.getText();
         throw new UnmarshallerException(message, exc);
      }
   }

   private void parseProjectStructure(JSONValue children, Folder parentFolder, Project parentProject)
   {
      JSONArray itemsArray = children.isArray();
      for (int i = 0; i < itemsArray.size(); i++)
      {
         JSONObject itemObject = itemsArray.get(i).isObject();
         // Get item 
         JSONObject item = itemObject.get(ITEM).isObject();
         //         String id = item.get(ID).isString().stringValue();
         //
         String type = null;
         if (item.get(TYPE).isNull() == null)
         {
            type = item.get(TYPE).isString().stringValue();
         }

         // Project found in JSON Response
         if (Project.TYPE.equalsIgnoreCase(type))
         {
            Log.error(this.getClass(), "Unsupported operation. Unmarshalling a child projects is not supported");
         }
         // Folder
         else if (Folder.TYPE.equalsIgnoreCase(type))
         {
            Folder folder;
            String path = item.get(PATH).isString().stringValue();
            if (sourceFolders.contains(path))
            {
               folder = new SourceFolder(item, path.substring(projectPath.length() + 1));
               parentProject.addChild(folder);
               folder.setProject(parentProject);
               sourceFolders.remove(path);
               addPackages(itemObject.get(CHILDREN), folder, parentProject);
            }
            else
            {
               folder = new Folder(item);
               parentFolder.addChild(folder);
               folder.setProject(parentProject);
               parseProjectStructure(itemObject.get(CHILDREN), folder, parentProject);
            }
         }
         // File
         else if (File.TYPE.equalsIgnoreCase(type))
         {
            File file = new File(item);
            parentFolder.addChild(file);
            file.setProject(parentProject);
         }
         else
         {
            Log.error(this.getClass(), "Unsupported Resource type: " + type);
         }
      }
   }

   /**
    * @param jsonValue
    * @param folder
    * @param parentProject
    */
   private void addPackages(JSONValue jsonValue, Folder folder, Project parentProject)
   {
      // TODO Auto-generated method stub
      
   }

   //   /**
   //    * @param children
   //    * @param parentFolder
   //    * @param parentProject
   //    */
   //   private void getChildren(JSONValue children, Folder parentFolder, Project parentProject)
   //   {
   //      JSONArray itemsArray = children.isArray();
   //
   //      for (int i = 0; i < itemsArray.size(); i++)
   //      {
   //         JSONObject itemObject = itemsArray.get(i).isObject();
   //         // Get item 
   //         JSONObject item = itemObject.get(ITEM).isObject();
   //
   //         String id = item.get(ID).isString().stringValue();
   //
   //         String type = null;
   //         if (item.get(TYPE).isNull() == null)
   //         {
   //            type = item.get(TYPE).isString().stringValue();
   //         }
   //
   //         // Project found in JSON Response
   //         if (Project.TYPE.equalsIgnoreCase(type))
   //         {
   //            Log.error(this.getClass(), "Unsupported operation. Unmarshalling a child projects is not supported");
   //         }
   //         // Folder
   //         else if (Folder.TYPE.equalsIgnoreCase(type))
   //         {
   //            Folder folder;
   //            // find if Folder Object already exists. This is a refresh usecase.
   //            Resource existingFolder = parentFolder.findChildById(id);
   //            // Make sure found resource is Folder
   //            if (existingFolder != null && Folder.TYPE.equalsIgnoreCase(existingFolder.getResourceType()))
   //            {
   //               // use existing folder instance as 
   //               folder = (Folder)existingFolder;
   //            }
   //            else
   //            {
   //               String path = item.get(PATH).isString().stringValue();
   //               if (sourceFolders.contains(path))
   //               {
   //                  folder = new SourceFolder(item);
   //                  parentFolder.addChild(folder);
   //                  folder.setProject(parentProject);
   //               }
   //               else
   //               {
   //                  folder = new Folder(item);
   //                  parentFolder.addChild(folder);
   //                  folder.setProject(parentProject);
   //               }
   //            }
   //            // recursively get project
   //            getChildren(itemObject.get(CHILDREN), folder, parentProject);
   //         }
   //         // File
   //         else if (File.TYPE.equalsIgnoreCase(type))
   //         {
   //            File file = new File(item);
   //            parentFolder.addChild(file);
   //            file.setProject(parentProject);
   //         }
   //         else
   //         {
   //            Log.error(this.getClass(), "Unsupported Resource type: " + type);
   //         }
   //      }
   //   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Folder getPayload()
   {
      return project;
   }

}
