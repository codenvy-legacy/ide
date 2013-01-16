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
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;

/**
 * Implements {@link WorkspaceView}
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class WorkspaceViewImpl extends Composite implements WorkspaceView
{

   interface WorspaceViewUiBinder extends UiBinder<Widget, WorkspaceViewImpl>
   {
   }

   private static WorspaceViewUiBinder uiBinder = GWT.create(WorspaceViewUiBinder.class);

   @UiField
   SimplePanel centerPanel;

   @UiField
   SimplePanel leftPanel;

   @UiField
   SimplePanel topPanel;

   @UiField
   SimplePanel secondTopPanel;
   
   @UiField
   SimplePanel rightPanel;
   
   @UiField(provided=true) SplitLayoutPanel splitPanel = new SplitLayoutPanel(4);

   /**
    * Create view.
    */
   @Inject
   protected WorkspaceViewImpl()
   {
      initWidget(uiBinder.createAndBindUi(this));
      // add shadow from editor to all parts of ui
      splitPanel.getWidgetContainerElement(centerPanel).addClassName("ide-editor-area");
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public AcceptsOneWidget getCenterPanel()
   {
      return centerPanel;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public AcceptsOneWidget getLeftPanel()
   {
      return leftPanel;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AcceptsOneWidget getMenuPanel()
   {
      return topPanel;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AcceptsOneWidget getRightPanel()
   {
      return rightPanel;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      // ok
      // there are no events for now
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public AcceptsOneWidget getToolbarPanel()
   {
      return secondTopPanel;
   }
}
