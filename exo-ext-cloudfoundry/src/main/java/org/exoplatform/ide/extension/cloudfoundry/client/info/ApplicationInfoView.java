/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.info;

import org.exoplatform.ide.view.View;

import java.util.List;

/**
 * 
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface ApplicationInfoView extends View<ApplicationInfoView.ActionDelegate>
{
   public interface ActionDelegate
   {
      public void onOKClicked();
   }

   public void setName(String text);

   public void setState(String text);

   public void setInstances(String text);

   public void setVersion(String text);

   public void setDisk(String text);

   public void setMemory(String text);

   public void setStack(String text);

   public void setModel(String text);

   public void setApplicationUris(List<String> applications);

   public void setApplicationServices(List<String> services);

   public void setApplicationEnvironments(List<String> environments);

   public void showDialog();

   public void close();
}