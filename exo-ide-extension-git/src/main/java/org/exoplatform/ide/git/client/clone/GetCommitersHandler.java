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
package org.exoplatform.ide.git.client.clone;

import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.shared.Commiters;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import com.google.gwt.event.shared.EventHandler;

/**
 * Handler for {@link CloneRepositoryEvent} event.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 22, 2011 4:07:45 PM anya $
 * 
 */
public class GetCommitersHandler extends GitPresenter implements EventHandler
{
   public GetCommitersHandler()
   {
     IDE.addHandler(GetCommitersEvent.TYPE, this);
   }  
   
   public void onGetCommiters(GetCommitersEvent event)
   {
      if (((ItemContext)selectedItems.get(0)).getProject() != null)
      {
         doGetCommiters(((ItemContext)selectedItems.get(0)).getProject());   
      }
        
   }
   

   private void doGetCommiters(ProjectModel project) 
   {
      AutoBean<Commiters> commitersAutoBean = GitExtension.AUTO_BEAN_FACTORY.commiters();
      AutoBeanUnmarshaller<Commiters> unmarshaller = new AutoBeanUnmarshaller<Commiters>(commitersAutoBean);
      try
      {
         GitClientService.getInstance().getCommiters(vfs.getId(), project.getId(), new AsyncRequestCallback<Commiters>(unmarshaller)
         {
            
            @Override
            protected void onSuccess(Commiters result)
            {
               System.out.println(result.getCommiters().size());
               
            }
            
            @Override
            protected void onFailure(Throwable exception)
            {
               exception.printStackTrace();
               
            }
         });
      }
      catch (RequestException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      
   }
}
