/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
