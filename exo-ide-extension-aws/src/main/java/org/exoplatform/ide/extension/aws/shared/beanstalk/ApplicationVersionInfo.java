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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

/**
 * Info about version of AWS Beanstalk application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ApplicationVersionInfo {
    String getApplicationName();

    void setApplicationName(String name);

    String getDescription();

    void setDescription(String description);

    String getVersionLabel();

    void setVersionLabel(String versionLabel);

    S3Item getS3Location();

    void setS3Location(S3Item s3Location);

    long getCreated();

    void setCreated(long created);

    long getUpdated();

    void setUpdated(long updated);
}
