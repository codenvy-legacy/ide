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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.Template;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFileFromTemplateForm extends AbstractCreateFromTemplateForm<FileTemplate>
{

   public CreateFileFromTemplateForm(HandlerManager eventBus, List<Template> templateList, 
      AbstractCreateFromTemplatePresenter<FileTemplate> presenter)
   {
      super(eventBus, presenter);
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplateForm#createTypeLayout()
    */
   @Override
   void createTypeLayout()
   {
      templateListGrid = new TemplateListGrid<FileTemplate>();
      templateListGrid.setWidth("100%");
      templateListGrid.setHeight(HEIGHT - 30 +"px");
      // templateListGrid.setCanFocus(false);  // to fix bug IDE-258 "Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility."
      windowLayout.add(templateListGrid);
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplateForm#getCreateButtonTitle()
    */
   @Override
   String getCreateButtonTitle()
   {
      return "Create";
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplateForm#getFormTitle()
    */
   @Override
   String getFormTitle()
   {
      return "Create file";
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplateForm#getNameFieldLabel()
    */
   @Override
   String getNameFieldLabel()
   {
      return "File Name";
   }

}
