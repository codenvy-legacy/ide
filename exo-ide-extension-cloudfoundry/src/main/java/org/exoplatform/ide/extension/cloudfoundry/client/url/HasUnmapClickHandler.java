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
package org.exoplatform.ide.extension.cloudfoundry.client.url;


/**
 * A widget that implements this interface provides registration for
 * Unmap button click handler instance.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: HasUnmapClickHandler.java Aug 19, 2011 11:47:14 AM vereshchaka $
 */
public interface HasUnmapClickHandler {
    /**
     * Adds a {@link UnmapHandler} handler.
     *
     * @param handler
     *         the unmap button click handler
     */
    void addUnmapClickHandler(UnmapHandler handler);
}
