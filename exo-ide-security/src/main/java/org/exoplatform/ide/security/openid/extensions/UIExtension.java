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
package org.exoplatform.ide.security.openid.extensions;

import org.openid4java.message.MessageExtension;
import org.openid4java.message.Parameter;
import org.openid4java.message.ParameterList;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class UIExtension implements MessageExtension {
    public static final String TYPE_URI = "http://specs.openid.net/extensions/ui/1.0";

    private ParameterList params;

    /**
     * @param mode
     *         specifies the alternative user interface. At the moment 'popup' supported only. May be <code>null</code>.
     * @param showFavicon
     *         displays the favicon of the referring domain in the OpenID approval page
     */
    public UIExtension(String mode, boolean showFavicon) {
        this.params = new ParameterList();
        if (mode != null) {
            this.params.set(new Parameter("mode", mode));
        }
        this.params.set(new Parameter("icon", Boolean.toString(showFavicon)));
    }

    @Override
    public final String getTypeUri() {
        return TYPE_URI;
    }

    @Override
    public ParameterList getParameters() {
        return this.params;
    }

    @Override
    public void setParameters(ParameterList params) {
        this.params = params;
    }

    @Override
    public final boolean providesIdentifier() {
        return false;
    }

    @Override
    public final boolean signRequired() {
        return true;
    }
}
