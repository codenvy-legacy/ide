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

import org.exoplatform.gwt.commons.component.event.LockIFrameElementsEvent;
import org.exoplatform.gwt.commons.component.event.UnlockIFrameElementsEvent;
import org.exoplatform.ideall.client.browser.NavigationForm;
import org.exoplatform.ideall.client.editor.EditorForm;
import org.exoplatform.ideall.client.event.ClearFocusEvent;
import org.exoplatform.ideall.client.event.ClearFocusHandler;
import org.exoplatform.ideall.client.menu.GWTMenuWrapper;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.operation.OperationForm;
import org.exoplatform.ideall.client.statusbar.StatusBarForm;
import org.exoplatform.ideall.client.toolbar.GWTToolbarWrapper;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.events.MouseUpEvent;
import com.smartgwt.client.widgets.events.MouseUpHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DevToolForm extends VLayout implements DevToolPresenter.Display, ClearFocusHandler
{

   private static final int MARGIN = 3;

   private DevToolPresenter presenter;

   private HandlerManager eventBus;

   private ApplicationContext context;

   private NavigationForm navigationForm;

   private EditorForm editorForm;

   private OperationForm viewForm;

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

   public void buildLayout()
   {
      GWTMenuWrapper menuWrapper = new GWTMenuWrapper(eventBus);
      addMember(menuWrapper);

      GWTToolbarWrapper toolbarWrapper = new GWTToolbarWrapper(eventBus, context);
      addMember(toolbarWrapper);

      HLayout horizontalSplitLayout = new HLayout();
      horizontalSplitLayout.setMargin(MARGIN);
      addMember(horizontalSplitLayout);
      navigationForm = new NavigationForm(eventBus, context);
      navigationForm.setWidth("30%");
      navigationForm.setShowResizeBar(true);
      horizontalSplitLayout.addMember(navigationForm);

      horizontalSplitLayout.addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            eventBus.fireEvent(new LockIFrameElementsEvent());
         }
      });

      horizontalSplitLayout.addMouseUpHandler(new MouseUpHandler()
      {
         public void onMouseUp(MouseUpEvent event)
         {
            eventBus.fireEvent(new UnlockIFrameElementsEvent());
         }
      });

      VLayout verticalSplitLayout = new VLayout();
      verticalSplitLayout.setOverflow(Overflow.HIDDEN);
      horizontalSplitLayout.addMember(verticalSplitLayout);

      editorForm = new EditorForm(eventBus, context);
      editorForm.setShowResizeBar(true);
      editorForm.setResizeBarTarget("next");
      verticalSplitLayout.addMember(editorForm);

      viewForm = new OperationForm(eventBus, context);
      verticalSplitLayout.addMember(viewForm);
      viewForm.hide();

      verticalSplitLayout.addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            eventBus.fireEvent(new LockIFrameElementsEvent());
         }
      });

      verticalSplitLayout.addMouseUpHandler(new MouseUpHandler()
      {
         public void onMouseUp(MouseUpEvent event)
         {
            eventBus.fireEvent(new UnlockIFrameElementsEvent());
         }
      });

      StatusBarForm statusBar = new StatusBarForm(eventBus);
      addMember(statusBar);
   }

   public void onClearFocus(ClearFocusEvent event)
   {
      clearFocusItem.selectValue();
   }

}
