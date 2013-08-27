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
package org.exoplatform.ide.editor.java.client.codeassistant.services.marshal;

import org.exoplatform.gwtframework.commons.rest.Marshallable;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 11:43:16 AM 34360 2009-07-22 23:58:59Z evgen $
 */
public class String2ArrayMarshaller implements Marshallable {

    private String[] strings;

    /** @param strings */
    public String2ArrayMarshaller(String[] strings) {
        this.strings = strings;
    }

    /** @see org.exoplatform.gwtframework.commons.rest.Marshallable#marshal() */
    @Override
    public String marshal() {

        StringBuffer sb = new StringBuffer();
        sb.append("[");
        for (int i = 0, c = strings.length; i < c; i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(strings[i]);
        }
        sb.append("]");
        return sb.toString();
    }

}
