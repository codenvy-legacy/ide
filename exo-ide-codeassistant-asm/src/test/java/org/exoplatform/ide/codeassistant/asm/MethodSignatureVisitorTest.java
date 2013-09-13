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
package org.exoplatform.ide.codeassistant.asm;

import org.exoplatform.ide.codeassistant.asm.visitors.MethodSignatureVisitor;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.signature.SignatureReader;

public class MethodSignatureVisitorTest {

    @Test
    public void testMethodWithOneParamAndBaseReturn() {
        final String signature = "(Ljava/util/Set<Ljava/lang/Class<*>;>;)D";
        SignatureReader reader = new SignatureReader(signature);
        MethodSignatureVisitor v = new MethodSignatureVisitor();
        reader.accept(v);
        Assert.assertEquals(1, v.getParameters().size());
        Assert.assertEquals("java.util.Set<java.lang.Class<?>>", v.getParameters().get(0));

        Assert.assertEquals("double", v.getReturnType());
    }

    @Test
    public void testMethodWithManyParamsAndBaseReturn() {
        final String signature = "(Ljava/lang/String;Ljava/util/List<Ljava/lang/String;>;Ljava/lang/Class<*>;)V";
        SignatureReader reader = new SignatureReader(signature);
        MethodSignatureVisitor v = new MethodSignatureVisitor();
        reader.accept(v);
        Assert.assertEquals(3, v.getParameters().size());
        Assert.assertEquals("java.lang.String", v.getParameters().get(0));
        Assert.assertEquals("java.util.List<java.lang.String>", v.getParameters().get(1));
        Assert.assertEquals("java.lang.Class<?>", v.getParameters().get(2));

        Assert.assertEquals("void", v.getReturnType());
    }

    @Test
    public void testMethodWithFormalType() {
        final String signature = "<T:Ljava/lang/Number;InputStream:Ljava/lang/Object;>(TT;)TT;";
        SignatureReader reader = new SignatureReader(signature);
        MethodSignatureVisitor v = new MethodSignatureVisitor();
        reader.accept(v);
        Assert.assertEquals(1, v.getParameters().size());
        Assert.assertEquals("T", v.getParameters().get(0));

        Assert.assertEquals("T", v.getReturnType());
    }

    @Test
    public void testMethodArrays() {
        final String signature = "<T:Ljava/lang/Number;InputStream:Ljava/lang/Object;>([Ljava/lang/String;[[I[[[TT;)[[D";
        SignatureReader reader = new SignatureReader(signature);
        MethodSignatureVisitor v = new MethodSignatureVisitor();
        reader.accept(v);
        Assert.assertEquals(3, v.getParameters().size());
        Assert.assertEquals("java.lang.String[]", v.getParameters().get(0));
        Assert.assertEquals("int[][]", v.getParameters().get(1));
        Assert.assertEquals("T[][][]", v.getParameters().get(2));

        Assert.assertEquals("double[][]", v.getReturnType());
    }

    @Test
    public void testMethodGenerics() {
        final String signature =
                "<T:Ljava/lang/Number;InputStream:Ljava/lang/Object;K:Ljava/lang/Number;V::Ljava/util/List<+Ljava/lang/Object;>;>"
                + "(Ljava/util/Map<Ljava/util/Map<Ljava/util/Map<TK;+Ljava/util/Map<-Ljava/util/List<Ljava/lang/Number;>;TV;>;>;"
                + "Ljava/lang/Number;>;TV;>;Ljava/util/Set<Ljava/util/Comparable<TT;>;>;)"
                + "Ljava/util/Map<TT;Ljava/util/Comparable<+TT;>;>;";
        SignatureReader reader = new SignatureReader(signature);
        MethodSignatureVisitor v = new MethodSignatureVisitor();
        reader.accept(v);
        Assert.assertEquals(2, v.getParameters().size());
        Assert.assertEquals("java.util.Map<java.util.Map<java.util.Map<K, ? extends "
                            + "java.util.Map<? super java.util.List<java.lang.Number>, V>>, java.lang.Number>, V>",
                            v.getParameters().get(0));
        Assert.assertEquals("java.util.Set<java.util.Comparable<T>>", v.getParameters().get(1));

        Assert.assertEquals("java.util.Map<T, java.util.Comparable<? extends T>>", v.getReturnType());
    }

    @Test
    public void testMethodWithInnerClass() {
        final String signature =
                "(Ljava/lang/Class<+Lorg/exoplatform/ide/codeassistant/asm/test/Q$U;>;)Lorg/exoplatform/ide/codeassistant/asm/test/Q$U;";
        SignatureReader reader = new SignatureReader(signature);
        MethodSignatureVisitor v = new MethodSignatureVisitor();
        reader.accept(v);
        Assert.assertEquals(1, v.getParameters().size());
        Assert.assertEquals("java.lang.Class<? extends org.exoplatform.ide.codeassistant.asm.test.Q$U>", v
                .getParameters().get(0));

        Assert.assertEquals("org.exoplatform.ide.codeassistant.asm.test.Q$U", v.getReturnType());
    }

    @Test
    public void testMethodWithExceptions() {
        final String signature = "(Ljava/lang/Class<*>;)V";
        SignatureReader reader = new SignatureReader(signature);
        MethodSignatureVisitor v = new MethodSignatureVisitor();
        reader.accept(v);
        Assert.assertEquals(1, v.getParameters().size());
        Assert.assertEquals("java.lang.Class<?>", v.getParameters().get(0));

        Assert.assertEquals("void", v.getReturnType());
    }

    @Test
    public void testMethodWithExceptionAsParam() {
        final String signature =
                "<T:Ljava/util/ConcurrentModificationException;>(Ljava/util/ConcurrentModificationException;)TT;";
        SignatureReader reader = new SignatureReader(signature);
        MethodSignatureVisitor v = new MethodSignatureVisitor();
        reader.accept(v);
        Assert.assertEquals(1, v.getParameters().size());
        Assert.assertEquals("java.util.ConcurrentModificationException", v.getParameters().get(0));

        Assert.assertEquals("T", v.getReturnType());
    }

}
