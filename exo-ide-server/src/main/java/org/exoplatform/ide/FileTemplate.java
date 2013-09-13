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
package org.exoplatform.ide;

/**
 * File template data.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: FileTemplate.java Jul 26, 2011 11:53:35 AM vereshchaka $
 */
public class FileTemplate extends Template {
    private String mimeType;

    private String content;

    private String fileName;

    public FileTemplate() {
        super("file");
    }

    /** @return the fileName */
    public String getFileName() {
        return fileName;
    }

    /**
     * @param fileName
     *         the fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** @return the mimeType */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * @param mimeType
     *         the mimeType to set
     */
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** @return the content */
    public String getContent() {
        return content;
    }

    /**
     * @param content
     *         the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

}
