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

package com.google.collide.client.code.autocomplete.integration;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import com.google.collide.client.code.autocomplete.AutocompleteBox;
import com.google.collide.client.code.autocomplete.SignalEventEssence;
import com.google.collide.client.editor.Editor;
import com.google.collide.client.editor.FocusManager;
import com.google.collide.client.ui.list.SimpleList;
import com.google.collide.client.ui.list.SimpleList.View;
import com.google.collide.client.ui.menu.AutoHideController;
import com.google.collide.client.util.CssUtils;
import com.google.collide.client.util.Elements;
import com.google.collide.client.util.dom.DomUtils;
import com.google.collide.json.client.JsoArray;
import com.google.collide.json.shared.JsonArray;
import com.google.collide.shared.document.anchor.ReadOnlyAnchor;
import com.google.common.base.Preconditions;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.resources.client.CssResource;
import elemental.css.CSSStyleDeclaration;
import elemental.dom.Node;
import elemental.html.ClientRect;
import elemental.html.Element;
import elemental.html.TableCellElement;
import elemental.html.TableElement;

import org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal;
import org.exoplatform.ide.editor.client.api.contentassist.ContextInformation;
import org.exoplatform.ide.editor.client.api.contentassist.Point;
import org.exoplatform.ide.editor.shared.text.IDocument;
import org.waveprotocol.wave.client.common.util.SignalEvent;

/**
 * A controller for managing the UI for showing autocomplete proposals.
 *
 */
public class AutocompleteUiController implements AutocompleteBox {

  public interface Resources extends SimpleList.Resources {
    @Source("AutocompleteComponent.css")
    Css autocompleteComponentCss();
  }

  public interface Css extends CssResource {
    String cappedProposalLabel();

    String proposalIcon();

    String proposalLabel();

    String proposalGroup();

    String container();

    String items();

    String hint();

    int maxHeight();
  }
  
  private static final int MAX_COMPLETIONS_TO_SHOW = 150;
//  private static final AutocompleteProposal CAPPED_INDICATOR = new AutocompleteProposal("");
  private static final CompletionProposal CAPPED_INDICATOR = new CappedIndicatorProposal();

  private final SimpleList.ListItemRenderer<CompletionProposal> listItemRenderer =
      new SimpleList.ListItemRenderer<CompletionProposal>() {
        @Override
        public void render(Element itemElement, CompletionProposal itemData) {
          TableCellElement icon = Elements.createTDElement(css.proposalIcon());
          TableCellElement label = Elements.createTDElement(css.proposalLabel());
          TableCellElement group = Elements.createTDElement(css.proposalGroup());

          if (itemData != CAPPED_INDICATOR) {
            icon.appendChild((Node)itemData.getImage().getElement());
            label.setTextContent(itemData.getDisplayString());
            //group.setTextContent(itemData.getPath().getPathString());
          } else {
            label.setTextContent("Type for more results");
            label.addClassName(css.cappedProposalLabel());
          }
          itemElement.appendChild(icon);
          itemElement.appendChild(label);
          itemElement.appendChild(group);
        }

        @Override
        public Element createElement() {
          return Elements.createTRElement();
        }
      };

  private final SimpleList.ListEventDelegate<CompletionProposal> listDelegate =
      new SimpleList.ListEventDelegate<CompletionProposal>() {
        @Override
        public void onListItemClicked(Element itemElement, CompletionProposal itemData) {
          Preconditions.checkNotNull(delegate);
          if (itemData == CAPPED_INDICATOR) {
            return;
          }
          list.getSelectionModel().setSelectedItem(itemData);
        }

        @Override
         public void onListItemDoubleClicked(Element listItemBase, CompletionProposal itemData) {
            Preconditions.checkNotNull(delegate);
            if (itemData == CAPPED_INDICATOR) {
               return;
            }
            delegate.onSelect(itemData);
         }
      };

  private final AutoHideController autoHideController;
  private final Css css;
  private final SimpleList<CompletionProposal> list;
  private Events delegate;
  private final Editor editor;
  private final Element box;
  private final Element container;
  private final Element hint;

  /** Will be non-null when the popup is showing */
  private ReadOnlyAnchor anchor;

  /**
   * True to force the layout above the anchor, false to layout below. This
   * should be set when showing from the hidden state. It's used to keep
   * the position consistent while the box is visible.
   */
  private boolean positionAbove;

  /**
   * The currently displayed proposals. This may contain more proposals than actually shown since we
   * cap the maximum number of visible proposals. This will be null if the UI is not showing.
   */
  private CompletionProposal[] autocompleteProposals;

