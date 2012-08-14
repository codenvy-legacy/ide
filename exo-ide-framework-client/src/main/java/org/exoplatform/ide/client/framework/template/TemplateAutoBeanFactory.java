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
package org.exoplatform.ide.client.framework.template;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 27, 2012 2:35:55 PM anya $
 * 
 */
public interface TemplateAutoBeanFactory extends AutoBeanFactory
{
   TemplateAutoBeanFactory AUTO_BEAN_FACTORY = GWT.create(TemplateAutoBeanFactory.class);

   /**
    * A factory method for folder template.
    * 
    * @return an {@link AutoBean} of type {@link FolderTemplate}
    */
   AutoBean<FolderTemplate> folderTemplate();

   /**
    * A factory method for folder template.
    * 
    * @return an {@link AutoBean} of type {@link FolderTemplate}
    */
   AutoBean<Template> template();

   /**
    * A factory method for folder template.
    * 
    * @return an {@link AutoBean} of type {@link FileTemplate}
    */
   AutoBean<FileTemplate> fileTemplate();

   /**
    * A factory method for project template.
    * 
    * @return an {@link AutoBean} of type {@link ProjectTemplate}
    */
   AutoBean<ProjectTemplate> projectTemplate();
}
