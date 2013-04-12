/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.commons;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonTest {
    public static class Foo {
        private String fooBar;

        public String getFooBar() {
            return fooBar;
        }

        public void setFooBar(String fooBar) {
            this.fooBar = fooBar;
        }
    }

    @Test
    public void testSerializeDefault() throws Exception {
        String expectedJson = "{\"fooBar\":\"test\"}";
        Foo foo = new Foo();
        foo.setFooBar("test");
        assertEquals(expectedJson, JsonHelper.toJson(foo));
    }

    @Test
    public void testSerializeUnderscore() throws Exception {
        String expectedJson = "{\"foo_bar\":\"test\"}";
        Foo foo = new Foo();
        foo.setFooBar("test");
        assertEquals(expectedJson, JsonHelper.toJson(foo, JsonNameConventions.CAMEL_UNDERSCORE));
    }

    @Test
    public void testSerializeDash() throws Exception {
        String expectedJson = "{\"foo-bar\":\"test\"}";
        Foo foo = new Foo();
        foo.setFooBar("test");
        assertEquals(expectedJson, JsonHelper.toJson(foo, JsonNameConventions.CAMEL_DASH));
    }

    @Test
    public void testDeserializeDefault() throws Exception {
        String json = "{\"fooBar\":\"test\"}";
        Foo foo = JsonHelper.fromJson(json, Foo.class, null);
        assertEquals("test", foo.getFooBar());
    }

    @Test
    public void testDeserializeUnderscore() throws Exception {
        String json = "{\"foo_bar\":\"test\"}";
        Foo foo = JsonHelper.fromJson(json, Foo.class, null, JsonNameConventions.CAMEL_UNDERSCORE);
        assertEquals("test", foo.getFooBar());
    }

    @Test
    public void testDeserializeDash() throws Exception {
        String json = "{\"foo-bar\":\"test\"}";
        Foo foo = JsonHelper.fromJson(json, Foo.class, null, JsonNameConventions.CAMEL_DASH);
        assertEquals("test", foo.getFooBar());
    }
}
