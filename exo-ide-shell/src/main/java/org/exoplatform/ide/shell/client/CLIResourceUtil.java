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
package org.exoplatform.ide.shell.client;

import org.exoplatform.ide.shell.client.cli.*;
import org.exoplatform.ide.shell.shared.CLIResource;
import org.exoplatform.ide.shell.shared.CLIResourceParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 5, 2011 10:51:12 AM anya $
 */
public class CLIResourceUtil {
    /**
     * Parse the string command line to {@link CommandLine} instance. Specify the <b>parameters</b> of the concrete command, that
     * you need to parse.
     *
     * @param cmd
     *         command line
     * @param parameters
     *         command's parameters
     * @return {@link CommandLine} parsed command line
     * @throws Exception
     */
    public static CommandLine parseCommandLine(String cmd, Set<CLIResourceParameter> parameters) throws Exception {
        String[] arguments = Util.translateCommandline(cmd);
        Parser parser = new GnuParser();
        Options options = formOptions(parameters);
        return parser.parse(options, arguments);
    }

    /**
     * Form the list of options, that are available for pointed parameters and are necessary for parse command liine operation.
     *
     * @param parameters
     *         command's parameters
     * @return {@link Options} options
     */
    protected static Options formOptions(Set<CLIResourceParameter> parameters) {
        Options options = new Options();
        if (parameters == null)
            return options;

        for (CLIResourceParameter parameter : parameters) {
            if (parameter.getOptions() != null) {
                String optionName = null;
                String longOpt = null;
                // Get options (long format starts with "--")
                for (String opt : parameter.getOptions()) {
                    // Only the names of options must be pointed (without "-" and "--").
                    if (opt.startsWith("--")) {
                        longOpt = opt.replace("--", "");
                    } else {
                        optionName = opt.startsWith("-") ? opt.replaceFirst("-", "") : opt;
                    }
                }
                optionName = (optionName == null) ? longOpt : optionName;
                if (optionName == null) {
                    continue;
                }
                // TODO No description at the moment:
                options.addOption(new Option(optionName, longOpt, parameter.isHasArg(), ""));
            }
        }
        return options;
    }

    /**
     * Get command names form {@link CLIResource} set
     *
     * @param commands
     * @return list of all command names
     */
    public static List<String> getAllCommandNames(Set<CLIResource> commands) {
        List<String> names = new ArrayList<String>();
        for (CLIResource res : commands) {
            names.addAll(res.getCommand());
        }
        return names;
    }
}
