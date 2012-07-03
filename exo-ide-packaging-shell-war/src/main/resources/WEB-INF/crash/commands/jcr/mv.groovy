/**
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
import javax.jcr.Node;

import org.crsh.command.ScriptException;
import org.crsh.command.InvocationContext
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Man
import org.crsh.jcr.command.Path
import org.crsh.cmdline.annotations.Argument;

public class mv extends org.crsh.jcr.command.JCRCommand {

  @Command
  @Usage("move a node")
  @Man("""\
The mv command can move a node to a target location in the JCR tree. It can be used also to rename a node. The mv
command is a <Node,Node> command consuming a stream of node to move them and producing nodes that were moved.

[/registry]% mv Registry Registry2""")


  public void main(
    InvocationContext<Node, Node> context,
    @Argument @Usage("the source path") @Man("The path of the source node to move, absolute or relative") Path source,
    @Argument @Usage("the target path") @Man("The destination path absolute or relative") Path target) throws ScriptException {
    assertConnected()

    //
    if (context.piped) {
      if (target != null)
        throw new ScriptException("Only one argument is permitted when involved in a pipe");
      def targetParent = findNodeByPath(source);
      context.consume().each { node ->
        def targetPath = targetParent.path + "/" + node.name;
        session.workspace.move(node.path, targetPath);
        context.produce(node);
      };
    } else {
      def sourceNode = findNodeByPath(source);
      def targetPath = absolutePath(target);
      sourceNode.session.workspace.move(sourceNode.path, targetPath.string);
      def targetNode = findNodeByPath(targetPath);
      context.produce(targetNode);
    }
  }
}