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
package com.codenvy.ide.factory.shared;

/**
 * Copy Project from temp workspace spec.
 * 
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface CopySpec10 {

    String DOWNLOAD_URL = "downloadurl";

    /**
     * Identifiers and names of projects to copy. String should be in the following format:
     * <p/>
     * 
     * <pre>
     * project1_id:project1_name;
     * project2_id:project2_name;
     * </pre>
     */
    String PROJECT_ID   = "projectid";
}
