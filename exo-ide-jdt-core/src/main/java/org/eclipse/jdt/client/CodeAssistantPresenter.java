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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.IsWidget;

import org.eclipse.jdt.client.codeassistant.AbstractJavaCompletionProposal;
import org.eclipse.jdt.client.codeassistant.CompletionProposalCollector;
import org.eclipse.jdt.client.codeassistant.FillArgumentNamesCompletionProposalCollector;
import org.eclipse.jdt.client.codeassistant.JavaContentAssistInvocationContext;
import org.eclipse.jdt.client.codeassistant.LazyGenericTypeProposal;
import org.eclipse.jdt.client.codeassistant.TemplateCompletionProposalComputer;
import org.eclipse.jdt.client.codeassistant.api.IJavaCompletionProposal;
import org.eclipse.jdt.client.codeassistant.ui.CodeAssitantForm;
import org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler;
import org.eclipse.jdt.client.compiler.batch.CompilationUnit;
import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.IJavaElement;
import org.eclipse.jdt.client.core.IType;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.Signature;
import org.eclipse.jdt.client.core.dom.AST;
import org.eclipse.jdt.client.core.dom.ASTNode;
import org.eclipse.jdt.client.core.dom.ASTParser;
import org.eclipse.jdt.client.event.CancelParseEvent;
import org.eclipse.jdt.client.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.client.internal.compiler.flow.UnconditionalFlowInfo.AssertionFailedException;
import org.eclipse.jdt.client.runtime.IProgressMonitor;
import org.eclipse.jdt.client.templates.TemplateProposal;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantEvent;
import org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantHandler;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.keys.KeyHandler;
import org.exoplatform.ide.editor.text.BadLocationException;
import org.exoplatform.ide.editor.text.IDocument;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 24, 2012 5:11:46 PM evgen $
 */
