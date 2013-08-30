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
import org.crsh.command.ScriptException;
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Required
import org.crsh.cmdline.annotations.Argument
import org.crsh.jcr.command.Path;

public class cp extends org.crsh.jcr.command.JCRCommand {

  @Usage("copy a node to another")
  @Command
  @Man("""\
The cp command copies a node to a target location in the JCR tree.

[/registry]% cp foo bar""")
  public void main(
    @Required @Usage("the source path") @Man("The path of the source node to copy") @Argument Path source,
    @Required @Usage("the target path") @Man("The path of the target node to be copied") @Argument Path target) throws ScriptException {
    assertConnected();

    //
    def sourceNode = findItemByPath(source);

    //
    def targetPath = absolutePath(target);

    //
    sourceNode.session.workspace.copy(sourceNode.path, targetPath.string);
  }
}