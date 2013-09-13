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

package org.exoplatform.gwtframework.commons.rest;

public interface HTTPHeader {

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.1">HTTP/1.1 documentation</a>}. */
    public static final String ACCEPT = "Accept";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.2">HTTP/1.1 documentation</a>}. */
    public static final String ACCEPT_CHARSET = "Accept-Charset";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.3">HTTP/1.1 documentation</a>}. */
    public static final String ACCEPT_ENCODING = "Accept-Encoding";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.4">HTTP/1.1 documentation</a>}. */
    public static final String ACCEPT_LANGUAGE = "Accept-Language";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.8">HTTP/1.1 documentation</a>}. */
    public static final String AUTHORIZATION = "Authorization";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9">HTTP/1.1 documentation</a>}. */
    public static final String CACHE_CONTROL = "Cache-Control";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.10">HTTP/1.1 documentation</a>}. */
    public static final String CONNECTION = "Connection";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.11">HTTP/1.1 documentation</a>}. */
    public static final String CONTENT_ENCODING = "Content-Encoding";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.12">HTTP/1.1 documentation</a>}. */
    public static final String CONTENT_LANGUAGE = "Content-Language";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.13">HTTP/1.1 documentation</a>}. */
    public static final String CONTENT_LENGTH = "Content-Length";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.14">HTTP/1.1 documentation</a>}. */
    public static final String CONTENT_LOCATION = "Content-Location";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.17">HTTP/1.1 documentation</a>}. */
    public static final String CONTENT_TYPE = "Content-Type";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.18">HTTP/1.1 documentation</a>}. */
    public static final String DATE = "Date";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.19">HTTP/1.1 documentation</a>}. */
    public static final String ETAG = "ETag";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.21">HTTP/1.1 documentation</a>}. */
    public static final String EXPIRES = "Expires";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.23">HTTP/1.1 documentation</a>}. */
    public static final String HOST = "Host";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.24">HTTP/1.1 documentation</a>}. */
    public static final String IF_MATCH = "If-Match";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.25">HTTP/1.1 documentation</a>}. */
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.26">HTTP/1.1 documentation</a>}. */
    public static final String IF_NONE_MATCH = "If-None-Match";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.28">HTTP/1.1 documentation</a>}. */
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.29">HTTP/1.1 documentation</a>}. */
    public static final String LAST_MODIFIED = "Last-Modified";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.30">HTTP/1.1 documentation</a>}. */
    public static final String LOCATION = "Location";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.43">HTTP/1.1 documentation</a>}. */
    public static final String USER_AGENT = "User-Agent";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.44">HTTP/1.1 documentation</a>}. */
    public static final String VARY = "Vary";

    /** See {@link <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.47">HTTP/1.1 documentation</a>}. */
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    /** See {@link <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>}. */
    public static final String COOKIE = "Cookie";

    /** See {@link <a href="http://www.ietf.org/rfc/rfc2109.txt">IETF RFC 2109</a>}. */
    public static final String SET_COOKIE = "Set-Cookie";

    /**
     * WebDav "Depth" header. See <a href='http://www.ietf.org/rfc/rfc2518.txt'>
     * HTTP Headers for Distributed Authoring</a> section 9 for more information.
     */
    public static final String DEPTH = "Depth";

    /**
     * HTTP 1.1 "Accept-Ranges" header. See <a
     * href='http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html'> HTTP/1.1
     * section 14 "Header Field Definitions"</a> for more information.
     */
    public static final String ACCEPT_RANGES = "Accept-Ranges";

    /**
     * HTTP 1.1 "Allow" header. See <a
     * href='http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html'> HTTP/1.1
     * section 14 "Header Field Definitions"</a> for more information.
     */
    public static final String ALLOW = "Allow";

    /**
     * HTTP 1.1 "Content-Length" header. See <a
     * href='http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html'> HTTP/1.1
     * section 14 "Header Field Definitions"</a> for more information.
     */
    public static final String CONTENTLENGTH = "Content-Length";

