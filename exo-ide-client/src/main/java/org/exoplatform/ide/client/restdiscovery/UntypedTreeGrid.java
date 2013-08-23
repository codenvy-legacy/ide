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
package org.exoplatform.ide.client.restdiscovery;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;

import org.exoplatform.ide.client.framework.discovery.RestService;

import java.util.List;
import java.util.Set;

/**
 * A widget that implements this interface is untyped(or typed by {@link Object}) tree<br>
 * Used for REST Service Discovery<br>
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 23, 2010 3:48:02 PM evgen $
 */
public interface UntypedTreeGrid extends HasOpenHandlers<Object>, HasSelectionHandlers<Object>, HasClickHandlers {
    /**
     * Set root resources
     *
     * @param item
     *         root resource
     * @param restClassPath
     *         Paths of class mapping
     */
    void setRootValue(RestService item, Set<String> restClassPath);

    /**
     * Set paths mapped in root resource
     *
     * @param service
     *         root resource
     * @param list
     *         of methods and resources mapped in root resource
     */
    void setPaths(RestService service, List<?> list);

}
