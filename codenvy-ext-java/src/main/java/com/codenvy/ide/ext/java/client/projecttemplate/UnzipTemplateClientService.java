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
package com.codenvy.ide.ext.java.client.projecttemplate;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for unpack java-project templates.
 *
 * @author Artem Zatsarynnyy
 */
public interface UnzipTemplateClientService {

    void unzipMavenJarTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException;

    void unzipMavenWarTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException;

    void unzipMavenSpringTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException;

    void unzipAntJarTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException;

    void unzipAntSpringTemplate(String projectName, AsyncRequestCallback<Void> callback) throws RequestException;
}