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
import org.crsh.shell.ui.UIBuilder
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Option;
import org.crsh.cmdline.annotations.Argument
import org.crsh.jcr.command.Path

public class ls extends org.crsh.jcr.command.JCRCommand {

  @Usage("list the content of a node")
  @Man("""\
The ls command displays the content of a node. By default it lists the content of the current node, however it also
accepts a path argument that can be absolute or relative.

[/]% ls
/
+-properties
| +-jcr:primaryType: nt:unstructured
| +-jcr:mixinTypes: [exo:owneable,exo:privilegeable]
| +-exo:owner: '__system'
| +-exo:permissions: [any read,*:/platform/administrators read,*:/platform/administrators add_node,*:/platform/administrators set_property,*:/platform/administrators remove]
+-children
| +-/workspace
| +-/contents
| +-/Users
| +-/gadgets
| +-/folder""")
  @Command
  public Object main(
  @Usage("the path to list") @Man("The path of the node content to list") @Argument Path path,
  @Usage("the tree depth") @Man("The depth of the printed tree") @Option(names=["d","depth"]) Integer depth) throws ScriptException {
    assertConnected();

    //
    def node = path == null ? getCurrentNode() : findNodeByPath(path);
    if (depth == null || depth < 1) {
      depth = 1;
    }

    //
    def builder = new UIBuilder();
    formatNode(builder, node, depth, depth);
    return builder;
  }
}