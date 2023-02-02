package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin

class CreateDeleteFilePlugin implements Plugin<Project> {
    def TASK_NAME_CREATEFILE = 'createFile'
    def TASK_NAME_DELETEFILE = 'deleteFile'

    @Override
    void apply(Project project) {
        // create file task
        project.task(TASK_NAME_CREATEFILE) {
            def folder = 'tmp'
            def fileName = 'test.txt'
            doFirst {
                if(!project.file("${folder}").exists()) {
                    project.file("${folder}").mkdirs()
                }
                project.file("${folder}/${fileName}").createNewFile()
            }
        }

        // delete file task
        project.task(TASK_NAME_DELETEFILE) {
            def folder = 'tmp'
            def fileName = 'test.txt'
            doFirst {
                if(project.file("${folder}/${fileName}").exists()) {
                    project.file("${folder}/${fileName}").delete()
                }
            }
        }
    }
}