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
        if (variables.size() == 0) {
            return;
        }

        final Map<Path, Set<Variable.Replacement>> replacementMap = new HashMap<>();

        for (final Variable variable : variables) {
            for (String glob : variable.getFiles()) {
                try {
                    Files.walkFileTree(projectPath, new GlobFileVisitor(glob, new VisitorHandler() {
                        @Override
                        public void onPathMatched(Path file) {
                            if (replacementMap.containsKey(file)) {
                                replacementMap.get(file).addAll(variable.getEntries());
                            } else {
                                replacementMap.put(file, new HashSet<>(variable.getEntries()));
                            }
                        }
                    }));
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }

        if (replacementMap.size() > 0) {
            Map<String, String> variableProps = new HashMap<>();
            Map<String, String> textProps = new HashMap<>();
            for (Map.Entry<Path, Set<Variable.Replacement>> entry : replacementMap.entrySet()) {
                try {
                    variableProps.clear();
                    textProps.clear();

                    for (Variable.Replacement prop : entry.getValue()) {
                        switch (prop.getReplacemode()) {
                            case "variable_singlepass" :
                                variableProps.put(prop.getFind(), prop.getReplace());
                                break;
                            case "text_multipass" :
                                textProps.put(prop.getFind(), prop.getReplace());
                                break;
                        }
                    }

                    if (variableProps.size() > 0 || textProps.size() > 0) {
                        String content = new String(Files.readAllBytes(entry.getKey()));

                        String modified = Deserializer.resolveVariables(content, variableProps, false);
                        for (Map.Entry<String, String> replacement : textProps.entrySet()) {
                            if (modified.indexOf(replacement.getKey()) > 0) {
                                modified = modified.replace(replacement.getKey(), replacement.getValue());
                            }
                        }

                        if (!content.equals(modified)) {
                            Files.write(entry.getKey(), modified.getBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    LOG.error(e.getLocalizedMessage(), e);
                }
            }
        }
    }

    /** Handle path matches. */
    public interface VisitorHandler {
        void onPathMatched(Path file);
    }

    /** File visitor */
    public final class GlobFileVisitor extends SimpleFileVisitor<Path> {

        /** Glob pattern to match specific file. */
        private final PathMatcher matcher;

        /** Handler which will be invoked on path matched. */
        private final VisitorHandler handler;

        /** Create constructor */
        GlobFileVisitor(final String pattern, VisitorHandler handler) {
            this.handler = handler;
            this.matcher = FileSystems.getDefault().getPathMatcher("glob:**" + pattern);
        }

        /** {@inheritDoc} */
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            if (matcher.matches(file) && file.toFile().isFile()) {
                handler.onPathMatched(file);
            }

            return FileVisitResult.CONTINUE;
        }

        /** {@inheritDoc} */
        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
            //need to continue work instead of exception
            return FileVisitResult.CONTINUE;
        }
    }
}
