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
package com.codenvy.ide.ext.java.client.core;

import com.codenvy.ide.ext.java.client.BaseTest;
import com.codenvy.ide.ext.java.client.core.quickfix.TestOptions;
import com.codenvy.ide.ext.java.emul.FileSystem;
import com.codenvy.ide.ext.java.jdt.core.JavaCore;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.AST;
import com.codenvy.ide.ext.java.jdt.core.dom.ASTParser;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.core.dom.FieldDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.ITypeBinding;
import com.codenvy.ide.ext.java.jdt.core.dom.TypeDeclaration;
import com.codenvy.ide.ext.java.jdt.core.dom.VariableDeclarationFragment;
import com.codenvy.ide.ext.java.jdt.internal.corext.dom.TypeRules;
import com.codenvy.ide.ext.java.worker.WorkerMessageHandler;
import com.googlecode.gwt.test.utils.GwtReflectionUtils;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Ignore
public class TypeRulesTest extends BaseTest {

    /**
     *
     */
    private static final FileSystem NAME_ENVIRONMENT = new FileSystem(new String[]{System.getProperty("java.home")
                                                                                   + "/lib/rt.jar"}, null, "UTF-8");
    private HashMap<String, String> options;

    @Before
    public void setUp() throws Exception {
        options = TestOptions.getDefaultOptions();
        options.put(JavaCore.COMPILER_PB_NO_EFFECT_ASSIGNMENT, JavaCore.IGNORE);
        options.put(JavaCore.COMPILER_PB_UNNECESSARY_TYPE_CHECK, JavaCore.IGNORE);
        options.put(JavaCore.COMPILER_PB_UNUSED_LOCAL, JavaCore.IGNORE);
        options.put(JavaCore.COMPILER_PB_UNCHECKED_TYPE_OPERATION, JavaCore.IGNORE);
        new WorkerMessageHandler(null);
        GwtReflectionUtils.setPrivateFieldValue(WorkerMessageHandler.get(), "nameEnvironment", new FileSystem(
                new String[]{System.getProperty("java.home") + "/lib/rt.jar"}, null, "UTF-8"));
//      JdtExtension.get().getOptions().putAll(options);
    }

    @Test
    public void testCanAssign() throws Exception {
        VariableDeclarationFragment[] targets = createVariables();

        StringBuffer errors = new StringBuffer();
        for (int k = 0; k < targets.length; k++) {
            for (int n = 0; n < targets.length; n++) {
                VariableDeclarationFragment f1 = targets[k];
                VariableDeclarationFragment f2 = targets[n];
                String line = f2.getName().getIdentifier() + "= " + f1.getName().getIdentifier();

                StringBuffer buf = new StringBuffer();
                buf.append("package test1;\n");
                buf.append("import java.util.Vector;\n");
                buf.append("import java.util.Collection;\n");
                buf.append("import java.io.Serializable;\n");
                buf.append("import java.net.Socket;\n");
                buf.append("public class E<T, U extends Number> {\n");
                buf.append("    boolean bool= false;\n");
                buf.append("    char c= 0;\n");
                buf.append("    byte b= 0;\n");
                buf.append("    short s= 0;\n");
                buf.append("    int i= 0;\n");
                buf.append("    long l= 0;\n");
                buf.append("    float f= 0;\n");
                buf.append("    double d= 0;\n");

                buf.append("    Boolean bool_class= null;\n");
                buf.append("    Character c_class= null;\n");
                buf.append("    Byte b_class= null;\n");
                buf.append("    Short s_class= null;\n");
                buf.append("    Integer i_class= null;\n");
                buf.append("    Long l_class= null;\n");
                buf.append("    Float f_class= null;\n");
                buf.append("    Double d_class= null;\n");

                buf.append("    Object object= null;\n");
                buf.append("    Vector vector= null;\n");
                buf.append("    Socket socket= null;\n");
                buf.append("    Cloneable cloneable= null;\n");
                buf.append("    Collection collection= null;\n");
                buf.append("    Serializable serializable= null;\n");
                buf.append("    Object[] objectArr= null;\n");
                buf.append("    int[] int_arr= null;\n");
                buf.append("    long[] long_arr= null;\n");
                buf.append("    Vector[] vector_arr= null;\n");
                buf.append("    Socket[] socket_arr= null;\n");
                buf.append("    Collection[] collection_arr= null;\n");
                buf.append("    Object[][] objectArrArr= null;\n");
                buf.append("    Collection[][] collection_arrarr= null;\n");
                buf.append("    Vector[][] vector_arrarr= null;\n");
                buf.append("    Socket[][] socket_arrarr= null;\n");

                buf.append("    Collection<String> collection_string= null;\n");
                buf.append("    Collection<Object> collection_object= null;\n");
                buf.append("    Collection<Number> collection_number= null;\n");
                buf.append("    Collection<Integer> collection_integer= null;\n");
                buf.append("    Collection<? extends Number> collection_upper_number= null;\n");
                buf.append("    Collection<? super Number> collection_lower_number= null;\n");
                buf.append("    Vector<Object> vector_object= null;\n");
                buf.append("    Vector<Number> vector_number= null;\n");
                buf.append("    Vector<Integer> vector_integer= null;\n");
                buf.append("    Vector<? extends Number> vector_upper_number= null;\n");
                buf.append("    Vector<? super Number> vector_lower_number= null;\n");
                buf.append("    Vector<? extends Exception> vector_upper_exception= null;\n");
                buf.append("    Vector<? super Exception> vector_lower_exception= null;\n");

                buf.append("    T t= null;\n");
                buf.append("    U u= null;\n");
                buf.append("    Vector<T> vector_t= null;\n");
                buf.append("    Vector<U> vector_u= null;\n");
                buf.append("    Vector<? extends T> vector_upper_t= null;\n");
                buf.append("    Vector<? extends U> vector_upper_u= null;\n");
                buf.append("    Vector<? super T> vector_lower_t= null;\n");
                buf.append("    Vector<? super U> vector_lower_u= null;\n");
                buf.append("}\n");
                buf.append("class F<T, U extends Number> extends E<T, U> {\n");
                buf.append("    void foo() {\n");
                buf.append("        ").append(line).append(";\n");
                buf.append("    }\n");
                buf.append("}\n");
                char[] content = buf.toString().toCharArray();

                ASTParser parser = ASTParser.newParser(AST.JLS3);
                parser.setSource(content);
                parser.setResolveBindings(true);
                parser.setUnitName("E.java");
                parser.setNameEnvironment(NAME_ENVIRONMENT);
                parser.setCompilerOptions(options);
                CompilationUnit astRoot = (CompilationUnit)parser.createAST();
                IProblem[] problems = astRoot.getProblems();

                ITypeBinding b1 = f1.resolveBinding().getType();
                assertNotNull(b1);
                ITypeBinding b2 = f2.resolveBinding().getType();
                assertNotNull(b2);

                boolean res2 = TypeRules.canAssign(b1, b2);
                if (res2 != (problems.length == 0)) {
                    errors.append(line).append('\n');
                }
            }
        }
        assertTrue(errors.toString(), errors.length() == 0);
    }

