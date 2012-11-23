/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.exoplatform.ide.java.client.templates;

import com.google.gwt.dom.client.Style.Overflow;

import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.ide.java.client.codeassistant.api.JavaCompletionProposal;
import org.exoplatform.ide.java.client.codeassistant.ui.StyledString;
import org.exoplatform.ide.java.client.templates.api.DocumentTemplateContext;
import org.exoplatform.ide.java.client.templates.api.GlobalTemplateVariables;
import org.exoplatform.ide.java.client.templates.api.Template;
import org.exoplatform.ide.java.client.templates.api.TemplateBuffer;
import org.exoplatform.ide.java.client.templates.api.TemplateContext;
import org.exoplatform.ide.java.client.templates.api.TemplateException;
import org.exoplatform.ide.java.client.templates.api.TemplateVariable;
import org.exoplatform.ide.runtime.Assert;
import org.exoplatform.ide.text.BadLocationException;
import org.exoplatform.ide.text.BadPositionCategoryException;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.DocumentEvent;
import org.exoplatform.ide.text.DocumentImpl;
import org.exoplatform.ide.text.Region;
import org.exoplatform.ide.text.RegionImpl;

/**
 * A template proposal.
 */
public class TemplateProposal implements JavaCompletionProposal
{

   private final Template fTemplate;

   private final TemplateContext fContext;

   private final Image fImage;

   private final Region fRegion;

   private int fRelevance;

   private Region fSelectedRegion; // initialized by apply()

   private StyledString fDisplayString;

   private InclusivePositionUpdater fUpdater;

   private int cursorPosition;

   /**
    * Creates a template proposal with a template and its context.
    * 
    * @param template the template
    * @param context the context in which the template was requested.
    * @param region the region this proposal is applied to
    * @param image the icon of the proposal.
    */
   public TemplateProposal(Template template, TemplateContext context, Region region, Image image)
   {
      Assert.isNotNull(template);
      Assert.isNotNull(context);
      Assert.isNotNull(region);

      fTemplate = template;
      fContext = context;
      fImage = image;
      fRegion = region;

      fDisplayString = null;

      fRelevance = computeRelevance();
   }

   /**
    * Computes the relevance to match the relevance values generated by the core content assistant.
    * 
    * @return a sensible relevance value.
    */
   private int computeRelevance()
   {
      // see org.eclipse.jdt.internal.codeassist.RelevanceConstants
      final int R_DEFAULT = 0;
      final int R_INTERESTING = 5;
      final int R_CASE = 10;
      final int R_NON_RESTRICTED = 3;
      final int R_EXACT_NAME = 4;
      final int R_INLINE_TAG = 31;

      int base = R_DEFAULT + R_INTERESTING + R_NON_RESTRICTED;

      try
      {
         if (fContext instanceof DocumentTemplateContext)
         {
            DocumentTemplateContext templateContext = (DocumentTemplateContext)fContext;
            Document document = templateContext.getDocument();

            String content = document.get(fRegion.getOffset(), fRegion.getLength());
            if (fTemplate.getName().startsWith(content))
               base += R_CASE;
            if (fTemplate.getName().equalsIgnoreCase(content))
               base += R_EXACT_NAME;
            if (fContext instanceof JavaDocContext)
               base += R_INLINE_TAG;
         }
      }
      catch (BadLocationException e)
      {
         // ignore - not a case sensitive match then
      }

      // see CompletionProposalCollector.computeRelevance
      // just under keywords, but better than packages
      final int TEMPLATE_RELEVANCE = 1;
      return base * 16 + TEMPLATE_RELEVANCE;
   }

   /**
    * Returns the template of this proposal.
    * 
    * @return the template of this proposal
    * @since 3.1
    */
   public final Template getTemplate()
   {
      return fTemplate;
   }

   /**
    * Returns the context in which the template was requested.
    * 
    * @return the context in which the template was requested
    * @since 3.1
    */
   protected final TemplateContext getContext()
   {
      return fContext;
   }

   /**
    * {@inheritDoc}
    * 
    */
   public final void apply(Document document)
   {
      apply(document, (char)0, 0, fRegion.getOffset());
   }

