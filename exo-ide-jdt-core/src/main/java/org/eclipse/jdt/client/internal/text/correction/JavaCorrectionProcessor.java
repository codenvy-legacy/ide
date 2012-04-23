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
package org.eclipse.jdt.client.internal.text.correction;

import org.eclipse.jdt.client.codeassistant.CompletionProposalComparator;
import org.eclipse.jdt.client.codeassistant.api.ICompletionProposal;
import org.eclipse.jdt.client.codeassistant.api.IInvocationContext;
import org.eclipse.jdt.client.codeassistant.api.IJavaCompletionProposal;
import org.eclipse.jdt.client.codeassistant.api.IProblemLocation;
import org.eclipse.jdt.client.codeassistant.api.IQuickFixProcessor;
import org.eclipse.jdt.client.core.JavaCore;
import org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jdt.client.internal.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jdt.client.runtime.IStatus;
import org.eclipse.jdt.client.runtime.MultiStatus;
import org.eclipse.jdt.client.runtime.Status;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaCorrectionProcessor
{

   /*
    * @see IContentAssistProcessor#computeCompletionProposals(ITextViewer, int)
    */
   public ICompletionProposal[] computeQuickAssistProposals(IQuickAssistInvocationContext quickAssistContext) {
//      ISourceViewer viewer= quickAssistContext.getSourceViewer();
//      int documentOffset= quickAssistContext.getOffset();
//
//      IEditorPart part= fAssistant.getEditor();
//
//      ICompilationUnit cu= JavaUI.getWorkingCopyManager().getWorkingCopy(part.getEditorInput());
//      IAnnotationModel model= JavaUI.getDocumentProvider().getAnnotationModel(part.getEditorInput());
//
      AssistContext context= null;
//      if (cu != null) {
//         int length= viewer != null ? viewer.getSelectedRange().y : 0;
//         context= new AssistContext(cu, viewer, part, documentOffset, length);
//      }
//      
//      Annotation[] annotations= fAssistant.getAnnotationsAtOffset();
//
//      fErrorMessage= null;

      ICompletionProposal[] res= null;
      if (model != null && context != null && annotations != null) {
         ArrayList<IJavaCompletionProposal> proposals= new ArrayList<IJavaCompletionProposal>(10);
         IStatus status= collectProposals(context, model, annotations, true, !fAssistant.isUpdatedOffset(), proposals);
         res= proposals.toArray(new ICompletionProposal[proposals.size()]);
         if (!status.isOK()) {
            fErrorMessage= status.getMessage();
            JavaPlugin.log(status);
         }
      }

      if (res == null || res.length == 0) {
         return new ICompletionProposal[0];// { new ChangeCorrectionProposal(CorrectionMessages.NoCorrectionProposal_description, new NullChange(""), 0, null) }; //$NON-NLS-1$
      }
      if (res.length > 1) {
         Arrays.sort(res, new CompletionProposalComparator());
      }
      return res;
   }
   
   public static IStatus collectProposals(IInvocationContext context, IAnnotationModel model, Annotation[] annotations, boolean addQuickFixes, boolean addQuickAssists, Collection<IJavaCompletionProposal> proposals) {
      ArrayList<ProblemLocation> problems= new ArrayList<ProblemLocation>();
//
//      // collect problem locations and corrections from marker annotations
//      for (int i= 0; i < annotations.length; i++) {
//         Annotation curr= annotations[i];
//         ProblemLocation problemLocation= null;
//         if (curr instanceof IJavaAnnotation) {
//            problemLocation= getProblemLocation((IJavaAnnotation) curr, model);
//            if (problemLocation != null) {
//               problems.add(problemLocation);
//            }
//         }
//         if (problemLocation == null && addQuickFixes && curr instanceof SimpleMarkerAnnotation) {
//            collectMarkerProposals((SimpleMarkerAnnotation) curr, proposals);
//         }
//      }
      MultiStatus resStatus= null;
//
      IProblemLocation[] problemLocations= problems.toArray(new IProblemLocation[problems.size()]);
      if (addQuickFixes) {
         IStatus status= collectCorrections(context, problemLocations, proposals);
         if (!status.isOK()) {
            resStatus= new MultiStatus(JavaCore.PLUGIN_ID, IStatus.ERROR, CorrectionMessages.JavaCorrectionProcessor_error_quickfix_message, null);
            resStatus.add(status);
         }
      }
      if (addQuickAssists) {
         IStatus status= collectAssists(context, problemLocations, proposals);
         if (!status.isOK()) {
            if (resStatus == null) {
               resStatus= new MultiStatus(JavaCore.PLUGIN_ID, IStatus.ERROR, CorrectionMessages.JavaCorrectionProcessor_error_quickassist_message, null);
            }
            resStatus.add(status);
         }
      }
      if (resStatus != null) {
         return resStatus;
      }
      return Status.OK_STATUS;
   }
   

   public static IStatus collectCorrections(IInvocationContext context, IProblemLocation[] locations, Collection<IJavaCompletionProposal> proposals) {
      IQuickFixProcessor curr= (IQuickFixProcessor) desc.getProcessor(context.getCompilationUnit(), IQuickFixProcessor.class);
      if (curr != null) {
         IJavaCompletionProposal[] res= curr.getCorrections(context, locations);
         if (res != null) {
            for (int k= 0; k < res.length; k++) {
               proposals.add(res[k]);
            }
         }
      }
//      ContributedProcessorDescriptor[] processors= getCorrectionProcessors();
//      SafeCorrectionCollector collector= new SafeCorrectionCollector(context, proposals);
//      for (int i= 0; i < processors.length; i++) {
//         ContributedProcessorDescriptor curr= processors[i];
//         IProblemLocation[] handled= getHandledProblems(locations, curr);
//         if (handled != null) {
//            collector.setProblemLocations(handled);
//            collector.process(curr);
//         }
//      }
//      return collector.getStatus();
   }
   
   public static IStatus collectAssists(IInvocationContext context, IProblemLocation[] locations, Collection<IJavaCompletionProposal> proposals) {
      IQuickAssistProcessor curr= (IQuickAssistProcessor) desc.getProcessor(context.getCompilationUnit(), IQuickAssistProcessor.class);
      if (curr != null) {
         IJavaCompletionProposal[] res= curr.getAssists(context, locations);
         if (res != null) {
            for (int k= 0; k < res.length; k++) {
               proposals.add(res[k]);
            }
         }
      }
//      ContributedProcessorDescriptor[] processors= getAssistProcessors();
//      SafeAssistCollector collector= new SafeAssistCollector(context, locations, proposals);
//      collector.process(processors);
//
//      return collector.getStatus();
   }
   
}
