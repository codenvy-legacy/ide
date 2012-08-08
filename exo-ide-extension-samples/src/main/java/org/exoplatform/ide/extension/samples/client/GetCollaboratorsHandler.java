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
package org.exoplatform.ide.extension.samples.client;

import com.google.gwt.core.client.GWT;

import com.google.gwt.http.client.RequestException;

import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.extension.samples.shared.Collaborators;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: GetCollaboratorsHandler.java Aug 6, 2012
 * 
 */
public class GetCollaboratorsHandler implements EventHandler
{

   public interface Display
   {
      void showCollaborators(Collaborators collaborators);
   }
   
   private Display display;

   void onGetCollaborators(GetCollboratorsEvent event)
   {
      AutoBean<Collaborators> autoBean = SamplesExtension.AUTO_BEAN_FACTORY.collaborators();
      AutoBeanUnmarshaller<Collaborators> unmarshaller = new AutoBeanUnmarshaller<Collaborators>(autoBean);
      try
      {
         SamplesClientService.getInstance().getCollaborators("eXoIDE", "rails-demo",
            new AsyncRequestCallback<Collaborators>(unmarshaller)
            {

               @Override
               protected void onSuccess(Collaborators result)
               {
                  showColloborators(result);

               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO Auto-generated method stub

               }
            });
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   protected void showColloborators(Collaborators result)
   {
      display = GWT.create(Display.class);
      bindDisplay();
      display.showCollaborators(result);
   }

   private void bindDisplay()
   {
      // TODO Auto-generated method stub
      
   }
}