   /*
    * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#apply(org.eclipse.jface.text.ITextViewer, char, int,
    * int)
    */
   public void apply(Document document, char trigger, int stateMask, int offset)
   {

      try
      {
         fContext.setReadOnly(false);
         int start;
         TemplateBuffer templateBuffer;
         try
         {
            // beginCompoundChange(viewer);

            int oldReplaceOffset = getReplaceOffset();
            try
            {
               // this may already modify the document (e.g. add imports)
               templateBuffer = fContext.evaluate(fTemplate);
            }
            catch (TemplateException e1)
            {
               fSelectedRegion = fRegion;
               return;
            }

            start = getReplaceOffset();
            int shift = start - oldReplaceOffset;
            int end = Math.max(getReplaceEndOffset(), offset + shift);

            // insert template string
            if (end > document.getLength())
               end = offset;
            String templateString = templateBuffer.getString();
            document.replace(start, end - start, templateString);
            cursorPosition = getCaretOffset(templateBuffer);
         }
         finally
         {
            // endCompoundChange(viewer);
         }

         // TODO
         // translate positions
         // LinkedModeModel model= new LinkedModeModel();
         // TemplateVariable[] variables= templateBuffer.getVariables();
         //
         // MultiVariableGuess guess= fContext instanceof CompilationUnitContext ? ((CompilationUnitContext)
         // fContext).getMultiVariableGuess() : null;
         //
         // boolean hasPositions= false;
         // for (int i= 0; i != variables.length; i++) {
         // TemplateVariable variable= variables[i];
         //
         // if (variable.isUnambiguous())
         // continue;
         //
         // LinkedPositionGroup group= new LinkedPositionGroup();
         //
         // int[] offsets= variable.getOffsets();
         // int length= variable.getLength();
         //
         // LinkedPosition first;
         // if (guess != null && variable instanceof MultiVariable) {
         // first= new VariablePosition(document, offsets[0] + start, length, guess, (MultiVariable) variable);
         // guess.addSlave((VariablePosition) first);
         // } else {
         // String[] values= variable.getValues();
         // ICompletionProposal[] proposals= new ICompletionProposal[values.length];
         // for (int j= 0; j < values.length; j++) {
         // ensurePositionCategoryInstalled(document, model);
         // Position pos= new Position(offsets[0] + start, length);
         // document.addPosition(getCategory(), pos);
         // proposals[j]= new PositionBasedCompletionProposal(values[j], pos, length);
         // }
         //
         // if (proposals.length > 1)
         // first= new ProposalPosition(document, offsets[0] + start, length, proposals);
         // else
         // first= new LinkedPosition(document, offsets[0] + start, length);
         // }
         //
         // for (int j= 0; j != offsets.length; j++)
         // if (j == 0)
         // group.addPosition(first);
         // else
         // group.addPosition(new LinkedPosition(document, offsets[j] + start, length));
         //
         // model.addGroup(group);
         // hasPositions= true;
         // }
         //
         // if (hasPositions) {
         // model.forceInstall();
         // JavaEditor editor= getJavaEditor();
         // if (editor != null) {
         // model.addLinkingListener(new EditorHighlightingSynchronizer(editor));
         // }
         //
         // LinkedModeUI ui= new EditorLinkedModeUI(model, viewer);
         // ui.setExitPosition(viewer, getCaretOffset(templateBuffer) + start, 0, Integer.MAX_VALUE);
         // ui.enter();
         //
         // fSelectedRegion= ui.getSelectedRegion();
         // } else {
          fSelectedRegion= new RegionImpl(getCaretOffset(templateBuffer) + start, 0);
         // }

      }
      catch (BadLocationException e)
      {
         // openErrorDialog(viewer.getTextWidget().getShell(), e);
         fSelectedRegion = fRegion;
      }

   }

   // private void endCompoundChange(ITextViewer viewer) {
   // if (viewer instanceof ITextViewerExtension) {
   // ITextViewerExtension extension= (ITextViewerExtension) viewer;
   // IRewriteTarget target= extension.getRewriteTarget();
   // target.endCompoundChange();
   // }
   // }
   //
   // private void beginCompoundChange(ITextViewer viewer) {
   // if (viewer instanceof ITextViewerExtension) {
   // ITextViewerExtension extension= (ITextViewerExtension) viewer;
   // IRewriteTarget target= extension.getRewriteTarget();
   // target.beginCompoundChange();
   // }
   // }

   // /**
   // * Returns the currently active java editor, or <code>null</code> if it
   // * cannot be determined.
   // *
   // * @return the currently active java editor, or <code>null</code>
   // */
   // private JavaEditor getJavaEditor() {
   // IEditorPart part= JavaPlugin.getActivePage().getActiveEditor();
   // if (part instanceof JavaEditor)
   // return (JavaEditor) part;
   // else
   // return null;
   // }

