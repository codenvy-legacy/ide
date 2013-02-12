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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.i18n.client.HasDirection.Direction;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import org.exoplatform.gwtframework.ui.client.CellTreeResource;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.Label;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialog;
import org.exoplatform.gwtframework.ui.client.tablayout.TabPanel;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerClientBundle;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerExtension;
import org.exoplatform.ide.extension.java.jdi.client.DebuggerPresenter;
import org.exoplatform.ide.extension.java.jdi.shared.BreakPoint;
import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;
import org.exoplatform.ide.extension.java.jdi.shared.Variable;

import java.util.Collections;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class DebuggerView extends ViewImpl implements DebuggerPresenter.Display
{

   private static final String ID = "ideDebuggerView";

   private static final String IDE_DEBUGGER_VARIABEL_PANEL_ID = "idedebuggervariabelpanelid";

   private static final String IDE_DEBUGGER_BREAKPOINTS_PANEL_ID = "idedebuggerbreakpointspanelid";

   private static DebugWindowUiBinder uiBinder = GWT.create(DebugWindowUiBinder.class);

   interface DebugWindowUiBinder extends UiBinder<Widget, DebuggerView>
   {
   }

   @UiField
   TabPanel variablesPanel;

   @UiField
   TabPanel breakPointsPanel;

   @UiField
   ImageButton resumeButton;

   @UiField
   ImageButton disconnectButton;

   @UiField
   ImageButton removeAllBreakpointsButton;

   @UiField
   ImageButton stepIntoButton;

   @UiField
   ImageButton stepOverButton;

   @UiField
   ImageButton stepReturnButton;

   @UiField
   ImageButton changeValueButton;

   @UiField
   ImageButton evaluateExpressionButton;

   @UiField
   Label vmName;

   CellList<BreakPoint> breakpointsContainer;

   private CellTree.Resources res = GWT.create(CellTreeResource.class);

   private DebuggerInfo debuggerInfo;

   private SingleSelectionModel<Variable> variablesTreeSelectionModel;

   private Variable selectedVariable;

   FrameTreeViewModel frameTreeViewModel;

   private Dialog dialog;

   private Window expDialog;

   public DebuggerView(DebuggerInfo debuggerInfo)
   {

      super(ID, ViewType.OPERATION, DebuggerExtension.LOCALIZATION_CONSTANT.debug(), new Image(
         DebuggerClientBundle.INSTANCE.debugApp()));
      add(uiBinder.createAndBindUi(this));
      this.debuggerInfo = debuggerInfo;
      setCanBeClosed(false);
      BreakPointCell breakpointCell = new BreakPointCell();
      breakpointsContainer = new CellList<BreakPoint>(breakpointCell);
      breakpointsContainer.setHeight("100%");
      breakpointsContainer.setWidth("100%");
      breakpointsContainer.getElement().setId(IDE_DEBUGGER_BREAKPOINTS_PANEL_ID);

      buildVariablesTreePanel(Collections.<Variable> emptyList());
      breakPointsPanel.addTab("breakpointstabid", new Image(DebuggerClientBundle.INSTANCE.breakPointsIcon()),
         DebuggerExtension.LOCALIZATION_CONSTANT.breakPoints(), breakpointsContainer, false);
      breakPointsPanel.setWidth("100%");
      breakPointsPanel.setHeight("100%");

      vmName.setText(debuggerInfo.getVmName() + " " + debuggerInfo.getVmVersion(), Direction.RTL);
      vmName.setDirectionEstimator(true);
   }

   private void buildVariablesTreePanel(List<Variable> variables)
   {
      variablesPanel.removeTab("variabletabid");
      variablesTreeSelectionModel = new SingleSelectionModel<Variable>();

      setChangeValueButtonEnable(false);
      variablesTreeSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler()
      {
         public void onSelectionChange(SelectionChangeEvent event)
         {
            selectedVariable = variablesTreeSelectionModel.getSelectedObject();
            setChangeValueButtonEnable(selectedVariable != null);
         }
      });

      frameTreeViewModel = new FrameTreeViewModel(variablesTreeSelectionModel, debuggerInfo);
      CellTree frameTree = new CellTree(frameTreeViewModel, null, res);
      ScrollPanel scrollPanel = new ScrollPanel();
      if (variables.size() > 0)
      {
         frameTree = new CellTree(frameTreeViewModel, null, res);
         frameTreeViewModel.getDataProvider().setList(variables);
         scrollPanel.add(frameTree);
         scrollPanel.getElement().setId(IDE_DEBUGGER_VARIABEL_PANEL_ID);
      }
      variablesPanel.addTab("variabletabid", new Image(DebuggerClientBundle.INSTANCE.variable()),
         DebuggerExtension.LOCALIZATION_CONSTANT.variables(), scrollPanel, false);
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
   public void setBreakPoints(List<BreakPoint> breakPoints)
   {
      breakpointsContainer.setRowData(breakPoints);
   }

   @Override
   public HasClickHandlers getStepIntoButton()
   {
      return stepIntoButton;
   }

   @Override
   public HasClickHandlers getStepOverButton()
   {
      return stepOverButton;
   }

   @Override
   public HasClickHandlers getStepReturnButton()
   {
      return stepReturnButton;
   }

   @Override
   public HasClickHandlers getChangeValueButton()
   {
      return changeValueButton;
   }

   @Override
   public HasClickHandlers getEvaluateExpressionButton()
   {
      return evaluateExpressionButton;
   }

   @Override
   public void setVariables(List<Variable> variables)
   {
      buildVariablesTreePanel(variables);
   }

   @Override
   public List<Variable> getVariables()
   {
      return frameTreeViewModel.getDataProvider().getList();
   }

   @Override
   public void setEnableResumeButton(boolean isEnable)
   {
      resumeButton.setEnabled(isEnable);
   }

   @Override
   public void setRemoveAllBreakpointsButton(boolean isEnable)
   {
      removeAllBreakpointsButton.setEnabled(isEnable);
   }

   @Override
   public void setDisconnectButton(boolean isEnable)
   {
      disconnectButton.setEnabled(isEnable);
   }

   @Override
   public void setStepIntoButton(boolean isEnable)
   {
      stepIntoButton.setEnabled(isEnable);
   }

   @Override
   public void setStepOverButton(boolean isEnable)
   {
      stepOverButton.setEnabled(isEnable);
   }

   @Override
   public void setStepReturnButton(boolean isEnable)
   {
      stepReturnButton.setEnabled(isEnable);
   }

   @Override
   public void setChangeValueButtonEnable(boolean isEnable)
   {
      changeValueButton.setEnabled(isEnable);
   }

   @Override
   public void setEvaluateExpressionButtonEnable(boolean isEnable)
   {
      evaluateExpressionButton.setEnabled(isEnable);
   }

   public TabPanel getVariablesPanel()
   {
      return variablesPanel;
   }

   @Override
   public Variable getSelectedVariable()
   {
      return selectedVariable;
   }

   @Override
   public void showExpirationDialog(final BooleanValueReceivedHandler handler)
   {
      if (expDialog == null)
         buildExperationDialog(handler);
      expDialog.showCentered();
   }

   /**
    * @param handler
    * @return
    */
   private Window buildExperationDialog(final BooleanValueReceivedHandler handler)
   {
      expDialog = new Window(DebuggerExtension.LOCALIZATION_CONSTANT.prolongExpirationTimeTitle());

      VerticalPanel mainLayout = new VerticalPanel();
      mainLayout.setWidth("100%");
      mainLayout.setHeight("100%");
      mainLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.setSpacing(10);

      HorizontalPanel content = new HorizontalPanel();
      content.setWidth("100%");
      content.setHeight(32 + "px");
      content.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
      content.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

      Label label = new Label();
      label.getElement().setInnerHTML(DebuggerExtension.LOCALIZATION_CONSTANT.prolongExpirationTimeQuestion());
      content.add(label);

      mainLayout.add(content);

      HorizontalPanel buttonsLayout = new HorizontalPanel();
      buttonsLayout.setHeight("22px");
      buttonsLayout.setSpacing(5);
      buttonsLayout.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
      mainLayout.add(buttonsLayout);

      ImageButton yesButton = new ImageButton("Yes");
      buttonsLayout.add(yesButton);
      yesButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            handler.booleanValueReceived(true);
            expDialog.hide();
         }
      });

      ImageButton noButton = new ImageButton("No");;
      buttonsLayout.add(noButton);
      noButton.addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            handler.booleanValueReceived(false);
            expDialog.hide();
         }
      });

      expDialog.add(mainLayout);
      return expDialog;
   }

   @Override
   public void closeExpirationDialog()
   {
      if (expDialog != null)
         expDialog.hide();
   }

}