    private VariableDeclarationFragment[] createVariables() {
        StringBuffer buf = new StringBuffer();
        buf.append("package test1;\n");
        buf.append("import java.util.Vector;\n");
        buf.append("import java.util.Collection;\n");
        buf.append("import java.io.Serializable;\n");
        buf.append("import java.net.Socket;\n");
        buf.append("public class E<T, U extends Number> {\n");
        buf.append("    boolean bool= false;\n");
        buf.append("    char c= 0;\n");
        buf.append("    byte b= 0;\n");
        buf.append("    short s= 0;\n");
        buf.append("    int i= 0;\n");
        buf.append("    long l= 0;\n");
        buf.append("    float f= 0;\n");
        buf.append("    double d= 0;\n");

        buf.append("    Boolean bool_class= null;\n");
        buf.append("    Character c_class= null;\n");
        buf.append("    Byte b_class= null;\n");
        buf.append("    Short s_class= null;\n");
        buf.append("    Integer i_class= null;\n");
        buf.append("    Long l_class= null;\n");
        buf.append("    Float f_class= null;\n");
        buf.append("    Double d_class= null;\n");

        buf.append("    Object object= null;\n");
        buf.append("    Vector vector= null;\n");
        buf.append("    Socket socket= null;\n");
        buf.append("    Cloneable cloneable= null;\n");
        buf.append("    Collection collection= null;\n");
        buf.append("    Serializable serializable= null;\n");
        buf.append("    Object[] objectArr= null;\n");
        buf.append("    int[] int_arr= null;\n");
        buf.append("    long[] long_arr= null;\n");
        buf.append("    Vector[] vector_arr= null;\n");
        buf.append("    Socket[] socket_arr= null;\n");
        buf.append("    Collection[] collection_arr= null;\n");
        buf.append("    Object[][] objectArrArr= null;\n");
        buf.append("    Collection[][] collection_arrarr= null;\n");
        buf.append("    Vector[][] vector_arrarr= null;\n");
        buf.append("    Socket[][] socket_arrarr= null;\n");

        buf.append("    Collection<String> collection_string= null;\n");
        buf.append("    Collection<Object> collection_object= null;\n");
        buf.append("    Collection<Number> collection_number= null;\n");
        buf.append("    Collection<Integer> collection_integer= null;\n");
        buf.append("    Collection<? extends Number> collection_upper_number= null;\n");
        buf.append("    Collection<? super Number> collection_lower_number= null;\n");
        buf.append("    Vector<Object> vector_object= null;\n");
        buf.append("    Vector<Number> vector_number= null;\n");
        buf.append("    Vector<Integer> vector_integer= null;\n");
        buf.append("    Vector<? extends Number> vector_upper_number= null;\n");
        buf.append("    Vector<? super Number> vector_lower_number= null;\n");
        buf.append("    Vector<? extends Exception> vector_upper_exception= null;\n");
        buf.append("    Vector<? super Exception> vector_lower_exception= null;\n");

        buf.append("    T t= null;\n");
        buf.append("    U u= null;\n");
        buf.append("    Vector<T> vector_t= null;\n");
        buf.append("    Vector<U> vector_u= null;\n");
        buf.append("    Vector<? extends T> vector_upper_t= null;\n");
        buf.append("    Vector<? extends U> vector_upper_u= null;\n");
        buf.append("    Vector<? super T> vector_lower_t= null;\n");
        buf.append("    Vector<? super U> vector_lower_u= null;\n");
        buf.append("}\n");

        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(buf.toString());
        parser.setResolveBindings(true);
        parser.setNameEnvironment(NAME_ENVIRONMENT);
        parser.setUnitName("E.java");
        CompilationUnit astRoot = (CompilationUnit)parser.createAST();
        IProblem[] problems = astRoot.getProblems();

        assertEquals("problems", 0, problems.length);

        TypeDeclaration type = (TypeDeclaration)astRoot.types().get(0);
        FieldDeclaration[] fields = type.getFields();

        VariableDeclarationFragment[] targets = new VariableDeclarationFragment[fields.length];
        for (int i = 0; i < fields.length; i++) {
            targets[i] = (VariableDeclarationFragment)fields[i].fragments().get(0);
        }
        return targets;
    }
}
