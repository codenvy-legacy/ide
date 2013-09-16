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

import com.google.appengine.repackaged.net.sourceforge.yamlbeans.YamlException;
import com.google.appengine.repackaged.net.sourceforge.yamlbeans.YamlReader;
import com.google.appengine.repackaged.net.sourceforge.yamlbeans.YamlWriter;
import com.google.apphosting.utils.config.AppEngineConfigException;

import java.io.Reader;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

/**
 * Java representation of application yaml file.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class YamlAppInfo {
    // https://developers.google.com/appengine/docs/python/config/appconfig#Skipping_Files
//   private static List DEFAULT_SKIP_FILES = Arrays.asList(
//      "^(.*/)?#.*#",
//      "^(.*/)?.*~",
//      "^(.*/)?.*\\.py[co]",
//      "^(.*/)?.*/RCS/.*",
//      "^(.*/)?\\..*",
//      "^(.*/)?\\.project", // IDE project file
//      "^(.*/)?.*\\.bak"
//   );

    public String application;
    public String version;
    public String runtime;
    public String api_version;
    public List   builtins;
    public String includes;
    public List   handlers;
    public List   libraries;
    public List   inbound_services;
    public String default_expiration;
    //   public List skip_files;
    //   public Object nobuild_files;
    public String derived_file_type;
    public Map    admin_console;
    public List   error_handlers;
    public List   backends;
    public String threadsafe = "false";
    public String api_config;
    public String code_lock;
    public Map    env_variables;

    public static YamlAppInfo parse(Reader reader) {
        YamlReader yamlReader = new YamlReader(reader);
        try {
            return yamlReader.read(YamlAppInfo.class);
        } catch (YamlException ye) {
            throw new AppEngineConfigException(ye.getMessage(), ye);
        }
    }

    public String toYaml() {
//      if (skip_files == null)
//      {
//         skip_files = DEFAULT_SKIP_FILES;
//      }
        try {
            StringWriter buf = new StringWriter();
            YamlWriter yamlWriter = new YamlWriter(buf);
            yamlWriter.write(this);
            yamlWriter.close();
            return buf.toString();
        } catch (YamlException ye) {
            throw new AppEngineConfigException(ye.getMessage(), ye);
        }
    }

    @Override
    public String toString() {
        return "YamlAppInfo{" +
               "application='" + application + '\'' +
               ", version='" + version + '\'' +
               ", runtime='" + runtime + '\'' +
               ", api_version='" + api_version + '\'' +
               ", builtins=" + builtins +
               ", includes='" + includes + '\'' +
               ", handlers=" + handlers +
               ", libraries=" + libraries +
               ", inbound_services=" + inbound_services +
               ", default_expiration='" + default_expiration + '\'' +
//         ", skip_files=" + skip_files +
               ", derived_file_type='" + derived_file_type + '\'' +
               ", admin_console=" + admin_console +
               ", error_handlers=" + error_handlers +
               ", backends=" + backends +
               ", threadsafe='" + threadsafe + '\'' +
               ", api_config='" + api_config + '\'' +
               ", code_lock='" + code_lock + '\'' +
               ", env_variables=" + env_variables +
               '}';
    }
}
