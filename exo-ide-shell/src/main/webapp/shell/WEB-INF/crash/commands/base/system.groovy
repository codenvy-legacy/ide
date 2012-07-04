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
package crash.commands.base

import org.crsh.command.CRaSHCommand
import org.crsh.cmdline.annotations.Usage
import org.crsh.cmdline.annotations.Command
import org.crsh.command.InvocationContext
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import org.crsh.cmdline.annotations.Man
import org.crsh.cmdline.annotations.Argument
import org.crsh.cmdline.spi.Value
import org.crsh.cmdline.spi.Completer
import org.crsh.cmdline.ParameterDescriptor

/**
 * @author <a href="mailto:alain.defrance@exoplatform.com">Alain Defrance</a>
 * @version $Revision$
 */
@Usage("vm system properties commands")
class system extends CRaSHCommand implements Completer {

  @Usage("list the vm system properties")
  @Command
  public void ls(
    InvocationContext<Void, Map.Entry> context) {

    def formatString = "%1\$-35s %2\$-20s\r\n";
    Formatter formatter = new Formatter(context.writer);
    formatter.format(formatString, "NAME", "VALUE");

    System.getProperties().each {
      if (it != null) {
        formatter.format formatString, it.key, it.value
        context.produce it
      }
    }
  }

  @Usage("set the vm system properties")
  @Command
  public void set(
    InvocationContext<Void, Void> context,
    @PropertyName PropName name,
    @PropertyValue String value) {
    System.setProperty name.toString(), value
  }

  @Usage("get the vm system properties")
  @Command
  public void get(
    InvocationContext<Void, Void> context,
    @PropertyName PropName name) {
    context.writer.print System.getProperty(name.toString())
  }

  @Usage("remove the vm system properties")
  @Command
  public void remove(
    InvocationContext<Void, Void> context,
    @PropertyName PropName name) {
    System.clearProperty name.toString()
  }

  Map<String, Boolean> complete(ParameterDescriptor<?> parameter, String prefix)
  {
    def c = [:];
    if (parameter.getJavaValueType() == PropName.class) {
      System.getProperties().each() {
        if (it.key.startsWith(prefix)) {
          c.put(it.key.substring(prefix.length()), true);
        }
      }
    }
    return c;
  }
}

@Retention(RetentionPolicy.RUNTIME)
@Usage("the property name")
@Man("The name of the property")
@Argument(name = "name")
@interface PropertyName { }
class PropName extends Value {
  PropName(String string) {
    super(string)
  }
}

@Retention(RetentionPolicy.RUNTIME)
@Usage("the property value")
@Man("The value of the property")
@Argument(name = "value")
@interface PropertyValue { }