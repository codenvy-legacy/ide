/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.dtogen.client;

import org.exoplatform.ide.dtogen.shared.ServerError;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ServerErrorImpl extends RoutableDtoClientImpl implements ServerError {

    protected ServerErrorImpl() {

    }

    @Override
    public final native String getDetails() /*-{
        return this["details"];
    }-*/;

    public final native ServerErrorImpl setDetails(String details) /*-{
        this["details"] = details;
        return this;
    }-*/;

    public final native boolean hasDetails() /*-{
        return this.hasOwnProperty("details");
    }-*/;

    @Override
    public final native FailureReason getFailureReason() /*-{
        return @org.exoplatform.ide.dtogen.shared.ServerError.FailureReason::valueOf(Ljava/lang/String;)(this["failureReason"]);
    }-*/;

    public final native ServerErrorImpl setFailureReason(FailureReason failureReason) /*-{
        failureReason = failureReason.@org.exoplatform.ide.dtogen.shared.ServerError.FailureReason::toString()();
        this["failureReason"] = failureReason;
        return this;
    }-*/;

    public final native boolean hasFailureReason() /*-{
        return this.hasOwnProperty("failureReason");
    }-*/;

}
