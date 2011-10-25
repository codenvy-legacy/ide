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
package org.exoplatform.ide.template;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.everrest.core.impl.provider.json.ArrayValue;
import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.everrest.core.impl.provider.json.ObjectValue;
import org.exoplatform.ide.FileTemplate;
import org.exoplatform.ide.FolderTemplate;
import org.exoplatform.ide.ProjectTemplate;
import org.exoplatform.ide.Template;
import org.exoplatform.ide.helper.JsonHelper;
import org.exoplatform.ide.helper.ParsingResponseException;
import org.exoplatform.ide.vfs.server.ContentStream;
import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.jcr.RepositoryException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

/**
 * This REST service is used for getting and storing templates
 * for file and projects. 
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplatesRestService.java Apr 4, 2011 3:21:46 PM vereshchaka $
 *
 */
@Path("/ide/templates")
public class TemplatesRestService
{

   /**
    * File name filter. Need to filter non "zip" files.
    */
   private static FilenameFilter projectsZipFilter = new FilenameFilter()
   {
      @Override
      public boolean accept(File dir, String name)
      {
         return name.endsWith(".zip");
      }
   };

   public static final String FILE_TEMPLATE_FILE = "fileTemplates.js";

   public static final String PROJECT_TEMPLATE_FILE = "projectTemplates.js";

   public static final String WEBDAV_SCHEME = "jcr-webdav";

   public static final String DEF_WS = "dev-monit";

   private static Log log = ExoLogger.getLogger(TemplatesRestService.class);

   private String workspace;

   private String config = "/ide-home/templates";

   private static final String FILE_TEMPLATE = "fileTemplates";

   private static final String PROJECT_TEMPLATE = "folderTemplates";

   private VirtualFileSystemRegistry vfsRegistry;

