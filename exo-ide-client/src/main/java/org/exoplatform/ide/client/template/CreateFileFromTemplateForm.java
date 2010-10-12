/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
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
      // templateListGrid.setCanFocus(false);  // to fix bug IDE-258 "Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility."
      windowLayout.addMember(templateListGrid);
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
