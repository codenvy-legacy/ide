package com.codenvy.ide.factory.server.test;

import com.codenvy.api.factory.Variable;
import com.codenvy.ide.factory.server.VariableReplacer;

import org.testng.annotations.*;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.codenvy.commons.lang.IoUtil.deleteRecursive;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Test for {@link com.codenvy.ide.factory.server.VariableReplacer}. */
public class FactoryServiceTest {

    private Path root;

    private Path createFile(Path parent, String fileName, byte[] content) throws IOException {
        Path newFile = Files.createFile(Paths.get(parent.toString(), fileName), new FileAttribute[]{});
        assertTrue(newFile.toFile().exists());

        if (content != null) {
            FileOutputStream fOut = new FileOutputStream(newFile.toFile());
            fOut.write(content);
            fOut.close();
        }

        return newFile;
    }

    private String getFileContent(Path path) throws IOException {
        byte[] raw = Files.readAllBytes(path);

        return new String(raw);
    }

    @BeforeMethod
    public void setUp() throws Exception {
        root = Files.createTempDirectory("", new FileAttribute[]{});
    }

    @AfterMethod
    public void tearDown() throws Exception {
        deleteRecursive(root.toFile());
    }


    @Test(testName = "shouldSimpleReplaceVar")
    public void shouldSimpleReplaceVar() throws Exception {
        final String template = "some super content\n with ${%s} and another variable ${%s}";
        final String templateReplaced = "some super content\n with %s and another variable %s";
        final String f1 = "VAR_NUM_1";
        final String f2 = "VAR_NUM_2";
        final String r1 = "value1";
        final String r2 = "value2";
        final String file = "test_file.txt";

        createFile(root, file, String.format(template, f1, f2).getBytes());

        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(root.toString(), file)));

        List<Variable.Replacement> replacement = new ArrayList<>(2);
        replacement.add(new Variable.Replacement(f1, r1));
        replacement.add(new Variable.Replacement(f2, r2));

        List<String> glob = new ArrayList<>(1);
        glob.add("**test_file.txt");

        Variable variable = new Variable(glob, replacement);

        new VariableReplacer(root).performReplacement(Collections.singletonList(variable));

        assertEquals(String.format(templateReplaced, r1, r2), getFileContent(Paths.get(root.toString(), file)));
    }

    @Test(testName = "shouldSimpleReplaceByExtensionVar")
    public void shouldSimpleReplaceByExtensionVar() throws Exception {
        final String template = "some super content\n with ${%s} and another variable ${%s}";
        final String templateReplaced = "some super content\n with %s and another variable %s";
        final String f1 = "VAR_NUM_1";
        final String f2 = "VAR_NUM_2";
        final String r1 = "value1";
        final String r2 = "value2";
        final String file1 = "test_file.txt";
        final String file2 = "test_file.java";
        final String file3 = "test_file.class";

        createFile(root, file2, String.format(template, f1, f2).getBytes());
        createFile(root, file1, String.format(template, f1, f2).getBytes()); //our file for replacement
        createFile(root, file3, String.format(template, f1, f2).getBytes());

        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(root.toString(), file2)));
        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(root.toString(), file1)));
        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(root.toString(), file3)));

        List<Variable.Replacement> replacement = new ArrayList<>(2);
        replacement.add(new Variable.Replacement(f1, r1));
        replacement.add(new Variable.Replacement(f2, r2));

        List<String> glob = new ArrayList<>(1);
        glob.add("**test_file.java");

        Variable variable = new Variable(glob, replacement);

        new VariableReplacer(root).performReplacement(Collections.singletonList(variable));

        assertEquals(String.format(templateReplaced, r1, r2), getFileContent(Paths.get(root.toString(), file2)));
        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(root.toString(), file1)));
        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(root.toString(), file3)));
    }

    @Test(testName = "shouldSimpleReplaceInChildDirectory")
    public void shouldSimpleReplaceInChildDirectory() throws Exception {
        Path child = Files.createDirectories(Paths.get(root.toString(), "some", "another", "dir"), new FileAttribute[]{});

        final String template = "some super content\n with ${%s} and another variable ${%s}";
        final String templateReplaced = "some super content\n with %s and another variable %s";
        final String f1 = "VAR_NUM_1";
        final String f2 = "VAR_NUM_2";
        final String r1 = "value1";
        final String r2 = "value2";
        final String file = "test_file.txt";

        createFile(child, file, String.format(template, f1, f2).getBytes());

        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(child.toString(), file)));

        List<Variable.Replacement> replacement = new ArrayList<>(2);
        replacement.add(new Variable.Replacement(f1, r1));
        replacement.add(new Variable.Replacement(f2, r2));

        List<String> glob = new ArrayList<>(1);
        glob.add("**test_file.txt");

        Variable variable = new Variable(glob, replacement);

        new VariableReplacer(root).performReplacement(Collections.singletonList(variable));

        assertEquals(String.format(templateReplaced, r1, r2), getFileContent(Paths.get(child.toString(), file)));
    }

    @Test(testName = "shouldSimpleReplaceInChildDirectoryV2")
    public void shouldSimpleReplaceInChildDirectoryV2() throws Exception {
        Path child = Files.createDirectories(Paths.get(root.toString(), "some", "another", "dir"), new FileAttribute[]{});

        final String template = "some super content\n with ${%s} and another variable ${%s}";
        final String templateReplaced = "some super content\n with %s and another variable %s";
        final String f1 = "VAR_NUM_1";
        final String f2 = "VAR_NUM_2";
        final String r1 = "value1";
        final String r2 = "value2";
        final String file = "test_file.txt";

        createFile(child, file, String.format(template, f1, f2).getBytes());

        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(child.toString(), file)));

        List<Variable.Replacement> replacement = new ArrayList<>(2);
        replacement.add(new Variable.Replacement(f1, r1));
        replacement.add(new Variable.Replacement(f2, r2));

        List<String> glob = new ArrayList<>(1);
        glob.add("some/another/dir/test_file.txt");

        Variable variable = new Variable(glob, replacement);

        new VariableReplacer(root).performReplacement(Collections.singletonList(variable));

        assertEquals(String.format(templateReplaced, r1, r2), getFileContent(Paths.get(child.toString(), file)));
    }

    @Test(testName = "shouldSimpleReplaceVar")
    public void shouldSimpleReplaceMultipassVar() throws Exception {
        final String template = "some super content\n with ${%s} and another variable %s";
        final String templateReplaced = "some super content\n with %s and another variable %s";
        final String f1 = "VAR_NUM_1";
        final String f2 = "VAR_NUM_2";
        final String r1 = "value1";
        final String r2 = "value2";
        final String file = "test_file.txt";

        createFile(root, file, String.format(template, f1, f2).getBytes());

        assertEquals(String.format(template, f1, f2), getFileContent(Paths.get(root.toString(), file)));

        List<Variable.Replacement> replacement = new ArrayList<>(2);
        replacement.add(new Variable.Replacement(f1, r1));
        replacement.add(new Variable.Replacement(f2, r2, "text_multipass"));

        List<String> glob = new ArrayList<>(1);
        glob.add("**test_file.txt");

        Variable variable = new Variable(glob, replacement);

        new VariableReplacer(root).performReplacement(Collections.singletonList(variable));

        assertEquals(String.format(templateReplaced, r1, r2), getFileContent(Paths.get(root.toString(), file)));
    }

}