   public TemplatesRestService(String workspace, String templateConfig, VirtualFileSystemRegistry vfsRegistry)
   {
      this.workspace = workspace;
      this.vfsRegistry = vfsRegistry;
      if (templateConfig != null)
      {
         this.config = templateConfig;
         if (config.endsWith("/"))
            config = config.substring(0, config.length() - 1);
      }
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/project/list")
   public List<ProjectTemplate> getProjectTemplateList() throws URISyntaxException, IOException,
      ParsingResponseException
   {
      return getProjectTemplates();
   }

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/file/add")
   public void addFileTemplate(String body) throws IOException, URISyntaxException, TemplateServiceException,
      ParsingResponseException
   {
      JsonValue fileTemplateJsonValue = JsonHelper.parseJson(body);
      String newTemplateName = fileTemplateJsonValue.getElement("name").getStringValue();
      //check if such template already exists
      for (FileTemplate fileTemplate : getFileTemplates())
      {
         if (newTemplateName.equals(fileTemplate.getName()))
         {
            throw new TemplateServiceException("File template " + newTemplateName + " already exists");
         }
      }

      //add new file template to existing templates in file
      String templateContent = readTemplates(FILE_TEMPLATE);
      JsonValue templatesJsonValue = JsonHelper.parseJson(templateContent);
      addTemplateToArray(templatesJsonValue, fileTemplateJsonValue);

      writeTemplates(FILE_TEMPLATE, templatesJsonValue.toString());

   }

   /**
    * Add file template list to settings file template file.
    * This method is used only for templates transfer from registry to plain text file.
    * In order to avoid data loss, this method doesn't check, is file templates are
    * existed already. It only adds all file templates to file.
    * @param body
    * @throws IOException
    * @throws URISyntaxException
    * @throws TemplateServiceException
    * @throws ParsingResponseException 
    */
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/file/add/list")
   public void addFileTemplateList(String body) throws IOException, URISyntaxException, TemplateServiceException,
      ParsingResponseException
   {
      JsonValue newTemplateJsonValue = JsonHelper.parseJson(body);
      ArrayValue templatesToAdd = (ArrayValue)newTemplateJsonValue;

      //get array value of existing templates
      String templateContent = readTemplates(FILE_TEMPLATE);
      JsonValue jsonValue = JsonHelper.parseJson(templateContent);
      ArrayValue jsonTemplateArray = null;
      if (jsonValue.getElement("templates") != null)
      {
         jsonTemplateArray = (ArrayValue)jsonValue.getElement("templates");
      }
      else
      {
         jsonTemplateArray = new ArrayValue();
         jsonValue.addElement("templates", jsonTemplateArray);
      }

      Iterator<JsonValue> arrIterator = templatesToAdd.getElements();
      while (arrIterator.hasNext())
      {
         ObjectValue obj = (ObjectValue)arrIterator.next();
         jsonTemplateArray.addElement(obj);
      }

      writeTemplates(FILE_TEMPLATE, jsonValue.toString());

   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/file/list")
   public List<FileTemplate> getFileTemplateList() throws URISyntaxException, IOException, ParsingResponseException
   {
      return getFileTemplates();
   }

   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/file/get")
   public FileTemplate getFileTemplateByName(@QueryParam("name") String name) throws URISyntaxException, IOException,
      ParsingResponseException
   {
      return findFileTemplate(name);
   }

   @Path("/file/delete")
   @POST
   public void deleteFileTemplate(@QueryParam("name") String template) throws IOException, URISyntaxException,
      IllegalStateException, ParsingResponseException
   {
      deleteTemplate(template, FILE_TEMPLATE);
   }

   @Path("/project/delete")
   @POST
   public void deleteProjectTemplate(@QueryParam("name") String template) throws IOException, URISyntaxException,
      IllegalStateException, ParsingResponseException
   {
      deleteTemplate(template, PROJECT_TEMPLATE);
   }

   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/project/add")
   public void addProjectTemplate(String body) throws IOException, URISyntaxException, TemplateServiceException,
      ParsingResponseException
   {
      JsonValue newTemplateJsonValue = JsonHelper.parseJson(body);
      String newTemplateName = newTemplateJsonValue.getElement("name").getStringValue();
      //check if such template already exists
      for (ProjectTemplate projectTemplate : getProjectTemplateList())
      {
         if (newTemplateName.equals(projectTemplate.getName()))
         {
            throw new TemplateServiceException("Project template " + newTemplateName + " already exists");
         }
      }

      //add new file template to existing templates in file
      String templateContent = readTemplates(PROJECT_TEMPLATE);
      JsonValue jsonValue = JsonHelper.parseJson(templateContent);
      addTemplateToArray(jsonValue, newTemplateJsonValue);

      writeTemplates(PROJECT_TEMPLATE, jsonValue.toString());
   }

   /**
    * Add project template list to settings project template file.
    * This method is used only for templates transfer from registry to plain text file.
    * In order to avoid data loss, this method doesn't check, is file templates are
    * existed already. It only adds all file templates to file.
    * @param body
    * @throws IOException
    * @throws URISyntaxException
    * @throws TemplateServiceException
    * @throws ParsingResponseException 
    */
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/project/add/list")
   public void addProjectTemplateList(String body) throws IOException, URISyntaxException, TemplateServiceException,
      ParsingResponseException
   {
      JsonValue newTemplateJsonValue = JsonHelper.parseJson(body);
      ArrayValue templatesToAdd = (ArrayValue)newTemplateJsonValue;

      //get array value of existing templates
      String templateContent = readTemplates(PROJECT_TEMPLATE);
      JsonValue jsonValue = JsonHelper.parseJson(templateContent);
      ArrayValue jsonTemplateArray = null;
      if (jsonValue.getElement("templates") != null)
      {
         jsonTemplateArray = (ArrayValue)jsonValue.getElement("templates");
      }
      else
      {
         jsonTemplateArray = new ArrayValue();
         jsonValue.addElement("templates", jsonTemplateArray);
      }

      Iterator<JsonValue> arrIterator = templatesToAdd.getElements();
      while (arrIterator.hasNext())
      {
         ObjectValue obj = (ObjectValue)arrIterator.next();
         jsonTemplateArray.addElement(obj);
      }

      writeTemplates(PROJECT_TEMPLATE, jsonValue.toString());

   }

   /**
    * Create new IDE project from predefined tempate
    * @param vfsId id of VFS
    * @param name name of new project
    * @param parentId parent of the project
    * @param templateName name of the project template
    * @return created project
    * @throws VirtualFileSystemException
    * @throws IOException
    */
   @POST
   @Path("/project/create")
   @Produces(MediaType.APPLICATION_JSON)
   public Project createProjectFromTemplate(@QueryParam("vfsid") String vfsId, @QueryParam("name") String name,
      @QueryParam("parentId") String parentId, @QueryParam("templateName") String templateName)
      throws VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      Folder projectFolder = vfs.createFolder(parentId, name);
      InputStream templateStream =
         Thread.currentThread().getContextClassLoader().getResourceAsStream("projects/" + templateName + ".zip");
      if (templateStream == null)
         throw new InvalidArgumentException("Can't find " + templateName + ".zip");
      vfs.importZip(projectFolder.getId(), templateStream, true);
      org.exoplatform.ide.vfs.shared.Item projectItem = vfs.getItem(projectFolder.getId(), PropertyFilter.ALL_FILTER);
      if (projectItem instanceof Project)
      {
         return (Project)projectItem;
      }
      else
         throw new IllegalStateException("Something other than project was created on" + name);
   }

   //--------- Implementation -----------------------

   /**
    * Delete user template, that stored in file.
    * 
    * @param templateName - the name of template
    * @param fileTemplateName - the name of file (file templates and project templates are stored in different files)
    * @throws IOException
    * @throws IllegalStateException
    * @throws ParsingResponseException 
    */
   private void deleteTemplate(String templateName, String fileTemplateName) throws IOException, IllegalStateException,
      ParsingResponseException
   {
      if (templateName == null)
      {
         throw new IllegalStateException("Template name required. ");
      }

      //get the existing templates
      String templateContent = readTemplates(fileTemplateName);
      JsonValue jsonValue = JsonHelper.parseJson(templateContent);
      ArrayValue jsonTemplates = new ArrayValue();
      //iterate existed templates and copy to new array those, which will not be deleted.
      if (jsonValue.getElement("templates") != null)
      {
         ArrayValue jsonTemplateArray = (ArrayValue)jsonValue.getElement("templates");
         Iterator<JsonValue> arrIterator = jsonTemplateArray.getElements();

         boolean exists = false;

         //iterate throw existing templates, and copy all templates, except those,
         //which must be deleted to new json array
         while (arrIterator.hasNext())
         {
            ObjectValue obj = (ObjectValue)arrIterator.next();
            if (!obj.getElement("name").getStringValue().equals(templateName))
            {
               jsonTemplates.addElement(obj);
            }
            else
            {
               exists = true;
            }
         }
         if (!exists)
            throw new IllegalStateException("No such template. ");
      }

      //save new templates json array
      ObjectValue templatesObjValue = new ObjectValue();
      templatesObjValue.addElement("templates", jsonTemplates);

      writeTemplates(fileTemplateName, templatesObjValue.toString());
   }

   private FileTemplate findFileTemplate(String name) throws IOException, ParsingResponseException
   {
      //get the existing templates
      String templateContent = getTemplateFileContent(FILE_TEMPLATE_FILE);
      JsonValue jsonValue = JsonHelper.parseJson(templateContent);
      ArrayValue jsonTemplateArray = (ArrayValue)jsonValue.getElement("templates");
      Iterator<JsonValue> arrIterator = jsonTemplateArray.getElements();

      //iterate throw existing templates
      while (arrIterator.hasNext())
      {
         ObjectValue obj = (ObjectValue)arrIterator.next();
         if (obj.getElement("name").getStringValue().equals(name))
         {
            return parseFileTemplateObject(obj);
         }
      }
      return null;
   }

   private List<ProjectTemplate> getProjectTemplates() throws URISyntaxException, IOException, ParsingResponseException
   {
      List<ProjectTemplate> projectTemplateList = new ArrayList<ProjectTemplate>();
      //      String templateContent = readTemplates(PROJECT_TEMPLATE);
      //      JsonValue jsonValue = JsonHelper.parseJson(templateContent);
      //      if (jsonValue.getElement("templates") != null)
      //      {
      //         ArrayValue jsonTemplateArray = (ArrayValue)jsonValue.getElement("templates");
      //         Iterator<JsonValue> arrIterator = jsonTemplateArray.getElements();
      //
      //         while (arrIterator.hasNext())
      //         {
      //            ObjectValue obj = (ObjectValue)arrIterator.next();
      //            projectTemplateList.add(parseProjectTemplateObject(obj));
      //         }
      //      }

      URL url = Thread.currentThread().getContextClassLoader().getResource("projects");
      if (url != null)
      {
         File projectsFolder = new File(url.toURI());
         File[] projects = projectsFolder.listFiles(projectsZipFilter);
         for (File f : projects)
         {
            ZipFile zip = new ZipFile(f);
            ZipArchiveEntry entry = zip.getEntry(".project");
            //if zip not contains ".project" file then search in next archive
            if (entry == null)
               continue;
            JsonParser jp = new JsonParser();
            try
            {
               jp.parse(zip.getInputStream(entry));
               ConvertibleProperty[] array =
                  (ConvertibleProperty[])ObjectBuilder.createArray(ConvertibleProperty[].class, jp.getJsonObject());
               List<ConvertibleProperty> properties = Arrays.asList(array);
               String name = f.getName();
               name = name.substring(0, name.lastIndexOf(".zip"));
               projectTemplateList.add(createTemplateFromMethaData(properties, name));
            }
            catch (JsonException e)
            {
               throw new RuntimeException(e.getMessage(), e);
            }

         }
      }

      return projectTemplateList;
   }

   private ProjectTemplate createTemplateFromMethaData(List<ConvertibleProperty> properties, String templateName)
   {
      ProjectTemplate template = new ProjectTemplate();
      template.setDefault(true);
      template.setName(templateName);
      for (ConvertibleProperty prop : properties)
      {
         String name = prop.getName();
         if ("vfs:projectType".equals(name))
         {
            template.setType(prop.getValue().get(0));
         }
         else if ("exoide:projectDescription".equals(name))
         {
            template.setDescription(prop.getValue().get(0));
         }
      }
      return template;
   }

   private List<FileTemplate> getFileTemplates() throws URISyntaxException, IOException, ParsingResponseException
   {
      String templateContent = readTemplates(FILE_TEMPLATE);
      List<FileTemplate> fileTemplateList = new ArrayList<FileTemplate>();

      JsonValue jsonValue = JsonHelper.parseJson(templateContent);
      if (jsonValue.getElement("templates") == null)
         return fileTemplateList;

      ArrayValue jsonTemplateArray = (ArrayValue)jsonValue.getElement("templates");
      Iterator<JsonValue> arrIterator = jsonTemplateArray.getElements();

      while (arrIterator.hasNext())
      {
         ObjectValue obj = (ObjectValue)arrIterator.next();
         fileTemplateList.add(parseFileTemplateObject(obj));
      }

      return fileTemplateList;
   }

   //   private ProjectTemplate parseProjectTemplateObject(ObjectValue obj)
   //   {
   //      ProjectTemplate projectTemplate = new ProjectTemplate();
   //      projectTemplate.setName(obj.getElement("name").getStringValue());
   //      projectTemplate.setDefault(obj.getElement("isDefault").getBooleanValue());
   //      if (obj.getElement("description") != null)
   //      {
   //         projectTemplate.setDescription(obj.getElement("description").getStringValue());
   //      }
   //      if (obj.getElement("type") != null)
   //      {
   //         projectTemplate.setType(obj.getElement("type").getStringValue());
   //      }
   //      if (obj.getElement("children") != null)
   //      {
   //         ArrayValue childrenArray = (ArrayValue)obj.getElement("children");
   //         projectTemplate.getChildren().addAll(parseChildrenArrayJsonObject(childrenArray));
   //      }
   //      return projectTemplate;
   //   }

   private List<Template> parseChildrenArrayJsonObject(ArrayValue childrenArray)
   {
      List<Template> childrenTemplates = new ArrayList<Template>();

      Iterator<JsonValue> arrIterator = childrenArray.getElements();
      while (arrIterator.hasNext())
      {
         ObjectValue child = (ObjectValue)arrIterator.next();
         if ("file".equals(child.getElement("childType").getStringValue()))
         {
            FileTemplate fileTemplate = new FileTemplate();
            fileTemplate.setName(child.getElement("name").getStringValue());
            fileTemplate.setFileName(child.getElement("fileName").getStringValue());
            childrenTemplates.add(fileTemplate);
         }
         else
         {
            childrenTemplates.add(parseFolderTemplateObject(child));
         }
      }
      return childrenTemplates;
   }

   /**
    * Parse folder template json object, when get the list of project templates.
    * @param obj
    * @return
    */
   private FolderTemplate parseFolderTemplateObject(ObjectValue obj)
   {
      FolderTemplate folderTemplate = new FolderTemplate();
      folderTemplate.setName(obj.getElement("name").getStringValue());
      if (obj.getElement("children") != null)
      {
         ArrayValue childrenArray = (ArrayValue)obj.getElement("children");
         folderTemplate.getChildren().addAll(parseChildrenArrayJsonObject(childrenArray));
      }
      return folderTemplate;
   }

   /**
    * Parse file template json object, when get the list of file templates.
    * @param obj
    * @return
    */
   private FileTemplate parseFileTemplateObject(ObjectValue obj)
   {
      FileTemplate fileTemplate = new FileTemplate();
      fileTemplate.setName(obj.getElement("name").getStringValue());
      fileTemplate.setDescription(obj.getElement("description").getStringValue());
      fileTemplate.setMimeType(obj.getElement("mimeType").getStringValue());
      fileTemplate.setContent(obj.getElement("content").getStringValue());
      fileTemplate.setDefault(obj.getElement("isDefault").getBooleanValue());
      return fileTemplate;
   }

   private String getTemplateFileContent(String fileName) throws IOException
   {
      final String settingsFolderPath = System.getProperty("org.exoplatform.ide.server.settings-path");
      InputStream input =
         Thread.currentThread().getContextClassLoader().getResourceAsStream(settingsFolderPath + "/" + fileName);
      Writer writer = new StringWriter();
      char[] buffer = new char[1024];
      try
      {
         Reader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
         int n;
         while ((n = reader.read(buffer)) != -1)
         {
            writer.write(buffer, 0, n);
         }
      }
      finally
      {
         input.close();
      }
      String userConf = writer.toString();
      return userConf;
   }

   //----------read and write templates----------------

   protected void writeTemplates(String templateType, String data) throws IOException
   {
      try
      {
         VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);
         checkConfigNode(vfs);

         String id = "";
         try
         {
            Item item = vfs.getItemByPath(config + "/" + templateType, null, PropertyFilter.NONE_FILTER);
            id = item.getId();
            vfs.updateContent(id, MediaType.TEXT_PLAIN_TYPE, new ByteArrayInputStream(data.getBytes("UTF-8")), null);
         }
         catch (ItemNotFoundException infe)
         {
            String parentId = vfs.getItemByPath(config, null, PropertyFilter.NONE_FILTER).getId();
            vfs.createFile(parentId, templateType, MediaType.TEXT_PLAIN_TYPE,
               new ByteArrayInputStream(data.getBytes("UTF-8")));
         }
      }

      catch (VirtualFileSystemException e)
      {
         throw new WebApplicationException(e);
      }

   }

