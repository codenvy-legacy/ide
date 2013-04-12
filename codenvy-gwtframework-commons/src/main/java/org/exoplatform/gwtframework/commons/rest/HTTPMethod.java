/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.gwtframework.commons.rest;

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
