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

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.events.TabCloseClickEvent;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.editor.MinMaxControlButton;
import org.exoplatform.ide.client.event.perspective.CodeHelperPanelRestoredEvent;
import org.exoplatform.ide.client.event.perspective.CodeHelperPanelRestoredHandler;
import org.exoplatform.ide.client.event.perspective.MaximizeCodeHelperPanelEvent;
import org.exoplatform.ide.client.event.perspective.RestoreCodeHelperPanelEvent;
import org.exoplatform.ide.client.framework.form.FormClosedEvent;
import org.exoplatform.ide.client.framework.form.FormOpenedEvent;
import org.exoplatform.ide.client.module.development.event.ShowOutlineEvent;
import org.exoplatform.ide.client.panel.SimpleTabPanel;
import org.exoplatform.ide.client.panel.TabContainer;
import org.exoplatform.ide.client.versioning.VersionContentForm;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperForm extends Layout implements CodeHelperPresenter.Display, CodeHelperPanelRestoredHandler
{
   public static final String ID = "CodeHelper";

   private static final String TAB_SET_ID = "ideCodeHelperTabSet";

   private HandlerManager eventBus;

   private CodeHelperPresenter presenter;

   private TabContainer tabSet;

   private Layout tabBarColtrols;

   protected MinMaxControlButton minMaxControlButton;

   public CodeHelperForm(HandlerManager eventBus)
   {
      setID(ID);

      this.eventBus = eventBus;
      new Handlers(eventBus);

      tabSet = new TabContainer(eventBus, TAB_SET_ID);
      createButtons();
      OutlineForm outlineForm= new OutlineForm(eventBus);
      Image tabIcon = new Image(IDEImageBundle.INSTANCE.outline());
      tabSet.addTabPanel(outlineForm, "Outline", tabIcon, true);
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
         event.cancel();
         
        hide();
         eventBus.fireEvent(new ShowOutlineEvent(false));
      }
   };

   @Override
   public void show()
   {
      super.show();
      eventBus.fireEvent(new FormOpenedEvent(ID));
   }

   @Override
   public void hide()
   {
      super.hide();
      eventBus.fireEvent(new FormClosedEvent(ID));
   }

   /**
    * @see org.exoplatform.ide.client.outline.CodeHelperPresenter.Display#addPanel(org.exoplatform.ide.client.panel.SimpleTabPanel)
    */
   public void addPanel(SimpleTabPanel panel)
   {
      Image tabIcon = new Image(IDEImageBundle.INSTANCE.viewVersions());
      tabSet.addTabPanel(panel, "Version", tabIcon, true);
      tabSet.selectTabPanel(VersionContentForm.ID);
   }

   /**
    * @see org.exoplatform.ide.client.outline.CodeHelperPresenter.Display#closePanel(java.lang.String)
    */
   public void closePanel(String panelId)
   {
      tabSet.closeTabPanel(panelId);
   }

}
