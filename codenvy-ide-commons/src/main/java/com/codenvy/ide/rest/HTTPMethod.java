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
package com.codenvy.ide.rest;

/**
 * Created by The eXo Platform SARL        .<br/>
 * HTTP methods
 *
 * @author Gennady Azarenkov
 * @version $Id: $
 */
public interface HTTPMethod {

    public static final String GET = "GET";

    public static final String PUT = "PUT";

    public static final String POST = "POST";

    public static final String DELETE = "DELETE";

    public static final String SEARCH = "SEARCH";

    public static final String PROPFIND = "PROPFIND";

    public static final String PROPPATCH = "PROPPATCH";

    public static final String HEAD = "HEAD";

    public static final String CHECKIN = "CHECKIN";

    public static final String CHECKOUT = "CHECKOUT";

    public static final String COPY = "COPY";

    public static final String LOCK = "LOCK";

    public static final String MOVE = "MOVE";

    public static final String UNLOCK = "UNLOCK";

    public static final String OPTIONS = "OPTIONS";

    public static final String MKCOL = "MKCOL";

    public static final String REPORT = "REPORT";

    public static final String UPDATE = "UPDATE";

    public static final String ACL = "ACL";

}
