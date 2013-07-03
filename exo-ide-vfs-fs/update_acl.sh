#!/bin/sh
java -cp target/dependency/*:target/classes org.exoplatform.ide.vfs.impl.fs.AclMain $@
