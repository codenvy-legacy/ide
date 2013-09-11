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

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.ide.client.framework.util.ImageUtil;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 */
public class FileTemplateImpl extends AbstractTemplate implements FileTemplate {
    private String mimeType;

    private String content;

    private String fileName;

    public FileTemplateImpl(String mimeType, String name, String description, String content, String nodeName) {
        super(name, description, nodeName);
        this.mimeType = mimeType;
        this.content = content;
    }

    public FileTemplateImpl(String mimeType, String name, String description, String content, boolean isDefault) {
        super(name, description, isDefault);
        this.mimeType = mimeType;
        this.content = content;
    }

    public FileTemplateImpl(String name, String fileName, String mimeType) {
        super(name);
        this.fileName = fileName;
        this.mimeType = mimeType;
    }

    public FileTemplateImpl(String name, String fileName) {
        super(name);
        this.fileName = fileName;
    }

    public FileTemplateImpl(String name, String description, String mimeType, boolean isDefault) {
        super(name, description, isDefault);
        this.mimeType = mimeType;
    }

    /** @see org.exoplatform.ide.client.framework.template.FileTemplate#getContent() */
    @Override
    public String getContent() {
        return content;
    }

    /** @see org.exoplatform.ide.client.framework.template.FileTemplate#setContent(java.lang.String) */
    @Override
    public void setContent(String content) {
        this.content = content;
    }

    /** @see org.exoplatform.ide.client.framework.template.FileTemplate#getMimeType() */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    /** @see org.exoplatform.ide.client.framework.template.FileTemplate#setMimeType(java.lang.String) */
    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** @see org.exoplatform.ide.client.framework.template.FileTemplate#getFileName() */
    @Override
    public String getFileName() {
        return fileName;
    }

    /** @see org.exoplatform.ide.client.framework.template.FileTemplate#setFileName(java.lang.String) */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** @see org.exoplatform.ide.client.framework.template.FileTemplate#getIcon() */
    @Override
    public ImageResource getIcon() {
        return ImageUtil.getIcon(getMimeType());
    }

}
