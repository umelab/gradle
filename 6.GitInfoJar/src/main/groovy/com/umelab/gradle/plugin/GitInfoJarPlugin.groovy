package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.GradleException
import org.gradle.api.tasks.bundling.Jar

class GitInfoJarPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.getPlugins().apply('java')
        overrideJarTask(project)
    }

    private void overrideJarTask(Project project) {
        def revno = '012345'
        def branch = 'feature/branch'
        def jdkInfo = '1.8.0_121 (Oracle Corporation)'
        def stdout = ''
        project.tasks.withType(Jar) { jar ->
            doFirst {
                if (isGitAvailable(project)) {
                    // get revno
                    project.exec {
                        commandLine 'git', 'rev-parse', '--short', 'HEAD'
                        standardOutput = new ByteArrayOutputStream()
                        errorOutput = new ByteArrayOutputStream()
                        stdout = standardOutput
                        ignoreExitValue = true
                    }
                    revno = stdout.toString().trim()
                    // get branch
                    project.exec {
                        commandLine 'git', 'rev-parse', '--abbrev-ref', 'HEAD'
                        standardOutput = new ByteArrayOutputStream()
                        errorOutput = new ByteArrayOutputStream()
                        stdout = standardOutput
                        ignoreExitValue = true
                    }
                    branch = stdout.toString().trim()
                } else { // if git is not available
                    throw new GradleException("Git is not available")
                }
                // jdk info
                jdkInfo = "${System.getProperty('java.version')} (${System.getProperty('java.vendor')})"
                // set manifest
                manifest {
                    attributes("Git-Revno": revno,
                                "Git-Branch": branch,
                                "JDK-Version": jdkInfo)
                }
            }
        }
    }

    private boolean isGitAvailable(project) {
        def exitCode = project.exec {
            commandLine 'git', 'rev-parse', '--git-dir'
            standardOutput = new ByteArrayOutputStream()
            errorOutput = new ByteArrayOutputStream()
            ignoreExitValue = true
        }.exitValue

        if (exitCode == 0)  return true
        else                return false
    }
}