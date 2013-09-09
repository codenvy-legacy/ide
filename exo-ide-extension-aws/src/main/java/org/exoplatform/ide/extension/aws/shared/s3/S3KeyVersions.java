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
package org.exoplatform.ide.extension.aws.shared.s3;

import java.util.List;

/**
 * Information about S3 key versions. Used with rest-method (deleteVersion()) to delete S3 keys with specific version ID.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3KeyVersions {
    /**
     * Get S3 key name
     *
     * @return
     */
    String getS3Key();

    /**
     * Set S3 key name
     *
     * @param s3Key
     */
    void setS3Key(String s3Key);

    /**
     * Get list of versions for S3 key
     *
     * @return
     */
    List<String> getVersions();

    /**
     * Set list of versions for S3 key
     *
     * @param versions
     */
    void setVersions(List<String> versions);
}
