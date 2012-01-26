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

import org.eclipse.jdt.client.compiler.batch.CompilationUnit;
import org.eclipse.jdt.client.core.CompletionProposal;
import org.eclipse.jdt.client.core.CompletionRequestor;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.core.compiler.IProblem;
import org.eclipse.jdt.client.event.RunCodeAssistantEvent;
import org.eclipse.jdt.client.event.RunCodeAssistantHandler;
import org.eclipse.jdt.client.internal.codeassist.CompletionEngine;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}: Jan 24, 2012 5:11:46 PM evgen $
 * 
 */
public class CodeAssistantController implements RunCodeAssistantHandler, EditorActiveFileChangedHandler
{

   private FileModel currentFile;

   private Editor currentEditor;

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
      if (currentFile == null)
         return;
      CodeAssistantRequestor requestor = new CodeAssistantRequestor();

      char[] fileContent = currentFile.getContent().toCharArray();
      CompletionEngine e =
         new CompletionEngine(new DummyNameEnvirement(currentFile.getProject().getId()), requestor,
            JavaCore.getOptions(), null);
      e.complete(
         new CompilationUnit(fileContent, currentFile.getName().substring(0, currentFile.getName().lastIndexOf('.')),
            "UTF-8"),
         getCompletionPosition(currentFile.getContent(), currentEditor.getCursorRow(), currentEditor.getCursorCol()), 0);

      StringBuilder b = new StringBuilder("CodeAssistant proposals: <br/><pre>");

      for (CompletionProposal proposal : requestor.proposals)
      {
         b.append(proposal.getCompletion());
         if (proposal.getRequiredProposals() != null)
         {
            b.append(" required -- ");
            for (CompletionProposal req : proposal.getRequiredProposals())
            {
               b.append(req.getCompletion());
            }
         }
         b.append("<br>");
      }
      b.append("</pre>");
      IDE.fireEvent(new OutputEvent(b.toString()));
   }

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
      currentEditor = event.getEditor();
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

}
