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
package org.exoplatform.cloudshell.client;

import junit.framework.Assert;

import org.exoplatform.ide.shell.client.cli.*;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ComandLineParserTest {

    String tabl = "/ide/git/commit;POST=git commit\n" + "git commit.body.params=message,all\n"
                  + "git commit.body.message=-m\n" + "git commit.body.all=-a\n";

    @Test
    public void parserTest() throws Exception {
        String cmd = "git commit -m='My first commit'      -a false";
        String[] args = Util.translateCommandline(cmd);
        Option msg = new Option("m", true, "Commit message");
        msg.setLongOpt("message");
        Option a = new Option("a", true, "Add file");
        Options options = new Options();
        options.addOption(a);
        options.addOption(msg);
        Parser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);
        Assert.assertEquals("My first commit", line.getOptionValue("m"));
        Assert.assertFalse(Boolean.valueOf(line.getOptionValue("a")));

        String[] commands = tabl.split("\n");
        String command = commands[0].split("=")[1];

        String url = "";
        if (command.equalsIgnoreCase(line.getArgs()[0] + " " + line.getArgs()[1])) {
            url = commands[0].split("=")[0];
        }
        String body = "{\"message\":\"" + line.getOptionValue("m") + "\",\"all\":" + line.getOptionValue("a") + "\"}";

    }

    @Test
    public void parserLongOptTest() throws Exception {
        String cmd = "git commit --message='My first commit'      -a false";
        String[] args = Util.translateCommandline(cmd);
        Option msg = new Option("m", true, "Commit message");
        msg.setLongOpt("message");
        Option a = new Option("a", true, "Add file");
        Options options = new Options();
        options.addOption(a);
        options.addOption(msg);
        Parser parser = new GnuParser();
        CommandLine line = parser.parse(options, args);
        Assert.assertEquals("My first commit", line.getOptionValue("m"));
        Assert.assertFalse(Boolean.valueOf(line.getOptionValue("a")));

    }

}
