/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.application.perspective;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.event.LockIFrameElementsEvent;
import org.exoplatform.gwtframework.ui.event.UnlockIFrameElementsEvent;
import org.exoplatform.gwtframework.ui.smartgwt.GWTMenuWrapper;
import org.exoplatform.gwtframework.ui.smartgwt.GWTToolbarWrapper;
import org.exoplatform.ideall.client.editor.EditorForm;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.event.ClearFocusEvent;
import org.exoplatform.ideall.client.event.perspective.EditorPanelRestoredEvent;
import org.exoplatform.ideall.client.event.perspective.MaximizeEditorPanelEvent;
import org.exoplatform.ideall.client.event.perspective.MaximizeEditorPanelHandler;
import org.exoplatform.ideall.client.event.perspective.MaximizeOperationPanelEvent;
import org.exoplatform.ideall.client.event.perspective.MaximizeOperationPanelHandler;
import org.exoplatform.ideall.client.event.perspective.OperationPanelRestoredEvent;
import org.exoplatform.ideall.client.event.perspective.RestoreEditorPanelEvent;
import org.exoplatform.ideall.client.event.perspective.RestoreEditorPanelHandler;
import org.exoplatform.ideall.client.event.perspective.RestoreOperationPanelEvent;
import org.exoplatform.ideall.client.event.perspective.RestoreOperationPanelHandler;
import org.exoplatform.ideall.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ideall.client.event.perspective.RestorePerspectiveHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.navigation.NavigationForm;
import org.exoplatform.ideall.client.operation.OperationForm;
import org.exoplatform.ideall.client.statusbar.StatusBarForm;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.events.MouseUpEvent;
import com.smartgwt.client.widgets.events.MouseUpHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DefaultPerspective extends VLayout implements MaximizeEditorPanelHandler, RestoreEditorPanelHandler,
   MaximizeOperationPanelHandler, RestoreOperationPanelHandler, EditorActiveFileChangedHandler,
   RestorePerspectiveHandler
{

   private static final int MARGIN = 3;

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private GWTMenuWrapper menuWrapper;

   private GWTToolbarWrapper toolbarWrapper;

   protected HLayout horizontalSplitLayout;

   protected VLayout verticalSplitLayout;

   private NavigationForm navigationForm;

   private EditorForm editorForm;

   private OperationForm operationForm;

   protected StatusBarForm statusBar;

   /*
    * PERSPECTIVE STATE
    */

   private boolean navigationPanelVisible;

   private boolean operationPanelVisible;

   private int operationPanelHeight;

   private boolean editorPanelMaximized;

   private boolean operationPanelMaximized;

   public DefaultPerspective(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      this.handlers = new Handlers(eventBus);
      buildPerspective();

      eventBus.addHandler(MaximizeEditorPanelEvent.TYPE, this);
      eventBus.addHandler(RestoreEditorPanelEvent.TYPE, this);
      eventBus.addHandler(MaximizeOperationPanelEvent.TYPE, this);
      eventBus.addHandler(RestoreOperationPanelEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(RestorePerspectiveEvent.TYPE, this);
   }

   private void buildPerspective()
   {
      menuWrapper = new GWTMenuWrapper(eventBus);
      addMember(menuWrapper);

      toolbarWrapper = new GWTToolbarWrapper(eventBus);
      addMember(toolbarWrapper);

      horizontalSplitLayout = new HLayout();
      horizontalSplitLayout.setMargin(MARGIN);
      horizontalSplitLayout.setWidth100();
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
      verticalSplitLayout.setHeight100();
      verticalSplitLayout.setOverflow(Overflow.HIDDEN);
      horizontalSplitLayout.addMember(verticalSplitLayout);

      editorForm = new EditorForm(eventBus, context);
      editorForm.setShowResizeBar(true);
      editorForm.setResizeBarTarget("next");
      verticalSplitLayout.addMember(editorForm);

      operationForm = new OperationForm(eventBus, context);
      operationForm.setHeight(180);
      operationForm.hide();
      verticalSplitLayout.addMember(operationForm);      

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

   @Override
   protected void onDestroy()
   {
      handlers.removeHandlers();
      super.onDestroy();
   }

   private void maximizeEditorPanel()
   {
      navigationPanelVisible = navigationForm.isVisible();
      operationPanelVisible = operationForm.isVisible();

      navigationForm.hide();
      horizontalSplitLayout.setResizeBarSize(0);

      operationForm.hide();
      verticalSplitLayout.setResizeBarSize(0);

      statusBar.hide();

      editorPanelMaximized = true;
   }

   private void restoreEditorPanel()
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

      editorPanelMaximized = false;

      eventBus.fireEvent(new EditorPanelRestoredEvent());
   }

   private void maximizeOperationPanel()
   {
      System.out.println("maximizing operation panel");
      
      navigationPanelVisible = navigationForm.isVisible();
      navigationForm.hide();
      horizontalSplitLayout.setResizeBarSize(0);

      editorForm.hide();

      verticalSplitLayout.setResizeBarSize(0);

      operationPanelHeight = operationForm.getHeight();
      operationForm.setHeight100();
      System.out.println("operation panel height: " + operationPanelHeight);

      statusBar.hide();

      eventBus.fireEvent(new ClearFocusEvent());

      operationPanelMaximized = true;
      
      System.out.println("end maximizing......");
   }

   private void restoreOperationPanel()
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

      operationPanelMaximized = false;

      eventBus.fireEvent(new OperationPanelRestoredEvent());
   }

   public void onMaximizeEditorPanel(MaximizeEditorPanelEvent event)
   {
      maximizeEditorPanel();
   }

   public void onRestoreEditorPanel(RestoreEditorPanelEvent event)
   {
      restoreEditorPanel();
   }

   public void onMaximizeOperationPanel(MaximizeOperationPanelEvent event)
   {
      maximizeOperationPanel();
   }

   public void onRestoreOperationPanel(RestoreOperationPanelEvent event)
   {
      restoreOperationPanel();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      if (operationPanelMaximized)
      {
         restoreOperationPanel();

         return;
      }
   }

   private void restorePerspective()
   {
      if (editorPanelMaximized)
      {
         restoreEditorPanel();

         return;
      }

      if (operationPanelMaximized)
      {
         restoreOperationPanel();

         return;
      }
   }

   public void onRestorePerspective(RestorePerspectiveEvent event)
   {
      restorePerspective();
   }

}
