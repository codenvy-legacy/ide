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
package com.codenvy.ide.dtogen.client;

import com.codenvy.ide.dtogen.shared.ServerError;

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
        return @com.codenvy.ide.dtogen.shared.ServerError.FailureReason::valueOf(Ljava/lang/String;)(this["failureReason"]);
    }-*/;

    public final native ServerErrorImpl setFailureReason(FailureReason failureReason) /*-{
        failureReason = failureReason.@com.codenvy.ide.dtogen.shared.ServerError.FailureReason::toString()();
        this["failureReason"] = failureReason;
        return this;
    }-*/;

    public final native boolean hasFailureReason() /*-{
        return this.hasOwnProperty("failureReason");
    }-*/;

}
