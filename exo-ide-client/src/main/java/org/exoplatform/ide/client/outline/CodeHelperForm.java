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
package org.exoplatform.ide.client.outline;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.editor.MinMaxControlButton;
import org.exoplatform.ide.client.event.perspective.CodeHelperPanelRestoredEvent;
import org.exoplatform.ide.client.event.perspective.CodeHelperPanelRestoredHandler;
import org.exoplatform.ide.client.event.perspective.MaximizeCodeHelperPanelEvent;
import org.exoplatform.ide.client.event.perspective.RestoreCodeHelperPanelEvent;
import org.exoplatform.ide.client.framework.form.FormClosedEvent;
import org.exoplatform.ide.client.framework.form.FormOpenedEvent;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.ViewHighlightManager;
import org.exoplatform.ide.client.framework.ui.ViewType;
import org.exoplatform.ide.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ide.client.panel.Panel;

import java.util.List;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.events.TabCloseClickEvent;

/**
 * Form for panel, that displayed in right side of IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperForm extends Layout implements CodeHelperPresenter.Display, CodeHelperPanelRestoredHandler
{
   public static final String ID = "ideCodeHelperPanel";

   private static final String TAB_SET_ID = "ideCodeHelperTabSet";

   private HandlerManager eventBus;

   private CodeHelperPresenter presenter;

   private Panel tabSet;

   private Layout tabBarColtrols;

   protected MinMaxControlButton minMaxControlButton;

   public CodeHelperForm(HandlerManager eventBus)
   {
      setID(ID);

      this.eventBus = eventBus;
      new Handlers(eventBus);

      tabSet = new Panel(eventBus, TAB_SET_ID);
      tabSet.getViewTypes().add(ViewType.OUTLINE);
      tabSet.getViewTypes().add(ViewType.VERSIONS);
      
      createButtons();
      addMember(tabSet);
      tabSet.addCloseClickHandler(closeClickHandler);

      presenter = new CodeHelperPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   private void createButtons()
   {
      tabBarColtrols = new Layout();
      tabBarColtrols.setHeight(18);
      tabBarColtrols.setAutoWidth();

      minMaxControlButton =
         new MinMaxControlButton(eventBus, true, new MaximizeCodeHelperPanelEvent(), new RestoreCodeHelperPanelEvent());
      tabBarColtrols.addMember(minMaxControlButton);

      tabSet.setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, tabBarColtrols);
   }

   public void onCodeHelperPanelRestored(CodeHelperPanelRestoredEvent event)
   {
      minMaxControlButton.setMaximize(true);
   }

   private CloseClickHandler closeClickHandler = new CloseClickHandler()
   {
      public void onCloseClick(TabCloseClickEvent event)
      {
         if (tabSet.getTabs().length <= 1)
            hide();
         //TODO: remove close click handler for outline tab from 
         //code of CodeHelperForm
         
         //need to call ShowOutlineEvent, to tell DevelopmentModuleEventHandler
         //that outline panel was closed by user, and need to store in cookies
         //that outline panel is closed.
         if (event.getTab().getPane().getTitle().equals(OutlineForm.ID))
         {
            //cancel closing of tab, because it will be closed in DevelopmentModuleEventHandler
            event.cancel();
            eventBus.fireEvent(new ShowOutlineEvent(false));
         }
      }
   };

   @Override
   public void show()
   {
      if (tabSet.getSelectedTab() != null)
      {
         ViewHighlightManager.getInstance().selectView((View)tabSet.getSelectedTab().getPane());
      }
      super.show();
      eventBus.fireEvent(new FormOpenedEvent(ID));
   }

   @Override
   public void hide()
   {
      if (tabSet.getSelectedTab() != null)
      {
         ViewHighlightManager.getInstance().viewClosed((View)tabSet.getSelectedTab().getPane());
      }
      super.hide();
      eventBus.fireEvent(new FormClosedEvent(ID));
   }

   /**
    * @see org.exoplatform.ide.client.outline.CodeHelperPresenter.Display#addView(org.exoplatform.ide.client.panel.SimpleTabPanel)
    */
   public void addView(View view, Image tabIcon, String title)
   {
      if (tabSet.isViewIsOpened(view.getViewId()))
      {
         return;
      }
      tabSet.openView(view, title, tabIcon, true);
      tabSet.selectTabPanel(view.getViewId());
   }

   /**
    * @see org.exoplatform.ide.client.outline.CodeHelperPresenter.Display#closePanel(java.lang.String)
    */
   public void closePanel(String panelId)
   {
      if (tabSet.isViewIsOpened(panelId))
      {
      tabSet.closeView(panelId);
      }
      if (tabSet.getNumTabs() == 0)
      {
         hide();
      }
   }

   /**
    * @see org.exoplatform.ide.client.outline.CodeHelperPresenter.Display#isShown()
    */
   public boolean isShown()
   {
      return isVisible();
   }

   /**
    * @see org.exoplatform.ide.client.outline.CodeHelperPresenter.Display#getViewTypes()
    */
   public List<String> getViewTypes()
   {
      return tabSet.getViewTypes();
   }
   
}
