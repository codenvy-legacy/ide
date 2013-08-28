/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.eclipse.jdt.client.codeassistant.ui;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.ui.*;

import org.eclipse.jdt.client.AssistDisplay;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is UI component that represent autocompletion form. This form works with any bean, but also required implementation
 * of {@link TokenWidgetFactory} to build {@link ProposalWidget}
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 25, 2010 4:18:55 PM evgen $
 */
public class CodeAssitantForm extends Composite implements ResizeHandler, AssistDisplay
{
   private static final String PANEL_ID = "exo-ide-autocomplete-panel";

   private static final String INPUT_ID = "exo-ide-autocomplete-edit";

   private static final String DOC_ID = "exo-ide-autocomplete-doc-panel";

   private AbsolutePanel absolutePanel;

   private AbsolutePanel lockLayer;

   private Widget descriptionPanel;

   private LockLayer blockMouseEventsPanel;

   private CodeAssistantScrollPanel scrollPanel;

   private FlowPanel flowPanel;

   private MousHandler mousHandler;

   private ProposalWidget selectedWidget;

   private AutoCompleteFormKeyboardManager keyboardManager;

   private HandlerRegistration keyboardManagerRegistration;

   private ProposalSelectedHandler handler;

   private List<ProposalWidget> widgets = new ArrayList<ProposalWidget>();

   private HandlerRegistration resizeHandler;

   private boolean editorHasFocus = true;

   public CodeAssitantForm(int left, int top, CompletionProposal[] items, ProposalSelectedHandler handler)
   {
      this.handler = handler;

      resizeHandler = Window.addResizeHandler(this);

      absolutePanel = new AbsolutePanel();

      initWidget(absolutePanel);

      lockLayer = new AbsolutePanel();
      RootPanel.get().add(lockLayer, 0, 0);

      lockLayer.setWidth("" + Window.getClientWidth() + "px");
      lockLayer.setHeight("" + Window.getClientHeight() + "px");
      DOM.setElementAttribute(lockLayer.getElement(), "id", "menu-lock-layer-id");
      DOM.setStyleAttribute(lockLayer.getElement(), "zIndex", "" + (Integer.MAX_VALUE));

      blockMouseEventsPanel = new LockLayer();
      // blockMouseEventsPanel.setStyleName("exo-lockLayer");
      blockMouseEventsPanel.setWidth("" + Window.getClientWidth() + "px");
      blockMouseEventsPanel.setHeight("" + Window.getClientHeight() + "px");
      lockLayer.add(blockMouseEventsPanel, 0, 0);

      flowPanel = new FlowPanel();

      scrollPanel = new CodeAssistantScrollPanel();
      scrollPanel.setAlwaysShowScrollBars(true);
      scrollPanel.add(flowPanel);

      mousHandler = new MousHandler();
      flowPanel.setWidth("100%");

      scrollPanel.setHeight("195px");
      scrollPanel.setWidth("300px");

      scrollPanel.getElement().setId(PANEL_ID);

      int clientHeight = Window.getClientHeight();
      if (top + 220 > clientHeight)
      {
         top = top - 214;
      }

      scrollPanel.setStyleName(CodeAssistantClientBundle.INSTANCE.css().panelStyle());

      lockLayer.add(scrollPanel, left, top);

      keyboardManager = new AutoCompleteFormKeyboardManager();

      keyboardManagerRegistration = Event.addNativePreviewHandler(keyboardManager);

      addProposalsToPanel(items);

   }

   private void addProposalsToPanel(CompletionProposal[] proposals)
   {
      flowPanel.clear();
      widgets.clear();
      if (proposals.length == 0)
      {
         flowPanel.add(new Label("No Proposals"));
         return;
      }
      for (CompletionProposal t : proposals)
      {
         ProposalWidget w = new ProposalWidget(t);
         w.addClickHandler(mousHandler);
         w.addDoubleClickHandler(mousHandler);
         widgets.add(w);
         flowPanel.add(w);
      }
      if (!widgets.isEmpty())
      {
         selectWidget(0);
      }
      else
      {
         selectedWidget = null;
         if (descriptionPanel != null)
         {
            descriptionPanel.removeFromParent();
            descriptionPanel = null;
         }
      }
   }

