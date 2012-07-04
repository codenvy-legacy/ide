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
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Command
import org.crsh.command.InvocationContext
import org.crsh.cmdline.annotations.Argument
import org.crsh.cmdline.annotations.Required
import org.crsh.jcr.command.Path;

@Usage("mixin commands")
@Man("""The mixin command manipulates JCR node mixins. Mixins can be added to or removed from nodes.""")
public class mixin extends org.crsh.jcr.command.JCRCommand {

  // It consumes a node stream or path arguments
  @Usage("add a mixin to one or several nodes")
  @Man("""\
The add command addds a mixin to one or several nodes, this command is a <Node,Void> command, and can
add a mixin from an incoming node stream, for instance:

[/]% select * from mynode | mixin add mix:versionable
""")
  @Command
  public void add(
     InvocationContext<Node, Void> context,
     @Usage("the mixin name to add")
     @Argument
     @Required
     String mixin,
     @Argument @Usage("the paths of the node receiving the mixin") List<Path> paths)
  {
     context.writer <<= "Mixin $mixin added to nodes";
     perform(context, paths, { node ->
       node.addMixin(mixin);
       context.writer <<= " $node.path";
     });
  }

  // It consumes a node stream or path arguments
  @Usage("removes a mixin from one or several nodes")
  @Man("""\
The remove command removes a mixin from one or several nodes, this command is a <Node,Void> command, and can
remove a mixin from an incoming node stream, for instance:

[/]% select * from mynode | mixin remove mix:versionable
""")
  @Command
  public void remove(
     InvocationContext<Node,Void> context,
     @Usage("the mixin name to remove")
     @Required
     String mixin,
     @Argument @Usage("the paths of the node receiving the mixin") List<Path> paths)
  {
     context.writer <<= "Mixin $mixin removed from nodes";
     perform(context, paths, { node ->
       node.removeMixin(mixin);
       context.writer <<= " $node.path";
     });
  }

  private void perform(InvocationContext<Node,Void> context, List<Path> paths, def closure) {
    assertConnected();
    if (context.piped) {
      if (paths != null && !paths.empty) {
        throw new ScriptException("No path arguments are permitted in a pipe");
      }
      context.consume().each { node ->
        closure(node);
      };
    } else {
      paths.each { path ->
        def node = getNodeByPath(path);
        closure(node);
      };
    }
  }
}