  public AutocompleteUiController(Editor editor, Resources res) {
    this.editor = editor;
    this.css = res.autocompleteComponentCss();

    box = Elements.createDivElement();
    box.setId("codenvy-ide-autocomplete-panel");
    // Prevent our mouse events from going to the editor
    DomUtils.stopMousePropagation(box);

    TableElement tableElement = Elements.createTableElement();
    tableElement.setClassName(css.items());

    container = Elements.createDivElement(css.container());
    DomUtils.preventExcessiveScrollingPropagation(container);
    container.appendChild(tableElement);
    box.appendChild(container);

    hint = Elements.createDivElement(css.hint());
    CssUtils.setDisplayVisibility2(hint, false);
    box.appendChild(hint);

    list =
        SimpleList.create((View) box, container, tableElement, res.defaultSimpleListCss(),
            listItemRenderer, listDelegate);

    autoHideController = AutoHideController.create(box);
    autoHideController.setCaptureOutsideClickOnClose(false);
    autoHideController.setDelay(-1);
  }

  @Override
  public boolean isShowing() {
    return autoHideController.isShowing();
  }

  @Override
  public boolean consumeKeySignal(SignalEventEssence signal) {
    Preconditions.checkState(isShowing());
    Preconditions.checkNotNull(delegate);

    if ((signal.keyCode == KeyCodes.KEY_TAB) || (signal.keyCode == KeyCodes.KEY_ENTER)) {
      delegate.onSelect(list.getSelectionModel().getSelectedItem());
      return true;
    }

    if (signal.keyCode == KeyCodes.KEY_ESCAPE) {
      delegate.onCancel();
      return true;
    }

    if (signal.type != SignalEvent.KeySignalType.NAVIGATION) {
      return false;
    }

    if ((signal.keyCode == KeyCodes.KEY_DOWN)) {
       if (list.getSelectionModel().getSelectedIndex() == list.getSelectionModel().size()-1) 
       {
          list.getSelectionModel().setSelectedItem(0);
       }
       else
       {
          list.getSelectionModel().selectNext();
       }
      return true;
    }

    if (signal.keyCode == KeyCodes.KEY_UP) {
       if (list.getSelectionModel().getSelectedIndex() == 0)
       {
          list.getSelectionModel().setSelectedItem(list.getSelectionModel().size()-1);
       }
       else
       {
          list.getSelectionModel().selectPrevious();
       }
      return true;
    }

    if ((signal.keyCode == KeyCodes.KEY_LEFT) || (signal.keyCode == KeyCodes.KEY_RIGHT)) {
      delegate.onCancel();
      return true;
    }

    if (signal.keyCode == KeyCodes.KEY_PAGEUP) {
      list.getSelectionModel().selectPreviousPage();
      return true;
    }

    if (signal.keyCode == KeyCodes.KEY_PAGEDOWN) {
      list.getSelectionModel().selectNextPage();
      return true;
    }

    return false;
  }

  @Override
  public void setDelegate(Events delegate) {
    this.delegate = delegate;
  }

  @Override
  public void dismiss() {
    boolean hadFocus = list.hasFocus();
    autoHideController.hide();

    if (anchor != null) {
      editor.getBuffer().removeAnchoredElement(anchor, autoHideController.getView().getElement());
      anchor = null;
    }

    autocompleteProposals = null;

    FocusManager focusManager = editor.getFocusManager();
    if (hadFocus && !focusManager.hasFocus()) {
      focusManager.focus();
    }
  }

  @Override
  public void positionAndShow(CompletionProposal[] items) {
    this.autocompleteProposals = items;
    this.anchor = editor.getSelection().getCursorAnchor();

    boolean showingFromHidden = !autoHideController.isShowing();
    if (showingFromHidden) {
      list.getSelectionModel().clearSelection();
    }
    
    final JsonArray<CompletionProposal> itemsToDisplay = JsoArray.<CompletionProposal>create();
    if (items.length <= MAX_COMPLETIONS_TO_SHOW) {
      //itemsToDisplay = items;
       for (int i = 0; i < items.length; i++)
       {
          itemsToDisplay.add(items[i]);
       }
    } else {
       for (int i = 0; i < MAX_COMPLETIONS_TO_SHOW; i++)
       {
          itemsToDisplay.add(items[i]);
       }
      //itemsToDisplay = items.getItems().slice(0, MAX_COMPLETIONS_TO_SHOW);
      itemsToDisplay.add(CAPPED_INDICATOR);
    }
    list.render(itemsToDisplay);

    if (list.getSelectionModel().getSelectedItem() == null) {
      list.getSelectionModel().setSelectedItem(0);
    }

    String hintText = null;//items.getHint();
    if (hintText == null) {
      hint.setTextContent("");
      CssUtils.setDisplayVisibility2(hint, false);
    } else {
      hint.setTextContent(hintText);
      CssUtils.setDisplayVisibility2(hint, true);
    }

    autoHideController.show();

    editor.getBuffer().addAnchoredElement(anchor, box);

    ensureRootElementWillBeOnScreen(showingFromHidden);
  }