   // private void ensurePositionCategoryInstalled(final IDocument document, LinkedModeModel model) {
   // if (!document.containsPositionCategory(getCategory())) {
   // document.addPositionCategory(getCategory());
   // fUpdater= new InclusivePositionUpdater(getCategory());
   // document.addPositionUpdater(fUpdater);
   //
   // model.addLinkingListener(new ILinkedModeListener() {
   //
   // /*
   // * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.eclipse.jface.text.link.LinkedModeModel, int)
   // */
   // public void left(LinkedModeModel environment, int flags) {
   // ensurePositionCategoryRemoved(document);
   // }
   //
   // public void suspend(LinkedModeModel environment) {}
   // public void resume(LinkedModeModel environment, int flags) {}
   // });
   // }
   // }

   private void ensurePositionCategoryRemoved(Document document)
   {
      if (document.containsPositionCategory(getCategory()))
      {
         try
         {
            document.removePositionCategory(getCategory());
         }
         catch (BadPositionCategoryException e)
         {
            // ignore
         }
         document.removePositionUpdater(fUpdater);
      }
   }

   private String getCategory()
   {
      return "TemplateProposalCategory_" + toString(); //$NON-NLS-1$
   }

   private int getCaretOffset(TemplateBuffer buffer)
   {

      TemplateVariable[] variables = buffer.getVariables();
      for (int i = 0; i != variables.length; i++)
      {
         TemplateVariable variable = variables[i];
         if (variable.getType().equals(GlobalTemplateVariables.Cursor.NAME))
            return variable.getOffsets()[0];
      }

      return buffer.getString().length();
   }

   /**
    * Returns the offset of the range in the document that will be replaced by applying this template.
    * 
    * @return the offset of the range in the document that will be replaced by applying this template
    */
   protected final int getReplaceOffset()
   {
      int start;
      if (fContext instanceof DocumentTemplateContext)
      {
         DocumentTemplateContext docContext = (DocumentTemplateContext)fContext;
         start = docContext.getStart();
      }
      else
      {
         start = fRegion.getOffset();
      }
      return start;
   }

   /**
    * Returns the end offset of the range in the document that will be replaced by applying this template.
    * 
    * @return the end offset of the range in the document that will be replaced by applying this template
    */
   protected final int getReplaceEndOffset()
   {
      int end;
      if (fContext instanceof DocumentTemplateContext)
      {
         DocumentTemplateContext docContext = (DocumentTemplateContext)fContext;
         end = docContext.getEnd();
      }
      else
      {
         end = fRegion.getOffset() + fRegion.getLength();
      }
      return end;
   }

