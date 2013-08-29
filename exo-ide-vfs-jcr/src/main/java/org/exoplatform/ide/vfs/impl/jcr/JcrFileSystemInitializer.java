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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.commons.ContainerUtils;
import org.exoplatform.ide.vfs.server.RequestContext;
import org.exoplatform.ide.vfs.server.URLHandlerFactorySetup;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemProvider;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;

import javax.jcr.RepositoryException;

/**
 * Useful for local build if we have limited and known set of available virtual file systems. Do not use this component
 * when run in cloud environment. In cloud environment virtual file systems should be added dynamically when new
 * workspace is up and removed when workspace goes down.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 * @see org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry
 * @see org.exoplatform.ide.vfs.server.VirtualFileSystemFactory
 */
public final class JcrFileSystemInitializer implements Startable
{
   private static final Log LOG = ExoLogger.getExoLogger(JcrFileSystemInitializer.class);

   private final RepositoryService repositoryService;
   private final MediaType2NodeTypeResolver mediaType2NodeTypeResolver;
   private final Collection<String> vfsIds;
   private final String workspace;
   private final VirtualFileSystemRegistry vfsRegistry;
   private final EventListenerList listeners;

   public JcrFileSystemInitializer(InitParams initParams,
                                   RepositoryService repositoryService,
                                   MediaType2NodeTypeResolver itemType2NodeTypeResolver,
                                   VirtualFileSystemRegistry vfsRegistry,
                                   EventListenerList listeners)
   {
      this(repositoryService,
         ContainerUtils.readValuesParam(initParams, "repository-ids"),
         itemType2NodeTypeResolver,
         ContainerUtils.readValueParam(initParams, "jcr-workspace"),
         vfsRegistry,
         listeners);
   }

   public JcrFileSystemInitializer(InitParams initParams,
                                   RepositoryService repositoryService,
                                   MediaType2NodeTypeResolver itemType2NodeTypeResolver,
                                   VirtualFileSystemRegistry vfsRegistry)
   {
      this(repositoryService,
         ContainerUtils.readValuesParam(initParams, "repository-ids"),
         itemType2NodeTypeResolver,
         ContainerUtils.readValueParam(initParams, "jcr-workspace"),
         vfsRegistry,
         null);
   }

   public JcrFileSystemInitializer(InitParams initParams,
                                   RepositoryService repositoryService,
                                   VirtualFileSystemRegistry vfsRegistry,
                                   EventListenerList listeners)
   {
      this(initParams, repositoryService, new MediaType2NodeTypeResolver(), vfsRegistry, listeners);
   }

   public JcrFileSystemInitializer(InitParams initParams,
                                   RepositoryService repositoryService,
                                   VirtualFileSystemRegistry vfsRegistry)
   {
      this(initParams, repositoryService, vfsRegistry, null);
   }

   /* ================================================================== */

   public JcrFileSystemInitializer(RepositoryService repositoryService,
                                   Collection<String> vfsIds,
                                   String workspace,
                                   VirtualFileSystemRegistry vfsRegistry,
                                   EventListenerList listeners)
   {
      this(repositoryService, vfsIds, new MediaType2NodeTypeResolver(), workspace, vfsRegistry, listeners);
   }

   public JcrFileSystemInitializer(RepositoryService repositoryService,
                                   Collection<String> vfsIds,
                                   MediaType2NodeTypeResolver mediaType2NodeTypeResolver,
                                   String workspace,
                                   VirtualFileSystemRegistry vfsRegistry,
                                   EventListenerList listeners)
   {
      this.repositoryService = repositoryService;
      this.vfsIds = new HashSet<String>(vfsIds);
      this.workspace = workspace;
      this.vfsRegistry = vfsRegistry;
      this.listeners = listeners;
      if (mediaType2NodeTypeResolver == null)
      {
         throw new NullPointerException("MediaType2NodeTypeResolver may not be null. ");
      }
      this.mediaType2NodeTypeResolver = mediaType2NodeTypeResolver;
   }

   /** @see org.picocontainer.Startable#start() */
   @Override
   public void start()
   {
      URLHandlerFactorySetup.setup(vfsRegistry, listeners);
      for (String vfsId : vfsIds)
      {
         try
         {
            vfsRegistry.registerProvider(vfsId, new JcrFileSystemProvider(repositoryService,
               mediaType2NodeTypeResolver, workspace, null, vfsId));
         }
         catch (VirtualFileSystemException e)
         {
            LOG.error(e.getMessage(), e);
         }
      }
   }

   /** @see org.picocontainer.Startable#stop() */
   @Override
   public void stop()
   {
      for (String vfsId : vfsIds)
      {
         try
         {
            vfsRegistry.unregisterProvider(vfsId);
         }
         catch (VirtualFileSystemException e)
         {
            LOG.error(e.getMessage(), e);
         }
      }
   }

   private static final class JcrFileSystemProvider implements VirtualFileSystemProvider
   {
      private final RepositoryService repositoryService;
      private final MediaType2NodeTypeResolver mediaType2NodeTypeResolver;
      private final String workspace;
      private final String rootNodePath;
      private final String vfsId;

      JcrFileSystemProvider(RepositoryService repositoryService,
                            MediaType2NodeTypeResolver mediaType2NodeTypeResolver,
                            String workspace,
                            String rootNodePath,
                            String vfsId)
      {
         this.repositoryService = repositoryService;
         this.mediaType2NodeTypeResolver = mediaType2NodeTypeResolver;
         this.workspace = workspace;
         this.rootNodePath = rootNodePath;
         this.vfsId = vfsId;
      }

      @Override
      public VirtualFileSystem newInstance(RequestContext requestContext, EventListenerList listeners)
         throws VirtualFileSystemException
      {
         ManageableRepository repository;
         String ws = workspace;
         try
         {
            repository = repositoryService.getCurrentRepository();
            if (ws == null)
            {
               ws = repository.getConfiguration().getDefaultWorkspaceName();
            }
         }
         catch (RepositoryException re)
         {
            throw new VirtualFileSystemException(re.getMessage(), re);
         }
         return new JcrFileSystem(repository,
            ws,
            rootNodePath,
            vfsId,
            mediaType2NodeTypeResolver,
            requestContext != null ? requestContext.getUriInfo().getBaseUri() : URI.create(""),
            listeners);
      }

      @Override
      public void close()
      {
      }
   }
}
