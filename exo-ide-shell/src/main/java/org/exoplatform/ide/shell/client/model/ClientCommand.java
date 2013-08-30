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
package org.exoplatform.ide.shell.client.model;

import org.exoplatform.ide.shell.client.cli.CommandLine;
import org.exoplatform.ide.shell.client.cli.HelpFormatter;
import org.exoplatform.ide.shell.client.cli.Options;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;

import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 10, 2011 evgen $
 */
public abstract class ClientCommand extends CLIResource {

    protected Options options;

    public ClientCommand(Set<String> command, String path, String method, Set<String> consumes, Set<String> produces,
                         Set<CLIResourceParameter> params, String description) {
        super(command, path, method, consumes, produces, params, description);
    }

    public ClientCommand(Set<String> command, String path, String method, Set<String> consumes, Set<String> produces,
                         Set<CLIResourceParameter> params) {
        this(command, path, method, consumes, produces, params, null);
    }

    /**
     *
     */
    public ClientCommand(Set<String> command, Options options, String description) {
        super(command, null, null, null, null, null, description);
        this.options = options;
        options.addOption("h", false, "display this help");
    }

    /** @return the options */
    public Options getOptions() {
        return options;
    }

    public void printHelp(String usage) {
        printHelp(usage, null);
    }

    public void printHelp(String usage, String header) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(usage, header, options, null);
    }

    public abstract void execute(CommandLine commandLine);

}
