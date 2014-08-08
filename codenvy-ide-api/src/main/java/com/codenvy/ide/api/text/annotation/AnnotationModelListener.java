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
package com.codenvy.ide.api.text.annotation;

/**
 * Interface for objects interested in getting informed about annotation model
 * changes. Changes are the addition or removal of annotations managed by the
 * model. Clients may implement this interface.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface AnnotationModelListener {
    /**
     * Called if a model change occurred on the given model.
     *
     * @param event
     *         the event to be sent out
     */
    void modelChanged(AnnotationModelEvent event);
}
