/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
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
package org.exoplatform.ide.client.workspace;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

import org.exoplatform.ide.client.workspace.WorkspacePeresenter.Display;
import com.google.gwt.user.client.ui.SplitLayoutPanel;

/**
 * Created by The eXo Platform SAS
 * Author : eXoPlatform
 *          exo@exoplatform.com
 * Jul 24, 2012  
 */
public class WorkspaceView extends Composite implements Display
{

   interface WorspaceViewUiBinder extends UiBinder<Widget, WorkspaceView>
   {
   }

   private static WorspaceViewUiBinder uiBinder = GWT.create(WorspaceViewUiBinder.class);

   @UiField
   SimplePanel centerPanel;

   @UiField
   SimplePanel leftPanel;

   @UiField
   SimplePanel topPanel;
   
   @UiField(provided=true) SplitLayoutPanel splitPanel = new SplitLayoutPanel(4);

   /**
    * Because this class has a default constructor, it can
    * be used as a binder template. In other words, it can be used in other
    * *.ui.xml files as follows:
    * <ui:UiBinder xmlns:ui="urn:ui:com.google.gwt.uibinder"
     *   xmlns:g="urn:import:**user's package**">
    *  <g:**UserClassName**>Hello!</g:**UserClassName>
    * </ui:UiBinder>
    * Note that depending on the widget that is used, it may be necessary to
    * implement HasHTML instead of HasText.
    */
   @Inject
   protected WorkspaceView()
   {
      initWidget(uiBinder.createAndBindUi(this));
      // add shadow from editor to all parts of ui
      splitPanel.getWidgetContainerElement(centerPanel).addClassName("ide-editor-area");
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public HasWidgets getCenterPanel()
   {
      return centerPanel;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public HasWidgets getLeftPanel()
   {
      return leftPanel;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void clearCenterPanel()
   {
      centerPanel.clear();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public HasWidgets getMenuPanel()
   {
      return topPanel;
   }

}