  private void ensureRootElementWillBeOnScreen(boolean showingFromHidden) {
    // Remove any max-heights so we can get its desired height
    container.getStyle().removeProperty("max-height");
    ClientRect bounds = box.getBoundingClientRect();
    int height = (int) bounds.getHeight();
    int delta = height - (int) container.getBoundingClientRect().getHeight();

    ClientRect bufferBounds = editor.getBuffer().getBoundingClientRect();
    int lineHeight = editor.getBuffer().getEditorLineHeight();
    int lineTop = (int) bounds.getTop() - CssUtils.parsePixels(box.getStyle().getMarginTop());

    int spaceAbove =  lineTop - (int) bufferBounds.getTop();
    int spaceBelow = (int) bufferBounds.getBottom() - lineTop - lineHeight;

    if (showingFromHidden) {
      // If it was already showing, we don't adjust the positioning.
      positionAbove = spaceAbove >= css.maxHeight() && spaceBelow < css.maxHeight();
    }

    // Get available height.
    int maxHeight = positionAbove ? spaceAbove : spaceBelow;

    // Restrict to specified height.
    maxHeight = Math.min(maxHeight, css.maxHeight());

    // Fit to content size.
    maxHeight = Math.min(maxHeight, height);

    container.getStyle().setProperty(
        "max-height", (maxHeight - delta) + CSSStyleDeclaration.Unit.PX);

    int marginTop = positionAbove ? -maxHeight : lineHeight;
    box.getStyle().setMarginTop(marginTop, CSSStyleDeclaration.Unit.PX);

    if (showingFromHidden) {
       // Adjust the box horizontal position if it's out of the editor's right bound.
       // If box was already showing, we don't adjust the horizontal positioning to avoid flickering.
       int editorScrollLeft = editor.getBuffer().getScrollLeft();
       int boxLeftPosition = CssUtils.parsePixels(box.getStyle().getLeft()) - editorScrollLeft;
       int boxWidth = (int) bounds.getWidth();
       int editorWidth = editor.getBuffer().getWidth();
       int boxRightOffset = 8; // need for better visibility
       if ((boxLeftPosition + boxWidth) > editorWidth - boxRightOffset)
       {
          if (editorWidth > boxWidth)
          {
             box.getStyle().setLeft(editorWidth + editorScrollLeft - boxWidth - boxRightOffset, CSSStyleDeclaration.Unit.PX);
          }
       }
    }
  }

  SimpleList<CompletionProposal> getList() {
    return list;
  }

  /**
   * Empty {@link CompletionProposal} used to add special label for indicating that proposals list is tщo much to show.
   */
  private static class CappedIndicatorProposal implements CompletionProposal {

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.shared.text.IDocument)
    */
   @Override
   public void apply(IDocument document) {}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getSelection(org.exoplatform.ide.editor.shared.text.IDocument)
    */
   @Override
   public Point getSelection(IDocument document) {return null;}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getAdditionalProposalInfo()
    */
   @Override
   public Widget getAdditionalProposalInfo() {return null;}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getDisplayString()
    */
   @Override
   public String getDisplayString() {return null;}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getImage()
    */
   @Override
   public Image getImage() {return null;}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getContextInformation()
    */
   @Override
   public ContextInformation getContextInformation() {return null;}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.shared.text.IDocument, char, int)
    */
   @Override
   public void apply(IDocument document, char trigger, int offset) {}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#isValidFor(org.exoplatform.ide.editor.shared.text.IDocument, int)
    */
   @Override
   public boolean isValidFor(IDocument document, int offset) {return false;}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#getTriggerCharacters()
    */
   @Override
   public char[] getTriggerCharacters() {return null;}

   /**
    * @see org.exoplatform.ide.editor.client.api.contentassist.CompletionProposal#isAutoInsertable()
    */
   @Override
   public boolean isAutoInsertable() {return false;}
     
  }
}
