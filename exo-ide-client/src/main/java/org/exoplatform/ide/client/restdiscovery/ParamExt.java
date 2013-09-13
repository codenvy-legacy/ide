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

import org.exoplatform.gwtframework.commons.wadl.Param;
import org.exoplatform.ide.client.restdiscovery.ui.RestServiceParameterListGrid;

/**
 * Represents extended parameter for {@link RestServiceParameterListGrid}.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ParamExt.java Mar 23, 2011 10:20:43 AM vereshchaka $
 */
public class ParamExt {

    private Param param;

    private boolean group = false;

    private String title;

    public ParamExt(Param param) {
        this.param = param;
    }

    public ParamExt(String title) {
        this.title = title;
        group = true;
    }

    /** @return the param */
    public Param getParam() {
        return param;
    }

    /** @return the title */
    public String getTitle() {
        return title;
    }

    /** @return the group */
    public boolean isGroup() {
        return group;
    }

}
