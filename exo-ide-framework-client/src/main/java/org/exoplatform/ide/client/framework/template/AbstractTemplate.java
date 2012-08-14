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
package org.exoplatform.ide.client.framework.template;

import com.google.gwt.resources.client.ImageResource;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class AbstractTemplate implements Template
{

   private String name;

   private String description;

   private String nodeName;

   /**
    * If template is default, than it must be created by server. If not default (user template), than it must be crated by client.
    */
   private boolean isDefault;

   public AbstractTemplate()
   {
   }

   public AbstractTemplate(String name)
   {
      this.name = name;
   }

   public AbstractTemplate(String name, String description, String nodeName)
   {
      this.name = name;
      this.description = description;
      this.nodeName = nodeName;
   }

   public AbstractTemplate(String name, String description, boolean isDefault)
   {
      this.name = name;
      this.description = description;
      this.isDefault = isDefault;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#isDefault()
    */
   @Override
   public Boolean isDefault()
   {
      return isDefault;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#setDefault(boolean)
    */
   @Override
   public void setDefault(Boolean isDefault)
   {
      this.isDefault = isDefault;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#getName()
    */
   @Override
   public String getName()
   {
      return name;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#setName(java.lang.String)
    */
   @Override
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#getDescription()
    */
   @Override
   public String getDescription()
   {
      return description;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#setDescription(java.lang.String)
    */
   @Override
   public void setDescription(String description)
   {
      this.description = description;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#getNodeName()
    */
   @Override
   public String getNodeName()
   {
      return nodeName;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#setNodeName(java.lang.String)
    */
   @Override
   public void setNodeName(String nodeName)
   {
      this.nodeName = nodeName;
   }

   /**
    * @see org.exoplatform.ide.client.framework.template.Template#getIcon()
    */
   @Override
   public abstract ImageResource getIcon();

}
