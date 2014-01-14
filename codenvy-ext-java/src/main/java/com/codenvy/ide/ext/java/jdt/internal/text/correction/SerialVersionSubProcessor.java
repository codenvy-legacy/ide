/*******************************************************************************
 * Copyright (c) 2000, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.jdt.internal.text.correction;

import com.codenvy.ide.ext.java.jdt.codeassistant.api.IProblemLocation;
import com.codenvy.ide.ext.java.jdt.quickassist.api.InvocationContext;
import com.codenvy.ide.runtime.Assert;

import java.util.Collection;

/**
 * Subprocessor for serial version quickfix proposals.
 *
 * @since 3.1
 */
public final class SerialVersionSubProcessor {

    //   public static final class SerialVersionProposal extends FixCorrectionProposal
    //   {
    //      private boolean fIsDefaultProposal;
    //
    //      public SerialVersionProposal(IProposableFix fix, int relevance, IInvocationContext context, boolean isDefault)
    //      {
    //         super(fix, createCleanUp(isDefault), relevance, new Image(JdtClientBundle.INSTANCE.add_obj()), context);
    //         fIsDefaultProposal = isDefault;
    //      }
    //
    //      private static ICleanUp createCleanUp(boolean isDefault)
    //      {
    //         Map<String, String> options = new HashMap<String, String>();
    //         options.put(CleanUpConstants.ADD_MISSING_SERIAL_VERSION_ID, CleanUpOptions.TRUE);
    //         if (isDefault)
    //         {
    //            options.put(CleanUpConstants.ADD_MISSING_SERIAL_VERSION_ID_DEFAULT, CleanUpOptions.TRUE);
    //         }
    //         else
    //         {
    //            options.put(CleanUpConstants.ADD_MISSING_SERIAL_VERSION_ID_GENERATED, CleanUpOptions.TRUE);
    //         }
    //         return new PotentialProgrammingProblemsCleanUp(options);
    //      }
    //
    //      public boolean isDefaultProposal()
    //      {
    //         return fIsDefaultProposal;
    //      }
    //
    //      /**
    //       * {@inheritDoc}
    //       */
    //      @Override
    //      public Object getAdditionalProposalInfo(IProgressMonitor monitor)
    //      {
    //         if (fIsDefaultProposal)
    //         {
    //            return CorrectionMessages.INSTANCE.SerialVersionDefaultProposal_message_default_info();
    //         }
    //         else
    //         {
    //            return CorrectionMessages.INSTANCE.SerialVersionHashProposal_message_generated_info();
    //         }
    //      }
    //   }

    /**
     * Determines the serial version quickfix proposals.
     *
     * @param context
     *         the invocation context
     * @param location
     *         the problem location
     * @param proposals
     *         the proposal collection to extend
     */
    public static final void getSerialVersionProposals(final InvocationContext context,
                                                       final IProblemLocation location, final Collection<ICommandAccess> proposals) {

        Assert.isNotNull(context);
        Assert.isNotNull(location);
        Assert.isNotNull(proposals);
        System.out.println("SerialVersionSubProcessor.getSerialVersionProposals()");
        //TODO
        //      IProposableFix[] fixes =
        //         PotentialProgrammingProblemsFix.createMissingSerialVersionFixes(context.getASTRoot(), location);
        //      if (fixes != null)
        //      {
        //         proposals.add(new SerialVersionProposal(fixes[0], 9, context, true));
        //         proposals.add(new SerialVersionProposal(fixes[1], 9, context, false));
        //      }
    }
}
