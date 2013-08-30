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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;

import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.java.jdi.shared.*;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class DebuggerClientService {
    private static String BASE_URL;

    private static DebuggerClientService instance;

    private final String restContext;

    public DebuggerClientService() {
        this.restContext = Utils.getRestContext();
        BASE_URL = restContext + Utils.getWorkspaceName() + "/java/debug";
        instance = this;
    }

    public static DebuggerClientService getInstance() {
        return instance;
    }

    public void connect(String host, int port, AsyncRequestCallback<DebuggerInfo> callback) throws RequestException {
        String params = "host=" + host + "&port=" + port;
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/connect?" + params).loader(new EmptyLoader()).send(callback);
    }

    public void disconnect(String id, AsyncRequestCallback<String> callback) throws RequestException {
        Loader loader = new GWTLoader();
        loader.setMessage("DisConnection... ");
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/disconnect/" + id).loader(loader).send(callback);
    }

    public void addBreakPoint(String id, BreakPoint breakPoint, AsyncRequestCallback<BreakPoint> callback)
            throws RequestException {
        AutoBean<BreakPoint> ab = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoint(breakPoint);
        String json = AutoBeanCodex.encode(ab).getPayload();
        AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/breakpoints/add/" + id).data(json)
                    .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
    }

    public void deleteBreakPoint(String id, BreakPoint breakPoint, AsyncRequestCallback<BreakPoint> callback)
            throws RequestException {
        AutoBean<BreakPoint> ab = DebuggerExtension.AUTO_BEAN_FACTORY.breakPoint(breakPoint);
        String json = AutoBeanCodex.encode(ab).getPayload();
        AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/breakpoints/delete/" + id).data(json)
                    .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
    }

    public void getBreakPoints(String id, AsyncRequestCallback<BreakPointList> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/breakpoints/" + id).loader(new EmptyLoader()).send(callback);
    }

    public void checkEvents(String id, AsyncRequestCallback<DebuggerEventList> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/events/" + id).loader(new EmptyLoader()).send(callback);
    }

    public void dump(String id, AsyncRequestCallback<StackFrameDump> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/dump/" + id).loader(new EmptyLoader()).send(callback);
    }

    public void resume(String id, AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/resume/" + id).loader(new EmptyLoader()).send(callback);
    }

    public void getValue(String id, Variable var, AsyncRequestCallback<Value> callback) throws RequestException {
        AutoBean<VariablePath> autoBean2 = DebuggerExtension.AUTO_BEAN_FACTORY.variablePath(var.getVariablePath());
        String json = AutoBeanCodex.encode(autoBean2).getPayload();
        AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/value/get/" + id).data(json)
                    .header("Content-Type", "application/json").loader(new EmptyLoader()).send(callback);
    }

    public void setValue(String id, UpdateVariableRequest request, AsyncRequestCallback<String> callback)
            throws RequestException {
        AutoBean<UpdateVariableRequest> autoBean = DebuggerExtension.AUTO_BEAN_FACTORY.updateVariableRequest(request);
        String json = AutoBeanCodex.encode(autoBean).getPayload();
        AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/value/set/" + id).data(json)
                    .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_JSON).loader(new EmptyLoader()).send(callback);
    }

    public void stepInto(String id, AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/step/into/" + id).loader(new EmptyLoader()).send(callback);
    }

    public void stepOver(String id, AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/step/over/" + id).loader(new EmptyLoader()).send(callback);
    }

    public void stepReturn(String id, AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/step/out/" + id).loader(new EmptyLoader()).send(callback);
    }

    public void stopApplication(ApplicationInstance runningApp, AsyncRequestCallback<String> callback)
            throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, runningApp.getStopURL()).loader(new EmptyLoader()).send(callback);
    }

    public void deleteAllBreakPoint(String id, AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, BASE_URL + "/breakpoints/delete_all/" + id).loader(new EmptyLoader())
                    .send(callback);
    }

    public void evaluateExpression(String id, String expression, AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        AsyncRequest.build(RequestBuilder.POST, BASE_URL + "/expression/" + id).data(expression)
                    .header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN).header(HTTPHeader.CONTENTTYPE, MimeType.TEXT_PLAIN)
                    .loader(new EmptyLoader()).send(callback);
    }


}
