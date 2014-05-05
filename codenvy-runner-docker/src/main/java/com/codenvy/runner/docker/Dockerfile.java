/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.runner.docker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * @author andrew00x
 */
public class Dockerfile {
    private final File file;

    private List<String>      comments;
    private List<DockerImage> images;

    public Dockerfile(File file) {
        this.file = file;
    }

    public List<String> getComments() {
        if (comments == null) {
            comments = new LinkedList<>();
        }
        return comments;
    }

    public List<DockerImage> getImages() {
        if (images == null) {
            images = new LinkedList<>();
        }
        return images;
    }

    public File getFile() {
        return file;
    }
}
