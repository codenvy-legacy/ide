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

import com.codenvy.ide.commons.exception.ServerDisconnectedException;
import com.codenvy.ide.commons.exception.ServerException;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;


public abstract class AsyncRequestCallback<T> implements RequestCallback {

    // http code 207 is "Multi-Status"
    //IE misinterpreting HTTP status code 204 as 1223 (http://www.mail-archive.com/jquery-en@googlegroups.com/msg13093.html)

    private static final int[] DEFAULT_SUCCESS_CODES = {Response.SC_OK, Response.SC_CREATED, Response.SC_NO_CONTENT,
                                                        207, 1223};

    //   private static final int[] REDIRECT_CODES = {Response.SC_TEMPORARY_REDIRECT, Response.SC_MOVED_PERMANENTLY,
    //      Response.SC_MOVED_TEMPORARILY, Response.SC_USE_PROXY};

    private int[] successCodes;

    private final Unmarshallable<T> unmarshaller;

    private AsyncRequestLoader loader;

    private final T payload;

    private AsyncRequest request;

    /**
     * @param obj
     * @param loader
     */
    public AsyncRequestCallback() {
        this(null);
    }

    /**
     * Constructor retrieves unmarshaller with initialized (this is important!)
     * object.
     * When response comes callback calls Unmarshallable.unmarshal which populates
     * the object.
     *
     * @param unmarshaller
     * @param loader
     */
    public AsyncRequestCallback(Unmarshallable<T> unmarshaller) {
        this.successCodes = DEFAULT_SUCCESS_CODES;
        if (unmarshaller == null) {
            this.payload = null;
        } else {
            this.payload = unmarshaller.getPayload();
            //         if(unmarshaller == null)
            //            throw new NullPointerException("Unmarshallable result is not initialized in advance");
        }
        this.unmarshaller = unmarshaller;

        //this.loader = loader;
    }

    /** @return the result */
    public T getPayload() {
        return payload;
    }

    /**
     * @param successCodes
     *         the successCodes to set
     */
    public void setSuccessCodes(int[] successCodes) {
        this.successCodes = successCodes;
    }

    public final void setLoader(AsyncRequestLoader loader) {
        this.loader = loader;
    }

    /** @see com.google.gwt.http.client.RequestCallback#onError(com.google.gwt.http.client.Request, java.lang.Throwable) */
    public final void onError(Request request, Throwable exception) {
        if (loader != null) {
            loader.hide();
        }

        onFailure(exception);
    }

    /**
     * @see com.google.gwt.http.client.RequestCallback#onResponseReceived(com.google.gwt.http.client.Request,
     *      com.google.gwt.http.client.Response)
     */
    public final void onResponseReceived(Request request, Response response) {
        if (loader != null) {
            loader.hide();
        }

      /*
       * If there is no connection to the server then status equals 0 ( In Internet Explorer status is 12029 )
       */
        if (response.getStatusCode() == 0 || response.getStatusCode() == 12029) {
            onServerDisconnected();
            return;
        }

        if (response.getStatusCode() == HTTPStatus.UNAUTHORIZED) {
            onUnauthorized(response);
            return;
        }

        if (isSuccessful(response)) {
            try {
                if (unmarshaller != null) {
                    unmarshaller.unmarshal(response);
                }

                onSuccess(payload);
            } catch (Exception e) {
                onFailure(e);
            }
        } else {
            onFailure(new ServerException(response));
        }
    }

    protected final boolean isSuccessful(Response response) {
        if (successCodes == null) {
            successCodes = DEFAULT_SUCCESS_CODES;
        }

        if ("Authentication-required".equals(response.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED)))
            return false;

        for (int code : successCodes)
            if (response.getStatusCode() == code)
                return true;
        return false;
    }

    /**
     * If response is successfully received and
     * response status code is in set of success codes.
     *
     * @param request
     * @param response
     */
    protected abstract void onSuccess(T result);

    /**
     * If error received from server.
     *
     * @param exception
     *         caused failure
     */
    protected abstract void onFailure(Throwable exception);

    /** If server disconnected. */
    protected void onServerDisconnected() {
        onFailure(new ServerDisconnectedException(request));
    }

    /**
     * If unauthorized.
     *
     * @param response
     */
    protected void onUnauthorized(Response response) {
        onFailure(new UnauthorizedException(response, request));
    }

    public void setRequest(AsyncRequest request) {
        this.request = request;
    }

    public AsyncRequest getRequest() {
        return request;
    }

}
