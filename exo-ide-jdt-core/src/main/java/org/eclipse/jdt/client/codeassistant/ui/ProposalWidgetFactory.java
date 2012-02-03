package org.eclipse.jdt.client.codeassistant.ui;

import org.eclipse.jdt.client.codeassistant.ui.widgets.AnonymousClassDeclaration;
import org.eclipse.jdt.client.codeassistant.ui.widgets.FieldRef;
import org.eclipse.jdt.client.codeassistant.ui.widgets.MethodDeclarationWidget;
import org.eclipse.jdt.client.codeassistant.ui.widgets.MethodRef;
import org.eclipse.jdt.client.core.CompletionProposal;

public class ProposalWidgetFactory
{
   public ProposalWidget biuld(CompletionProposal proposal)
   {
      switch (proposal.getKind())
      {
         case CompletionProposal.ANONYMOUS_CLASS_DECLARATION :
            return new AnonymousClassDeclaration(proposal);

         case CompletionProposal.KEYWORD :
         case CompletionProposal.LABEL_REF :
            return new SimpleProposalWidget(proposal);

         case CompletionProposal.METHOD_REF :
            return new MethodRef(proposal);

         case CompletionProposal.FIELD_REF :
            return new FieldRef(proposal);

         case CompletionProposal.METHOD_DECLARATION :
            return new MethodDeclarationWidget(proposal);
      }
      return new SimpleProposalWidget(proposal);
   }
}
