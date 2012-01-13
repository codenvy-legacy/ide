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
package org.exoplatform.ide.client.model.template;

import java.util.ArrayList;
import java.util.List;

/**
 * Data for file templates. Used by unmarshaller and client service.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: FileTemplateList.java Jul 28, 2011 11:13:59 AM vereshchaka $
 * 
 */
public class FileTemplateList
{

   private List<FileTemplate> fileTemplates = new ArrayList<FileTemplate>();

   /**
    * @return the templates
    */
   public List<FileTemplate> getFileTemplates()
   {
      return fileTemplates;
   }

   /**
    * @param templates
    */
   public void setFileTemplates(List<FileTemplate> templates)
   {
      this.fileTemplates = templates;
   }

}
