/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ide.groovy;

import org.apache.commons.fileupload.FileItem;
import org.codehaus.groovy.control.CompilationFailedException;
import org.everrest.core.ResourceBinder;
import org.everrest.core.ResourcePublicationException;
import org.everrest.groovy.GroovyClassLoaderProvider;
import org.everrest.groovy.ResourceId;
import org.everrest.groovy.SourceFile;
import org.everrest.groovy.SourceFolder;
import org.exoplatform.commons.utils.SecurityHelper;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.configuration.ConfigurationManager;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.jcr.ext.registry.RegistryEntry;
import org.exoplatform.services.jcr.ext.registry.RegistryService;
import org.exoplatform.services.jcr.ext.resource.UnifiedNodeReference;
import org.exoplatform.services.jcr.ext.script.groovy.GroovyScript2RestLoaderPlugin;
import org.exoplatform.services.jcr.ext.script.groovy.GroovyScriptAddRepoPlugin;
import org.exoplatform.services.jcr.ext.script.groovy.ObservationListenerConfiguration;
import org.exoplatform.services.jcr.ext.script.groovy.XMLGroovyScript2Rest;
import org.exoplatform.services.jcr.impl.core.query.lucene.IndexOfflineRepositoryException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.script.groovy.GroovyScriptInstantiator;
import org.picocontainer.Startable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import javax.annotation.security.RolesAllowed;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.observation.Event;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: GroovyScript2RestLoader.java 34445 2009-07-24 07:51:18Z
 *          dkatayev $
 */
@SuppressWarnings("deprecation")
@Path("script/groovy")
public class GroovyScript2RestLoader extends BaseGroovyScriptManager implements Startable
{
   protected static class InnerGroovyJaxrsPublisher extends GroovyResourcePublisher
   {
      public InnerGroovyJaxrsPublisher(ResourceBinder binder, GroovyClassLoaderProvider classLoaderProvider)
      {
         //TODO DependencySupplier
         super(binder, classLoaderProvider, null);
      }
   }

   /** Logger. */
   static final Log LOG = ExoLogger.getLogger(GroovyScript2RestLoader.class);

   /** Default node types for Groovy scripts. */
   private static final String DEFAULT_NODETYPE = "exo:groovyResourceContainer";

   /** Service name. */
   private static final String SERVICE_NAME = "GroovyScript2RestLoader";

   private static final int DELAYED_AUTOLOAD_TIMEOUT = 20000; // 20 sec

   /** See {@link InitParams}. */
   protected InitParams initParams;

   /** See {@link RepositoryService}. */
   protected RepositoryService repositoryService;

   /** See {@link ConfigurationManager}. */
   protected ConfigurationManager configurationManager;

   /** See {@link RegistryService}. */
   protected RegistryService registryService;

   /** See {@link SessionProviderService} */
   protected ThreadLocalSessionProviderService sessionProviderService;

   /** Keeps configuration for observation listener. */
   private ObservationListenerConfiguration observationListenerConfiguration;

   //protected GroovyJaxrsPublisher groovyPublisher;

   protected List<GroovyScript2RestLoaderPlugin> loadPlugins;

   protected List<GroovyScriptAddRepoPlugin> addRepoPlugins;

   /** See {@link ResourceBinder}. */
   private ResourceBinder binder;

   /** Node type for Groovy scripts. */
   private String nodeType;

   /**
    * @param binder binder for RESTful services
    * @param groovyScriptInstantiator instantiate groovy scripts
    * @param repositoryService See {@link RepositoryService}
    * @param sessionProviderService See {@link SessionProviderService}
    * @param configurationManager for solve resource loading issue in common way
    * @param params initialized parameters
    */
   public GroovyScript2RestLoader(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, org.exoplatform.services.jcr.ext.resource.jcr.Handler jcrUrlHandler,
      InitParams params)
   {
      this(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager, null,
         new InnerGroovyJaxrsPublisher(binder, new JcrGroovyClassLoaderProvider()),
         jcrUrlHandler, params);
   }

   /**
    * @param binder binder for RESTful services
    * @param groovyScriptInstantiator instantiates Groovy scripts
    * @param repositoryService See {@link RepositoryService}
    * @param sessionProviderService See {@link SessionProviderService}
    * @param configurationManager for solve resource loading issue in common way
    * @param registryService See {@link RegistryService}
    * @param params initialized parameters
    */
   public GroovyScript2RestLoader(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, RegistryService registryService,
      org.exoplatform.services.jcr.ext.resource.jcr.Handler jcrUrlHandler, InitParams params)
   {
      this(binder, groovyScriptInstantiator, repositoryService, sessionProviderService, configurationManager,
         registryService, new InnerGroovyJaxrsPublisher(binder, new JcrGroovyClassLoaderProvider()), jcrUrlHandler, params);
   }

   public GroovyScript2RestLoader(ResourceBinder binder, GroovyScriptInstantiator groovyScriptInstantiator,
      RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService,
      ConfigurationManager configurationManager, RegistryService registryService, GroovyResourcePublisher groovyPublisher,
      org.exoplatform.services.jcr.ext.resource.jcr.Handler jcrUrlHandler, InitParams params)
   {
      super(groovyPublisher);
      this.binder = binder;
      this.repositoryService = repositoryService;
      this.configurationManager = configurationManager;
      this.registryService = registryService;
      this.sessionProviderService = sessionProviderService;
      this.groovyPublisher = groovyPublisher;
      this.initParams = params;
   }

