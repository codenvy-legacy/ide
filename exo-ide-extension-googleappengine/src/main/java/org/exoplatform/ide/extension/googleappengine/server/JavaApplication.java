/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.server;

import com.google.appengine.tools.admin.AppAdminFactory;
import com.google.appengine.tools.admin.Application;
import com.google.appengine.tools.admin.GenericApplication;
import com.google.appengine.tools.admin.ResourceLimits;
import com.google.appengine.tools.admin.UpdateListener;
import com.google.apphosting.utils.config.AppEngineWebXml;
import com.google.apphosting.utils.config.BackendsXml;
import com.google.apphosting.utils.config.CronXml;
import com.google.apphosting.utils.config.DosXml;
import com.google.apphosting.utils.config.IndexesXml;
import com.google.apphosting.utils.config.QueueXml;
import com.google.apphosting.utils.config.WebXml;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static com.codenvy.commons.lang.IoUtil.deleteRecursive;



/**
 * Wrapper for com.google.appengine.tools.admin.Application to make possible cleanup temporary files.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JavaApplication implements GenericApplication {
    private final Application delegate;

    public JavaApplication(Application delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getAppId() {
        return delegate.getAppId();
    }

    @Override
    public String getVersion() {
        return delegate.getVersion();
    }

    @Override
    public String getSourceLanguage() {
        return "Java";
    }

    @Override
    public String getServer() {
        return null; // ???
    }

    @Override
    public boolean isPrecompilationEnabled() {
        return delegate.isPrecompilationEnabled();
    }

    @Override
    public List<ErrorHandler> getErrorHandlers() {
        return delegate.getErrorHandlers();
    }

    @Override
    public String getMimeTypeIfStatic(String path) {
        return delegate.getMimeTypeIfStatic(path);
    }

    public AppEngineWebXml getAppEngineWebXml() {
        return delegate.getAppEngineWebXml();
    }

    @Override
    public CronXml getCronXml() {
        return delegate.getCronXml();
    }

    @Override
    public QueueXml getQueueXml() {
        return delegate.getQueueXml();
    }

    @Override
    public DosXml getDosXml() {
        return delegate.getDosXml();
    }

    @Override
    public String getPagespeedYaml() {
        return delegate.getPagespeedYaml();
    }

    @Override
    public IndexesXml getIndexesXml() {
        return delegate.getIndexesXml();
    }

    public WebXml getWebXml() {
        return delegate.getWebXml();
    }

    @Override
    public BackendsXml getBackendsXml() {
        return delegate.getBackendsXml();
    }

    @Override
    public String getApiVersion() {
        return delegate.getApiVersion();
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public java.io.File getStagingDir() {
        return delegate.getStagingDir();
    }

    @Override
    public void resetProgress() {
        delegate.resetProgress();
    }

    @Override
    public java.io.File createStagingDirectory(AppAdminFactory.ApplicationProcessingOptions opts,
                                               ResourceLimits resourceLimits) throws IOException {
        return delegate.createStagingDirectory(opts, resourceLimits);
    }

    @Override
    public void cleanStagingDirectory() {
        delegate.cleanStagingDirectory();
        deleteRecursive(new java.io.File(getPath()));
    }

    @Override
    public void setListener(UpdateListener l) {
        delegate.setListener(l);
    }

    @Override
    public void setDetailsWriter(PrintWriter detailsWriter) {
        delegate.setDetailsWriter(detailsWriter);
    }

    @Override
    public void statusUpdate(String message, int amount) {
        delegate.statusUpdate(message, amount);
    }

    @Override
    public void statusUpdate(String message) {
        delegate.statusUpdate(message);
    }

    @Override
    public String getAppYaml() {
        return delegate.getAppYaml();
    }
    
    /**
     * Not implement yet. Don't use. Always return null
     * 
     * @see com.google.appengine.tools.admin.GenericApplication#getInstanceClass()
     */
    @Override
    public String getInstanceClass() {
        return null;
    }
}
