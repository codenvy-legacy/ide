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

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.Utils;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * This REST service is used for getting and storing templates
 * for projects. 
 * <p/>
 * When got template name, go to folder in resources, 
 * read the xml file with project structure, and create project structure.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplatesRestService.java Apr 4, 2011 3:21:46 PM vereshchaka $
 *
 */
@Path("/ide/templates")
public class TemplatesRestService
{
   private static final String TEMPLATES_PATH = "org/exoplatform/ide/template/samples";

   private static final String TEMPLATES_FILE = "Templates.xml";

   public static final String WEBDAV_SCHEME = "jcr-webdav";

   public static final String DEF_WS = "dev-monit";

   private static final String WEBDAV_CONTEXT = "jcr";

   private static Log log = ExoLogger.getLogger(TemplatesRestService.class);

   private final RepositoryService repositoryService;

   private final ThreadLocalSessionProviderService sessionProviderService;

   public TemplatesRestService(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
   }

   /**
    * Get the list of default templates from <code>Templates.xml</code> file
    * @param uriInfo - the uri info
    * @param type - the type of template (<code>project</code> or <code>file</code>)
    * @return the list of templates
    * @throws TemplateServiceException
    */
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   @Path("/list")
   public List<TemplateDescription> getTemplatesList(@Context UriInfo uriInfo, @HeaderParam("type") String type)
      throws TemplateServiceException
   {
      List<TemplateDescription> templateDescList = new ArrayList<TemplateDescription>();

      try
      {
         Document dom = getTemplatesDescriptionDocument();

         Node templatesNode = dom.getElementsByTagName("templates").item(0);

         NodeList templateNodes = templatesNode.getChildNodes();
         for (int i = 0; i < templateNodes.getLength(); i++)
         {
            Node templateNode = templateNodes.item(i);
            if (templateNode.getNodeName().equals("template"))
            {
               TemplateDescription template = getTemplateDescription(templateNode, type);
               if (template != null)
               {
                  templateDescList.add(template);
               }
            }
         }
      }
      catch (ParserConfigurationException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (SAXException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (IOException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }

      return templateDescList;
   }

   /**
    * Create the project or file from one of sample templates.
    * 
    * @param uriInfo - the uri info
    * @param templateName - the name of template
    * @param itemLocation - the location of new item, where the word after the last slash - is new name 
    *    (e.g. <code>http://localhost/jcr/db1/dev-monit/newProjectName</code>)
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    * @throws TemplateServiceException 
    */
   @GET
   @Path("/create")
   public void createFromTemplate(@Context UriInfo uriInfo, @HeaderParam("template-name") String templateName,
      @HeaderParam("location") String itemLocation, @HeaderParam("type") String type) throws TemplateServiceException
   {
      try
      {
         String location = cropLocation(itemLocation, uriInfo);

         /*
          * the name of new project or file
          */
         final String itemName = location.substring(location.lastIndexOf("/") + 1);

         /*
          * crop the new item (project or file) name
          */
         location = location.substring(0, location.lastIndexOf("/"));

         /*
          * The name of repository, e.g. db1
          */
         final String repositoryName = location.substring(0, location.indexOf("/"));

         /*
          * The path to project parent folder, e.g. dev-monit/folder1
          */
         final String repoPath = location.substring(location.indexOf("/") + 1);

         Session session = null;
         session = Utils.getSession(repositoryService, sessionProviderService, repositoryName, repoPath);

         if ("project".equals(type))
         {
            /*
             * Path to project folder with sources
             */
            String templateSource = getPathToProjectManifest(templateName);

            if (templateSource == null)
            {
               throw new TemplateServiceException("Can't find project template" + templateName);
            }
            Document templateDoc = getTemplateManifestDocument(templateSource);
            String parentFolderPath = getParentFolderPath(repoPath);
            createProjectFromTemplate(templateDoc, session, itemName, templateSource, parentFolderPath);
         }
         else if ("file".equals(type))
         {
            /*
             * Node from Templates.xml file
             */
            Node fileNode = getFileTemplateNode(templateName);
            if (fileNode == null)
            {
               throw new TemplateServiceException("Can't find file template" + templateName);
            }
            String parentFolderPath = getParentFolderPath(repoPath);
            createFileFromTemplate(session, fileNode, parentFolderPath, TEMPLATES_PATH, itemName);
         }
         else
         {
            throw new TemplateServiceException("Undefined type of template " + type);
         }
      }
      catch (UnsupportedEncodingException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (ParserConfigurationException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (SAXException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (IOException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (ItemExistsException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (ConstraintViolationException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (InvalidItemStateException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (VersionException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (LockException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (NoSuchNodeTypeException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (RepositoryException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }

   }

   @GET
   @Path("/file-content")
   public String getFileTemplateContent(@Context UriInfo uriInfo, @HeaderParam("template-name") String templateName) throws TemplateServiceException
   {
      try
      {
         Node node = getFileTemplateNode(templateName);

         if (node == null)
         {
            throw new TemplateServiceException("Can't find file template" + templateName);
         }

         String src = null;

         NodeList nodeList = node.getChildNodes();

         for (int i = 0; i < nodeList.getLength(); i++)
         {
            Node childNode = nodeList.item(i);
            if (childNode.getNodeName().equals("src"))
            {
               src = childNode.getChildNodes().item(0).getNodeValue();
            }
         }

         InputStream fileData =
            Thread.currentThread().getContextClassLoader().getResourceAsStream(TEMPLATES_PATH + "/" + src);
         if (fileData != null) {
            Writer writer = new StringWriter();
 
            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(
                        new InputStreamReader(fileData, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
               fileData.close();
            }
            return writer.toString();
        } else {       
            return "";
        }
      }
      catch (ParserConfigurationException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (SAXException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
      catch (IOException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         throw new TemplateServiceException(HTTPStatus.INTERNAL_ERROR, e.getMessage());
      }
   }

   //--------- Implementation -----------------------

   /**
    * Find file template node by name in <code>Templates.xml</code> file
    * @param templateName
    * @return
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   private Node getFileTemplateNode(String templateName) throws ParserConfigurationException, SAXException, IOException
   {
      Document dom = getTemplatesDescriptionDocument();
      Node templatesNode = dom.getElementsByTagName("templates").item(0);

      NodeList templateNodes = templatesNode.getChildNodes();
      for (int i = 0; i < templateNodes.getLength(); i++)
      {
         Node templateNode = templateNodes.item(i);
         if (templateNode.getNodeName().equals("template"))
         {
            String fileName = getTemplateName(templateNode, "file");
            if (fileName != null && fileName.equals(templateName))
            {
               return templateNode;
            }
         }
      }

      return null;
   }

   private void createFileFromTemplate(Session session, Node node, String location, String templateSource,
      String fileName) throws PathNotFoundException, RepositoryException
   {
      String src = null;
      String mimeType = null;
      String fileNodeType = null;
      String jcrContentNodeType = null;

      NodeList nodeList = node.getChildNodes();

      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node childNode = nodeList.item(i);
         if (childNode.getNodeName().equals("src"))
         {
            src = childNode.getChildNodes().item(0).getNodeValue();
         }
         if (childNode.getNodeName().equals("mime-type"))
         {
            mimeType = childNode.getChildNodes().item(0).getNodeValue();
         }
         if (childNode.getNodeName().equals("node-type"))
         {
            fileNodeType = childNode.getChildNodes().item(0).getNodeValue();
         }
         if (childNode.getNodeName().equals("jcr-content-node-type"))
         {
            jcrContentNodeType = childNode.getChildNodes().item(0).getNodeValue();
         }
      }

      InputStream fileData =
         Thread.currentThread().getContextClassLoader().getResourceAsStream(templateSource + "/" + src);

      Utils.putFile(session, location, fileName, fileData, mimeType, fileNodeType, jcrContentNodeType);
      session.save();
   }

   /**
    * Get the Document element of xml file, which describes default templates.
    * 
    * @return {@link Document}
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   private Document getTemplatesDescriptionDocument() throws ParserConfigurationException, SAXException, IOException
   {
      /*
       * Source of file which containts information about all default templates
       */
      InputStream templatesStream =
         Thread.currentThread().getContextClassLoader().getResourceAsStream(TEMPLATES_PATH + "/" + TEMPLATES_FILE);

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();

      Document dom = builder.parse(templatesStream);

      return dom;
   }

   /**
    * Get the Document element of xml file, which describes the project structure.
    * 
    * @param templateSource - path to folder, where template stored in resources.
    * @return {@link Document}
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   private Document getTemplateManifestDocument(String templateSource) throws ParserConfigurationException,
      SAXException, IOException
   {
      InputStream templateStream =
         Thread.currentThread().getContextClassLoader().getResourceAsStream(templateSource + "/manifest.xml");

      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder;
      Document dom = null;

      builder = factory.newDocumentBuilder();
      dom = builder.parse(templateStream);

      return dom;
   }

   /**
    * Crop from the full location prefix (such as http://localhost:8080/jcr)
    * and leave only repository name and path to project
    * @param fullLocation - the full location of new project
    * @param uriInfo - the uri info
    * @return {@link String}
    * @throws UnsupportedEncodingException
    */
   private String cropLocation(String fullLocation, UriInfo uriInfo) throws UnsupportedEncodingException
   {
      String location = fullLocation;

      if (location.endsWith("/"))
      {
         location = location.substring(0, location.length() - 1);
      }

      location = URLDecoder.decode(location, "UTF-8");

      String prefix = uriInfo.getBaseUriBuilder().segment(WEBDAV_CONTEXT, "/").build().toString();

      if (!location.startsWith(prefix))
      {
         return null;
      }

      location = location.substring(prefix.length());

      return location;
   }

   /**
    * Parse template node from templates description file Templates.xml.
    * If node's child element <code>type</code> equals to <code>type</code> parameter,
    * than return value of node's child element <code>name</code>.
    * Otherwise return null
    * 
    * @param node - node to parse
    * @param type - type of template we need
    * @return {@link String}
    */
   private String getTemplateName(Node node, String type)
   {
      NodeList nodeList = node.getChildNodes();

      String typeNode = null;

      String name = null;

      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node childNode = nodeList.item(i);
         if (childNode.getNodeName().equals("name"))
         {
            name = childNode.getChildNodes().item(0).getNodeValue();
         }

         if (childNode.getNodeName().equals("type"))
         {
            typeNode = childNode.getChildNodes().item(0).getNodeValue();
         }
      }

      if (!typeNode.equals(type))
         return null;

      return name;
   }

   private TemplateDescription getTemplateDescription(Node node, String type)
   {
      NodeList nodeList = node.getChildNodes();

      String typeNode = null;

      String name = null;

      String description = null;

      String mimeType = null;

      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node childNode = nodeList.item(i);
         if (childNode.getNodeName().equals("name"))
         {
            name = childNode.getChildNodes().item(0).getNodeValue();
         }

         if (childNode.getNodeName().equals("type"))
         {
            typeNode = childNode.getChildNodes().item(0).getNodeValue();
         }

         if (childNode.getNodeName().equals("description"))
         {
            description = childNode.getChildNodes().item(0).getNodeValue();
         }

         if (childNode.getNodeName().equals("mime-type"))
         {
            mimeType = childNode.getChildNodes().item(0).getNodeValue();
         }
      }

      if (!typeNode.equals(type))
         return null;

      return new TemplateDescription(name, description, mimeType);
   }

   /**
    * Parse template node from templates description file Templates.xml
    * and get the path to template in resources.
    * 
    * @param node - the node to parse
    * @param type - the type of template (project of file)
    * @param name - the name of template
    * @return {@link String}
    */
   private String getTemplateSource(Node node, String type, String name)
   {
      NodeList nodeList = node.getChildNodes();

      String typeNode = null;

      String src = null;

      String nameNode = null;

      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node childNode = nodeList.item(i);
         if (childNode.getNodeName().equals("name"))
         {
            nameNode = childNode.getChildNodes().item(0).getNodeValue();
         }

         if (childNode.getNodeName().equals("type"))
         {
            typeNode = childNode.getChildNodes().item(0).getNodeValue();
         }

         if (childNode.getNodeName().equals("src"))
         {
            src = childNode.getChildNodes().item(0).getNodeValue();
         }
      }

      if (type.equals(typeNode) && name.equals(nameNode))
         return src;

      return null;
   }

   /**
    * Create folder from project.
    * <p/>
    * If node has item <code>items</code>, then
    * create subfolder and file from this folder.
    * 
    * @param session - session
    * @param node - folder's node (to get folder's name and children)
    * @param location - the location of parent's folder
    * @param templateSource - the source path to project template, e.g.
    *    <code>org/exoplatform/ide/template/samples/linkedin</code>
    * @throws TemplateServiceException 
    * @throws RepositoryException 
    * @throws ConstraintViolationException 
    * @throws VersionException 
    * @throws LockException 
    * @throws NoSuchNodeTypeException 
    * @throws PathNotFoundException 
    * @throws ItemExistsException 
    */
   private void createFolder(Session session, Node node, String location, String templateSource)
      throws TemplateServiceException, ItemExistsException, PathNotFoundException, NoSuchNodeTypeException,
      LockException, VersionException, ConstraintViolationException, RepositoryException
   {
      NodeList nodeList = node.getChildNodes();
      String name = null;
      NodeList childNodeList = null;
      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node child = nodeList.item(i);
         if ("name".equals(child.getNodeName()))
         {
            name = child.getChildNodes().item(0).getNodeValue();
         }
         else if ("items".equals(child.getNodeName()))
         {
            childNodeList = child.getChildNodes();
         }
      }
      if (name == null)
      {
         throw new TemplateServiceException("Folder must have name to create");
      }
      Utils.putFolder(session, location, name);

      if (childNodeList == null)
      {
         return;
      }

      for (int i = 0; i < childNodeList.getLength(); i++)
      {
         Node child = childNodeList.item(i);
         if ("folder".equals(child.getNodeName()))
         {
            createFolder(session, child, location + "/" + name, templateSource);
         }
         if ("file".equals(child.getNodeName()))
         {
            createFile(session, child, location + "/" + name, templateSource);
         }
      }
   }

   /**
    * Create file.
    * <p/>
    * Parses node, which can contain such subnodes: name, src, mime-type, node-type, jcr-content-node-type
    * 
    * @param session - session
    * @param node - file's node from <code>manifest.xml</code> file
    * @param location - the location of new file
    * @param templateSource - path to template folder in resources (to find content of file) 
    * @throws PathNotFoundException
    * @throws RepositoryException
    */
   private void createFile(Session session, Node node, String location, String templateSource)
      throws PathNotFoundException, RepositoryException
   {

      String name = null;
      String src = null;
      String mimeType = null;
      String fileNodeType = null;
      String jcrContentNodeType = null;

      NodeList nodeList = node.getChildNodes();

      for (int i = 0; i < nodeList.getLength(); i++)
      {
         Node childNode = nodeList.item(i);
         if (childNode.getNodeName().equals("name"))
         {
            name = childNode.getChildNodes().item(0).getNodeValue();
         }
         if (childNode.getNodeName().equals("src"))
         {
            src = childNode.getChildNodes().item(0).getNodeValue();
         }
         if (childNode.getNodeName().equals("mime-type"))
         {
            mimeType = childNode.getChildNodes().item(0).getNodeValue();
         }
         if (childNode.getNodeName().equals("node-type"))
         {
            fileNodeType = childNode.getChildNodes().item(0).getNodeValue();
         }
         if (childNode.getNodeName().equals("jcr-content-node-type"))
         {
            jcrContentNodeType = childNode.getChildNodes().item(0).getNodeValue();
         }
      }

      InputStream fileData =
         Thread.currentThread().getContextClassLoader().getResourceAsStream(templateSource + "/" + src);

      Utils.putFile(session, location, name, fileData, mimeType, fileNodeType, jcrContentNodeType);
   }

   /**
    * Crate project from template.
    * <p>
    * Create project folder. Than get the node list of <code>items</code> element in
    * <code>manifest.xml</code> file and create folder and files of project.
    * 
    * @param templateDoc - the {@link Document} element of <code>manifest</code> file
    * @param session - the session
    * @param projectName - the name of project
    * @param templateSource - path to templates folder in resources
    * @param location - the location of new project (path to parent folder)
    * @throws AccessDeniedException
    * @throws ItemExistsException
    * @throws ConstraintViolationException
    * @throws InvalidItemStateException
    * @throws VersionException
    * @throws LockException
    * @throws NoSuchNodeTypeException
    * @throws RepositoryException
    * @throws TemplateServiceException
    */
   private void createProjectFromTemplate(Document templateDoc, Session session, String projectName,
      String templateSource, String location) throws AccessDeniedException, ItemExistsException,
      ConstraintViolationException, InvalidItemStateException, VersionException, LockException,
      NoSuchNodeTypeException, RepositoryException, TemplateServiceException
   {
      //create project folder
      Utils.putFolder(session, location, projectName);

      if (location == null)
      {
         location = projectName;
      }
      else
      {
         location += "/" + projectName;
      }

      if (templateDoc.getElementsByTagName("items").item(0) != null)
      {
         NodeList childNodeList = templateDoc.getElementsByTagName("items").item(0).getChildNodes();
         for (int i = 0; i < childNodeList.getLength(); i++)
         {
            Node child = childNodeList.item(i);
            if ("folder".equals(child.getNodeName()))
            {
               createFolder(session, child, location, templateSource);
            }
            if ("file".equals(child.getNodeName()))
            {
               createFile(session, child, location, templateSource);
            }
         }
      }
      session.save();
   }

   /**
    * Get the parent folder path for project from repo path.
    * E.g. if repo path is <code>db1/dev-monit/folder1</code>,
    * then parent folder path will be <code>folder1</code>.
    * If repo path is <code>db1/dev-monit</code>, then parent
    * folder path will be <code>null</code>.
    * @param repoPath - the repo path (e.g. <code>db1/dev-monit/folder</code>)
    * @return {@link String}
    */
   private String getParentFolderPath(String repoPath)
   {
      String path = repoPath;
      if (path.startsWith("/"))
      {
         path = path.substring(1);
      }
      if (path.contains("/"))
      {
         path.substring(path.indexOf("/"));
      }
      else
      {
         path = null;
      }
      return path;
   }

   /**
    * Get the path to project <code>manifest.xml</code> file by template name.
    * 
    * @param templateName - the name of template
    * @return {@link String}
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    */
   private String getPathToProjectManifest(String templateName) throws ParserConfigurationException, SAXException,
      IOException
   {
      Document dom = getTemplatesDescriptionDocument();
      Node templatesNode = dom.getElementsByTagName("templates").item(0);

      NodeList templateNodes = templatesNode.getChildNodes();
      /*
       * Work throught all templates, and try to find necessary project template
       */
      for (int i = 0; i < templateNodes.getLength(); i++)
      {
         Node templateNode = templateNodes.item(i);
         if (templateNode.getNodeName().equals("template"))
         {
            String templateSource = getTemplateSource(templateNode, "project", templateName);
            if (templateSource != null)
            {
               return templateSource;
            }
         }
      }

      return null;
   }

}
