/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.java.messages;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.dto.shared.RoutingType;
import com.google.gwt.webworker.client.messages.Message;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@RoutingType(type = RoutingTypes.COMPUTE_CORRECTION)
public interface ComputeCorrMessage extends Message{
    String id();

    String documentContent();

    int documentOffset();

    int documentSelectionLength();

    boolean updatedOffset();

    Array<ProblemLocationMessage> problemLocations();

    String projectId();
}
