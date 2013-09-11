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
package org.exoplatform.ide.editor.javascript.client.contentassist;

import com.google.gwt.core.client.JavaScriptObject;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
final class JsProposal extends JavaScriptObject {

    /**
     *
     */
    protected JsProposal() {
    }

    public native String getProposal()/*-{
        return this.proposal;
    }-*/;

    public native String getDescription()/*-{
        return this.description;
    }-*/;

    public native int getEscapePosition()/*-{
        return this.escapePosition ? this.escapePosition : -1;
    }-*/;

    public native Position[] getPositions()/*-{
        return this.positions ? this.positions : [];
    }-*/;

}