   private native void scroll(Element scroll, int pos)/*-{
                                                      scroll.scrollTop = scroll.scrollTop + pos;
                                                      }-*/;

   private void selectWidget(int i)
   {
      Element scroll = scrollPanel.getElement();
      Element item = widgets.get(i).getElement();
      if (i == 0)
      {
         scroll(scroll, -scroll.getScrollTop());
      }
      else if (item.getAbsoluteTop() < scroll.getAbsoluteTop())
      {
         scroll(scroll, item.getAbsoluteTop() - scroll.getAbsoluteTop());
      }
      else if (item.getAbsoluteBottom() + 15 > scroll.getAbsoluteBottom())
      {
         scroll(scroll, item.getAbsoluteBottom() - scroll.getAbsoluteBottom() + 15);
      }

      selectToken(widgets.get(i));
   }

   /** @param widget */
   public void selectToken(ProposalWidget widget)
   {
      if (widget.equals(selectedWidget))
      {
         return;
      }

      if (selectedWidget != null)
      {
         selectedWidget.setDefaultStyle();
      }

      selectedWidget = widget;

      selectedWidget.setSelectedStyle();

      timer.cancel();
      if (descriptionPanel != null)
      {
         descriptionPanel.removeFromParent();
         descriptionPanel = null;
      }
      if (selectedWidget != null && selectedWidget.getDecription() != null)
      {
         timer.schedule(1000);
      }

   }

   private Timer timer = new Timer()
   {

      @Override
      public void run()
      {
         if (descriptionPanel != null)
         {
            descriptionPanel.removeFromParent();
         }
         if (selectedWidget != null)
         {
            descriptionPanel = selectedWidget.getDecription();
            if (descriptionPanel == null)
            {
               return;
            }

            int width = 300;
            descriptionPanel.getElement().setId(DOC_ID);
            descriptionPanel.setWidth(width + "px");
            descriptionPanel.setHeight((scrollPanel.getOffsetHeight() - 2) + "px");

            descriptionPanel.setStyleName(CodeAssistantClientBundle.INSTANCE.css().description());
            int clientWidth = Window.getClientWidth();

            if (clientWidth < scrollPanel.getAbsoluteLeft() + scrollPanel.getOffsetWidth() + 3 + width)
               lockLayer.add(descriptionPanel, scrollPanel.getAbsoluteLeft() - width - 4, scrollPanel.getAbsoluteTop());
            else
               lockLayer.add(descriptionPanel, scrollPanel.getAbsoluteLeft() + scrollPanel.getOffsetWidth() + 3,
                  scrollPanel.getAbsoluteTop());
         }
      }
   };

   /**
    * 
    */
   private void removeHandlers()
   {

      if (keyboardManagerRegistration != null)
      {
         keyboardManagerRegistration.removeHandler();
         keyboardManagerRegistration = null;
      }
      resizeHandler.removeHandler();
   }

   protected class AutoCompleteFormKeyboardManager implements Event.NativePreviewHandler
   {

      /** @see com.google.gwt.user.client.Event.NativePreviewHandler#onPreviewNativeEvent(com.google.gwt.user.client.Event.NativePreviewEvent) */
      public void onPreviewNativeEvent(NativePreviewEvent event)
      {
         NativeEvent nativeEvent = event.getNativeEvent();
         int type = event.getTypeInt();
         int typeEvent = Event.ONKEYDOWN;
         if (BrowserResolver.CURRENT_BROWSER.equals(Browser.FIREFOX))
         {
            typeEvent = Event.ONKEYPRESS;
         }

         if (type == typeEvent)
         {
            switch (nativeEvent.getKeyCode())
            {
               case KeyCodes.KEY_DOWN :
                  event.cancel();
                  moveSelectionDown();
                  break;

               case KeyCodes.KEY_UP :
                  event.cancel();
                  moveSelectionUp();
                  break;

               case KeyCodes.KEY_ENTER :
                  event.cancel();
                  proposalSelected();
                  break;

               case KeyCodes.KEY_ESCAPE :
                  cancelCodeAssistant();
                  break;

               case KeyCodes.KEY_PAGEDOWN :
                  event.cancel();
                  moveSelectionToNextPage();
                  break;

               case KeyCodes.KEY_PAGEUP :
                  event.cancel();
                  moveSelectionToPreviousPage();
                  break;
            }
         }
      }
   }

