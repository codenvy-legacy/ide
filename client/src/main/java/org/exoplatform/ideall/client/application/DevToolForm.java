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
package org.exoplatform.ideall.client.application;

import org.exoplatform.ideall.client.application.perspective.DefaultPerspective;
import org.exoplatform.ideall.client.event.ClearFocusEvent;
import org.exoplatform.ideall.client.event.ClearFocusHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DevToolForm extends Layout implements DevToolPresenter.Display, ClearFocusHandler
{

   private DevToolPresenter presenter;

   private HandlerManager eventBus;

   private ApplicationContext context;

   private TextItem clearFocusItem;

   public DevToolForm(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      setWidth100();
      setHeight100();
      setOverflow(Overflow.HIDDEN);

      DynamicForm clearFocusForm = new DynamicForm();
      clearFocusItem = new TextItem();
      clearFocusForm.setItems(clearFocusItem);
      clearFocusForm.setWidth(1);
      clearFocusForm.setHeight(1);
      addChild(clearFocusForm);
      clearFocusForm.setOverflow(Overflow.HIDDEN);
      clearFocusForm.setLeft(-100);
      clearFocusForm.setTop(-100);
      eventBus.addHandler(ClearFocusEvent.TYPE, this);

      draw();

      presenter = new DevToolPresenter(eventBus, context);
      presenter.bindDisplay(this);
   }

   public void showDefaultPerspective()
   {
      addMember(new DefaultPerspective(eventBus, context));
   }

   public void onClearFocus(ClearFocusEvent event)
   {
      clearFocusItem.selectValue();
   }

}
