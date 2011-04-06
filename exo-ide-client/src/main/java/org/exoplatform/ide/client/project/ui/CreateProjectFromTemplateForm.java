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
package org.exoplatform.ide.client.project.ui;

import java.util.List;

import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter;
import org.exoplatform.ide.client.template.ui.AbstractCreateFromTemplateForm;
import org.exoplatform.ide.client.template.ui.TemplateListGrid;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateProjectFromTemplateForm extends AbstractCreateFromTemplateForm<ProjectTemplate>
{

   public CreateProjectFromTemplateForm(HandlerManager eventBus, List<Template> templateList,
      AbstractCreateFromTemplatePresenter<ProjectTemplate> presenter)
   {
      super(eventBus, presenter);
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplateForm#createTypeLayout()
    */
   @Override
   protected void createTypeLayout()
   {
      templateListGrid = new TemplateListGrid<ProjectTemplate>();
      // templateListGrid.setCanFocus(false);  // to fix bug IDE-258 "Enable navigation by using keyboard in the Navigation, Search and Outline Panel to improve IDE accessibility."
      templateListGrid.setWidth("100%");
      templateListGrid.setHeight(200);
      windowLayout.add(templateListGrid);
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplateForm#getCreateButtonTitle()
    */
   @Override
   protected String getCreateButtonTitle()
   {
      return "Create";
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplateForm#getFormTitle()
    */
   @Override
   protected String getFormTitle()
   {
      return "Create project";
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplateForm#getNameFieldLabel()
    */
   @Override
   protected String getNameFieldLabel()
   {
      return "Project name";
   }

}
