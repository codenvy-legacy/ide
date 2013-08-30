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
package org.exoplatform.ide.client.framework.template;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public abstract class TemplateService {

    private static TemplateService instance;

    public static TemplateService getInstance() {
        return instance;
    }

    protected TemplateService() {
        instance = this;
    }


    public abstract void getProjectTemplateList(AsyncRequestCallback<List<ProjectTemplate>> callback)
            throws RequestException;


    public abstract void createProjectFromTemplate(String vfsId, String parentId, String name, String templateName,
                                                   AsyncRequestCallback<ProjectModel> callback) throws RequestException;


}
