// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.exoplatform.ide.texteditor.codeassistant;

import com.google.gwt.user.client.ui.FocusPanel;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import elemental.events.Event;
import elemental.events.EventListener;
import elemental.html.Element;

import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.runtime.Assert;
import org.exoplatform.ide.texteditor.Editor;
import org.exoplatform.ide.texteditor.FocusManager;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal;
import org.exoplatform.ide.util.SignalEvent;
import org.exoplatform.ide.util.SignalEventUtils;
import org.exoplatform.ide.util.UserAgent;
import org.exoplatform.ide.util.dom.DomUtils;

import java.util.Arrays;
import java.util.Collections;

/**
 * A controller for managing the UI for showing autocomplete proposals.
 *
 */
public class AutocompleteUiController implements AutocompleteBox
{

   public interface Resources extends ClientBundle
   {
      @Source("AutocompleteComponent.css")
      Css autocompleteComponentCss();
   }

   public interface Css extends CssResource
   {
      String cappedProposalLabel();

      String proposalIcon();

      String proposalLabel();

      String proposalGroup();

      String container();

      String items();

      String hint();

      int maxHeight();
   }

   //  private static final AutocompleteProposal CAPPED_INDICATOR = new AutocompleteProposal("");

   private final Cell<CompletionProposal> proposalCell = new AbstractCell<CompletionProposal>()
   {

      @Override
      public void render(com.google.gwt.cell.client.Cell.Context context, CompletionProposal value, SafeHtmlBuilder sb)
      {
         sb.appendHtmlConstant(value.getDisplayString());
      }
   };

   private final CellList<CompletionProposal> list;

   private Events delegate;

   private final TextEditorPartDisplay editor;

   private final PopupPanel popup;

   /**
    * True to force the layout above the anchor, false to layout below. This
    * should be set when showing from the hidden state. It's used to keep
    * the position consistent while the box is visible.
    */
   private boolean positionAbove;

   private SingleSelectionModel<CompletionProposal> selectionModel = new SingleSelectionModel<CompletionProposal>();

   /**
    * The currently displayed proposals. This may contain more proposals than actually shown since we
    * cap the maximum number of visible proposals. This will be null if the UI is not showing.
    */
   private JsonArray<CompletionProposal> proposals;

   private ListDataProvider<CompletionProposal> provider;

