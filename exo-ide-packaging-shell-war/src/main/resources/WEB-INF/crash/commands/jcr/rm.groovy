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
import org.crsh.command.ScriptException;
import org.crsh.command.Description;
import org.crsh.command.InvocationContext
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Man
import org.crsh.jcr.command.Path
import org.crsh.cmdline.annotations.Argument;

public class rm extends org.crsh.jcr.command.JCRCommand {

  @Command
  @Usage("remove one or several node or a property")
  @Man("""\
The rm command removes a node or property specified by its path either absolute or relative. This operation
is executed against the JCR session, meaning that it will not be effective until it is commited to the JCR server.

[/]% rm foo
Node /foo removed

It is possible to specify several nodes.

[/]% rm foo bar
Node /foo /bar removed

rm is a <Node,Void> command removing all the consumed nodes.""")
  public void main(
    InvocationContext<Node, Void> context,
    @Argument @Usage("the paths to remove") @Man("The paths of the node to remove") List<Path> paths) throws ScriptException {
    assertConnected();

    //
    context.writer <<= 'Removed nodes ';

    //
    if (context.piped) {
      if (paths != null && paths.empty)
        throw new ScriptException("No path arguments are permitted in a pipe");

      // Node stream
      context.consume().each { node ->
        context.writer <<= " $node.path";
        node.remove();
      };
    } else {
      // First collect nodes
      def nodes = [];
      paths.each { path ->
        def node = getNodeByPath(path);
        if (node == null)
          throw new ScriptException("Node path does not exist");
        nodes.add(node);
      };
      // Then remove if we have been able to find them all
      nodes.each { node ->
        context.writer <<= " $node.path";
        node.remove();
      };
    }
  }
}