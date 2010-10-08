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

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.IButton;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.ui.DialogWindow;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.module.vfs.api.Item;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.StatefulCanvas;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.ToolbarItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

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

}
