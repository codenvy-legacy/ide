/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.jdi.client.ui;

import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerPresenter;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPointList;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DebuggerView extends ViewImpl implements DebuggerPresenter.Display
{
   
   private static final String ID = "ideDebuggerView";

   private static DebugWindowUiBinder uiBinder = GWT.create(DebugWindowUiBinder.class);

   interface DebugWindowUiBinder extends UiBinder<Widget, DebuggerView>
   {
   }

   @UiField
   TabPanel debugPanel;

   @UiField
   ImageButton resumeButton;

   @UiField
   ImageButton disconnectButton;

   @UiField
   ImageButton removeAllBreakpointsButton;

   @UiField
   ImageButton addBreakPointButton;

   @UiField
   ImageButton getBreakPointsButton;

   @UiField
   ImageButton checkEventsButton;

   @UiField
   TextBox fqn;

   @UiField
   TextBox lineNumber;
   
   CellList<BreakPoint> breakpointsContainer;

   CellTree frameTree;

   List<BreakPoint> breakpoints;

   private SingleSelectionModel<Variable> selectionModel;

   private FrameTreeViewModel frameTreeViewModel;

   private CellTree.Resources res = GWT.create(CellTreeResource.class);

   public DebuggerView()
   {

      super(ID, ViewType.OPERATION, DebuggerExtension.LOCALIZATION_CONSTANT.debug());
      add(uiBinder.createAndBindUi(this));
//      setWidth("500px");
//      setHeight("500px");

      frameTreeViewModel = new FrameTreeViewModel(selectionModel);
      frameTree = new CellTree(frameTreeViewModel, null, res);

      BreakpointCell breakpointCell = new BreakpointCell();
      breakpointsContainer = new CellList<BreakPoint>(breakpointCell);
      breakpointsContainer.setHeight("100%");
      breakpointsContainer.setWidth("100%");
      breakpoints = new ArrayList<BreakPoint>();
      breakpointsContainer.setRowData(breakpoints);
      debugPanel.addTab("tabId", null, "breakpoints", breakpointsContainer, false);
      ScrollPanel scrollPanel = new ScrollPanel(frameTree);
      debugPanel.addTab("tabId1", null, "Frame", scrollPanel, false);
      fqn.setValue("org.exoplatform.services.jcr.webdav.WebDavServiceImpl");
      lineNumber.setValue(645 + "");
      
      
   }

   static class BreakpointCell extends AbstractCell<BreakPoint>
   {

      @Override
      public void render(Context context, BreakPoint breakpoint, SafeHtmlBuilder sb)
      {
         // Value can be null, so do a null check..
         if (breakpoint == null)
         {
            return;
         }
         sb.appendHtmlConstant("<table>");
         sb.appendHtmlConstant("<tr><td rowspan='3'>");
         sb.appendEscaped(breakpoint.getLocation().getClassName());
         sb.appendHtmlConstant("</td>");
         sb.appendHtmlConstant("<td style='font-size:95%;'>");
         sb.appendEscaped("[line:" + breakpoint.getLocation().getLineNumber() + "]");
         sb.appendHtmlConstant("</td></tr></table>");
      }

   }

   @Override
   public HasClickHandlers getResumeButton()
   {
      return resumeButton;
   }

   @Override
   public HasClickHandlers getRemoveAllBreakpointsButton()
   {
      return removeAllBreakpointsButton;
   }

   @Override
   public HasClickHandlers getDisconnectButton()
   {
      return disconnectButton;
   }

   @Override
   public HasClickHandlers getAddBreakPointButton()
   {
      return addBreakPointButton;
   }

   @Override
   public void addBreakPoint(BreakPoint breakPoint)
   {
      breakpoints.add(breakPoint);
      breakpointsContainer.setRowData(breakpoints);
   }

   @Override
   public HasValue<String> getFqn()
   {
      return fqn;
   }

   @Override
   public HasValue<String> getLine()
   {
      return lineNumber;
   }

   @Override
   public HasClickHandlers getBreakPointsButton()
   {
      return getBreakPointsButton;
   }

   @Override
   public void setBreakPoints(BreakPointList breakPoints)
   {
      breakpointsContainer.setRowData(breakpoints);
   }

   @Override
   public void cleare()
   {
      breakpointsContainer.setRowData(new ArrayList<BreakPoint>());
   }

   @Override
   public HasClickHandlers getCheckEventsButton()
   {
      return checkEventsButton;
   }

   @Override
   public ListDataProvider<Variable> getDataProvider()
   {
      return frameTreeViewModel.getDataProvider();
   }

}