   /**
    * Get node type for store scripts, may throw {@link IllegalStateException}
    * if <tt>nodeType</tt> not initialized yet.
    * 
    * @return return node type
    */
   public String getNodeType()
   {
      if (nodeType == null)
      {
         throw new IllegalStateException("Node type not initialized, yet. ");
      }
      return nodeType;
   }

   /**
    * @see org.picocontainer.Startable#start()
    */
   public void start()
   {
      if (registryService != null && initParams != null && !registryService.getForceXMLConfigurationValue(initParams))
      {
         SessionProvider sessionProvider = SessionProvider.createSystemProvider();
         try
         {
            readParamsFromRegistryService(sessionProvider);
         }
         catch (Exception e)
         {
            readParamsFromFile();
            try
            {
               writeParamsToRegistryService(sessionProvider);
            }
            catch (Exception exc)
            {
               LOG.error("Cannot write init configuration to RegistryService.", exc);
            }
         }
         finally
         {
            sessionProvider.close();
         }
      }
      else
      {
         readParamsFromFile();
      }

      // Add script from configuration files to JCR.
      addScripts();

      if (addRepoPlugins != null && addRepoPlugins.size() > 0)
      {
         try
         {
            Set<URL> repos = new HashSet<URL>();
            for (GroovyScriptAddRepoPlugin pl : addRepoPlugins)
            {
               repos.addAll(pl.getRepositories());
            }
            this.groovyPublisher.getGroovyClassLoader().setResourceLoader(
               new JcrGroovyResourceLoader(repos.toArray(new URL[repos.size()])));
         }
         catch (MalformedURLException e)
         {
            LOG.error("Unable add groovy script repository. ", e);
         }
      }

      if (observationListenerConfiguration != null)
      {
         try
         {
            // Deploy auto-load scripts and start Observation Listeners.
            final String repositoryName = observationListenerConfiguration.getRepository();
            List<String> workspaceNames = observationListenerConfiguration.getWorkspaces();

            final ManageableRepository repository = repositoryService.getRepository(repositoryName);

            // JCR it offers an asynchronous workspace reindexing (since 1.14.0-CR2). But while it
            // is performed in background queries can't be executed. In this case autoload scripts could only
            // be loaded after reindexing finished.
            final Set<String> delayedWorkspacePublishing = new HashSet<String>();

            for (String workspaceName : workspaceNames)
            {
               Session session = repository.getSystemSession(workspaceName);
               try
               {
                  autoLoadScripts(session);
               }
               catch (IndexOfflineRepositoryException e)
               {
                  delayedWorkspacePublishing.add(workspaceName);
               }

               session.getWorkspace().getObservationManager().addEventListener(
                  new GroovyScript2RestUpdateListener(repositoryName, workspaceName, this, session),
                  Event.PROPERTY_ADDED | Event.PROPERTY_CHANGED | Event.PROPERTY_REMOVED, "/", true, null,
                  new String[]{getNodeType()}, false);
            }
            if (!delayedWorkspacePublishing.isEmpty())
            {
               LOG.warn("The following workspaces are being reindexed now: " + delayedWorkspacePublishing
                  + ". Groove scripts from those workspaces marked as AutoLoad will be loaded later.");
               // lauch delayed autoLoad
               new Thread(new Runnable()
               {
                  public void run()
                  {
                     while (true)
                     {
                        if (delayedWorkspacePublishing.isEmpty())
                        {
                           // finish thread
                           return;
                        }
                        for (Iterator iterator = delayedWorkspacePublishing.iterator(); iterator.hasNext();)
                        {
                           String workspaceName = (String)iterator.next();
                           try
                           {
                              Session session = repository.getSystemSession(workspaceName);
                              autoLoadScripts(session);
                              // if no exception, then remove item from set
                              iterator.remove();
                           }
                           catch (IndexOfflineRepositoryException e)
                           {
                              //it's okay. Retrying;
                           }
                           catch (Exception e)
                           {
                              // skip
                              LOG.error(e);
                           }
                        }
                        try
                        {
                           Thread.sleep(DELAYED_AUTOLOAD_TIMEOUT);
                        }
                        catch (InterruptedException e)
                        {
                           // skip
                        }
                     }
                  }
               }, "GrooveSrciptDelayedAutoLoader-" + repositoryName).start();
            }
         }
         catch (Exception e)
         {
            LOG.error("Error occurs ", e);
         }
      }

      // Finally bind this object as RESTful service.
      // NOTE this service does not implement ResourceContainer, as usually
      // done for this type of services. It can't be binded in common way cause
      // to dependencies problem. And in other side not possible to use third
      // part which can be injected by GroovyScript2RestLoader.
      binder.addResource(this, null);
   }

   private void autoLoadScripts(Session session) throws RepositoryException
   {
      String workspaceName = session.getWorkspace().getName();
      String repositoryName = observationListenerConfiguration.getRepository();

      String xpath = "//element(*, " + getNodeType() + ")[@exo:autoload='true']";
      Query query = session.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);

