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
package org.exoplatform.ide.extension.cloudfoundry.server.json;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CreateApplication {
    /*
     * JSON output:
     * {
     *    name: "my-app",
     *    uris: ["my-app.cloudfoundry.com"],
     *    instances: 1,
     *    resources: {memory: 128},
     *    staging: {framework: "sinatra", runtime: "ruby18"}
     * }
     */
    private final String name;
    private final int    instances;
    private final String url;
    private final int    memory;
    private final String framework;
    private final String runtime;
    private final String command;

    private Map<String, String> options;

    public CreateApplication(String name,
                             int instances,
                             String url,
                             int memory,
                             String framework,
                             String runtime,
                             String command) {
        this.name = name;
        this.instances = instances;
        this.url = url;
        this.memory = memory;
        this.framework = framework;
        this.runtime = runtime;
        this.command = command;
    }

    // Next getters for correct serialization to JSON format.

    /** Application name. */
    public String getName() {
        return name;
    }

    /** Number of VM instances used for application. */
    public int getInstances() {
        return instances;
    }

    /** Cloudfoundry API expected for array of string with one item. */
    public String[] getUris() {
        return new String[]{url};
    }

    /** Application resources. Just one parameter: memory size in MB. */
    public Map<String, Integer> getResources() {
        return Collections.singletonMap("memory", memory);
    }

    /**
     * Staging, send framework name only, 'runtime' and 'command' are optional. Send them only for 'standalone'
     * application.
     */
    public Map<String, String> getStaging() {
        Map<String, String> m = new HashMap<String, String>(3);
        m.put("framework", framework);
        m.put("runtime", runtime);
        if (!(command == null || command.isEmpty())) {
            m.put("command", command);
        }
        return m;
    }

    public Map<String, String> getOptions() {
        if (options == null) {
            options = new HashMap<String, String>();
        }
        return options;
    }

    @Override
    public String toString() {
        return "CreateApplication{" +
               "name='" + name + '\'' +
               ", instances=" + instances +
               ", url='" + url + '\'' +
               ", memory=" + memory +
               ", framework='" + framework + '\'' +
               ", runtime='" + runtime + '\'' +
               ", command='" + command + '\'' +
               ", options=" + options +
               '}';
    }
}
