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
package org.exoplatform.ide.client.framework.template.marshal;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public interface Const {

    public interface TemplateType {
        String FILE = "file";

        String PROJECT = "project";
    }

    String TEMPLATE = "template";

    String TEMPLATES = "templates";

    // common nodes
    String NAME = "name";

    String DESCRIPTION = "description";

    String TEMPLATE_TYPE = "template-type";

    // file template nodes
    String MIME_TYPE = "mime-type";

    String CONTENT = "content";

    // project template nodes
    String FILE = "file";

    String FOLDER = "folder";

    String ITEMS = "items";

    String TEMPLATE_FILE_NAME = "template-file-name";

    String FILE_NAME = "file-name";

    String CLASSPATH = "classpath";
}
