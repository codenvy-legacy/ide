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
package org.exoplatform.ide.extension.googleappengine.server.python;

import com.google.appengine.repackaged.net.sourceforge.yamlbeans.YamlException;
import com.google.appengine.repackaged.net.sourceforge.yamlbeans.YamlReader;
import com.google.appengine.tools.admin.AppAdminFactory.ApplicationProcessingOptions;
import com.google.appengine.tools.admin.Application;
import com.google.appengine.tools.admin.GenericApplication;
import com.google.appengine.tools.admin.ResourceLimits;
import com.google.appengine.tools.admin.UpdateListener;
import com.google.apphosting.utils.config.AppEngineConfigException;
import com.google.apphosting.utils.config.BackendsXml;
import com.google.apphosting.utils.config.BackendsYamlReader;
import com.google.apphosting.utils.config.CronXml;
import com.google.apphosting.utils.config.CronYamlReader;
import com.google.apphosting.utils.config.DosXml;
import com.google.apphosting.utils.config.DosYamlReader;
import com.google.apphosting.utils.config.IndexYamlReader;
import com.google.apphosting.utils.config.IndexesXml;
import com.google.apphosting.utils.config.QueueXml;
import com.google.apphosting.utils.config.QueueYamlReader;

import org.exoplatform.ide.extension.googleappengine.server.YamlAppInfo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static com.codenvy.commons.lang.IoUtil.deleteRecursive;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PythonApplication implements GenericApplication {
    private final File           appDir;
    private final String         appDirPath;
    private       YamlAppInfo    appInfo;
    private       String         appInfoString;
    private       CronXml        cronXml;
    private       QueueXml       queueXml;
    private       DosXml         dosXml;
    private       IndexesXml     indexesXml;
    private       BackendsXml    backendsXml;
    private       File           stagingDir;
    private       UpdateListener updateListener;
    private       PrintWriter    errorWriter;

    private final Map<String, Pattern> staticFilesPatterns = new HashMap<String, Pattern>();

    public PythonApplication(File appDir) {
        this.appDir = appDir;
        appDirPath = appDir.getAbsolutePath();

        appInfo = readAppYaml(getPath() + "/app.yaml");

        CronYamlReader cronReader = new CronYamlReader(appDirPath);
        cronXml = cronReader.parse();

        QueueYamlReader queueYamlReader = new QueueYamlReader(appDirPath);
        queueXml = queueYamlReader.parse();

        DosYamlReader dosYamlReader = new DosYamlReader(appDirPath);
        dosXml = dosYamlReader.parse();

        indexesXml = readIndexYaml(getPath() + "/index.yaml");

        BackendsYamlReader backendsYaml = new BackendsYamlReader(appDirPath);
        backendsXml = backendsYaml.parse();
    }

    public static IndexesXml readIndexYaml(String path) {
        File indexFile = new File(path);
        if (!indexFile.exists()) {
            return null;
        }
        Reader fileReader = null;
        try {
            fileReader = new FileReader(indexFile);
            YamlReader reader = new YamlReader(fileReader);
            reader.getConfig().setPropertyElementType(IndexYamlReader.IndexYaml.class, "indexes",
                                                      IndexYamlReader.IndexYaml.Index.class);
            reader.getConfig().setPropertyElementType(IndexYamlReader.IndexYaml.Index.class, "properties",
                                                      IndexYamlReader.IndexYaml.Property.class);
            IndexYamlReader.IndexYaml indexYaml = reader.read(IndexYamlReader.IndexYaml.class);
            if (indexYaml == null || indexYaml.getIndexes() == null || indexYaml.getIndexes().isEmpty()) {
                // No index configured but file exists.
                // It looks like legal for python sdk but java sdk fails for the same situation.
                // Return null instead of empty index if index is not configured at all.
                return null;
            }
            return indexYaml.toXml(null);
        } catch (YamlException e) {
            throw new AppEngineConfigException(e.getMessage(), e);
        } catch (IOException ioe) {
            throw new AppEngineConfigException(ioe.getMessage(), ioe);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static YamlAppInfo readAppYaml(String path) {
        Reader fileReader = null;
        try {
            fileReader = new FileReader(path);
            YamlAppInfo appInfo = YamlAppInfo.parse(fileReader);
            List<Map> yamlErrorHandlers = appInfo.error_handlers;
            if (yamlErrorHandlers != null) {
                for (Map yamlErrorHandler : yamlErrorHandlers) {
                    if (yamlErrorHandler.get("mime_type") == null) {
                        yamlErrorHandler.put("mime_type",
                                             Application.guessContentTypeFromName((String)yamlErrorHandler.get("file")));
                    }
                    if (yamlErrorHandler.get("error_code") == null) {
                        yamlErrorHandler.put("error_code", "default");
                    }
                }
            }
            return appInfo;
        } catch (IOException ioe) {
            throw new AppEngineConfigException(ioe.getMessage(), ioe);
        } finally {
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public String getAppId() {
        return appInfo.application;
    }

    @Override
    public String getVersion() {
        return appInfo.version;
    }

    @Override
    public String getSourceLanguage() {
        return "Python";
    }

    @Override
    public String getServer() {
        return null; // ???
    }

    @Override
    public boolean isPrecompilationEnabled() {
        // ???
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<ErrorHandler> getErrorHandlers() {
        List<ErrorHandler> errorHandlers = new ArrayList<ErrorHandler>();
        List<Map> yamlErrorHandlers = appInfo.error_handlers;
        if (yamlErrorHandlers != null) {
            for (Map yamlErrorHandler : yamlErrorHandlers) {
                ErrorHandler appErrorHandler = new ErrorHandlerImpl(
                        (String)yamlErrorHandler.get("file"),
                        (String)yamlErrorHandler.get("error_code"),
                        (String)yamlErrorHandler.get("mime_type"));
                errorHandlers.add(appErrorHandler);
            }
        }
        return errorHandlers;
    }

    @Override
    public String getMimeTypeIfStatic(String path) {
        for (Object o : appInfo.handlers) {
            Map m = (Map)o;
            String staticDir = (String)m.get("static_dir");
            String regex = staticDir != null ? staticDir + ".*" : (String)m.get("upload");
            if (regex != null) {
                Pattern pattern = staticFilesPatterns.get(regex);
                if (pattern == null) {
                    pattern = Pattern.compile(regex);
                    staticFilesPatterns.put(regex, pattern);
                }
                if (pattern.matcher(path).matches()) {
                    String mimeType = (String)m.get("mime_type");
                    if (mimeType == null) {
                        mimeType = Application.guessContentTypeFromName(path);
                    }
                    return mimeType;
                }
            }
        }
        return null;
    }

    @Override
    public CronXml getCronXml() {
        return cronXml;
    }

    @Override
    public QueueXml getQueueXml() {
        return queueXml;
    }

    @Override
    public DosXml getDosXml() {
        return dosXml;
    }

    @Override
    public String getPagespeedYaml() {
        return null;
    }

    @Override
    public IndexesXml getIndexesXml() {
        return indexesXml;
    }

    @Override
    public BackendsXml getBackendsXml() {
        return backendsXml;
    }

    @Override
    public String getApiVersion() {
        return appInfo.api_version;
    }

    @Override
    public String getPath() {
        return appDirPath;
    }

    @Override
    public File getStagingDir() {
        return stagingDir;
    }

    @Override
    public void resetProgress() {
    }

    @Override
    public File createStagingDirectory(ApplicationProcessingOptions applicationProcessingOptions,
                                       ResourceLimits resourceLimits) throws IOException {
        // Do not create staging directory.
        return stagingDir;
    }

    @Override
    public void cleanStagingDirectory() {
        if (stagingDir != null) {
            deleteRecursive(stagingDir);
        }
        // Delete original application directory. Always get fresh copy of sources from IDE.
        deleteRecursive(appDir);
    }

    @Override
    public void setListener(UpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    @Override
    public void setDetailsWriter(PrintWriter printWriter) {
        errorWriter = printWriter;
    }

    @Override
    public void statusUpdate(String s, int i) {
    }

    @Override
    public void statusUpdate(String s) {
    }

    @Override
    public String getAppYaml() {
        if (appInfoString == null) {
            appInfoString = appInfo.toYaml();
        }
        return appInfoString;
    }

    public static class ErrorHandlerImpl implements ErrorHandler {
        private final String file;
        private final String errorCode;
        private final String mimeType;

        public ErrorHandlerImpl(String file, String errorCode, String mimeType) {
            this.file = file;
            this.errorCode = errorCode;
            this.mimeType = mimeType;
        }

        @Override
        public String getFile() {
            return file;
        }

        @Override
        public String getErrorCode() {
            return errorCode;
        }

        @Override
        public String getMimeType() {
            return mimeType;
        }
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
