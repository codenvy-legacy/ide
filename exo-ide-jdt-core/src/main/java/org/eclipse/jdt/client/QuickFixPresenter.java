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
package org.eclipse.jdt.client;

import com.google.gwt.event.shared.HandlerManager;

import org.eclipse.jdt.client.codeassistant.api.ICompletionProposal;
import org.eclipse.jdt.client.codeassistant.api.IProblemLocation;
import org.eclipse.jdt.client.codeassistant.ui.CodeAssitantForm;
import org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler;
import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.core.dom.CompilationUnit;
import org.eclipse.jdt.client.event.ShowQuickFixEvent;
import org.eclipse.jdt.client.event.ShowQuickFixHandler;
import org.eclipse.jdt.client.internal.text.correction.JavaCorrectionProcessor;
import org.eclipse.jdt.client.internal.text.correction.ProblemLocation;
import org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.SelectionRange;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.editor.text.IRegion;
import org.exoplatform.ide.editor.text.Position;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class QuickFixPresenter implements IQuickAssistInvocationContext, EditorActiveFileChangedHandler,
   ShowQuickFixHandler, UpdateOutlineHandler, ProposalSelectedHandler
{

   private Editor editor;

   private CompilationUnit compilationUnit;

   private FileModel file;

   private AssistDisplay display;

   private IProblemLocation[] currentAnnotations;

   private IDocument document;

   private int currLength;

   private int currOffset;

   private JavaCorrectionProcessor correctionProcessor;

   /**
    * 
    */
   public QuickFixPresenter(HandlerManager eventBus)
   {
      correctionProcessor = new JavaCorrectionProcessor();
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ShowQuickFixEvent.TYPE, this);
      eventBus.addHandler(UpdateOutlineEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      editor = event.getEditor();
      file = event.getFile();
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getOffset()
    */
   @Override
   public int getOffset()
   {
      return currOffset;
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getLength()
    */
   @Override
   public int getLength()
   {
      return currLength;
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getDocument()
    */
   @Override
   public IDocument getDocument()
   {
      return document;
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#getProblemsAtOffset()
    */
   @Override
   public IProblemLocation[] getProblemsAtOffset()
   {
      return currentAnnotations;
   }

   /**
    * @see org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext#isUpdatedOffset()
    */
   @Override
   public boolean isUpdatedOffset()
   {
      return false;
   }

   /**
    * @see org.eclipse.jdt.client.event.ShowQuickFixHandler#onShowQuickFix(org.eclipse.jdt.client.event.ShowQuickFixEvent)
    */
   @Override
   public void onShowQuickFix(ShowQuickFixEvent event)
   {
      if (editor == null || compilationUnit == null)
         return;
      IProblem[] problems = compilationUnit.getProblems();

      boolean isReinvoked = false;
      //      fIsProblemLocationAvailable = false;

      if (display != null)
      {
         if (isUpdatedOffset())
         {
            isReinvoked = true;
            //            restorePosition();
            //            hide();
            //            fIsProblemLocationAvailable = true;
         }
      }

      //      fPosition = null;
      //      fCurrentAnnotations = null;

      document = editor.getDocument();
      ArrayList<IProblemLocation> resultingAnnotations = new ArrayList<IProblemLocation>(20);
      try
      {
         SelectionRange selectedRange = editor.getSelectionRange();
         currOffset = document.getLineOffset(selectedRange.getStartLine()) + selectedRange.getStartSymbol();
         currLength = document.getLineOffset(selectedRange.getEndLine()) + selectedRange.getEndSymbol() - currOffset;
         boolean goToClosest = (currLength == 0) && !isReinvoked;

         int newOffset =
            collectQuickFixableAnnotations(editor, currOffset, goToClosest, problems, resultingAnnotations);
         if (newOffset != currOffset)
         {
            //            storePosition(currOffset, currLength);
            int row = document.getLineOfOffset(newOffset);
            editor.goToPosition(row, newOffset - row);
            currOffset = newOffset;
            //            fViewer.revealRange(newOffset, 0);
            //            fIsProblemLocationAvailable = true;
            //            if (fIsCompletionActive)
            //            {
            //               hide();
            //            }
         }
      }
      catch (BadLocationException e)
      {
         //TODO
         e.printStackTrace();
         //         JavaPlugin.log(e);
      }
      currentAnnotations = resultingAnnotations.toArray(new IProblemLocation[resultingAnnotations.size()]);
      ICompletionProposal[] proposals = correctionProcessor.computeQuickAssistProposals(this);
      display = new CodeAssitantForm(0, 0, proposals, this);
   }

   /**
    * @see org.eclipse.jdt.client.UpdateOutlineHandler#onUpdateOutline(org.eclipse.jdt.client.UpdateOutlineEvent)
    */
   @Override
   public void onUpdateOutline(UpdateOutlineEvent event)
   {
      if (event.getFile().getId().equals(file.getId()))
         compilationUnit = event.getCompilationUnit();
      else
         compilationUnit = null;
   }

   private static IRegion getRegionOfInterest(Editor editor, int invocationLocation) throws BadLocationException
   {
      IDocument document = editor.getDocument();
      if (document == null)
      {
         return null;
      }
      return document.getLineInformationOfOffset(invocationLocation);
   }

   public static int collectQuickFixableAnnotations(Editor editor, int invocationLocation, boolean goToClosest,
      IProblem[] problems, ArrayList<IProblemLocation> resultingAnnotations) throws BadLocationException
   {

      if (problems.length == 0)
      {
         return invocationLocation;
      }

      if (goToClosest)
      {
         IRegion lineInfo = getRegionOfInterest(editor, invocationLocation);
         if (lineInfo == null)
         {
            return invocationLocation;
         }
         int rangeStart = lineInfo.getOffset();
         int rangeEnd = rangeStart + lineInfo.getLength();

         ArrayList<Position> allPositions = new ArrayList<Position>();
         List<IProblemLocation> allAnnotations = new ArrayList<IProblemLocation>();
         int bestOffset = Integer.MAX_VALUE;
         for (IProblem problem : problems)
         {
            Position pos = new Position(problem.getSourceStart(), problem.getSourceEnd() - problem.getSourceStart());
            if (pos != null && isInside(pos.offset, rangeStart, rangeEnd))
            { // inside our range?
               IProblemLocation annot = new ProblemLocation(problem);
               allAnnotations.add(annot);
               allPositions.add(pos);
               bestOffset = processAnnotation(annot, pos, invocationLocation, bestOffset);
            }
         }
         if (bestOffset == Integer.MAX_VALUE)
         {
            return invocationLocation;
         }
         for (int i = 0; i < allPositions.size(); i++)
         {
            Position pos = allPositions.get(i);
            if (isInside(bestOffset, pos.offset, pos.offset + pos.length))
            {
               resultingAnnotations.add(allAnnotations.get(i));
            }
         }
         return bestOffset;
      }
      else
      {
         for (IProblem problem : problems)
         {
            Position pos = new Position(problem.getSourceStart(), problem.getSourceEnd() - problem.getSourceStart());
            if (pos != null && isInside(invocationLocation, pos.offset, pos.offset + pos.length))
            {
               resultingAnnotations.add(new ProblemLocation(problem));
            }
         }
         return invocationLocation;
      }
   }

   private static int processAnnotation(IProblemLocation annot, Position pos, int invocationLocation, int bestOffset)
   {
      int posBegin = pos.offset;
      int posEnd = posBegin + pos.length;
      if (isInside(invocationLocation, posBegin, posEnd))
      { // covers invocation location?
         return invocationLocation;
      }
      else if (bestOffset != invocationLocation)
      {
         int newClosestPosition = computeBestOffset(posBegin, invocationLocation, bestOffset);
         if (newClosestPosition != -1)
         {
            if (newClosestPosition != bestOffset)
            { // new best
               if (JavaCorrectionProcessor.hasCorrections(annot))
               { // only jump to it if there are proposals
                  return newClosestPosition;
               }
            }
         }
      }
      return bestOffset;
   }

   /**
    * Computes and returns the invocation offset given a new
    * position, the initial offset and the best invocation offset
    * found so far.
    * <p>
    * The closest offset to the left of the initial offset is the
    * best. If there is no offset on the left, the closest on the
    * right is the best.</p>
    * @param newOffset the offset to llok at
    * @param invocationLocation the invocation location
    * @param bestOffset the current best offset
    * @return -1 is returned if the given offset is not closer or the new best offset
    */
   private static int computeBestOffset(int newOffset, int invocationLocation, int bestOffset)
   {
      if (newOffset <= invocationLocation)
      {
         if (bestOffset > invocationLocation)
         {
            return newOffset; // closest was on the right, prefer on the left
         }
         else if (bestOffset <= newOffset)
         {
            return newOffset; // we are closer or equal
         }
         return -1; // further away
      }

      if (newOffset <= bestOffset)
         return newOffset; // we are closer or equal

      return -1; // further away
   }

   private static boolean isInside(int offset, int start, int end)
   {
      return offset == start || offset == end || (offset > start && offset < end); // make sure to handle 0-length ranges
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onTokenSelected(org.eclipse.jdt.client.codeassistant.api.IJavaCompletionProposal, boolean)
    */
   @Override
   public void onTokenSelected(ICompletionProposal proposal, boolean editorHasFocus)
   {
      try
      {
         proposal.apply(document);
         onCancelAutoComplete(editorHasFocus);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onCancelAutoComplete(boolean)
    */
   @Override
   public void onCancelAutoComplete(boolean editorHasFocus)
   {
      if (!editorHasFocus)
         editor.setFocus();
      //      keyHandler.removeHandler();
      display = null;
   }

}
