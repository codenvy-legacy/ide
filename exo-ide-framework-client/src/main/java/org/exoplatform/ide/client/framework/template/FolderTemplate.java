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
package org.exoplatform.ide.client.framework.template;

import com.google.gwt.resources.client.ImageResource;

import java.util.List;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 26, 2012 12:51:08 PM anya $
 */
public interface FolderTemplate extends Template {

    /** @return children on template */
    List<AbstractTemplate> getChildren();

    void setChildren(List<AbstractTemplate> children);

    /** @see org.exoplatform.ide.client.framework.template.AbstractTemplate#getIcon() */
    ImageResource getIcon();

}