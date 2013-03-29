/*
 * Copyright (C) 2003-2013 eXo Platform SAS.
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
package com.codenvy.ide.java.client.perspective;

import com.codenvy.ide.api.ui.perspective.PerspectiveView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;


/**
 * Standard Java Perspective View
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class JavaPerspectiveView extends Composite implements PerspectiveView<PerspectiveView.ActionDelegate>
{

   private static JavaPerspectiveViewUiBinder uiBinder = GWT.create(JavaPerspectiveViewUiBinder.class);

   interface JavaPerspectiveViewUiBinder extends UiBinder<Widget, JavaPerspectiveView>
   {
   }

   @UiField
   SplitLayoutPanel splitPanel;

   @UiField
   SimplePanel editorPanel;

   @UiField
   SimplePanel naviPanel;

   @UiField
   SimplePanel infoPanel;

   @UiField
   SimplePanel toolPanel;

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
   public JavaPerspectiveView()
   {
      initWidget(uiBinder.createAndBindUi(this));
      splitPanel.getWidgetContainerElement(editorPanel).addClassName("ide-editor-area");
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void setDelegate(ActionDelegate delegate)
   {
      // do nothing
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public AcceptsOneWidget getEditorPanel()
   {
      return editorPanel;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public AcceptsOneWidget getNavigationPanel()
   {
      return naviPanel;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public AcceptsOneWidget getInformationPanel()
   {
      return infoPanel;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public AcceptsOneWidget getToolPanel()
   {
      return toolPanel;
   }

}
