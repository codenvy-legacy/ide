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

import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.UIHelper;
import org.exoplatform.ide.client.framework.ui.api.Direction;
import org.exoplatform.ide.client.framework.ui.api.Panel;
import org.exoplatform.ide.client.framework.ui.api.Perspective;
import org.exoplatform.ide.client.menu.Menu;
import org.exoplatform.ide.client.menu.MenuImpl;
import org.exoplatform.ide.client.ui.impl.Layer;
import org.exoplatform.ide.client.ui.impl.PerspectiveImpl;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class IDEForm extends Layer implements IDEPresenter.Display, ResizeHandler
{

   private PerspectiveImpl perspective;

   private MenuImpl menu;

   private Toolbar toolbar;

   private Toolbar statusbar;

   public IDEForm()
   {
      super("ide");
      AbsolutePanel ideRootPanel = new AbsolutePanel();
      DOM.setStyleAttribute(ideRootPanel.getElement(), "overflow", "hidden");
      DOM.setStyleAttribute(ideRootPanel.getElement(), "background", "#FFFFFF");
      ideRootPanel.setWidth("100%");
      ideRootPanel.setHeight("100%");

      RootPanel.get().add(ideRootPanel);

      createMenu();
      createToolbar();
      createStatusbar();
      createPerspective();

      //      DebugLayer debugController = new DebugLayer();
      //      addLayer(debugController);      

      ideRootPanel.add(this, 0, 0);

      resize(Window.getClientWidth(), Window.getClientHeight());
      Window.addResizeHandler(this);
   }

   /**
    * Creates Top Menu.
    */
   private void createMenu()
   {
      menu = new MenuImpl();
      add(menu);
   }

   /**
    * Creates Toolbar.
    */
   private void createToolbar()
   {
      toolbar = new Toolbar("exoIDEToolbar");
      add(toolbar, 0, 20);
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
      add(statusbar, 0, 100);
   }

   /**
    * Create Perspective.
    */
   private void createPerspective()
   {
      perspective = new PerspectiveImpl();
      addLayer(perspective);

      Panel navigationPanel = perspective.addPanel("navigation", Direction.WEST, 300);
      navigationPanel.acceptType("navigation");

      Panel informationPane = perspective.addPanel("information", Direction.EAST, 200);
      informationPane.acceptType("information");

      Panel operationPanel = perspective.addPanel("operation", Direction.SOUTH, 150);
      operationPanel.acceptType("operation");

      Panel editorPanel = perspective.addPanel("editor", Direction.CENTER, 0);
      editorPanel.acceptType("editor");
   }

   /**
    * @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent)
    */
   @Override
   public void onResize(ResizeEvent event)
   {
      int width = Window.getClientWidth();
      int height = Window.getClientHeight();
      resize(width, height);
   }

   /**
    * @see org.exoplatform.ide.client.ui.impl.Layer#onResize(int, int)
    */
   @Override
   public void onResize(int width, int height)
   {
      menu.setWidth("" + width + "px");

      toolbar.setWidth("" + width + "px");

      statusbar.setWidth("" + width + "px");
      DOM.setStyleAttribute(statusbar.getElement(), "top", "" + (height - 30) + "px");
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
