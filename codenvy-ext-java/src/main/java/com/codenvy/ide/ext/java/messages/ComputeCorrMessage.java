/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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