   public AutocompleteUiController(TextEditorPartDisplay editor)
   {
      this.editor = editor;
      popup = new PopupPanel(true);
      popup.setSize("200px", "100px");
      list = new CellList<CompletionProposal>(proposalCell);
      list.setSelectionModel(selectionModel);
      list.setEmptyListWidget(new Label("No proposal"));
      list.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.DISABLED);
      provider = new ListDataProvider<CompletionProposal>();
      provider.addDataDisplay(list);
      ScrollPanel scrollPanel = new ScrollPanel();
      scrollPanel.add(list);
      popup.add(scrollPanel);
      addKeyboardHandlers((Element)popup.getElement());
      
   }

   /**
    * @param element
    */
   private void addKeyboardHandlers(Element element)
   {
      EventListener listener = new EventListener()
      {
         
         @Override
         public void handleEvent(Event evt)
         {
            SignalEvent signalEvent = SignalEventUtils.create(evt);
            if(signalEvent == null)
               return;
            processEvent(signalEvent);
            evt.preventDefault();
            evt.stopPropagation();
            evt.stopImmediatePropagation();
         }
      };
//      if(UserAgent.isFirefox())
//      {
       element.addEventListener(Event.KEYPRESS, listener, false);
//      }
//      else
//      {
//         element.addEventListener(Event.KEYDOWN, listener, false);
//      }
   }

   /**
    * @param signalEvent
    */
   private void processEvent(SignalEvent signalEvent)
   {
//      if ((signalEvent.getKeyCode() == KeyCodes.KEY_TAB) || (signalEvent.getKeyCode() == KeyCodes.KEY_ENTER))
//      {
//         delegate.onSelect(selectionModel.getSelectedObject());
//         return;
//      }
//
//      if (signalEvent.getKeyCode() == KeyCodes.KEY_ESCAPE)
//      {
//         delegate.onCancel();
//      }
      consumeKeySignal(new SignalEventEssence(signalEvent));
   }

   @Override
   public boolean isShowing()
   {
      return popup.isShowing();
   }

   @Override
   public boolean consumeKeySignal(SignalEventEssence signal)
   {
      Assert.isTrue(isShowing());
      Assert.isNotNull(delegate);

      if ((signal.keyCode == KeyCodes.KEY_TAB) || (signal.keyCode == KeyCodes.KEY_ENTER))
      {
         delegate.onSelect(selectionModel.getSelectedObject());
         return true;
      }

      if (signal.keyCode == KeyCodes.KEY_ESCAPE)
      {
         delegate.onCancel();
         return true;
      }

      if (signal.type != SignalEvent.KeySignalType.NAVIGATION)
      {
         return false;
      }

      int index = proposals.indexOf(selectionModel.getSelectedObject());
      if ((signal.keyCode == KeyCodes.KEY_DOWN))
      {
         if (index == proposals.size() - 1)
         {
            selectionModel.setSelected(proposals.get(0), true);
            ensureProposalVisible(0);
         }
         else
         {
            selectionModel.setSelected(proposals.get(++index), true);
            ensureProposalVisible(index);
         }
         return true;
      }

      if (signal.keyCode == KeyCodes.KEY_UP)
      {
         if (index == 0)
         {
            selectionModel.setSelected(proposals.get(proposals.size() - 1), true);
            ensureProposalVisible(proposals.size() - 1);
         }
         else
         {
            selectionModel.setSelected(proposals.get(--index), true);
            ensureProposalVisible(index);
         }
         return true;
      }

      if ((signal.keyCode == KeyCodes.KEY_LEFT) || (signal.keyCode == KeyCodes.KEY_RIGHT))
      {
         delegate.onCancel();
         return true;
      }

      //      if (signal.keyCode == KeyCodes.KEY_PAGEUP)
      //      {
      //         list.getSelectionModel().selectPreviousPage();
      //         return true;
      //      }
      //
      //      if (signal.keyCode == KeyCodes.KEY_PAGEDOWN)
      //      {
      //         list.getSelectionModel().selectNextPage();
      //         return true;
      //      }

      return false;
   }

   private void ensureProposalVisible(int index)
   {
      com.google.gwt.dom.client.Element element = list.getRowElement(index);
      element.scrollIntoView();
   }

   @Override
   public void setDelegate(Events delegate)
   {
      this.delegate = delegate;
   }

   @Override
   public void dismiss()
   {
      boolean hadFocus = DomUtils.isElementOrChildFocused((Element)popup.getElement());
      popup.hide();

      proposals = null;

      FocusManager focusManager = editor.getFocusManager();

      if (hadFocus && !focusManager.hasFocus())
      {
         focusManager.focus();
      }
   }

   @Override
   public void positionAndShow(CompletionProposal[] items)
   {
      if (items != null && items.length != 0)
      {
         this.proposals = JsonCollections.createArray(items);
         provider.setList(Arrays.asList(items));
         list.setPageSize(items.length);
         selectionModel.setSelected(items[0], true);
      }
      else
      {
         proposals = JsonCollections.createArray();
         provider.setList(Collections.<CompletionProposal> emptyList());
      }
      //      this.anchor = editor.getSelection().getCursorAnchor();

      boolean showingFromHidden = !popup.isShowing();
      //      if (showingFromHidden)
      //      {
      //         list.getSelectionModel().clearSelection();
      //      }

      //      final JsonArray<CompletionProposal> itemsToDisplay = JsoArray.<CompletionProposal> create();
      //      if (items.length <= MAX_COMPLETIONS_TO_SHOW)
      //      {
      //         //itemsToDisplay = items;
      //         for (int i = 0; i < items.length; i++)
      //         {
      //            itemsToDisplay.add(items[i]);
      //         }
      //      }
      //      else
      //      {
      //         for (int i = 0; i < MAX_COMPLETIONS_TO_SHOW; i++)
      //         {
      //            itemsToDisplay.add(items[i]);
      //         }
      //         //itemsToDisplay = items.getItems().slice(0, MAX_COMPLETIONS_TO_SHOW);
      //         //itemsToDisplay.add(CAPPED_INDICATOR);
      //      }

      //      if (list.getSelectionModel().getSelectedItem() == null)
      //      {
      //         list.getSelectionModel().setSelectedItem(0);
      //      }

      //      String hintText = null;//items.getHint();
      //      if (hintText == null)
      //      {
      //         hint.setTextContent("");
      //         CssUtils.setDisplayVisibility2(hint, false);
      //      }
      //      else
      //      {
      //         hint.setTextContent(hintText);
      //         CssUtils.setDisplayVisibility2(hint, true);
      //      }
      Editor e = (Editor)editor;
      com.google.gwt.user.client.Element element =
         (com.google.gwt.user.client.Element)e.getBuffer().getView().getElement();
      final int cursorY =
         e.getBuffer().convertLineNumberToY(editor.getSelection().getCursorLineNumber()) + element.getAbsoluteTop()
            + e.getBuffer().getEditorLineHeight();
      final int cursorX =
         e.getBuffer().convertColumnToX(editor.getSelection().getCursorLine(), editor.getSelection().getCursorColumn())
            + element.getAbsoluteLeft();
      popup.setPopupPositionAndShow(new PositionCallback()
      {

         @Override
         public void setPosition(int offsetWidth, int offsetHeight)
         {
            //TODO calculation of position
            popup.setPopupPosition(cursorX, cursorY);
         }
      });

      //      editor.getBuffer().addAnchoredElement(anchor, box);

      ensureRootElementWillBeOnScreen(showingFromHidden);
   }

   private void ensureRootElementWillBeOnScreen(boolean showingFromHidden)
   {
      // Remove any max-heights so we can get its desired height
      //      container.getStyle().removeProperty("max-height");
      //      ClientRect bounds = ((Element)popup.getElement()).getBoundingClientRect();
      //      int height = (int)bounds.getHeight();
      //      int delta = height - (int)container.getBoundingClientRect().getHeight();
      //
      //      ClientRect bufferBounds = editor.getBuffer().getBoundingClientRect();
      //      int lineHeight = editor.getBuffer().getEditorLineHeight();
      //      int lineTop = (int)bounds.getTop() - CssUtils.parsePixels(box.getStyle().getMarginTop());
      //
      //      int spaceAbove = lineTop - (int)bufferBounds.getTop();
      //      int spaceBelow = (int)bufferBounds.getBottom() - lineTop - lineHeight;
      //
      //      if (showingFromHidden)
      //      {
      //         // If it was already showing, we don't adjust the positioning.
      //         positionAbove = spaceAbove >= css.maxHeight() && spaceBelow < css.maxHeight();
      //      }
      //
      //      // Get available height.
      //      int maxHeight = positionAbove ? spaceAbove : spaceBelow;
      //
      //      // Restrict to specified height.
      //      maxHeight = Math.min(maxHeight, css.maxHeight());
      //
      //      // Fit to content size.
      //      maxHeight = Math.min(maxHeight, height);
      //
      //      container.getStyle().setProperty("max-height", (maxHeight - delta) + CSSStyleDeclaration.Unit.PX);
      //
      //      int marginTop = positionAbove ? -maxHeight : lineHeight;
      //      box.getStyle().setMarginTop(marginTop, CSSStyleDeclaration.Unit.PX);
      //
      //      if (showingFromHidden)
      //      {
      //         // Adjust the box horizontal position if it's out of the editor's right bound.
      //         // If box was already showing, we don't adjust the horizontal positioning to avoid flickering.
      //         int editorScrollLeft = editor.getBuffer().getScrollLeft();
      //         int boxLeftPosition = CssUtils.parsePixels(box.getStyle().getLeft()) - editorScrollLeft;
      //         int boxWidth = (int)bounds.getWidth();
      //         int editorWidth = editor.getBuffer().getWidth();
      //         int boxRightOffset = 8; // need for better visibility
      //         if ((boxLeftPosition + boxWidth) > editorWidth - boxRightOffset)
      //         {
      //            if (editorWidth > boxWidth)
      //            {
      //               box.getStyle().setLeft(editorWidth + editorScrollLeft - boxWidth - boxRightOffset,
      //                  CSSStyleDeclaration.Unit.PX);
      //            }
      //         }
      //      }
   }

   CellList<CompletionProposal> getList()
   {
      return list;
   }
}
