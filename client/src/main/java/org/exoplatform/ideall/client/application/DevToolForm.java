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
import org.exoplatform.ideall.client.editor.EditorForm;
import org.exoplatform.ideall.client.event.ClearFocusEvent;
import org.exoplatform.ideall.client.event.ClearFocusHandler;
import org.exoplatform.ideall.client.event.layout.MaximizeEditorPanelEvent;
import org.exoplatform.ideall.client.event.layout.MaximizeEditorPanelHandler;
import org.exoplatform.ideall.client.event.layout.MaximizeOperationPanelEvent;
import org.exoplatform.ideall.client.event.layout.MaximizeOperationPanelHandler;
import org.exoplatform.ideall.client.event.layout.RestoreEditorPanelEvent;
import org.exoplatform.ideall.client.event.layout.RestoreEditorPanelHandler;
import org.exoplatform.ideall.client.event.layout.RestoreOperationPanelEvent;
import org.exoplatform.ideall.client.event.layout.RestoreOperationPanelHandler;
import org.exoplatform.ideall.client.menu.GWTMenuWrapper;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.navigation.NavigationForm;
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

public class DevToolForm extends VLayout implements DevToolPresenter.Display, ClearFocusHandler,
   MaximizeEditorPanelHandler, RestoreEditorPanelHandler, MaximizeOperationPanelHandler, RestoreOperationPanelHandler
{

   private static final int MARGIN = 3;

   private DevToolPresenter presenter;

   private HandlerManager eventBus;

   private ApplicationContext context;

   private NavigationForm navigationForm;

   private EditorForm editorForm;

   private OperationForm operationForm;

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

      eventBus.addHandler(MaximizeEditorPanelEvent.TYPE, this);
      eventBus.addHandler(RestoreEditorPanelEvent.TYPE, this);
      eventBus.addHandler(MaximizeOperationPanelEvent.TYPE, this);
      eventBus.addHandler(RestoreOperationPanelEvent.TYPE, this);

      draw();

      presenter = new DevToolPresenter(eventBus, context);
      presenter.bindDisplay(this);
   }

   protected HLayout horizontalSplitLayout;

   protected VLayout verticalSplitLayout;

   protected StatusBarForm statusBar;

   public void buildLayout()
   {
      GWTMenuWrapper menuWrapper = new GWTMenuWrapper(eventBus);
      addMember(menuWrapper);

      GWTToolbarWrapper toolbarWrapper = new GWTToolbarWrapper(eventBus);
      addMember(toolbarWrapper);

      horizontalSplitLayout = new HLayout();
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

      verticalSplitLayout = new VLayout();
      verticalSplitLayout.setOverflow(Overflow.HIDDEN);
      horizontalSplitLayout.addMember(verticalSplitLayout);

      editorForm = new EditorForm(eventBus, context);
      editorForm.setShowResizeBar(true);
      editorForm.setResizeBarTarget("next");
      verticalSplitLayout.addMember(editorForm);

      operationForm = new OperationForm(eventBus, context);
      verticalSplitLayout.addMember(operationForm);
      operationForm.hide();

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

      statusBar = new StatusBarForm(eventBus);
      addMember(statusBar);
   }

   public void onClearFocus(ClearFocusEvent event)
   {
      clearFocusItem.selectValue();
   }

   private boolean navigationPanelVisible;

   private boolean operationPanelVisible;

   private int operationPanelHeight;

   public void onMaximizeEditorPanel(MaximizeEditorPanelEvent event)
   {
      navigationPanelVisible = navigationForm.isVisible();
      operationPanelVisible = operationForm.isVisible();

      navigationForm.hide();
      horizontalSplitLayout.setResizeBarSize(0);

      operationForm.hide();
      verticalSplitLayout.setResizeBarSize(0);

      statusBar.hide();
   }

   public void onRestoreEditorPanel(RestoreEditorPanelEvent event)
   {
      if (navigationPanelVisible)
      {
         navigationForm.show();
      }
      horizontalSplitLayout.setResizeBarSize(9);

      if (operationPanelVisible)
      {
         operationForm.show();
      }
      verticalSplitLayout.setResizeBarSize(9);

      statusBar.show();
   }

   public void onMaximizeOperationPanel(MaximizeOperationPanelEvent event)
   {
      navigationPanelVisible = navigationForm.isVisible();
      navigationForm.hide();
      horizontalSplitLayout.setResizeBarSize(0);

      editorForm.hide();

      verticalSplitLayout.setResizeBarSize(0);

      operationPanelHeight = operationForm.getHeight();
      operationForm.setHeight100();

      statusBar.hide();

      onClearFocus(null);
   }

   public void onRestoreOperationPanel(RestoreOperationPanelEvent event)
   {
      if (navigationPanelVisible)
      {
         navigationForm.show();
      }

      horizontalSplitLayout.setResizeBarSize(9);

      editorForm.show();
      verticalSplitLayout.setResizeBarSize(9);

      operationForm.setHeight(operationPanelHeight);

      statusBar.show();
   }

}