   protected class MousHandler implements ClickHandler, DoubleClickHandler
   {

      /** @see com.google.gwt.event.dom.client.ClickHandler#onClick(com.google.gwt.event.dom.client.ClickEvent) */
      public void onClick(ClickEvent event)
      {
         ProposalWidget t = (ProposalWidget)event.getSource();
         selectToken(t);
         editorHasFocus = false;
      }

      /** @see com.google.gwt.event.dom.client.DoubleClickHandler#onDoubleClick(com.google.gwt.event.dom.client.DoubleClickEvent) */
      public void onDoubleClick(DoubleClickEvent event)
      {
         proposalSelected();
         editorHasFocus = false;
      }

   }

   /** Lock Layer uses for locking of screen. Uses for hiding popups. */
   private class LockLayer extends AbsolutePanel
   {

      public LockLayer()
      {
         sinkEvents(Event.ONMOUSEDOWN);
      }

      @Override
      public void onBrowserEvent(Event event)
      {
         switch (DOM.eventGetType(event))
         {
            case Event.ONMOUSEDOWN :
               cancelCodeAssistant();
               break;

         }
      }

   }

   /** @see com.google.gwt.event.logical.shared.ResizeHandler#onResize(com.google.gwt.event.logical.shared.ResizeEvent) */
   @Override
   public void onResize(ResizeEvent event)
   {
      cancelCodeAssistant();
   }

   /**
    * @see org.eclipse.jdt.client.AssistDisplay#moveSelectionUp()
    */
   @Override
   public void moveSelectionUp()
   {
      if (selectedWidget == null)
         return;

      int i = widgets.indexOf(selectedWidget);
      if (0 < i)
      {
         selectWidget(i - 1);
      }
      else
      {
         selectWidget(widgets.size() - 1);
      }
   }

   /**
    * @see org.eclipse.jdt.client.AssistDisplay#moveSelectionDown()
    */
   @Override
   public void moveSelectionDown()
   {
      if (selectedWidget == null)
         return;
      int i = widgets.indexOf(selectedWidget);
      if (widgets.size() - 1 > i)
      {
         selectWidget(i + 1);
      }
      else
      {
         selectWidget(0);
      }
   }

   /**
    * @see org.eclipse.jdt.client.AssistDisplay#moveSelectionToPreviousPage()
    */
   @Override
   public void moveSelectionToPreviousPage()
   {
      if (selectedWidget == null)
         return;
      int i = widgets.indexOf(selectedWidget);
      if (i > 0)
      {
         selectWidget(Math.max(0, i - getPageSize() + 1));
      }
   }

   /**
    * @see org.eclipse.jdt.client.AssistDisplay#moveSelectionToNextPage()
    */
   @Override
   public void moveSelectionToNextPage()
   {
      if (selectedWidget == null)
         return;
      int i = widgets.indexOf(selectedWidget);
      if (widgets.size() - 1 > i)
      {
         selectWidget(Math.min(i + getPageSize() - 1, widgets.size() - 1));
      }
   }

   private int getPageSize()
   {
      int scrollPanelHeight = scrollPanel.getElement().getOffsetHeight();

      int proposalWidgetHeight = 1;
      if (widgets.size() > 0)
      {
         proposalWidgetHeight = widgets.get(0).getElement().getOffsetHeight();
      }

      return scrollPanelHeight/proposalWidgetHeight;
   }

   /**
    * @see org.eclipse.jdt.client.AssistDisplay#proposalSelected()
    */
   @Override
   public void proposalSelected()
   {
      timer.cancel();
      removeHandlers();
      if (selectedWidget != null)
      {
         handler.onTokenSelected(selectedWidget.getProposal(), editorHasFocus);
      }
      lockLayer.removeFromParent();
   }

   /**
    * @see org.eclipse.jdt.client.AssistDisplay#cancelCodeAssistant()
    */
   @Override
   public void cancelCodeAssistant()
   {
      timer.cancel();
      removeHandlers();
      handler.onCancelAutoComplete(editorHasFocus);
      lockLayer.removeFromParent();
   }

   /**
    * @see org.eclipse.jdt.client.AssistDisplay#setNewProposals(org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal[])
    */
   @Override
   public void setNewProposals(CompletionProposal[] proposals)
   {
      addProposalsToPanel(proposals);
   }

}
