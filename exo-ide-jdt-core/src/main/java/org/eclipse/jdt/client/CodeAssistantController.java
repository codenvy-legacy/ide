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

import org.eclipse.jdt.client.codeassistant.ui.CodeAssitantForm;
import org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler;
import org.eclipse.jdt.client.codeassistant.ui.ProposalWidget;
import org.eclipse.jdt.client.codeassistant.ui.ProposalWidgetFactory;
import org.eclipse.jdt.client.compiler.batch.CompilationUnit;
import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.CompletionRequestor;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.event.RunCodeAssistantEvent;
import org.eclipse.jdt.client.event.RunCodeAssistantHandler;
import org.eclipse.jdt.client.internal.codeassist.CompletionEngine;
import org.eclipse.jdt.client.runtime.IProgressMonitor;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 24, 2012 5:11:46 PM evgen $
 * 
 */
public class CodeAssistantController implements RunCodeAssistantHandler, EditorActiveFileChangedHandler,
   ProposalSelectedHandler
{

   private FileModel currentFile;

   private CodeMirror currentEditor;

   private String afterToken;

   private String tokenToComplete;

   private String beforeToken;

   private int currentLineNumber;

   /**
    * 
    */
   public CodeAssistantController()
   {
      IDE.addHandler(RunCodeAssistantEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.eclipse.jdt.client.event.RunCodeAssistantHandler#onRunCodeAssistant(org.eclipse.jdt.client.event.RunCodeAssistantEvent)
    */
   @Override
   public void onRunCodeAssistant(RunCodeAssistantEvent event)
   {
      if (currentFile == null || currentEditor == null)
         return;
      CodeAssistantRequestor requestor = new CodeAssistantRequestor();
      requestor
         .setAllowsRequiredProposals(CompletionProposal.CONSTRUCTOR_INVOCATION, CompletionProposal.TYPE_REF, true);
      char[] fileContent = currentFile.getContent().toCharArray();
      CompletionEngine e =
         new CompletionEngine(new DummyNameEnvirement(currentFile.getProject().getId()), requestor,
            JavaCore.getOptions(), new IProgressMonitor()
            {
               private final static int TIMEOUT = 10000; // ms

               private long endTime;

               public void beginTask(String name, int totalWork)
               {
                  endTime = System.currentTimeMillis() + TIMEOUT;
               }

               public boolean isCanceled()
               {
                  return endTime <= System.currentTimeMillis();
               }

               @Override
               public void done()
               {
                  // TODO Auto-generated method stub

               }

               @Override
               public void internalWorked(double work)
               {
                  // TODO Auto-generated method stub

               }

               @Override
               public void setCanceled(boolean value)
               {
                  // TODO Auto-generated method stub

               }

               @Override
               public void setTaskName(String name)
               {
                  // TODO Auto-generated method stub

               }

               @Override
               public void subTask(String name)
               {
                  // TODO Auto-generated method stub

               }

               @Override
               public void worked(int work)
               {
                  // TODO Auto-generated method stub

               }
            });

      e.complete(
         new CompilationUnit(fileContent, currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')),
            "UTF-8"),
         getCompletionPosition(currentFile.getContent(), currentEditor.getCursorRow(), currentEditor.getCursorCol()), 0);

      currentLineNumber = currentEditor.getCursorRow();
      String lineContent = currentEditor.getLineContent(currentLineNumber);
      String subToken = lineContent.substring(0, currentEditor.getCursorCol() - 1);
      afterToken = lineContent.substring(currentEditor.getCursorCol() - 1);

      String token = "";
      if (!subToken.endsWith(" "))
      {
         String[] split = subToken.split("[ /+=!<>(){}\\[\\]?|&:\",'\\-#;]+");

         if (split.length != 0)
         {
            token = split[split.length - 1];
         }
      }
      if (token.contains("."))
      {
         String varToken = token.substring(0, token.lastIndexOf('.'));
         tokenToComplete = token.substring(token.lastIndexOf('.') + 1);
         beforeToken = subToken.substring(0, subToken.lastIndexOf(varToken) + varToken.length() + 1);
      }
      else
      {
         beforeToken = subToken.substring(0, subToken.lastIndexOf(token));
         tokenToComplete = token;
      }

      int posX = currentEditor.getCursorOffsetX() - tokenToComplete.length() * 8 + 8;
      int posY = currentEditor.getCursorOffsetY() + 4;
      Collections.sort(requestor.proposals, comparator);
      new CodeAssitantForm(posX, posY, tokenToComplete, requestor.proposals, new ProposalWidgetFactory(), this);
   }

   private Comparator<CompletionProposal> comparator = new Comparator<CompletionProposal>()
   {

      @Override
      public int compare(CompletionProposal o1, CompletionProposal o2)
      {

         if (o1.getRelevance() > o2.getRelevance())
            return -1;
         else if (o1.getRelevance() < o2.getRelevance())
            return 1;
         else
            return 0;
      }
   };

   public static class CodeAssistantRequestor extends CompletionRequestor
   {

      private List<CompletionProposal> proposals = new ArrayList<CompletionProposal>();

      /**
       * @see org.eclipse.jdt.client.core.CompletionRequestor#accept(org.eclipse.jdt.client.core.CompletionProposal)
       */
      @Override
      public void accept(CompletionProposal proposal)
      {
         proposals.add(proposal);
      }

      /**
       * @see org.eclipse.jdt.client.core.CompletionRequestor#completionFailure(org.eclipse.jdt.client.core.compiler.IProblem)
       */
      @Override
      public void completionFailure(IProblem problem)
      {
         IDE.fireEvent(new OutputEvent(problem.getMessage(), Type.ERROR));
         super.completionFailure(problem);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      currentFile = event.getFile();
      if (event.getEditor() instanceof CodeMirror)
         currentEditor = (CodeMirror)event.getEditor();
      else
         currentEditor = null;
   }

   private int getCompletionPosition(String content, int row, int col)
   {
      String[] strings = content.split("\n");
      if (strings.length < row)
         IDE.fireEvent(new OutputEvent("content length less than parameter 'row'", Type.ERROR));
      int pos = 0;

      for (int i = 0; i < row - 1; i++)
      {
         pos += strings[i].length() + 1;
      }
      return pos + col - 1;
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onStringSelected(java.lang.String)
    */
   @Override
   public void onStringSelected(String value)
   {
      currentEditor.replaceTextAtCurrentLine(beforeToken + value + afterToken, (beforeToken + value).length());
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onTokenSelected(org.eclipse.jdt.client.codeassistant.ui.ProposalWidget)
    */
   @Override
   public void onTokenSelected(ProposalWidget value)
   {
      String beforeCursor = beforeToken + String.valueOf(value.getProposal().getCompletion());
      int cursorPosition = 1;
      if (beforeCursor.contains("("))
         cursorPosition = beforeCursor.lastIndexOf('(') + 1;
      else
         cursorPosition = beforeCursor.length();
      currentEditor.replaceTextAtCurrentLine(beforeCursor + afterToken, cursorPosition);
   }

   /**
    * @see org.eclipse.jdt.client.codeassistant.ui.ProposalSelectedHandler#onCancelAutoComplete()
    */
   @Override
   public void onCancelAutoComplete()
   {
      currentEditor.setFocus();
   }

}
