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
