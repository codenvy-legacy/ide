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
package com.codenvy.ide.ext.git.shared;

import com.codenvy.ide.dto.DTO;

/**
 * Request to add content of working tree to Git index. This action prepares content to next commit.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AddRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
@DTO
public interface AddRequest extends GitRequest {
    /** Default file pattern that will be used if {@link #filepattern} is not set. All content of working tree will be added in index. */
    String[] DEFAULT_PATTERN = new String[]{"."};

    /** @return files to add content from */
    String[] getFilepattern();
    
    void setFilepattern(String[] pattern);
    
    AddRequest withFilepattern(String[] filepattern);

    /**
     * @return if <code>true</code> than never stage new files, but stage modified new contents of tracked files. It will remove files from
     *         the index if the corresponding files in the working tree have been removed. If <code>false</code> then new files and
     *         modified
     *         files added to the index.
     */
    boolean isUpdate();
    
    AddRequest withUpdate(boolean isUpdate);
}