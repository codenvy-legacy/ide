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
package org.exoplatform.ide.client.framework.paas;

import org.exoplatform.ide.vfs.client.model.ProjectModel;


/**
 * Class, where paases store their data, when they are registred in IDE.
 * <p/>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Paas.java Dec 1, 2011 4:14:16 PM vereshchaka $
 */
public class Paas
{
   private String name;
   
   private PaasComponent provider;
   
   public Paas(String name, PaasComponent provider)
   {
      this.name = name;
      this.provider = provider;
   }
   
   /**
    * @param name the name to set
    */
   public void setName(String name)
   {
      this.name = name;
   }
   
   public String getName()
   {
      return name;
   }
   
   public void getView(String projectName, PaasCallback paasCallback)
   {
      provider.getView(projectName, paasCallback);
   }
   
   public void validate()
   {
      provider.validate();
   }
   
   public void deploy(ProjectModel project)
   {
      provider.deploy(project);
   }
}
