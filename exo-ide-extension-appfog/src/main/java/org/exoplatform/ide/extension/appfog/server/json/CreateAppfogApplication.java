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
package org.exoplatform.ide.extension.appfog.server.json;

import org.exoplatform.ide.extension.appfog.shared.Infra;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateAppfogApplication {
   /*
    * JSON output:
    * {
    *    name: "my-app",
    *    uris: ["my-app.aws.af.cm"],
    *    instances: 1,
    *    resources: {memory: 128},
    *    staging: {framework: "sinatra", runtime: "ruby18"},
    *    infra: {provider: "aws", name: "aws"}
    * }
    */

    /** Usually take one parameter called "provider" with value of the name infrastructure. */
    private Infra infra;

    private final String name;
    private final int    instances;
    private final String url;
    private final int    memory;
    private final String framework;
    private final String runtime;
    private final String command;

    private Map<String, String> options;

    public CreateAppfogApplication(String name,
                                   int instances,
                                   String url,
                                   int memory,
                                   String framework,
                                   String runtime,
                                   String command,
                                   Infra infra) {
        this.name = name;
        this.instances = instances;
        this.url = url;
        this.memory = memory;
        this.framework = framework;
        this.runtime = runtime;
        this.command = command;
        this.infra = infra;
    }

    public String getName() {
        return name;
    }

    public int getInstances() {
        return instances;
    }

    public String[] getUris() {
        return new String[]{url};
    }

    public Map<String, Integer> getResources() {
        return Collections.singletonMap("memory", memory);
    }

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

    public Infra getInfra() {
        return infra;
    }

    public void setInfra(Infra infra) {
        this.infra = infra;
    }

    @Override
    public String toString() {
        return "CreateAppfogApplication{" +
               "infra=" + infra +
               ", name='" + name + '\'' +
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
