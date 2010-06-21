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
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.editor.api.TextEditor;
import org.exoplatform.gwtframework.ui.client.event.LockIFrameElementsEvent;
import org.exoplatform.gwtframework.ui.client.event.UnlockIFrameElementsEvent;
import org.exoplatform.gwtframework.ui.client.smartgwt.GWTMenuWrapper;
import org.exoplatform.gwtframework.ui.client.smartgwt.GWTStatusBarWrapper;
import org.exoplatform.gwtframework.ui.client.smartgwt.GWTToolbarWrapper;
import org.exoplatform.ideall.client.editor.EditorForm;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.event.ClearFocusEvent;
import org.exoplatform.ideall.client.event.perspective.CodeHelperPanelRestoredEvent;
import org.exoplatform.ideall.client.event.perspective.EditorPanelRestoredEvent;
import org.exoplatform.ideall.client.event.perspective.MaximizeCodeHelperPanelEvent;
import org.exoplatform.ideall.client.event.perspective.MaximizeCodeHelperPanelHandler;
import org.exoplatform.ideall.client.event.perspective.MaximizeEditorPanelEvent;
import org.exoplatform.ideall.client.event.perspective.MaximizeEditorPanelHandler;
import org.exoplatform.ideall.client.event.perspective.MaximizeOperationPanelEvent;
import org.exoplatform.ideall.client.event.perspective.MaximizeOperationPanelHandler;
import org.exoplatform.ideall.client.event.perspective.OperationPanelRestoredEvent;
import org.exoplatform.ideall.client.event.perspective.RestoreCodeHelperPanelEvent;
import org.exoplatform.ideall.client.event.perspective.RestoreCodeHelperPanelHandler;
import org.exoplatform.ideall.client.event.perspective.RestoreEditorPanelEvent;
import org.exoplatform.ideall.client.event.perspective.RestoreEditorPanelHandler;
import org.exoplatform.ideall.client.event.perspective.RestoreOperationPanelEvent;
import org.exoplatform.ideall.client.event.perspective.RestoreOperationPanelHandler;
import org.exoplatform.ideall.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ideall.client.event.perspective.RestorePerspectiveHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.navigation.NavigationForm;
import org.exoplatform.ideall.client.operation.OperationForm;
import org.exoplatform.ideall.client.outline.CodeHelperForm;
import org.exoplatform.ideall.client.outline.event.ShowOutlineEvent;
import org.exoplatform.ideall.client.outline.event.ShowOutlineHandler;
import org.exoplatform.ideall.vfs.api.File;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.events.MouseUpEvent;
import com.smartgwt.client.widgets.events.MouseUpHandler;
import com.smartgwt.client.widgets.events.ResizedEvent;
import com.smartgwt.client.widgets.events.ResizedHandler;
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
   RestorePerspectiveHandler, MaximizeCodeHelperPanelHandler, RestoreCodeHelperPanelHandler,
   ShowOutlineHandler
{

   private static final int MARGIN = 3;
   
   private static final int RESIZE_BAR_SIZE = 5;
   
   private static final int MIN_FORM_WIDTH = 120;
   
   private static final int MIN_CODE_HELPER_WIDTH = 100;
   
   private static final int MIN_OPERATION_HEIGHT = 50;
   
   /**
    * Minimum width of outline resize bar.
    * 
    * Set, when outline form hides
    * 
    * Use 1 instead of 0, because smartgwt exception occurs
    */
   private static final int OUTLINE_RESIZE_BAR_SIZE = 1;

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private GWTMenuWrapper menuWrapper;

   private GWTToolbarWrapper toolbarWrapper;

   protected HLayout horizontalSplitLayout;
   
   protected HLayout horizontalSplitLayout2;

   protected VLayout verticalSplitLayout;

   private NavigationForm navigationForm;

   private EditorForm editorForm;

   private OperationForm operationForm;
   
   private CodeHelperForm codeHelperForm;

   //protected StatusBarForm statusBar;

   protected GWTStatusBarWrapper statusBar;

   /*
    * PERSPECTIVE STATE
    */

   private boolean navigationPanelVisible;

   private boolean operationPanelVisible;

   private int operationPanelHeight;

   private boolean editorPanelMaximized;

   private boolean operationPanelMaximized;
   
   private boolean codeHelperPanelVisible;
   
   private boolean codeHelperPanelMaximized;
   
   private int codeHelperPanelWidth;

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
      eventBus.addHandler(MaximizeCodeHelperPanelEvent.TYPE, this);
      eventBus.addHandler(RestoreCodeHelperPanelEvent.TYPE, this);
      eventBus.addHandler(ShowOutlineEvent.TYPE, this);
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
      horizontalSplitLayout.setResizeBarSize(RESIZE_BAR_SIZE);
      addMember(horizontalSplitLayout);
      navigationForm = new NavigationForm(eventBus, context);
      navigationForm.setWidth("30%");
      navigationForm.setShowResizeBar(true);
      navigationForm.setMinWidth(MIN_FORM_WIDTH);
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
      
      horizontalSplitLayout2 = new HLayout();
      horizontalSplitLayout.addMember(horizontalSplitLayout2);
      

      verticalSplitLayout = new VLayout();
      verticalSplitLayout.setHeight100();
//      verticalSplitLayout.setOverflow(Overflow.HIDDEN);
      verticalSplitLayout.setResizeBarTarget("next");
      verticalSplitLayout.setShowResizeBar(true);
      verticalSplitLayout.setResizeBarSize(RESIZE_BAR_SIZE);
    
      verticalSplitLayout.setWidth("60%");
      verticalSplitLayout.setMinWidth(MIN_FORM_WIDTH);
      horizontalSplitLayout2.addMember(verticalSplitLayout);
      
      editorForm = new EditorForm(eventBus, context);
      editorForm.setShowResizeBar(true);
      editorForm.setResizeBarTarget("next");
      editorForm.setMinWidth(MIN_FORM_WIDTH);
      verticalSplitLayout.addMember(editorForm);

      operationForm = new OperationForm(eventBus, context);
      operationForm.setHeight(180);
      operationForm.setMinHeight(MIN_OPERATION_HEIGHT);
      operationForm.setMinWidth(MIN_FORM_WIDTH);
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
      
      codeHelperForm = new CodeHelperForm(eventBus, context);
      codeHelperForm.setMinWidth(MIN_CODE_HELPER_WIDTH);
      codeHelperForm.setWidth("30%");
      codeHelperForm.showCodeHelper(false);
      
      horizontalSplitLayout2.addMember(codeHelperForm);
      horizontalSplitLayout2.setResizeBarSize(OUTLINE_RESIZE_BAR_SIZE);
      
      horizontalSplitLayout2.addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            eventBus.fireEvent(new LockIFrameElementsEvent());
         }
      });

      horizontalSplitLayout2.addMouseUpHandler(new MouseUpHandler()
      {
         public void onMouseUp(MouseUpEvent event)
         {
            eventBus.fireEvent(new UnlockIFrameElementsEvent());
         }
      });
      
      verticalSplitLayout.addResizedHandler(new ResizedHandler(){

         public void onResized(ResizedEvent event)
         {
            codeHelperForm.showCodeHelper(codeHelperForm.isVisible());
         }
         
      });

      statusBar = new GWTStatusBarWrapper(eventBus);
      //statusBar = new StatusBarForm(eventBus, context);
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
      codeHelperPanelVisible = codeHelperForm.isVisible();

      navigationForm.hide();
      horizontalSplitLayout.setResizeBarSize(0);
      horizontalSplitLayout2.setResizeBarSize(OUTLINE_RESIZE_BAR_SIZE);

      operationForm.hide();
      verticalSplitLayout.setResizeBarSize(0);
      
      codeHelperForm.showCodeHelper(false);

      statusBar.hide();

      editorPanelMaximized = true;
   }

   private void restoreEditorPanel()
   {
      if (navigationPanelVisible)
      {
         navigationForm.show();
      }
      horizontalSplitLayout.setResizeBarSize(RESIZE_BAR_SIZE);

      if (operationPanelVisible)
      {
         operationForm.show();
      }
      
      if (codeHelperPanelVisible)
      {
         codeHelperForm.showCodeHelper(true);
         horizontalSplitLayout2.setResizeBarSize(RESIZE_BAR_SIZE);
      }

      verticalSplitLayout.setResizeBarSize(RESIZE_BAR_SIZE);
      statusBar.show();

      editorPanelMaximized = false;

      eventBus.fireEvent(new EditorPanelRestoredEvent());
   }

   private void maximizeOperationPanel()
   {
      navigationPanelVisible = navigationForm.isVisible();
      navigationForm.hide();
      codeHelperPanelVisible = codeHelperForm.isVisible();
      codeHelperForm.showCodeHelper(false);
      
      horizontalSplitLayout.setResizeBarSize(0);
      horizontalSplitLayout2.setResizeBarSize(OUTLINE_RESIZE_BAR_SIZE);

      editorForm.hide();

      verticalSplitLayout.setResizeBarSize(0);

      operationPanelHeight = operationForm.getHeight();
      operationForm.setHeight100();

      statusBar.hide();

      eventBus.fireEvent(new ClearFocusEvent());

      operationPanelMaximized = true;
   }

   private void restoreOperationPanel()
   {
      if (navigationPanelVisible)
      {
         navigationForm.show();
      }
      
      if (codeHelperPanelVisible)
      {
         codeHelperForm.showCodeHelper(true);
         horizontalSplitLayout2.setResizeBarSize(RESIZE_BAR_SIZE);
      }

      horizontalSplitLayout.setResizeBarSize(RESIZE_BAR_SIZE);

      editorForm.show();
      verticalSplitLayout.setResizeBarSize(RESIZE_BAR_SIZE);

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
      File file = event.getFile();
      TextEditor editor = event.getEditor();
      if (editor != null && file != null && file.getContentType() != null
               && (file.getContentType().equals(MimeType.APPLICATION_JAVASCRIPT) 
                        || file.getContentType().equals(MimeType.GOOGLE_GADGET)))
      {
         horizontalSplitLayout2.setResizeBarSize(RESIZE_BAR_SIZE);
      }
      else
      {
         horizontalSplitLayout2.setResizeBarSize(OUTLINE_RESIZE_BAR_SIZE);
      }
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
      
      if (codeHelperPanelMaximized)
      {
         restoreCodeHelperPanel();
         
         return;
      }
   }

   public void onRestorePerspective(RestorePerspectiveEvent event)
   {
      restorePerspective();
   }

   public void onMaximizeCodeHelperPanel(MaximizeCodeHelperPanelEvent event)
   {
      maximizeCodeHelperPanel();
   }

   public void onRestoreCodeHelperPanel(RestoreCodeHelperPanelEvent event)
   {
      restoreCodeHelperPanel();
   }
   
   private void maximizeCodeHelperPanel()
   {
      navigationPanelVisible = navigationForm.isVisible();
      
      navigationForm.hide();
      editorForm.hide();
      
      horizontalSplitLayout.setResizeBarSize(0);
      horizontalSplitLayout2.setResizeBarSize(OUTLINE_RESIZE_BAR_SIZE);
      verticalSplitLayout.hide();
      
      codeHelperPanelWidth = codeHelperForm.getWidth();
      codeHelperForm.setWidth100();
      codeHelperForm.setTabCanClose(false);

      statusBar.hide();

      eventBus.fireEvent(new ClearFocusEvent());

      codeHelperPanelMaximized = true;
   }
   
   private void restoreCodeHelperPanel()
   {
      if (navigationPanelVisible)
      {
         navigationForm.show();
      }
      
      editorForm.show();

      verticalSplitLayout.show();
      
      codeHelperForm.setWidth(codeHelperPanelWidth);

      horizontalSplitLayout.setResizeBarSize(RESIZE_BAR_SIZE);
      horizontalSplitLayout2.setResizeBarSize(RESIZE_BAR_SIZE);
      
      statusBar.show();
      
      codeHelperForm.setTabCanClose(true);

      codeHelperPanelMaximized = false;

      eventBus.fireEvent(new CodeHelperPanelRestoredEvent());
   }

   public void onShowOutline(ShowOutlineEvent event)
   {
      codeHelperForm.showCodeHelper(event.isShow());
      if (event.isShow())
      {
         horizontalSplitLayout2.setResizeBarSize(RESIZE_BAR_SIZE);
      }
      else
      {
         horizontalSplitLayout2.setResizeBarSize(OUTLINE_RESIZE_BAR_SIZE);
      }
   }
}
