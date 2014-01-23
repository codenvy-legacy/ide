/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.runner.docker.dockerfile;

import com.codenvy.api.core.util.Pair;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * @author andrew00x
 */
public class DockerfileParser {

    public static Dockerfile[] parse(Reader reader) throws IOException {
        final List<Dockerfile> parsed = new LinkedList<>();
        Dockerfile current = null;
        for (String line : CharStreams.readLines(reader)) {
            line = line.trim();
            Instruction instruction;
            if (line.isEmpty() || (instruction = getInstruction(line)) == null) {
                continue;
            }

            if (instruction == Instruction.FROM) {
                if (current != null) {
                    parsed.add(current);
                }
                current = new Dockerfile();
                instruction.setInstructionArgumentsToModel(current, line);
            } else {
                if (current == null) {
                    if (instruction != Instruction.COMMENT) {
                        throw new IllegalArgumentException("Dockerfile must start with 'FROM' instruction");
                    }
                } else {
                    instruction.setInstructionArgumentsToModel(current, line);
                }
            }
        }
        if (current != null) {
            parsed.add(current);
        }
        return parsed.toArray(new Dockerfile[parsed.size()]);
    }

    private static Instruction getInstruction(String line) {
        if (line.startsWith("#")) {
            return Instruction.COMMENT;
        }
        // By convention instruction should be UPPERCASE but it is not required.
        final String lowercase = line.toLowerCase();
        if (lowercase.startsWith("from")) {
            return Instruction.FROM;
        } else if (lowercase.startsWith("maintainer")) {
            return Instruction.MAINTAINER;
        } else if (lowercase.startsWith("run")) {
            return Instruction.RUN;
        } else if (lowercase.startsWith("cmd")) {
            return Instruction.CMD;
        } else if (lowercase.startsWith("expose")) {
            return Instruction.EXPOSE;
        } else if (lowercase.startsWith("env")) {
            return Instruction.ENV;
        } else if (lowercase.startsWith("add")) {
            return Instruction.ADD;
        } else if (lowercase.startsWith("entrypoint")) {
            return Instruction.ENTRYPOINT;
        } else if (lowercase.startsWith("volume")) {
            return Instruction.VOLUME;
        } else if (lowercase.startsWith("user")) {
            return Instruction.USER;
        } else if (lowercase.startsWith("workdir")) {
            return Instruction.WORKDIR;
        }
        return null;
    }

    private enum Instruction {
        FROM {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                model.setFrom(line.substring(name().length()).trim());
            }
        },
        MAINTAINER {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                model.getMaintainer().add(line.substring(name().length()).trim());
            }
        },
        RUN {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                model.getRun().add(line.substring(name().length()).trim());
            }
        },
        CMD {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                model.setCmd(line.substring(name().length()).trim());
            }
        },
        EXPOSE {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                final String args = line.substring(name().length()).trim();
                final int l = args.length();
                int i = 0, j = 0;
                while (j < l) {
                    while (j < l && !Character.isWhitespace(args.charAt(j))) {
                        j++;
                    }
                    model.getExpose().add(args.substring(i, j));
                    i = j;
                    while (i < l && Character.isWhitespace(args.charAt(i))) {
                        i++;
                    }
                    j = i;
                }
            }
        },
        ENV {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                final String args = line.substring(name().length()).trim();
                final int l = args.length();
                int i = 0;
                while (i < l && !Character.isWhitespace(args.charAt(i))) {
                    i++;
                }
                if (i < l) {
                    int j = i;
                    while (j < l && Character.isWhitespace(args.charAt(j))) {
                        j++;
                    }
                    if (j < l) {
                        model.getEnv().put(args.substring(0, i), args.substring(j));
                    } else {
                        model.getEnv().put(args.substring(0, i), null);
                    }
                } else {
                    model.getEnv().put(args, null);
                }
            }
        },
        ADD {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                final String args = line.substring(name().length()).trim();
                final int l = args.length();
                int i = 0;
                while (i < l && !Character.isWhitespace(args.charAt(i))) {
                    i++;
                }
                if (i < l) {
                    int j = i;
                    while (j < l && Character.isWhitespace(args.charAt(j))) {
                        j++;
                    }
                    if (j < l) {
                        model.getAdd().add(Pair.of(args.substring(0, i), args.substring(j)));
                    } else {
                        // respect this even it's not legal for docker file
                        model.getAdd().add(Pair.of(args.substring(0, i), (String)null));
                    }
                } else {
                    // respect this even it's not legal for docker file
                    model.getAdd().add(Pair.of(args, (String)null));
                }
            }
        },
        ENTRYPOINT {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                model.setEntrypoint(line.substring(name().length()).trim());
            }
        },
        VOLUME {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                String args = line.substring(name().length()).trim();
                if (!args.isEmpty()) {
                    final int l = args.length();
                    if (args.charAt(0) != '[' || args.charAt(l - 1) != ']') {
                        throw new IllegalArgumentException(String.format("Invalid argument '%s' for 'VOLUME' instruction", args));
                    }
                    int i = 1, j = 1, end = l - 1;
                    while (j < end) {
                        while (j < end && args.charAt(j) != ',') {
                            j++;
                        }
                        String volume = args.substring(i, j);
                        if (!volume.isEmpty()) {
                            if ((volume.charAt(0) == '"' && volume.charAt(volume.length() - 1) == '"')
                                || (volume.charAt(0) == '\'' && volume.charAt(volume.length() - 1) == '\'')) {
                                volume = volume.substring(1, volume.length() - 1);
                            }
                            if (!volume.isEmpty()) {
                                model.getVolume().add(volume);
                            }
                        }
                        i = j + 1;
                        while (i < end && Character.isWhitespace(args.charAt(i))) {
                            i++;
                        }
                        j = i;
                    }
                }
            }
        },
        USER {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                model.setUser(line.substring(name().length()).trim());
            }
        },
        WORKDIR {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                model.setWorkdir(line.substring(name().length()).trim());
            }
        },
        COMMENT {
            @Override
            void setInstructionArgumentsToModel(Dockerfile model, String line) {
                // ignore comments
            }
        };

        abstract void setInstructionArgumentsToModel(Dockerfile model, String line);
    }

    private DockerfileParser() {
    }
}
