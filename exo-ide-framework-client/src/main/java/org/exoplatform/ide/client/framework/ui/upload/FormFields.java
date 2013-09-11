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
package org.exoplatform.ide.client.framework.ui.upload;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class FormFields {

    public static final String FILE = "file";

    public static final String LOCATION = "location";

    public static final String PARENT_ID = "parentId";

    public static final String NAME = "name";

    public static final String MIME_TYPE = "mimeType";

    public static final String VFS_ID = "vfsId";

    /** Operation with file: update or create */
    public static final String ACTION_UPDATE = "update";

    /** The id of existing file, which will be overrided with new uploaded file. */
    public static final String FILE_ID = "fileId";

}
