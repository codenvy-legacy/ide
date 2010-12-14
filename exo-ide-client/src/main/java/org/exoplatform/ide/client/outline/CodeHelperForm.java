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
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.layout.Layout;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.editor.MinMaxControlButton;
import org.exoplatform.ide.client.event.perspective.CodeHelperPanelRestoredEvent;
import org.exoplatform.ide.client.event.perspective.CodeHelperPanelRestoredHandler;
import org.exoplatform.ide.client.event.perspective.MaximizeCodeHelperPanelEvent;
import org.exoplatform.ide.client.event.perspective.RestoreCodeHelperPanelEvent;
import org.exoplatform.ide.client.framework.ui.ViewType;
import org.exoplatform.ide.client.panel.Panel;

/**
 * Form for panel, that displayed in right side of IDE.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperForm extends Panel implements CodeHelperPresenter.Display, CodeHelperPanelRestoredHandler
{
   public static final String ID = "ideCodeHelperPanel";

  // private static final String TAB_SET_ID = "ideCodeHelperPanel";

   private HandlerManager eventBus;

   private CodeHelperPresenter presenter;

   private Layout tabBarColtrols;

   protected MinMaxControlButton minMaxControlButton;

   public CodeHelperForm(HandlerManager eventBus)
   {
     super(eventBus, ID);

      this.eventBus = eventBus;
      new Handlers(eventBus);

      getViewTypes().add(ViewType.OUTLINE);
      getViewTypes().add(ViewType.VERSIONS);
      
      createControlButtons();

      presenter = new CodeHelperPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   private void createControlButtons()
   {
      tabBarColtrols = new Layout();
      tabBarColtrols.setHeight(18);
      tabBarColtrols.setAutoWidth();

      minMaxControlButton =
         new MinMaxControlButton(eventBus, true, new MaximizeCodeHelperPanelEvent(), new RestoreCodeHelperPanelEvent());
      tabBarColtrols.addMember(minMaxControlButton);

      setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, tabBarColtrols);
   }

   public void onCodeHelperPanelRestored(CodeHelperPanelRestoredEvent event)
   {
      minMaxControlButton.setMaximize(true);
   }
}
