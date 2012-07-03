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
import javax.jcr.query.Query;
import org.crsh.shell.ui.UIBuilder;
import org.crsh.command.Description
import org.crsh.cmdline.annotations.Usage
import org.crsh.command.InvocationContext
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Command
import org.crsh.cmdline.annotations.Option
import org.crsh.cmdline.annotations.Argument;

@Description("Executes a query with the XPATH dialect, by default results are limited to 5 ")
public class xpath extends org.crsh.command.CRaSHCommand {

  @Usage("execute a JCR xpath query")
  @Command
  @Man("""Executes a JCR query with the xpath dialect, by default results are limited to 5.\
All results matched by the query are produced by this command.""")
  public void main(
    InvocationContext<Void, Node> context,
    @Option(names=["o","offset"])
    @Usage("the result offset")
    @Man("The offset of the first node to display")
    Integer offset,
    @Option(names=["l","limit"])
    @Usage("the result limit")
    @Man("The number of nodes displayed, by default this value is equals to 5")
    Integer limit,
    @Option(names=["a","all"])
    @Usage("ignore the limit argument")
    @Man("Display all the results by ignoring the limit argument, this should be used with care for large result set")
    Boolean all,
    @Argument
    @Usage("the query")
    @Man("The query")
    String query) throws ScriptException {
    assertConnected();
    assertConnected();

    //
    offset = offset ?: 0;
    limit = limit ?: 5;
    all = all ?: false;

    //
    if (offset < 0) {
      throw new ScriptException("No negative offset accepted; $offset");
    }

    //
    if (limit < 0) {
      throw new ScriptException("No negative limit accepted: -limit");
    }

    //
    if (all) {
      limit = null;
    }

    //
    def queryMgr = session.workspace.queryManager;

    //
    def select = queryMgr.createQuery(query, Query.XPATH);

    //
    def result = select.execute();

    //
    def nodes = result.nodes;
    def total = nodes.size;
    if (offset > 0) {
      nodes.skip(offset);
    }

    //
    def builder = new UIBuilder();

    //
    builder.node("The query matched " + total + " nodes") {
      def index = 0;
      while (nodes.hasNext()) {
        def n = nodes.next();
        if (limit != null && index >= limit)
          break;
        formatNode(builder, n, 0, 1);
        index++;
      }
    }

    //
    context.writer.print(builder);
  }
}