      QueryResult result = query.execute();
      NodeIterator nodeIterator = result.getNodes();
      while (nodeIterator.hasNext())
      {
         Node node = nodeIterator.nextNode();

         if (node.getPath().startsWith("/jcr:system"))
         {
            continue;
         }

         try
         {
            //TODO check params:
            groovyPublisher.publishPerRequest(node.getProperty("jcr:data").getStream(), new NodeScriptKey(
               repositoryName, workspaceName, node), null, null, null);
         }
         catch (CompilationFailedException e)
         {
            LOG.error(e.getMessage(), e);
         }
         catch (ResourcePublicationException e)
         {
            LOG.error(e.getMessage(), e);
         }
      }
   }

   /**
    * @see org.picocontainer.Startable#stop()
    */
   public void stop()
   {
      // nothing to do!
   }

   public void addPlugin(ComponentPlugin cp)
   {
      if (cp instanceof GroovyScript2RestLoaderPlugin)
      {
         if (loadPlugins == null)
         {
            loadPlugins = new ArrayList<GroovyScript2RestLoaderPlugin>();
         }
         loadPlugins.add((GroovyScript2RestLoaderPlugin)cp);
      }
      if (cp instanceof GroovyScriptAddRepoPlugin)
      {
         if (addRepoPlugins == null)
         {
            addRepoPlugins = new ArrayList<GroovyScriptAddRepoPlugin>();
         }
         addRepoPlugins.add((GroovyScriptAddRepoPlugin)cp);
      }
   }

   /**
    * Add scripts that specified in configuration.
    */
   protected void addScripts()
   {
      if (loadPlugins == null || loadPlugins.size() == 0)
      {
         return;
      }
      for (GroovyScript2RestLoaderPlugin loadPlugin : loadPlugins)
      {
         // If no one script configured then skip this item,
         // there is no reason to do anything.
         if (loadPlugin.getXMLConfigs().size() == 0)
         {
            continue;
         }

         Session session = null;
         try
         {
            ManageableRepository repository = repositoryService.getRepository(loadPlugin.getRepository());
            String workspace = loadPlugin.getWorkspace();
            session = repository.getSystemSession(workspace);
            String nodeName = loadPlugin.getNode();
            Node node = null;
            try
            {
               node = (Node)session.getItem(nodeName);
            }
            catch (PathNotFoundException e)
            {
               StringTokenizer tokens = new StringTokenizer(nodeName, "/");
               node = session.getRootNode();
               while (tokens.hasMoreTokens())
               {
                  String t = tokens.nextToken();
                  if (node.hasNode(t))
                  {
                     node = node.getNode(t);
                  }
                  else
                  {
                     node = node.addNode(t, "nt:folder");
                  }
               }
            }

            for (XMLGroovyScript2Rest xg : loadPlugin.getXMLConfigs())
            {
               String scriptName = xg.getName();
               if (node.hasNode(scriptName))
               {
                  LOG.warn("Node '" + node.getPath() + "/" + scriptName + "' already exists. ");
                  continue;
               }

               createScript(node, scriptName, xg.isAutoload(), configurationManager.getInputStream(xg.getPath()));
            }
            session.save();
         }
         catch (Exception e)
         {
            LOG.error("Failed add scripts. ", e);
         }
         finally
         {
            if (session != null)
            {
               session.logout();
            }
         }
      }
   }

   /**
    * Create JCR node.
    * 
    * @param parent parent node
    * @param name name of node to be created
    * @param stream data stream for property jcr:data
    * @return newly created node
    * @throws Exception if any errors occurs
    */
   protected Node createScript(Node parent, String name, boolean autoload, InputStream stream) throws Exception
   {
      Node scriptFile = parent.addNode(name, "nt:file");
      Node script = scriptFile.addNode("jcr:content", getNodeType());
      script.setProperty("exo:autoload", autoload);
      script.setProperty("jcr:mimeType", "script/groovy");
      script.setProperty("jcr:lastModified", Calendar.getInstance());
      script.setProperty("jcr:data", stream);
      return scriptFile;
   }

   /**
    * Read parameters from RegistryService.
    * 
    * @param sessionProvider the SessionProvider
    * @throws RepositoryException
    * @throws PathNotFoundException
    */
   protected void readParamsFromRegistryService(SessionProvider sessionProvider) throws PathNotFoundException,
      RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug("<<< Read init parametrs from registry service.");
      }

      observationListenerConfiguration = new ObservationListenerConfiguration();

      String entryPath = RegistryService.EXO_SERVICES + "/" + SERVICE_NAME + "/" + "nodeType";
      RegistryEntry registryEntry = registryService.getEntry(sessionProvider, entryPath);
      Document doc = registryEntry.getDocument();
      Element element = doc.getDocumentElement();
      nodeType = getAttributeSmart(element, "value");

      entryPath = RegistryService.EXO_SERVICES + "/" + SERVICE_NAME + "/" + "repository";
      registryEntry = registryService.getEntry(sessionProvider, entryPath);
      doc = registryEntry.getDocument();
      element = doc.getDocumentElement();
      observationListenerConfiguration.setRepository(getAttributeSmart(element, "value"));

      entryPath = RegistryService.EXO_SERVICES + "/" + SERVICE_NAME + "/" + "workspaces";
      registryEntry = registryService.getEntry(sessionProvider, entryPath);
      doc = registryEntry.getDocument();
      element = doc.getDocumentElement();
      String workspaces = getAttributeSmart(element, "value");

      String ws[] = workspaces.split(";");
      List<String> wsList = new ArrayList<String>();
      for (String w : ws)
      {
         wsList.add(w);
      }

      observationListenerConfiguration.setWorkspaces(wsList);

      LOG.info("NodeType from RegistryService: " + getNodeType());
      LOG.info("Repository from RegistryService: " + observationListenerConfiguration.getRepository());
      LOG.info("Workspaces node from RegistryService: " + observationListenerConfiguration.getWorkspaces());
   }

   /**
    * Write parameters to RegistryService.
    * 
    * @param sessionProvider the SessionProvider
    * @throws ParserConfigurationException
    * @throws SAXException
    * @throws IOException
    * @throws RepositoryException
    */
   protected void writeParamsToRegistryService(SessionProvider sessionProvider) throws IOException, SAXException,
      ParserConfigurationException, RepositoryException
   {
      if (LOG.isDebugEnabled())
      {
         LOG.debug(">>> Save init parametrs in registry service.");
      }

      Document doc;
      try
      {
         doc = SecurityHelper.doPrivilegedExceptionAction(new PrivilegedExceptionAction<Document>()
         {
            public Document run() throws ParserConfigurationException
            {
               return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            }
         });
      }
      catch (PrivilegedActionException e)
      {
         throw (ParserConfigurationException)e.getCause();
      }

      Element root = doc.createElement(SERVICE_NAME);
      doc.appendChild(root);

      Element element = doc.createElement("nodeType");
      setAttributeSmart(element, "value", getNodeType());
      root.appendChild(element);

      StringBuffer sb = new StringBuffer();
      for (String workspace : observationListenerConfiguration.getWorkspaces())
      {
         if (sb.length() > 0)
         {
            sb.append(';');
         }
         sb.append(workspace);
      }
      element = doc.createElement("workspaces");
      setAttributeSmart(element, "value", sb.toString());
      root.appendChild(element);

      element = doc.createElement("repository");
      setAttributeSmart(element, "value", observationListenerConfiguration.getRepository());
      root.appendChild(element);

      RegistryEntry serviceEntry = new RegistryEntry(doc);
      registryService.createEntry(sessionProvider, RegistryService.EXO_SERVICES, serviceEntry);
   }

   /**
    * Get attribute value.
    * 
    * @param element The element to get attribute value
    * @param attr The attribute name
    * @return Value of attribute if present and null in other case
    */
   protected String getAttributeSmart(Element element, String attr)
   {
      return element.hasAttribute(attr) ? element.getAttribute(attr) : null;
   }

   /**
    * Set attribute value. If value is null the attribute will be removed.
    * 
    * @param element The element to set attribute value
    * @param attr The attribute name
    * @param value The value of attribute
    */
   protected void setAttributeSmart(Element element, String attr, String value)
   {
      if (value == null)
      {
         element.removeAttribute(attr);
      }
      else
      {
         element.setAttribute(attr, value);
      }
   }

   /**
    * Read parameters from file.
    */
   protected void readParamsFromFile()
   {
      if (initParams != null)
      {
         nodeType =
            initParams.getValuesParam("nodetype") != null ? initParams.getValueParam("nodetype").getValue()
               : DEFAULT_NODETYPE;

         ObjectParameter param = initParams.getObjectParam("observation.config");
         observationListenerConfiguration = (ObservationListenerConfiguration)param.getObject();
      }
      else
      {
         nodeType = DEFAULT_NODETYPE;
      }

      LOG.info("NodeType from configuration file: " + getNodeType());
      if (observationListenerConfiguration != null)
      {
         LOG.info("Repository from configuration file: " + observationListenerConfiguration.getRepository());
         LOG.info("Workspaces node from configuration file: " + observationListenerConfiguration.getWorkspaces());
      }
   }

   ////////////////////////////////////////////////////////////////////////////////////

   /**
    * This method is useful for clients that can send script in request body
    * without form-data. At required to set specific Content-type header
    * 'script/groovy'.
    * 
    * @param stream the stream that contains groovy source code
    * @param uriInfo see {@link UriInfo}
    * @param repository repository name
    * @param workspace workspace name
    * @param path path to resource to be created
    * @return Response with status 'created'
    */
   @POST
   @Consumes({"script/groovy"})
   @Path("add/{repository}/{workspace}/{path:.*}")
   public Response addScript(InputStream stream, @Context UriInfo uriInfo, @PathParam("repository") String repository,
      @PathParam("workspace") String workspace, @PathParam("path") String path)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         Node node = (Node)ses.getItem(getPath(path));
         createScript(node, getName(path), false, stream);
         ses.save();
         URI location = uriInfo.getBaseUriBuilder().path(getClass(), "getScript").build(repository, workspace, path);
         return Response.created(location).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).entity(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * This method is useful for clients that send scripts as file in
    * 'multipart/*' request body. <br/>
    * NOTE even we use iterator item should be only one, rule one address - one
    * script. This method is created just for comfort loading script from HTML
    * form. NOT use this script for uploading few files in body of
    * 'multipart/form-data' or other type of multipart.
    * 
    * @param items iterator {@link FileItem}
    * @param uriInfo see {@link UriInfo}
    * @param repository repository name
    * @param workspace workspace name
    * @param path path to resource to be created
    * @return Response with status 'created'
    */
   @POST
   @Consumes({"multipart/*"})
   @Path("add/{repository}/{workspace}/{path:.*}")
   public Response addScript(Iterator<FileItem> items, @Context UriInfo uriInfo,
      @PathParam("repository") String repository, @PathParam("workspace") String workspace,
      @PathParam("path") String path)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         Node node = (Node)ses.getItem(getPath(path));
         InputStream stream = null;
         boolean autoload = false;
         while (items.hasNext())
         {
            FileItem fitem = items.next();
            if (fitem.isFormField() && fitem.getFieldName() != null
               && fitem.getFieldName().equalsIgnoreCase("autoload"))
            {
               autoload = Boolean.valueOf(fitem.getString());
            }
            else if (!fitem.isFormField())
            {
               stream = fitem.getInputStream();
            }
         }

         createScript(node, getName(path), autoload, stream);
         ses.save();
         URI location = uriInfo.getBaseUriBuilder().path(getClass(), "getScript").build(repository, workspace, path);
         return Response.created(location).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).entity(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * Check is specified source <code>script</code> contains valid Groovy source
    * code.
    * 
    * @param name script name. This name will be used by GroovyClassLoader to
    *           identify script, e.g. specified name will be used in error
    *           message in compilation of Groovy fails. If this parameter is
    *           <code>null</code> then GroovyClassLoader will use automatically
    *           generated name
    * @param script Groovy source stream
    * @param sources locations (string representation of URL) of source folders
    *           that should be add in class path when compile Groovy script.
    *           <b>NOTE</b> To be able load Groovy source files from specified
    *           folders the following rules must be observed:
    *           <ul>
    *           <li>Groovy source files must be located in folder with respect
    *           to package structure</li>
    *           <li>Name of Groovy source files must be the same as name of
    *           class located in file</li>
    *           <li>Groovy source file must have extension '.groovy'</li>
    *           </ul>
    * <br/>
    *           Example: If source stream that we want validate contains the
    *           following code:
    * 
    *           <pre>
    *           package c.b.a
    *           
    *           import a.b.c.A
    *           
    *           class B extends A {
    *           // Do something.
    *           }
    * </pre>
    * 
    *           Assume we store dependencies in JCR then URL of folder with
    *           Groovy sources may be like this:
    *           <code>jcr://repository/workspace#/groovy-library</code>. Then
    *           absolute path to JCR node that contains Groovy source must be as
    *           following: <code>/groovy-library/a/b/c/A.groovy</code>
    * @param files locations (string representation of URL) of source files that
    *           should be add in class path when compile Groovy script. Each
    *           location must point directly to file that contains Groovy
    *           source. Source file can have any name and extension
    * @return Response with corresponded status. 200 if source code is valid
    */
   @POST
   @Consumes({"script/groovy"})
   @Path("validate{name:.*}")
   public Response validateScript(@PathParam("name") String name, final InputStream script,
      @QueryParam("sources") List<String> sources, @QueryParam("file") List<String> files)
   {
      try
      {
         validateScript(name, script, createSourceFolders(sources), createSourceFiles(files));
         return Response.ok().build();
      }
      catch (MalformedScriptException e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
      }
      catch (MalformedURLException e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
      }
   }

   /**
    * Check is specified source <code>script</code> contains valid Groovy source
    * code.
    * 
    * @param name script name. This name will be used by GroovyClassLoader to
    *           identify script, e.g. specified name will be used in error
    *           message in compilation of Groovy fails. If this parameter is
    *           <code>null</code> then GroovyClassLoader will use automatically
    *           generated name
    * @param script Groovy source stream
    * @param src set of folders that contains Groovy source files that should be
    *           add in class-path when validate <code>script</code>, see
    *           {@link SourceFolder#getPath()}. <b>NOTE</b> To be able load
    *           Groovy source files from specified folders the following rules
    *           must be observed:
    *           <ul>
    *           <li>Groovy source files must be located in folder with respect
    *           to package structure</li>
    *           <li>Name of Groovy source files must be the same as name of
    *           class located in file</li>
    *           <li>Groovy source file must have extension '.groovy'</li>
    *           </ul>
    * @param files set of groovy source files that should be add in class-path
    *           when validate <code>script</code>. Each item must point directly
    *           to file that contains Groovy source, see
    *           {@link SourceFile#getPath()} . Source file can have any name and
    *           extension
    * @throws MalformedScriptException if <code>script</code> contains not valid
    *            source code
    */
   public void validateScript(String name, InputStream script, SourceFolder[] src, SourceFile[] files)
      throws MalformedScriptException
   {
      if (name != null && name.length() > 0 && name.startsWith("/"))
      {
         name = name.substring(1);
      }
      groovyPublisher.validateResource(script, name, src, files);
   }

   /**
    * This method is useful for clients that can send script in request body
    * without form-data. At required to set specific Content-type header
    * 'script/groovy'.
    * 
    * @param stream the stream that contains groovy source code
    * @param uriInfo see {@link UriInfo}
    * @param repository repository name
    * @param workspace workspace name
    * @param path path to resource to be created
    * @return Response with status 'created'
    */
   @POST
   @Consumes({"script/groovy"})
   @Path("update/{repository}/{workspace}/{path:.*}")
   public Response updateScript(InputStream stream, @Context UriInfo uriInfo,
      @PathParam("repository") String repository, @PathParam("workspace") String workspace,
      @PathParam("path") String path)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         Node node = (Node)ses.getItem("/" + path);
         node.getNode("jcr:content").setProperty("jcr:data", stream);
         ses.save();
         URI location = uriInfo.getBaseUriBuilder().path(getClass(), "getScript").build(repository, workspace, path);
         return Response.created(location).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).entity(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * This method is useful for clients that send scripts as file in
    * 'multipart/*' request body. <br/>
    * NOTE even we use iterator item should be only one, rule one address - one
    * script. This method is created just for comfort loading script from HTML
    * form. NOT use this script for uploading few files in body of
    * 'multipart/form-data' or other type of multipart.
    * 
    * @param items iterator {@link FileItem}
    * @param uriInfo see {@link UriInfo}
    * @param repository repository name
    * @param workspace workspace name
    * @param path path to resource to be created
    * @return Response with status 'created'
    */
   @POST
   @Consumes({"multipart/*"})
   @Path("update/{repository}/{workspace}/{path:.*}")
   public Response updateScript(Iterator<FileItem> items, @Context UriInfo uriInfo,
      @PathParam("repository") String repository, @PathParam("workspace") String workspace,
      @PathParam("path") String path)
   {
      Session ses = null;
      try
      {
         FileItem fitem = items.next();
         InputStream stream = null;
         if (!fitem.isFormField())
         {
            stream = fitem.getInputStream();
         }
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         Node node = (Node)ses.getItem("/" + path);
         node.getNode("jcr:content").setProperty("jcr:data", stream);
         ses.save();
         URI location = uriInfo.getBaseUriBuilder().path(getClass(), "getScript").build(repository, workspace, path);
         return Response.created(location).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).entity(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * Deploy groovy script as REST service. If this property set to 'true' then
    * script will be deployed as REST service if 'false' the script will be
    * undeployed. NOTE is script already deployed and <tt>state</tt> is
    * <tt>true</tt> script will be re-deployed.
    * 
    * @param repository repository name
    * @param workspace workspace name
    * @param path the path to JCR node that contains groovy script to be
    *           deployed
    * @param state <code>true</code> if resource should be loaded and
    *           <code>false</code> otherwise. If this attribute is not present
    *           in HTTP request then it will be considered as <code>true</code>
    * @param sources locations (string representation of URL) of source folders
    *           that should be add in class path when compile Groovy script.
    *           <b>NOTE</b> To be able load Groovy source files from specified
    *           folders the following rules must be observed:
    *           <ul>
    *           <li>Groovy source files must be located in folder with respect
    *           to package structure</li>
    *           <li>Name of Groovy source files must be the same as name of
    *           class located in file</li>
    *           <li>Groovy source file must have extension '.groovy'</li>
    *           </ul>
    * <br/>
    *           Example: If source stream that we want validate contains the
    *           following code:
    * 
    *           <pre>
    *           package c.b.a
    *           
    *           import a.b.c.A
    *           
    *           class B extends A {
    *           // Do something.
    *           }
    * </pre>
    * 
    *           Assume we store dependencies in JCR then URL of folder with
    *           Groovy sources may be like this:
    *           <code>jcr://repository/workspace#/groovy-library</code>. Then
    *           absolute path to JCR node that contains Groovy source must be as
    *           following: <code>/groovy-library/a/b/c/A.groovy</code>
    * @param files locations (string representation of URL) of source files that
    *           should be add in class path when compile Groovy script. Each
    *           location must point directly to file that contains Groovy
    *           source. Source file can have any name and extension
    * @param properties optional properties to be applied to loaded resource.
    *           Ignored if <code>state</code> parameter is false
    */
   @POST
   @Path("load/{repository}/{workspace}/{path:.*}")
   @RolesAllowed({"administrators"})
   public Response load(@PathParam("repository") String repository, @PathParam("workspace") String workspace,
      @PathParam("path") String path, @DefaultValue("true") @QueryParam("state") boolean state,
      @QueryParam("sources") List<String> sources, @QueryParam("file") List<String> files,
      MultivaluedMap<String, String> properties)
   {
      try
      {
         return load(repository, workspace, path, state, properties, createSourceFolders(sources),
            createSourceFiles(files));
      }
      catch (MalformedURLException e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
      }
   }

   /**
    * Deploy groovy script as REST service. If this property set to 'true' then
    * script will be deployed as REST service if 'false' the script will be
    * undeployed. NOTE is script already deployed and <tt>state</tt> is
    * <tt>true</tt> script will be re-deployed.
    * 
    * @param repository repository name
    * @param workspace workspace name
    * @param path the path to JCR node that contains groovy script to be
    *           deployed
    * @param state <code>true</code> if resource should be loaded and
    *           <code>false</code> otherwise. If this attribute is not present
    *           in HTTP request then it will be considered as <code>true</code>
    * @param properties optional properties to be applied to loaded resource.
    *           Ignored if <code>state</code> parameter is false
    * @param src set of folders that contains Groovy source files that should be
    *           add in class-path when compile file located at <code>path</code>
    *           . <b>NOTE</b> To be able load Groovy source files from specified
    *           folders the following rules must be observed:
    *           <ul>
    *           <li>Groovy source files must be located in folder with respect
    *           to package structure</li>
    *           <li>Name of Groovy source files must be the same as name of
    *           class located in file</li>
    *           <li>Groovy source file must have extension '.groovy'</li>
    *           </ul>
    * @param files set of groovy source files that should be add in class-path
    *           when compile file located at <code>path</code>. Each item must
    *           point directly to file that contains Groovy source, see
    *           {@link SourceFile#getPath()} . Source file can have any name and
    *           extension
    */
   public Response load(String repository, String workspace, String path, boolean state,
      MultivaluedMap<String, String> properties, SourceFolder[] src, SourceFile[] files)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         Node script = ((Node)ses.getItem("/" + path)).getNode("jcr:content");
         ResourceId key = new NodeScriptKey(repository, workspace, script);
         if (state)
         {
            groovyPublisher.unpublishResource(key);
            groovyPublisher.publishPerRequest(script.getProperty("jcr:data").getStream(), key, properties, src, files);
         }
         else
         {
            if (null == groovyPublisher.unpublishResource(key))
            {
               return Response.status(Response.Status.BAD_REQUEST).entity(
                  "Can't unbind script " + path + ", not bound or has wrong mapping to the resource class ").type(
                  MediaType.TEXT_PLAIN).build();
            }
         }
         return Response.status(Response.Status.NO_CONTENT).build();
      }
      catch (CompilationFailedException e)
      {
         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
      }
      catch (ResourcePublicationException e)
      {
         return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).type(MediaType.TEXT_PLAIN).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).type(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * Remove node that contains groovy script.
    * 
    * @param repository repository name
    * @param workspace workspace name
    * @param path JCR path to node that contains script
    */
   @POST
   @Path("delete/{repository}/{workspace}/{path:.*}")
   public Response deleteScript(@PathParam("repository") String repository, @PathParam("workspace") String workspace,
      @PathParam("path") String path)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         ses.getItem("/" + path).remove();
         ses.save();
         return Response.status(Response.Status.NO_CONTENT).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).entity(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * Change exo:autoload property. If this property is 'true' script will be
    * deployed automatically when JCR repository startup and automatically
    * re-deployed when script source code changed.
    * 
    * @param repository repository name
    * @param workspace workspace name
    * @param path JCR path to node that contains script
    * @param state value for property exo:autoload, if it is not specified then
    *           'true' will be used as default. <br />
    *           Example: .../scripts/groovy/test1.groovy/load is the same to
    *           .../scripts/groovy/test1.groovy/load?state=true
    */
   @POST
   @Path("autoload/{repository}/{workspace}/{path:.*}")
   public Response autoload(@PathParam("repository") String repository, @PathParam("workspace") String workspace,
      @PathParam("path") String path, @DefaultValue("true") @QueryParam("state") boolean state)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         Node script = ((Node)ses.getItem("/" + path)).getNode("jcr:content");
         script.setProperty("exo:autoload", state);
         ses.save();
         return Response.status(Response.Status.NO_CONTENT).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).entity(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * Get source code of groovy script.
    * 
    * @param repository repository name
    * @param workspace workspace name
    * @param path JCR path to node that contains script
    * @return groovy script as stream
    */
   @POST
   @Produces({"script/groovy"})
   @Path("src/{repository}/{workspace}/{path:.*}")
   public Response getScript(@PathParam("repository") String repository, @PathParam("workspace") String workspace,
      @PathParam("path") String path)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         Node scriptFile = (Node)ses.getItem("/" + path);
         return Response.status(Response.Status.OK).entity(
            scriptFile.getNode("jcr:content").getProperty("jcr:data").getStream()).type("script/groovy").build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).entity(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * Get groovy script's meta-information.
    * 
    * @param repository repository name
    * @param workspace workspace name
    * @param path JCR path to node that contains script
    * @return groovy script's meta-information
    */
   @POST
   @Produces({MediaType.APPLICATION_JSON})
   @Path("meta/{repository}/{workspace}/{path:.*}")
   public Response getScriptMetadata(@PathParam("repository") String repository,
      @PathParam("workspace") String workspace, @PathParam("path") String path)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));
         Node script = ((Node)ses.getItem("/" + path)).getNode("jcr:content");
         ResourceId key = new NodeScriptKey(repository, workspace, script);

         ScriptMetadata meta = new ScriptMetadata(script.getProperty("exo:autoload").getBoolean(), //
            groovyPublisher.isPublished(key), //
            script.getProperty("jcr:mimeType").getString(), //
            script.getProperty("jcr:lastModified").getDate().getTimeInMillis());
         return Response.status(Response.Status.OK).entity(meta).type(MediaType.APPLICATION_JSON).build();
      }
      catch (PathNotFoundException e)
      {
         String msg = "Path " + path + " does not exists";
         LOG.error(msg);
         return Response.status(Response.Status.NOT_FOUND).entity(msg).entity(MediaType.TEXT_PLAIN).build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   /**
    * Returns the list of all groovy-scripts found in workspace.
    * 
    * @param repository repository name
    * @param workspace workspace name
    * @param name additional search parameter. If not empty method returns the
    *           list of script names matching wildcard else returns all the
    *           scripts found in workspace.
    * @return list of groovy services
    */
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   @Path("list/{repository}/{workspace}")
   public Response list(@PathParam("repository") String repository, @PathParam("workspace") String workspace,
      @QueryParam("name") String name)
   {
      Session ses = null;
      try
      {
         ses =
            sessionProviderService.getSessionProvider(null).getSession(workspace,
               repositoryService.getRepository(repository));

         String xpath = "//element(*, exo:groovyResourceContainer)";

         Query query = ses.getWorkspace().getQueryManager().createQuery(xpath, Query.XPATH);
         QueryResult result = query.execute();
         NodeIterator nodeIterator = result.getNodes();

         ArrayList<String> scriptList = new ArrayList<String>();

         if (name == null || name.length() == 0)
         {
            while (nodeIterator.hasNext())
            {
               Node node = nodeIterator.nextNode();
               scriptList.add(node.getParent().getPath());
            }
         }
         else
         {
            StringBuilder p = new StringBuilder();
            // add '.*' pattern at the start
            p.append(".*");
            for (int i = 0; i < name.length(); i++)
            {
               char c = name.charAt(i);
               if (c == '*' || c == '?')
               {
                  p.append('.');
               }
               if (".()[]^$|".indexOf(c) != -1)
               {
                  p.append('\\');
               }
               p.append(c);
            }
            // add '.*' pattern at he end
            p.append(".*");

            Pattern pattern = Pattern.compile(p.toString(), Pattern.CASE_INSENSITIVE);
            while (nodeIterator.hasNext())
            {
               Node node = nodeIterator.nextNode();
               String scriptName = node.getParent().getPath();

               if (pattern.matcher(scriptName).matches())
               {
                  scriptList.add(scriptName);
               }
            }
         }
         Collections.sort(scriptList);
         return Response.status(Response.Status.OK).entity(new ScriptList(scriptList)).type(MediaType.APPLICATION_JSON)
            .build();
      }
      catch (Exception e)
      {
         LOG.error(e.getMessage(), e);
         return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage())
            .type(MediaType.TEXT_PLAIN).build();
      }
      finally
      {
         if (ses != null)
         {
            ses.logout();
         }
      }
   }

   private SourceFolder[] createSourceFolders(List<String> sources) throws MalformedURLException
   {
      SourceFolder[] src = null;
      if (sources != null && sources.size() > 0)
      {
         src = new SourceFolder[sources.size()];
         for (int i = 0; i < sources.size(); i++)
         {
            String str = sources.get(i);
            URL url = null;
            if (str.startsWith("jcr://"))
            {
               url = new URL(null, str, UnifiedNodeReference.getURLStreamHandler());
            }
            else
            {
               url = new URL(str);
            }
            src[i] = new SourceFolder(url);
         }
      }
      return src;
   }

   private SourceFile[] createSourceFiles(List<String> files) throws MalformedURLException
   {
      SourceFile[] srcFiles = null;
      if (files != null && files.size() > 0)
      {
         srcFiles = new SourceFile[files.size()];
         for (int i = 0; i < files.size(); i++)
         {
            String str = files.get(i);
            URL url = null;
            if (str.startsWith("jcr://"))
            {
               url = new URL(null, str, UnifiedNodeReference.getURLStreamHandler());
            }
            else
            {
               url = new URL(str);
            }
            srcFiles[i] = new SourceFile(url);
         }
      }
      return srcFiles;
   }

   /**
    * Extract path to node's parent from full path.
    * 
    * @param fullPath full path to node
    * @return node's parent path
    */
   protected static String getPath(String fullPath)
   {
      int sl = fullPath.lastIndexOf('/');
      return sl > 0 ? "/" + fullPath.substring(0, sl) : "/";
   }

   /**
    * Extract node's name from full node path.
    * 
    * @param fullPath full path to node
    * @return node's name
    */
   protected static String getName(String fullPath)
   {
      int sl = fullPath.lastIndexOf('/');
      return sl >= 0 ? fullPath.substring(sl + 1) : fullPath;
   }

   /**
    * Script meta-data, used for pass script meta-data as JSON.
    */
   public static class ScriptMetadata
   {
      /** Is script autoload. */
      private final boolean autoload;

      /** Is script loaded. */
      private final boolean load;

      /** Script media type (script/groovy). */
      private final String mediaType;

      /** Last modified date. */
      private final long lastModified;

      public ScriptMetadata(boolean autoload, boolean load, String mediaType, long lastModified)
      {
         this.autoload = autoload;
         this.load = load;
         this.mediaType = mediaType;
         this.lastModified = lastModified;
      }

      /**
       * @return {@link #autoload}
       */
      public boolean getAutoload()
      {
         return autoload;
      }

      /**
       * @return {@link #load}
       */
      public boolean getLoad()
      {
         return load;
      }

      /**
       * @return {@link #mediaType}
       */
      public String getMediaType()
      {
         return mediaType;
      }

      /**
       * @return {@link #lastModified}
       */
      public long getLastModified()
      {
         return lastModified;
      }
   }

   /**
    * Script list, used for pass script list as JSON.
    */
   public static class ScriptList
   {
      /** The list of scripts. */
      private List<String> list;

      /**
       * @return the list of scripts.
       */
      public List<String> getList()
      {
         return list;
      }

      /**
       * @param the list of scripts
       */
      public ScriptList(List<String> scriptList)
      {
         this.list = scriptList;
      }
   }
}
