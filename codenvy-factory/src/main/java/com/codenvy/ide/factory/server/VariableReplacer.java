package com.codenvy.ide.factory.server;

import com.codenvy.api.factory.Variable;
import com.codenvy.commons.lang.Deserializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Variable util to process variables replacement. */
public class VariableReplacer {

    private final Path projectPath;

    private static final Logger LOG = LoggerFactory.getLogger(VariableReplacer.class);

    public VariableReplacer(Path projectPath) {
        this.projectPath = projectPath;
    }

    /** Perform searching in project path files given by variables list and make replacement variables in each file if it found. */
    public void performReplacement(List<Variable> variables) {
        final Map<Path, Set<Variable.Replacement>> replacementMap = new HashMap<>();

        for (Variable variable : variables) {
            for (String glob : variable.getFiles()) {
                try {
                    Files.walkFileTree(projectPath, new GlobFileVisitor(glob, variable.getEntries(), replacementMap));
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }

        for (Map.Entry<Path, Set<Variable.Replacement>> entry : replacementMap.entrySet()) {
            try {
                Map<String, String> props = new HashMap<>(entry.getValue().size());
                for (Variable.Replacement prop : entry.getValue()) {
                    props.put(prop.getFind(), prop.getReplace());
                }

                String content = new String(Files.readAllBytes(entry.getKey()));
                String modified = Deserializer.resolveVariables(content, props, false);
                if (!content.equals(modified)) {
                    Files.write(entry.getKey(), modified.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                }
            } catch (IOException e) {
                LOG.error(e.getLocalizedMessage(), e);
            }
        }
    }

    /** File visitor */
    private class GlobFileVisitor extends SimpleFileVisitor<Path> {
        /** Generic map which contains specific file and list of replacements for it. */
        private final Map<Path, Set<Variable.Replacement>> replacementMap;

        /** Glob pattern to match specific file. */
        private final PathMatcher matcher;

        /** Replacement list which contains what we should find and then replace in specific files which is given by matcher. */
        private final List<Variable.Replacement> replacements;

        /** Create constructor */
        GlobFileVisitor(final String pattern,
                        final List<Variable.Replacement> replacements,
                        Map<Path, Set<Variable.Replacement>> replacementMap) {
            this.replacementMap = replacementMap;
            this.matcher = FileSystems.getDefault().getPathMatcher("glob:**" + pattern);
            this.replacements = replacements;
        }

        /** View file and put it into replacement map, which will be proceed in feature. */
        private void find(Path file) {
            if (matcher.matches(file) && file.toFile().isFile()) {
                if (replacementMap.containsKey(file)) {
                    replacementMap.get(file).addAll(replacements);
                } else {
                    replacementMap.put(file, new HashSet<>(replacements));
                }
            }
        }

        /** {@inheritDoc} */
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            return FileVisitResult.CONTINUE;
        }

        /** {@inheritDoc} */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            find(file);
            return FileVisitResult.CONTINUE;
        }

        /** {@inheritDoc} */
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            return FileVisitResult.CONTINUE;
        }
    }
}