   /**
    * Check is user configuration folder exists.
    * If doesn't exist, than create it.
    * @param vfs2 
    * @throws RepositoryException
    * @throws VirtualFileSystemException 
    */
   private void checkConfigNode(VirtualFileSystem vfs) throws VirtualFileSystemException
   {
//      String _workspace = workspace;
      //      if (_workspace == null)
      //      {
      //         _workspace = repository.getConfiguration().getDefaultWorkspaceName();
      //      }

      try
      {
         vfs.createFolder(vfs.getInfo().getRoot().getId(), config.substring(1));
      }
      catch (ItemAlreadyExistException e)
      {
         //skip exception handling
      }
      //      Session sys = null;
      //      try
      //      {
      //         // Create node for users configuration under system session.
      //         sys = ((ManageableRepository)repository).getSystemSession(_workspace);
      //         if (!(sys.itemExists(config)))
      //         {
      //            org.exoplatform.ide.Utils.putFolders(sys, config);
      //            sys.save();
      //         }
      //      }
      //      finally
      //      {
      //         if (sys != null)
      //            sys.logout();
      //      }
   }

   /**
    * @param templateType - the name of file to read templates (file or folder templates).
    * @return
    * @throws IOException
    */
   protected String readTemplates(String templateType) throws IOException
   {
      try
      {
         String tokenPath = config + "/" + templateType;
         VirtualFileSystem vfs = vfsRegistry.getProvider(workspace).newInstance(null);

         ContentStream contentStream = null;
         try
         {
            contentStream = vfs.getContent(tokenPath, null);
         }
         catch (ItemNotFoundException e)
         {
            return "{}";//TODO: small hack add for supporting previos version of IDE. In 1.2 changed structure of user settings
         }

         InputStream input = contentStream.getStream();
         if (input == null)
         {
            return "{}";//TODO: small hack add for supporting previos version of IDE. In 1.2 changed structure of user settings
         }
         Writer writer = new StringWriter();
         char[] buffer = new char[1024];
         try
         {
            Reader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1)
            {
               writer.write(buffer, 0, n);
            }
         }
         finally
         {
            input.close();
         }
         String data = writer.toString();
         return data;
      }

      catch (VirtualFileSystemException e)
      {
         throw new WebApplicationException(e);
      }
   }

   /**
    * Add template to "templates" element of <code>templatesJsonValue</code>.
    * <p/>
    * If <code>templatesJsonValue</code> doesn't contain "templates" element, than create it.
    * @param templatesJsonValue
    * @param template
    */
   private void addTemplateToArray(JsonValue templatesJsonValue, JsonValue template)
   {
      if (templatesJsonValue.getElement("templates") != null)
      {
         ArrayValue jsonTemplateArray = (ArrayValue)templatesJsonValue.getElement("templates");
         jsonTemplateArray.addElement(template);
      }
      else
      {
         ArrayValue jsonTemplateArray = new ArrayValue();
         templatesJsonValue.addElement("templates", jsonTemplateArray);
         jsonTemplateArray.addElement(template);
      }
   }

}
