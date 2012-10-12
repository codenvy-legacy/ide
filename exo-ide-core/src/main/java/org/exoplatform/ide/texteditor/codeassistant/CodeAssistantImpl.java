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
package org.exoplatform.ide.texteditor.codeassistant;

import com.google.gwt.core.client.GWT;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.json.JsonStringMap.IterationCallback;
import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.DocumentImpl;
import org.exoplatform.ide.text.Region;
import org.exoplatform.ide.text.TextUtilities;
import org.exoplatform.ide.text.store.DocumentTextStore;
import org.exoplatform.ide.text.store.LineFinder;
import org.exoplatform.ide.text.store.LineInfo;
import org.exoplatform.ide.texteditor.Buffer;
import org.exoplatform.ide.texteditor.Buffer.ScrollListener;
import org.exoplatform.ide.texteditor.UndoManager;
import org.exoplatform.ide.texteditor.api.KeyListener;
import org.exoplatform.ide.texteditor.api.TextEditorPartDisplay;
import org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistant;
import org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal;
import org.exoplatform.ide.texteditor.codeassistant.AutocompleteBox.Events;
import org.exoplatform.ide.texteditor.codeassistant.AutocompleteUiController.Resources;
import org.exoplatform.ide.util.ListenerRegistrar.Remover;
import org.exoplatform.ide.util.input.KeyCodeMap;
import org.exoplatform.ide.util.input.SignalEvent;
import org.exoplatform.ide.util.input.SignalEvent.KeySignalType;
import org.exoplatform.ide.util.loging.Log;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class CodeAssistantImpl implements CodeAssistant
{

   private JsonStringMap<CodeAssistProcessor> processors;

   private AutocompleteBox box;

   private TextEditorPartDisplay textEditor;

   private String lastErrorMessage;

   private String partitioning;

   private Events events = new Events()
   {

      @Override
      public void onSelect(CompletionProposal proposal)
      {
         dismissBox();
         applyProposal(proposal);
      }

      @Override
      public void onCancel()
      {
         dismissBox();
      }
   };

   private KeyListener keyListener = new KeyListener()
   {

      @Override
      public boolean onKeyPress(SignalEvent event)
      {
         if (box.isShowing() && box.consumeKeySignal(new SignalEventEssence(event)))
         {
            return true;
         }
         if (box.isShowing())
         {
            scheduleRequestCodeassistant();
            return false;
         }
         if (event.getKeySignalType() == KeySignalType.INPUT)
         {
            int letter = KeyCodeMap.getKeyFromEvent(event);
            if (computeAllAutoActivationTriggers().contains(String.valueOf((char)letter)))
            {
               scheduleRequestCodeassistant();
            }
         }
         return false;

      }
   };

   /**
    * A listener that dismisses the autocomplete box when the editor is scrolled.
    */
   private ScrollListener dismissingScrollListener = new ScrollListener()
   {

      @Override
      public void onScroll(Buffer buffer, int scrollTop)
      {
         dismissBox();
      }
   };

   private Remover keyListenerRemover;

   //TODO inject this
   private Resources res = GWT.create(Resources.class);

   /**
    * 
    */
   public CodeAssistantImpl()
   {
      processors = JsonCollections.createStringMap();
      partitioning = Document.DEFAULT_PARTITIONING;
      res.defaultSimpleListCss().ensureInjected();
      res.autocompleteComponentCss().ensureInjected();
   }

   /**
    * @param proposal
    */
   private void applyProposal(CompletionProposal proposal)
   {
      if (proposal == null)
         return;
      UndoManager undoManager = textEditor.getEditorDocumentMutator().getUndoManager();
      if (undoManager != null)
         undoManager.beginCompoundChange();
      try
      {
         proposal.apply(textEditor.getDocument());
         Region selection = proposal.getSelection(textEditor.getDocument());
         setSelection(textEditor, selection);
      }
      catch (Exception e)
      {
         Log.error(getClass(), e);
      }
      finally
      {
         if (undoManager != null)
            undoManager.endCompoundChange();
      }
   }

   /**
    * Computes the sorted set of all auto activation trigger characters.
    *
    * @return the sorted set of all auto activation trigger characters
    */
   private String computeAllAutoActivationTriggers()
   {
      if (processors == null)
      {
         return ""; //$NON-NLS-1$
      }
      final StringBuffer buf = new StringBuffer(5);
      processors.iterate(new IterationCallback<CodeAssistProcessor>()
      {

         @Override
         public void onIteration(String key, CodeAssistProcessor value)
         {
            char[] triggers = value.getCompletionProposalAutoActivationCharacters();
            if (triggers != null)
               buf.append(triggers);
         }
      });
      return buf.toString();
   }

   /**
    * @param textEditor
    * @param selection
    */
   private void setSelection(TextEditorPartDisplay textEditor, Region selection)
   {
      //TODO move this method to selection model
      DocumentImpl doc = (DocumentImpl)textEditor.getDocument();
      DocumentTextStore store = doc.getTextStore();
      LineFinder lineFinder = store.getLineFinder();
      try
      {
         int lineNumber = doc.getLineOfOffset(selection.getOffset());
         int lineOffset = doc.getLineOffset(lineNumber);
         LineInfo first = lineFinder.findLine(lineNumber);

         int cursorPos = selection.getOffset() + selection.getLength();
         int endineNumber = doc.getLineOfOffset(cursorPos);
         int endLineOffset = doc.getLineOffset(endineNumber);
         LineInfo last = lineFinder.findLine(endineNumber);
         textEditor.getSelection().setSelection(first, selection.getOffset() - lineOffset, last,
            cursorPos - endLineOffset);

      }
      catch (BadLocationException e)
      {
         Log.error(getClass(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistant#install(org.exoplatform.ide.texteditor.api.TextEditorPartDisplay)
    */
   @Override
   public void install(TextEditorPartDisplay display)
   {
      this.textEditor = display;
      box = new AutocompleteUiController(display, res);
      keyListenerRemover = display.getKeyListenerRegistrar().add(keyListener);
      box.setDelegate(events);
      display.getBuffer().getScrollListenerRegistrar().add(dismissingScrollListener);
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistant#uninstall()
    */
   @Override
   public void uninstall()
   {
      if (keyListenerRemover != null)
         keyListenerRemover.remove();
   }

   private void scheduleRequestCodeassistant()
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {

         @Override
         public void execute()
         {
            int offset =
               TextUtilities.getOffset(textEditor.getDocument(), textEditor.getSelection().getCursorLineNumber(),
                  textEditor.getSelection().getCursorColumn());
            if (offset > 0)
            {
               CompletionProposal[] proposals = computeCompletionProposals(textEditor, offset);
               if (!box.isShowing())
               {
                  if (proposals != null && proposals.length == 1 && proposals[0].isAutoInsertable())
                  {
                     applyProposal(proposals[0]);
                     return;
                  }
               }
               box.positionAndShow(proposals);
            }
         }
      });
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistant#showPossibleCompletions()
    */
   @Override
   public String showPossibleCompletions()
   {
      //TODO introduce async API
      scheduleRequestCodeassistant();
      return lastErrorMessage;
   }

   /**
    * Returns an array of completion proposals computed based on the specified document position.
    * The position is used to determine the appropriate content assist processor to invoke.
    *
    * @param display the display for which to compute the proposals
    * @param offset a document offset
    * @return an array of completion proposals or <code>null</code> if no proposals are possible
    */
   CompletionProposal[] computeCompletionProposals(TextEditorPartDisplay display, int offset)
   {
      lastErrorMessage = null;

      CompletionProposal[] result = null;

      CodeAssistProcessor p = getProcessor(display, offset);
      if (p != null)
      {
         result = p.computeCompletionProposals(display, offset);
         lastErrorMessage = p.getErrorMessage();
      }

      return result;
   }

   /**
    * Returns the code assist processor for the content type of the specified document position.
    *
    * @param display the text display
    * @param offset a offset within the document
    * @return a code-assist processor or <code>null</code> if none exists
    */
   private CodeAssistProcessor getProcessor(TextEditorPartDisplay display, int offset)
   {
      try
      {
         Document document = display.getDocument();
         String type = TextUtilities.getContentType(document, partitioning, offset, true);
         return getCodeAssistProcessor(type);

      }
      catch (BadLocationException x)
      {
      }

      return null;
   }

   private void dismissBox()
   {
      box.dismiss();
   }

   /**
    * @see org.exoplatform.ide.texteditor.api.codeassistant.CodeAssistant#getCodeAssistProcessor(java.lang.String)
    */
   @Override
   public CodeAssistProcessor getCodeAssistProcessor(String contentType)
   {
      return processors.get(contentType);
   }

   public void setCodeAssistantProcessor(String contentType, CodeAssistProcessor processor)
   {
      processors.put(contentType, processor);
   }

}
