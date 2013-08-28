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
package org.exoplatform.ide.client.framework.websocket.rest;

import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.exception.UnmarshallerException;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestLoader;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.commons.rest.RequestStatusHandler;
import org.exoplatform.ide.client.framework.websocket.Message;
import org.exoplatform.ide.client.framework.websocket.events.ReplyHandler;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.ServerException;
import org.exoplatform.ide.client.framework.websocket.rest.exceptions.UnauthorizedException;

/**
 * Callback to receive a {@link ResponseMessage}.
 *
 * @param <T>
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RequestCallback.java Nov 12, 2012 10:13:13 AM azatsarynnyy $
 */
public abstract class RequestCallback<T> implements ReplyHandler {

    // http code 207 is "Multi-Status"
    // IE misinterpreting HTTP status code 204 as 1223 (http://www.mail-archive.com/jquery-en@googlegroups.com/msg13093.html)
    private static final int[] DEFAULT_SUCCESS_CODES = {Response.SC_OK, Response.SC_CREATED, Response.SC_NO_CONTENT,
                                                        207, 1223};

    /** Status codes of the successful responses. */
    private int[] successCodes;

    /** Deserializer for the body of the {@link ResponseMessage}. */
    private final Unmarshallable<T> unmarshaller;

    /** An object deserialized from the response. */
    private final T payload;

    /** Handler to show an execution state of operation. */
    private RequestStatusHandler statusHandler;

    /** Loader to show while request is calling. */
    private AsyncRequestLoader loader;

    public RequestCallback() {
        this(null);
    }

    /**
     * Constructor retrieves unmarshaller with initialized (this is important!) object.
     * When response comes then callback calls <code>Unmarshallable.unmarshal()</code>
     * which populates the object.
     *
     * @param unmarshaller
     *         {@link Unmarshallable}
     */
    public RequestCallback(Unmarshallable<T> unmarshaller) {
        this.successCodes = DEFAULT_SUCCESS_CODES;
        this.loader = new EmptyLoader();

        if (unmarshaller == null) {
            this.payload = null;
        } else {
            this.payload = unmarshaller.getPayload();
        }
        this.unmarshaller = unmarshaller;
    }

    /**
     * Perform actions when response message was received.
     *
     * @param message
     *         message
     */
    public void onReply(Message message) {
        if (loader != null) {
            loader.hide();
        }

        if (!(message instanceof ResponseMessage))
            throw new IllegalArgumentException("Invalid input message.");

        ResponseMessage response = (ResponseMessage)message;

        if (response.getResponseCode() == HTTPStatus.UNAUTHORIZED) {
            UnauthorizedException exception = new UnauthorizedException(response);
            if (statusHandler != null) {
                statusHandler.requestError(response.getUuid(), exception);
            }
            onFailure(exception);
            return;
        }

        if (isSuccessful(response)) {
            try {
                if (unmarshaller != null) {
                    unmarshaller.unmarshal(response);
                }
                if (statusHandler != null) {
                    statusHandler.requestFinished(response.getUuid());
                }
                onSuccess(payload);
            } catch (UnmarshallerException e) {
                if (statusHandler != null) {
                    statusHandler.requestError(response.getUuid(), e);
                }
                onFailure(e);
            }
        } else {
            ServerException exception = new ServerException(response);
            if (statusHandler != null) {
                statusHandler.requestError(response.getUuid(), exception);
            }
            onFailure(exception);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onReply(String message) {

    }

    /**
     * Is response successful?
     *
     * @param response
     *         {@link ResponseMessage}
     * @return <code>true</code> if response is successful and <code>false</code> if response is not successful
     */
    protected final boolean isSuccessful(ResponseMessage response) {
        if (successCodes == null) {
            successCodes = DEFAULT_SUCCESS_CODES;
        }

        for (Pair header : response.getHeaders())
            if (HTTPHeader.JAXRS_BODY_PROVIDED.equals(header.getName())
                && "Authentication-required".equals(header.getValue()))
                return false;

        for (int code : successCodes)
            if (response.getResponseCode() == code)
                return true;

        return false;
    }

    /**
     * Set the array of successful HTTP status codes.
     *
     * @param successCodes
     *         the successCodes to set
     */
    public void setSuccessCodes(int[] successCodes) {
        this.successCodes = successCodes;
    }

    /** Get handler to show an execution state of request. */
    public final RequestStatusHandler getStatusHandler() {
        return statusHandler;
    }

    /**
     * Set handler to show an execution state of request.
     *
     * @param handler
     *         status handler
     */
    public final void setStatusHandler(RequestStatusHandler handler) {
        this.statusHandler = handler;
    }

    /** Get the loader to show while request is calling. */
    public final AsyncRequestLoader getLoader() {
        return loader;
    }

    /**
     * Set the loader to show while request is calling.
     *
     * @param loader
     *         loader to show while request is calling
     */
    public final void setLoader(AsyncRequestLoader loader) {
        this.loader = loader;
    }

    /**
     * Invokes if response is successfully received and
     * response status code is in set of success codes.
     *
     * @param result
     */
    protected abstract void onSuccess(T result);

    /**
     * Invokes if an error received from the server.
     *
     * @param exception
     *         caused failure
     */
    protected abstract void onFailure(Throwable exception);

}
