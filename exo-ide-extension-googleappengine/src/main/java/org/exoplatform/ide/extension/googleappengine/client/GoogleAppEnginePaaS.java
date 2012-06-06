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
package org.exoplatform.ide.extension.googleappengine.client;

import java.util.Arrays;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.paas.PaasComponent;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class GoogleAppEnginePaaS implements PaasComponent
{
   
   private PaasCallback paasCallback;

   public GoogleAppEnginePaaS()
   {
      IDE.getInstance().addPaas(
         new Paas("Google App Engine", this, Arrays.asList(ProjectResolver.APP_ENGINE_JAVA,
            ProjectResolver.APP_ENGINE_PYTHON))
         {
            @Override
            public boolean isFirstInDeployments()
            {
               return true;
            }
         });

   }

   @Override
   public void getView(String projectName, PaasCallback paasCallback)
   {
      this.paasCallback = paasCallback;
   }

   @Override
   public void validate()
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            paasCallback.onValidate(true);
         }
      });
   }

   @Override
   public void deploy(ProjectModel project)
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            IDE.fireEvent(new CreateApplicationEvent());
         }
      });
   }

   @Override
   public void createProject(ProjectModel project)
   {
   }

}
