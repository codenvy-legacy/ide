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
package org.exoplatform.ide.testframework.server;

import org.exoplatform.gwtframework.commons.rest.HTTPStatus;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@SuppressWarnings("serial")
public abstract class AbstractTestServlet extends HttpServlet {

    private HashMap<String, MockRequestHandler> handlers = new HashMap<String, MockRequestHandler>();

    @SuppressWarnings("unchecked")
    public AbstractTestServlet() {
        List<String> classes = new ClassScanner().getClasses();
        List<String> handlerNames = new ArrayList<String>();

        for (String className : classes) {
            try {
                if (handlerNames.contains(className)) {
                    continue;
                }

                Class c = Class.forName(className);
                if (c.getAnnotation(CanHandleRequest.class) != null) {
                    MockRequestHandler handler = (MockRequestHandler)c.newInstance();
                    String mapping = handler.getClass().getAnnotation(CanHandleRequest.class).value();
                    handlers.put(mapping, handler);
                    handlerNames.add(className);
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException,
                                                                                            IOException {
        String pathInfo = request.getPathInfo();

        if (pathInfo.startsWith("/")) {
            pathInfo = pathInfo.substring(1);
        }

        String mapping = pathInfo.substring(0, pathInfo.indexOf("/"));
        pathInfo = pathInfo.substring(pathInfo.indexOf("/"));

        MockRequestHandler handler = handlers.get(mapping);
        if (handler == null) {
            response.setStatus(HTTPStatus.NOT_IMPLEMENTED);
            return;
        }

        handler.handleRequest(pathInfo, request, response);
    }

}
