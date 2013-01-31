/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.codenvy.eclipse.ltk.core.refactoring.participants;

import com.codenvy.eclipse.ltk.internal.core.refactoring.ParticipantDescriptor;

import java.util.HashMap;
import java.util.Map;

/**
 * An opaque list to manage sharable participants.
 * <p>
 * The list is managed by the refactoring itself. Clients typically
 * only pass the list to the corresponding method defined in
 * {@link com.codenvy.eclipse.ltk.core.refactoring.participants.ParticipantManager}
 * </p>
 * <p>
 * Note: this class is not intended to be extended or instantiated by clients.
 * </p>
 *
 * @see ISharableParticipant
 * @see com.codenvy.eclipse.ltk.core.refactoring.participants.ProcessorBasedRefactoring
 * @see com.codenvy.eclipse.ltk.core.refactoring.participants.ParticipantManager
 * @since 3.0
 */
public class SharableParticipants
{

   private Map fMap = new HashMap();

   /* package */ void put(ParticipantDescriptor descriptor, RefactoringParticipant participant)
   {
      fMap.put(descriptor, participant);
   }

   /* package */ RefactoringParticipant get(ParticipantDescriptor descriptor)
   {
      return (RefactoringParticipant)fMap.get(descriptor);
   }
}
