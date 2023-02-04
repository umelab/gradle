package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin

class KeywordGrepPlugin implements Plugin<Project> {
    def TASK_NAME_GREP = 'replaceTag'
    def targetTag = '@update'
    def updateTag = '@newupdate'
    def lineFeed = '\n'

    @Override
    void apply(Project project) {
        project.task(TASK_NAME_GREP) {
            doFirst {
                def targetFilesPath = []
                project.fileTree(dir: 'src/main/java', include: '**/*.java').each { file ->
                    def targetContent = file.text
                    if (targetContent.contains(targetTag)) {
                        targetFilesPath += file.path
                    }
                } 
                targetFilesPath.each { path ->
                    def tempFile = File.createTempFile('tmp', 'java')
                    def readFile = new File(path)
                    tempFile.withWriter { wLine ->
                        readFile.eachLine { line ->
                            if (line.contains(targetTag)) {
                                wLine << line.replace(targetTag, updateTag) << lineFeed
                            } else {
                                wLine << line  << lineFeed
                            }
                        }
                    }
                    readFile.delete()
                    tempFile.renameTo(readFile)
                    println path
                }
            }
        }
    }
}