    /**
     * HTTP 1.1 "Content-Range" header. See <a
     * href='http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html'> HTTP/1.1
     * section 14 "Header Field Definitions"</a> for more information.
     */
    public static final String CONTENTRANGE = "Content-Range";

    /**
     * HTTP 1.1 "Content-type" header. See <a
     * href='http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html'> HTTP/1.1
     * section 14 "Header Field Definitions"</a> for more information.
     */
    public static final String CONTENTTYPE = "Content-type";

    /**
     * WebDav "DAV" header. See <a href='http://www.ietf.org/rfc/rfc2518.txt'>
     * HTTP Headers for Distributed Authoring</a> section 9 for more information.
     */
    public static final String DAV = "DAV";

    /**
     * HTTP 1.1 "Allow" header. See <a
     * href='http://msdn.microsoft.com/en-us/library/ms965954.aspx'> WebDAV/DASL
     * Request and Response Syntax</a> for more information.
     */
    public static final String DASL = "DASL";

    /**
     * MS-Author-Via Response Header. See <a
     * href='http://msdn.microsoft.com/en-us/library/cc250217.aspx'> MS-Author-Via
     * Response Header</a> for more information.
     */
    public static final String MSAUTHORVIA = "MS-Author-Via";

    /**
     * HTTP 1.1 "Range" header. See <a
     * href='http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html'> HTTP/1.1
     * section 14 "Header Field Definitions"</a> for more information.
     */
    public static final String RANGE = "Range";

    /**
     * WebDav "Destination" header. See <a
     * href='http://www.ietf.org/rfc/rfc2518.txt'> HTTP Headers for Distributed
     * Authoring</a> section 9 for more information.
     */
    public static final String DESTINATION = "Destination";

    /**
     * WebDav "DAV" header. See <a href='http://www.ietf.org/rfc/rfc2518.txt'>
     * HTTP Headers for Distributed Authoring</a> section 9 for more information.
     */
    public static final String LOCKTOKEN = "Lock-Token";

    /**
     * WebDav "If" header. See <a href='http://www.ietf.org/rfc/rfc2518.txt'> HTTP
     * Headers for Distributed Authoring</a> section 9 for more information.
     */
    public static final String IF = "If";

    /**
     * WebDav "Timeout" header. See <a href='http://www.ietf.org/rfc/rfc2518.txt'>
     * HTTP Headers for Distributed Authoring</a> section 9 for more information.
     */
    public static final String TIMEOUT = "Timeout";

    /** WebDav multipart/byteranges header. */
    public static final String MULTIPART_BYTERANGES = "multipart/byteranges; boundary=";

    /**
     * WebDav "Overwrite" header. See <a
     * href='http://www.ietf.org/rfc/rfc2518.txt'> HTTP Headers for Distributed
     * Authoring</a> section 9 for more information.
     */
    public static final String OVERWRITE = "Overwrite";

    /**
     * JCR-specific header to add an opportunity to create nodes of the specific
     * types via WebDAV.
     */
    public static final String FILE_NODETYPE = "File-NodeType";

    /**
     * JCR-specific header to add an opportunity to create nodes of the specific
     * types via WebDAV.
     */
    public static final String CONTENT_NODETYPE = "Content-NodeType";

    /** JCR-specific header to add an opportunity to set node mixins via WebDAV. */
    public static final String CONTENT_MIXINTYPES = "Content-MixinTypes";

    /**
     * X-HTTP-Method-Override header. See <a
     * href='http://code.google.com/apis/gdata/docs/2.0/basics.html'>here</a>.
     */
    public static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";

    /**
     * User-Agent header. See <a
     * href='http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html'> HTTP Header
     * Field Definitions sec. 14.43 Transfer-Encoding</a>.
     */
    public static final String USERAGENT = "User-Agent";

    /**
     * Transfer-Encoding header. See <a
     * href='http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html'> HTTP Header
     * Field Definitions sec. 14.41 Transfer-Encoding</a>.
     */
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";

    public static final String JAXRS_BODY_PROVIDED = "JAXRS-Body-Provided";

}
