/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.application;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;

import org.exoplatform.gwtframework.ui.client.component.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.framework.ui.api.Perspective;
import org.exoplatform.ide.client.menu.Menu;
import org.exoplatform.ide.client.menu.MenuImpl;
import org.exoplatform.ide.client.ui.StandartPerspective;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEForm extends DockLayoutPanel implements IDEPresenter.Display
{

   private StandartPerspective perspective;

   private MenuImpl menu;

   private Toolbar toolbar;

   private Toolbar statusbar;

   public IDEForm()
   {
      super(Unit.PX);
      DOM.setStyleAttribute(getElement(), "background", "#FFFFFF");
      RootLayoutPanel.get().add(this);

      createMenu();
      createToolbar();
      createStatusbar();
      createPerspective();
   }

   /**
    * Creates Top Menu.
    */
   private void createMenu()
   {
      menu = new MenuImpl();
      addNorth(menu, 20);
   }

   /**
    * Creates Toolbar.
    */
   private void createToolbar()
   {
      toolbar = new Toolbar("exoIDEToolbar");
      addNorth(toolbar, 32);
   }

   /**
    * Creates Statusbar.
    */
   private void createStatusbar()
   {
      statusbar = new Toolbar("exoIDEStatusbar");
      statusbar.setHeight("30px");
      String background =
         UIHelper.getGadgetImagesURL() + "../eXoStyle/skin/default/images/component/toolbar/statusbar_Background.png";
      statusbar.setBackgroundImage(background);
      statusbar.setItemsTopPadding(3);
      addSouth(statusbar, 30);
   }

   /**
    * Create Perspective.
    */
   private void createPerspective()
   {
      perspective = new StandartPerspective();
      add(perspective);
      Panel navigationPanel = perspective.addPanel("navigation", Direction.WEST, 300);
      navigationPanel.acceptType("navigation");

      Panel informationPanel = perspective.addPanel("information", Direction.EAST, 200);
      informationPanel.acceptType("information");

      Panel operationPanel = perspective.addPanel("operation", Direction.SOUTH, 150);
      operationPanel.acceptType("operation");

      Panel editorPanel = perspective.addPanel("editor", Direction.CENTER, 0);
      editorPanel.acceptType("editor");
   }

   /**
    * @see org.exoplatform.ide.client.application.IDEPresenter.Display#getMenu()
    */
   @Override
   public Menu getMenu()
   {
      return menu;
   }

   /**
    * @see org.exoplatform.ide.client.application.IDEPresenter.Display#getPerspective()
    */
   @Override
   public Perspective getPerspective()
   {
      return perspective;
   }

   /**
    * @see org.exoplatform.ide.client.application.IDEPresenter.Display#getToolbar()
    */
   @Override
   public Toolbar getToolbar()
   {
      return toolbar;
   }

   /**
    * @see org.exoplatform.ide.client.application.IDEPresenter.Display#getStatusbar()
    */
   @Override
   public Toolbar getStatusbar()
   {
      return statusbar;
   }

}
