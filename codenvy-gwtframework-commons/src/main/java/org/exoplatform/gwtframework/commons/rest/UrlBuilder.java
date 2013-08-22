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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;

/**
 * Extended {@link com.google.gwt.http.client.UrlBuilder} with constructor that consumes string url.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 29, 2011 evgen $
 */
public class UrlBuilder extends com.google.gwt.http.client.UrlBuilder {

    /**
     *
     */
    public UrlBuilder() {
    }

    /**
     * Parse url and set initial parameters(protocol, host, port, path)<br>
     *
     * @param url
     */
    public UrlBuilder(String url) {
        JavaScriptObject jso = parseUrl(url);
        JSONObject o = new JSONObject(jso);
        setHost(o.get("host").isString().stringValue());
        setProtocol(o.get("protocol").isString().stringValue());
        setPort(Integer.valueOf(o.get("port").isString().stringValue()));
        setPath(o.get("path").isString().stringValue());
        //fill query parameters
        JSONObject query = o.get("queryKey").isObject();
        for (String key : query.keySet()) {
            setParameter(key, query.get(key).isString().stringValue());
        }
    }

    private native JavaScriptObject parseUrl(String url)/*-{

        options = {
            strictMode: false,
            key: [ "source", "protocol", "authority", "userInfo", "user",
                "password", "host", "port", "relative", "path",
                "directory", "file", "query", "anchor" ],
            q: {
                name: "queryKey",
                parser: /(?:^|&)([^&=]*)=?([^&]*)/g
            },
            parser: {
                strict: /^(?:([^:\/?#]+):)?(?:\/\/((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?))?((((?:[^?#\/]*\/)*)([^?#]*))
                    (? :\? ([ ^#]*))
        ? (? :#(.*
    ))
        ?
    )/,
        loose: /^(?:(?![^:@]+:[^:@\/]*@)([^:\/?#.]+):)?(?:\/\/)?((?:(([^:@]*)(?::([^:@]*))?)?@)?([^:\/?#]*)(?::(\d*))?)(((\/
            (? : [ ^ ?#]
        (? ![ ^ ?#\/]*\.[^?#\/.]+(?:[?#]|$)))*\/?)?([^?#\/]*))(?:\?([^#]*))?(?:#(.*))?)/
    }
    }
    var o = options, m = o.parser[o.strictMode ? "strict" : "loose"]
        .exec(url), uri = {}, i = 14;

    while (i--)
        uri[o.key[i]] = m[i] || "";

    uri[o.q.name] = {};
    uri[o.key[12]].replace(o.q.parser, function ($0, $1, $2) {
        if ($1)
            uri[o.q.name][$1] = $2;
    });
    return uri;
    }-*/;

}
