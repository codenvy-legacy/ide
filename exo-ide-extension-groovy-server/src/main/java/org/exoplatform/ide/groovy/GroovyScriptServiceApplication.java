/*
 * Copyright (C) 2010 eXo Platform SAS.
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

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.groovy.codeassistant.ClassInfoStrorage;
import org.exoplatform.ide.groovy.codeassistant.CodeAssistant;
import org.exoplatform.ide.groovy.codeassistant.DocStorage;
import org.exoplatform.ide.groovy.codeassistant.bean.GroovyAutocompletionConfig;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GroovyScriptServiceApplication extends Application
{

   private final Set<Object> objects = new HashSet<Object>();

   private final Set<Class<?>> classes = new HashSet<Class<?>>();

   public GroovyScriptServiceApplication(ThreadLocalSessionProviderService sessionProvider, RepositoryService repositoryService,
      InitParams initParams)
   {
      if (initParams != null)
      {
         GroovyAutocompletionConfig config =
            (GroovyAutocompletionConfig)initParams.getObjectParam("autocompletion.configuration").getObject();

         if (config.getJarEntries() != null)
         {
            objects.add(new ClassInfoStrorage(sessionProvider, repositoryService, config.getWsName(), config
               .getJarEntries(), config.isRunInThread()));
         }

         if (config.getJarsDocs() != null)
         {
            objects.add(new DocStorage(config.getWsName(), repositoryService, sessionProvider, config.getJarsDocs(),
               config.isRunInThread()));
         }

         objects.add(new CodeAssistant(config.getWsName(), repositoryService, sessionProvider));

      }
      classes.add(GroovyTemplateService.class);

   }

   public Set<Class<?>> getClasses()
   {
      return classes;
   }

   public Set<Object> getSingletons()
   {
      return objects;
   }

}
