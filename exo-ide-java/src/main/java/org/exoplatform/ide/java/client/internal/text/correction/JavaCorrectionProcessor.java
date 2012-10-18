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
package org.exoplatform.ide.java.client.internal.text.correction;

import org.exoplatform.ide.java.client.MultiStatus;
import org.exoplatform.ide.java.client.codeassistant.CompletionProposalComparator;
import org.exoplatform.ide.java.client.codeassistant.api.IProblemLocation;
import org.exoplatform.ide.java.client.codeassistant.api.JavaCompletionProposal;
import org.exoplatform.ide.java.client.core.JavaCore;
import org.exoplatform.ide.java.client.quickassist.api.InvocationContext;
import org.exoplatform.ide.java.client.quickassist.api.QuickAssistProcessor;
import org.exoplatform.ide.java.client.quickassist.api.QuickFixProcessor;
import org.exoplatform.ide.runtime.CoreException;
import org.exoplatform.ide.runtime.IStatus;
import org.exoplatform.ide.runtime.Status;
import org.exoplatform.ide.texteditor.api.codeassistant.CompletionProposal;
import org.exoplatform.ide.texteditor.api.quickassist.QuickAssistInvocationContext;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaCorrectionProcessor
{

   private static QuickFixProcessor fixProcessor;

   private static QuickAssistProcessor[] assistProcessors;

   /**
    * 
    */
   public JavaCorrectionProcessor()
   {
      if (fixProcessor == null)
      {
         fixProcessor = new QuickFixProcessorImpl();
         assistProcessors = new QuickAssistProcessor[]{new QuickAssistProcessorImpl(), new AdvancedQuickAssistProcessor()};
      }
   }

   /*
    * @see IContentAssistProcessor#computeCompletionProposals(ITextViewer, int)
    */
   public CompletionProposal[] computeQuickAssistProposals(QuickAssistInvocationContext quickAssistContext)
      throws CoreException
   {

      //      ISourceViewer viewer= quickAssistContext.getSourceViewer();
      //      int documentOffset= quickAssistContext.getOffset();
      //
      //      IEditorPart part= fAssistant.getEditor();
      //
      //      ICompilationUnit cu= JavaUI.getWorkingCopyManager().getWorkingCopy(part.getEditorInput());
      //      IAnnotationModel model= JavaUI.getDocumentProvider().getAnnotationModel(part.getEditorInput());
      //
      AssistContext context =
         new AssistContext(quickAssistContext.getTextEditor().getDocument(), quickAssistContext.getOffset(),
            quickAssistContext.getLength());
      //      if (cu != null) {
      //         int length= viewer != null ? viewer.getSelectedRange().y : 0;
      //         context= new AssistContext(cu, viewer, part, documentOffset, length);
      //      }
      //      
      //TODO
      //            Annotation[] annotations= fAssistant.getAnnotationsAtOffset();
      //
      //      fErrorMessage= null;

      CompletionProposal[] res = null;
//      if (context != null)
//      {
//         ArrayList<JavaCompletionProposal> proposals = new ArrayList<JavaCompletionProposal>(10);
//         IStatus status =
//            collectProposals(context, true, !quickAssistContext.isUpdatedOffset(),
//               quickAssistContext.getProblemsAtOffset(), proposals);
//         res = proposals.toArray(new CompletionProposal[proposals.size()]);
//         if (!status.isOK())
//         {
//            throw new CoreException(status);
//            //            fErrorMessage = status.getMessage();
//            //            JavaPlugin.log(status);
//            //TODO;
//         }
//      }

      if (res == null || res.length == 0)
      {
         return new CompletionProposal[0];// { new ChangeCorrectionProposal(CorrectionMessages.NoCorrectionProposal_description, new NullChange(""), 0, null) }; //$NON-NLS-1$
      }
      if (res.length > 1)
      {
         Arrays.sort(res, new CompletionProposalComparator());
      }
      return res;
   }

   public static IStatus collectProposals(InvocationContext context, boolean addQuickFixes, boolean addQuickAssists,
      IProblemLocation[] problemLocations, Collection<JavaCompletionProposal> proposals) throws CoreException
   {
      MultiStatus resStatus = null;
      //
      if (addQuickFixes)
      {
         IStatus status = collectCorrections(context, problemLocations, proposals);
         if (!status.isOK())
         {
            resStatus =
               new MultiStatus(JavaCore.PLUGIN_ID, IStatus.ERROR,
                  CorrectionMessages.INSTANCE.JavaCorrectionProcessor_error_quickfix_message(), null);
            resStatus.add(status);
         }
      }
      if (addQuickAssists)
      {
         IStatus status = collectAssists(context, problemLocations, proposals);
         if (!status.isOK())
         {
            if (resStatus == null)
            {
               resStatus =
                  new MultiStatus(JavaCore.PLUGIN_ID, IStatus.ERROR,
                     CorrectionMessages.INSTANCE.JavaCorrectionProcessor_error_quickassist_message(), null);
            }
            resStatus.add(status);
         }
      }
      if (resStatus != null)
      {
         return resStatus;
      }
      return Status.OK_STATUS;
   }

   public static IStatus collectCorrections(InvocationContext context, IProblemLocation[] locations,
      Collection<JavaCompletionProposal> proposals) throws CoreException
   {
      JavaCompletionProposal[] res;
      res = fixProcessor.getCorrections(context, locations);
      if (res != null)
      {
         for (int k = 0; k < res.length; k++)
         {
            proposals.add(res[k]);
         }
      }
      return Status.OK_STATUS;
   }

   public static IStatus collectAssists(InvocationContext context, IProblemLocation[] locations,
      Collection<JavaCompletionProposal> proposals) throws CoreException
   {

      for (QuickAssistProcessor curr : assistProcessors)
      {
         JavaCompletionProposal[] res;
         res = curr.getAssists(context, locations);
         if (res != null)
         {
            for (int k = 0; k < res.length; k++)
            {
               proposals.add(res[k]);
            }
         }
      }
      return Status.OK_STATUS;

   }

   /**
    * @param annot
    * @return
    */
   public static boolean hasCorrections(IProblemLocation annot)
   {
      return fixProcessor.hasCorrections(annot.getProblemId());
   }

   /**
    * @param context
    * @return
    * @throws CoreException 
    */
   public static boolean hasAssists(InvocationContext context) throws CoreException
   {
      for (QuickAssistProcessor curr : assistProcessors)
      {
         if(curr.hasAssists(context))
          return true;
      }
      
      return false;
   }

}
