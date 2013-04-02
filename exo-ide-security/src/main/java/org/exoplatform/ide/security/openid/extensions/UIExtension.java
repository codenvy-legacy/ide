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
