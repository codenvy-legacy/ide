/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.template;

import com.google.gwt.resources.client.ImageResource;

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

    /** {@inheritDoc} */
    @Override
    public String getContent() {
        return content;
    }

    /** {@inheritDoc} */
    @Override
    public void setContent(String content) {
        this.content = content;
    }

    /** {@inheritDoc} */
    @Override
    public String getMimeType() {
        return mimeType;
    }

    /** {@inheritDoc} */
    @Override
    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    /** {@inheritDoc} */
    @Override
    public String getFileName() {
        return fileName;
    }

    /** {@inheritDoc} */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getIcon() {
        // TODO
        //      return ImageUtil.getIcon(getMimeType());
        return null;
    }

}