   /*
    * @see ICompletionProposal#getSelection(IDocument)
    */
   public Region getSelection(Document document)
   {
      return new RegionImpl(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
   }

   /*
    * @see ICompletionProposal#getAdditionalProposalInfo()
    */
   public Widget getAdditionalProposalInfo()
   {
      try
      {
         fContext.setReadOnly(true);
         TemplateBuffer templateBuffer;
         try
         {
            templateBuffer = fContext.evaluate(fTemplate);
         }
         catch (TemplateException e)
         {
            return null;
         }

         Document document = new DocumentImpl(templateBuffer.getString());
         // TODO
         // IndentUtil.indentLines(document, new LineRange(0, document.getNumberOfLines()), null, null);
         Widget w = new SimplePanel();
         w.setSize("100%", "100%");
         w.getElement().getStyle().setOverflow(Overflow.AUTO);
         w.getElement().setInnerHTML("<pre>" + document.get() + "</pre>");
         return w;

      }
      catch (BadLocationException e)
      {
         //         handleException(JavaPlugin.getActiveWorkbenchShell(), new CoreException(new Status(IStatus.ERROR, JavaPlugin.getPluginId(), IStatus.OK, "", e))); //$NON-NLS-1$
         return null;
      }
   }

   /*
    * @see ICompletionProposal#getDisplayString()
    */
   public String getDisplayString()
   {
      return getStyledDisplayString().getString();
   }

   /*
    * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension6#getStyledDisplayString()
    * @since 3.4
    */
   public StyledString getStyledDisplayString()
   {
      if (fDisplayString == null)
      {
         fDisplayString = new StyledString(fTemplate.getName(), StyledString.COUNTER_STYLER);
         fDisplayString.append(" - ");
         fDisplayString.append(fTemplate.getDescription(), StyledString.QUALIFIER_STYLER);
      }
      return fDisplayString;
   }

   public void setDisplayString(StyledString displayString)
   {
      fDisplayString = displayString;
   }

   /*
    * @see ICompletionProposal#getImage()
    */
   public Image getImage()
   {
      return fImage;
   }

//   /*
//    * @see ICompletionProposal#getContextInformation()
//    */
//   public ContextInformation getContextInformation()
//   {
//      return null;
//   }

   // private void openErrorDialog(Shell shell, Exception e) {
   // MessageDialog.openError(shell, TemplateContentAssistMessages.TemplateEvaluator_error_title, e.getMessage());
   // }
   //
   // private void handleException(Shell shell, CoreException e) {
   // ExceptionHandler.handle(e, shell, TemplateContentAssistMessages.TemplateEvaluator_error_title, null);
   // }

   /*
    * @see IJavaCompletionProposal#getRelevance()
    */
   public int getRelevance()
   {
      return fRelevance;
   }

   public void setRelevance(int relevance)
   {
      fRelevance = relevance;
   }

   // /*
   // * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getInformationControlCreator()
   // */
   // public IInformationControlCreator getInformationControlCreator() {
   // int orientation;
   // IEditorPart editor= getJavaEditor();
   // if (editor instanceof IWorkbenchPartOrientation)
   // orientation= ((IWorkbenchPartOrientation)editor).getOrientation();
   // else
   // orientation= SWT.LEFT_TO_RIGHT;
   // return new TemplateInformationControlCreator(orientation);
   // }

   // /*
   // * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#selected(org.eclipse.jface.text.ITextViewer,
   // boolean)
   // */
   // public void selected(ITextViewer viewer, boolean smartToggle) {
   // }
   //
   // /*
   // * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#unselected(org.eclipse.jface.text.ITextViewer)
   // */
   // public void unselected(ITextViewer viewer) {
   // }

   /*
    * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension2#validate(org.eclipse.jface.text.IDocument, int,
    * org.eclipse.jface.text.DocumentEvent)
    */
   public boolean validate(Document document, int offset, DocumentEvent event)
   {
      try
      {
         int replaceOffset = getReplaceOffset();
         if (offset >= replaceOffset)
         {
            String content = document.get(replaceOffset, offset - replaceOffset);
            String templateName = fTemplate.getName().toLowerCase();
            boolean valid = templateName.startsWith(content.toLowerCase());
            if (!valid && fContext instanceof JavaDocContext && templateName.startsWith("<")) { //$NON-NLS-1$
               valid = templateName.startsWith(content.toLowerCase(), 1);
            }
            return valid;
         }
      }
      catch (BadLocationException e)
      {
         // concurrent modification - ignore
      }
      return false;
   }

   /*
    * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getReplacementString()
    */
   public CharSequence getPrefixCompletionText(Document document, int completionOffset)
   {
      // bug 114360 - don't make selection templates prefix-completable
      if (isSelectionTemplate())
         return ""; //$NON-NLS-1$
      return fTemplate.getName();
   }

   /*
    * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension3#getReplacementOffset()
    */
   public int getPrefixCompletionStart(Document document, int completionOffset)
   {
      return getReplaceOffset();
   }

   /*
    * @see org.eclipse.jface.text.contentassist.ICompletionProposalExtension4#isAutoInsertable()
    */
   public boolean isAutoInsertable()
   {
      if (isSelectionTemplate())
         return false;
      return fTemplate.isAutoInsertable();
   }

   /**
    * Returns <code>true</code> if the proposal has a selection, e.g. will wrap some code.
    * 
    * @return <code>true</code> if the proposals completion length is non zero
    * @since 3.2
    */
   private boolean isSelectionTemplate()
   {
      if (fContext instanceof DocumentTemplateContext)
      {
         DocumentTemplateContext ctx = (DocumentTemplateContext)fContext;
         if (ctx.getCompletionLength() > 0)
            return true;
      }
      return false;
   }

   public int getCursorPosition()
   {
      return cursorPosition;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#apply(org.exoplatform.ide.editor.text.IDocument, char, int)
    */
   @Override
   public void apply(Document document, char trigger, int offset)
   {
      apply(document);
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#isValidFor(org.exoplatform.ide.editor.text.IDocument, int)
    */
   @Override
   public boolean isValidFor(Document document, int offset)
   {
      return false;
   }

   /**
    * @see org.exoplatform.ide.editor.api.contentassist.CompletionProposal#getTriggerCharacters()
    */
   @Override
   public char[] getTriggerCharacters()
   {
      return null;
   }

}
