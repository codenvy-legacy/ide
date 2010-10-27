/**
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
 *
 */

package org.exoplatform.ide.client.module.navigation.handler;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateHandler;
import org.exoplatform.ide.client.template.CreateFileFromTemplateForm;
import org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateFileFromTemplateCommandHandler implements CreateFileFromTemplateHandler,
   TemplateListReceivedHandler, ExceptionThrownHandler, ItemsSelectedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private List<Item> selectedItems = new ArrayList<Item>();

   public CreateFileFromTemplateCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      eventBus.addHandler(CreateFileFromTemplateEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   public void onCreateFileFromTemplate(CreateFileFromTemplateEvent event)
   {
      handlers.addHandler(TemplateListReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      TemplateService.getInstance().getTemplates();
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   public void onTemplateListReceived(TemplateListReceivedEvent event)
   {
      handlers.removeHandlers();

      TemplateList templateList = event.getTemplateList();
      CreateFileFromTemplatePresenter createFilePresenter =
         new CreateFileFromTemplatePresenter(eventBus, selectedItems, templateList.getTemplates());
      CreateFromTemplateDisplay<FileTemplate> createFileDisplay =
         new CreateFileFromTemplateForm(eventBus, templateList.getTemplates(), createFilePresenter);
      createFilePresenter.bindDisplay(createFileDisplay);
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

}