public class CodeAssistantPresenter implements RunCodeAssistantHandler, EditorActiveFileChangedHandler,
   ProposalSelectedHandler, KeyHandler
{

   public interface Display extends IsWidget
   {

      void moveSelectionUp();

      void moveSelectionDown();

      void proposalSelected();

      void cancelCodeAssistant();

      void setNewProposals(IJavaCompletionProposal[] proposals);

   }

   /**
    * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
    * @version $Id: 2:09:49 PM 34360 2009-07-22 23:58:59Z evgen $
    * 
    */
   private static final class ProgressMonitor implements IProgressMonitor
   {
      private final static int TIMEOUT = 60000; // ms

      private long endTime;

      public void beginTask(String name, int totalWork)
      {
         endTime = System.currentTimeMillis() + TIMEOUT;
      }

      public boolean isCanceled()
      {
         return false;// endTime <= System.currentTimeMillis();
      }

      @Override
      public void done()
      {
      }

      @Override
      public void internalWorked(double work)
      {
      }

      @Override
      public void setCanceled(boolean value)
      {
      }

      @Override
      public void setTaskName(String name)
      {
      }

      @Override
      public void subTask(String name)
      {
      }

      @Override
      public void worked(int work)
      {
      }
   }

   private FileModel currentFile;

   private CodeMirror currentEditor;

   private int completionPosition;

   private TemplateCompletionProposalComputer templateCompletionProposalComputer =
      new TemplateCompletionProposalComputer();

   private HandlerRegistration keyHandler;

   private Display display;

   /**
    * 
    */
   public CodeAssistantPresenter()
   {
      IDE.addHandler(RunCodeAssistantEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /** @see org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantHandler#onRunCodeAssistant(org.exoplatform.ide.editor.api.codeassitant.RunCodeAssistantEvent) */
   @Override
   public void onRunCodeAssistant(RunCodeAssistantEvent event)
   {
      if (currentFile == null || currentEditor == null)
         return;
      IDE.fireEvent(new CancelParseEvent());
      GWT.runAsync(new RunAsyncCallback()
      {

         @Override
         public void onSuccess()
         {
            codecomplete();
         }

         @Override
         public void onFailure(Throwable reason)
         {
            IDE.fireEvent(new OutputEvent(reason.getMessage(), Type.ERROR));
         }
      });
   }

   private IJavaCompletionProposal[] createProposals(boolean useOldAST)
   {
      IDocument document = currentEditor.getDocument();
      if (!useOldAST)
      {
         ASTParser parser = ASTParser.newParser(AST.JLS3);
         parser.setSource(document.get().toCharArray());
         parser.setKind(ASTParser.K_COMPILATION_UNIT);
         parser.setUnitName(currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')));
         parser.setNameEnvironment(new NameEnvironment(currentFile.getProject().getId()));
         parser.setResolveBindings(true);
         ASTNode ast = parser.createAST(null);
         unit = (org.eclipse.jdt.client.core.dom.CompilationUnit)ast;
      }
      try
      {
         completionPosition =
            currentEditor.getDocument().getLineOffset(currentEditor.getCursorRow() - 1) + currentEditor.getCursorCol()
               - 1;
      }
      catch (BadLocationException e1)
      {
         e1.printStackTrace();
      }
      // unit.getPosition(currentEditor.getCursorRow(), currentEditor.getCursorCol() - 1);
      CompletionProposalCollector collector = createCollector(document);
      char[] fileContent = document.get().toCharArray();
      CompletionEngine e =
         new CompletionEngine(new NameEnvironment(currentFile.getProject().getId()), collector, JavaCore.getOptions(),
            new ProgressMonitor());
      try
      {
         e.complete(
            new CompilationUnit(fileContent,
               currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')), "UTF-8"),
            completionPosition, 0);

         IJavaCompletionProposal[] javaCompletionProposals = collector.getJavaCompletionProposals();
         List<IJavaCompletionProposal> types =
            new ArrayList<IJavaCompletionProposal>(Arrays.asList(javaCompletionProposals));
         if (types.size() > 0 && collector.getInvocationContext().computeIdentifierPrefix().length() == 0)
         {
            IType expectedType = collector.getInvocationContext().getExpectedType();
            if (expectedType != null)
            {
               // empty prefix completion - insert LRU types if known, but prune if they already occur in the core list

               // compute minmimum relevance and already proposed list
               int relevance = Integer.MAX_VALUE;
               Set<String> proposed = new HashSet<String>();
               for (Iterator<IJavaCompletionProposal> it = types.iterator(); it.hasNext();)
               {
                  AbstractJavaCompletionProposal p = (AbstractJavaCompletionProposal)it.next();
                  IJavaElement element = p.getJavaElement();
                  if (element instanceof IType)
                     proposed.add(((IType)element).getFullyQualifiedName());
                  relevance = Math.min(relevance, p.getRelevance());
               }

               // insert history types
               List<String> history =
                  JdtExtension.get().getContentAssistHistory().getHistory(expectedType.getFullyQualifiedName())
                     .getTypes();
               relevance -= history.size() + 1;
               for (Iterator<String> it = history.iterator(); it.hasNext();)
               {
                  String type = it.next();
                  if (proposed.contains(type))
                     continue;

                  IJavaCompletionProposal proposal =
                     createTypeProposal(relevance, type, collector.getInvocationContext());

                  if (proposal != null)
                     types.add(proposal);
                  relevance++;
               }
            }
         }

         List<IJavaCompletionProposal> templateProposals =
            templateCompletionProposalComputer.computeCompletionProposals(collector.getInvocationContext(), null);
         IJavaCompletionProposal[] array =
            templateProposals.toArray(new IJavaCompletionProposal[templateProposals.size()]);
         javaCompletionProposals = types.toArray(new IJavaCompletionProposal[0]);
         IJavaCompletionProposal[] proposals =
            new IJavaCompletionProposal[javaCompletionProposals.length + array.length];
         System.arraycopy(javaCompletionProposals, 0, proposals, 0, javaCompletionProposals.length);
         System.arraycopy(array, 0, proposals, javaCompletionProposals.length, array.length);

         Arrays.sort(proposals, comparator);
         return proposals;
      }
      catch (AssertionFailedException ex)
      {
         IDE.fireEvent(new OutputEvent(ex.getMessage(), Type.ERROR));

      }
      catch (Exception ex)
      {
         String st = ex.getClass().getName() + ": " + ex.getMessage();
         for (StackTraceElement ste : ex.getStackTrace())
            st += "\n" + ste.toString();
         IDE.fireEvent(new OutputEvent(st, Type.ERROR));
      }
      return new IJavaCompletionProposal[0];
   }

   private IJavaCompletionProposal createTypeProposal(int relevance, String fullyQualifiedType,
      JavaContentAssistInvocationContext context)
   {
      IType type = TypeInfoStorage.get().getTypeByFqn(fullyQualifiedType);

      if (type == null)
         return null;

      CompletionProposal proposal =
         CompletionProposal.create(CompletionProposal.TYPE_REF, context.getInvocationOffset());
      proposal.setCompletion(fullyQualifiedType.toCharArray());
      proposal.setDeclarationSignature(Signature.getQualifier(type.getFullyQualifiedName().toCharArray()));
      proposal.setFlags(type.getFlags());
      proposal.setRelevance(relevance);
      proposal.setReplaceRange(context.getInvocationOffset(), context.getInvocationOffset());
      proposal.setSignature(Signature.createTypeSignature(fullyQualifiedType, true).toCharArray());

      return new LazyGenericTypeProposal(proposal, context);

   }

   /**
    * @param document
    * @return
    */
   private CompletionProposalCollector createCollector(IDocument document)
   {
      CompletionProposalCollector collector =
         new FillArgumentNamesCompletionProposalCollector(unit, document, completionPosition, currentFile.getProject()
            .getId(), JdtExtension.DOC_CONTEXT);
      collector
         .setAllowsRequiredProposals(CompletionProposal.CONSTRUCTOR_INVOCATION, CompletionProposal.TYPE_REF, true);
      collector.setIgnored(CompletionProposal.ANNOTATION_ATTRIBUTE_REF, false);
      collector.setIgnored(CompletionProposal.ANONYMOUS_CLASS_DECLARATION, false);
      collector.setIgnored(CompletionProposal.ANONYMOUS_CLASS_CONSTRUCTOR_INVOCATION, false);
      collector.setIgnored(CompletionProposal.FIELD_REF, false);
      collector.setIgnored(CompletionProposal.FIELD_REF_WITH_CASTED_RECEIVER, false);
      collector.setIgnored(CompletionProposal.KEYWORD, false);
      collector.setIgnored(CompletionProposal.LABEL_REF, false);
      collector.setIgnored(CompletionProposal.LOCAL_VARIABLE_REF, false);
      collector.setIgnored(CompletionProposal.METHOD_DECLARATION, false);
      collector.setIgnored(CompletionProposal.METHOD_NAME_REFERENCE, false);
      collector.setIgnored(CompletionProposal.METHOD_REF, false);
      collector.setIgnored(CompletionProposal.CONSTRUCTOR_INVOCATION, false);
      collector.setIgnored(CompletionProposal.METHOD_REF_WITH_CASTED_RECEIVER, false);
      collector.setIgnored(CompletionProposal.PACKAGE_REF, false);
      collector.setIgnored(CompletionProposal.POTENTIAL_METHOD_DECLARATION, false);
      collector.setIgnored(CompletionProposal.VARIABLE_DECLARATION, false);
      collector.setIgnored(CompletionProposal.TYPE_REF, false);
      collector.setRequireExtendedContext(true);
      return collector;
   }

   /**
    * 
    */
   private void codecomplete()
   {

      int posX = currentEditor.getCursorOffsetX() + 8;
      int posY = currentEditor.getCursorOffsetY() + 22;
      keyHandler = currentEditor.addHandler(this);
      display = new CodeAssitantForm(posX, posY, createProposals(false), this);

   }

   private Comparator<IJavaCompletionProposal> comparator = new Comparator<IJavaCompletionProposal>()
   {

      @Override
      public int compare(IJavaCompletionProposal o1, IJavaCompletionProposal o2)
      {

         if (o1.getRelevance() > o2.getRelevance())
            return -1;
         else if (o1.getRelevance() < o2.getRelevance())
            return 1;
         else
            return 0;
      }
   };

   /** @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent) */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      currentFile = event.getFile();
      if (event.getEditor() instanceof CodeMirror)
         currentEditor = (CodeMirror)event.getEditor();
      else
         currentEditor = null;
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onTokenSelected(org.eclipse.jdt.client.codeassistant.ui.ProposalWidget) */
   @Override
   public void onTokenSelected(IJavaCompletionProposal proposal, boolean editorHasFocus)
   {
      try
      {
         IDocument document = currentEditor.getDocument();
         proposal.apply(document);
         int cursorPosition = completionPosition;
         int replacementOffset = 0;
         if (proposal instanceof AbstractJavaCompletionProposal)
         {
            AbstractJavaCompletionProposal proposal2 = (AbstractJavaCompletionProposal)proposal;
            cursorPosition = proposal2.getCursorPosition();
            replacementOffset = proposal2.getReplacementOffset();
         }
         else if (proposal instanceof TemplateProposal)
         {
            cursorPosition = ((TemplateProposal)proposal).getCursorPosition();
            replacementOffset = completionPosition;
         }
         String string = document.get(0, replacementOffset + cursorPosition);
         String[] split = string.split("\n");
         currentEditor.goToPosition(split.length, split[split.length - 1].length() + 1);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
      }
      finally
      {
         onCancelAutoComplete(editorHasFocus);
      }
   }

   /** @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onCancelAutoComplete() */
   @Override
   public void onCancelAutoComplete(boolean editorHasFocus)
   {
      if (!editorHasFocus)
         currentEditor.setFocus();
      keyHandler.removeHandler();
      display = null;
   }

   private Timer timer = new Timer()
   {

      @Override
      public void run()
      {
         IJavaCompletionProposal[] proposals = createProposals(true);
         if (proposals.length == 0)
            display.cancelCodeAssistant();
         else
            display.setNewProposals(proposals);
      }
   };

   private org.eclipse.jdt.client.core.dom.CompilationUnit unit;

   /**
    * @see org.exoplatform.ide.editor.keys.KeyHandler#handleEvent(com.google.gwt.user.client.Event)
    */
   @Override
   public boolean handleEvent(Event event)
   {
      switch (event.getKeyCode())
      {
         case KeyCodes.KEY_DOWN :
            display.moveSelectionDown();
            return true;

         case KeyCodes.KEY_UP :
            display.moveSelectionUp();
            return true;

         case KeyCodes.KEY_ENTER :
            display.proposalSelected();
            return true;

         case KeyCodes.KEY_ESCAPE :
            display.cancelCodeAssistant();
            return true;

         case KeyCodes.KEY_RIGHT :
            if (currentEditor.getCursorCol() + 1 > currentEditor.getLineContent(currentEditor.getCursorRow()).length())
               display.cancelCodeAssistant();
            else
               generateNewProposals();
            return false;

         case KeyCodes.KEY_LEFT :
            if (currentEditor.getCursorCol() - 1 <= 0)
               display.cancelCodeAssistant();
            else
               generateNewProposals();
            return false;

         default :
            generateNewProposals();
            return false;
      }
   }

   /**
    * 
    */
   private void generateNewProposals()
   {
      IDE.fireEvent(new CancelParseEvent());
      timer.cancel();
      timer.schedule(1000);
   }

}
