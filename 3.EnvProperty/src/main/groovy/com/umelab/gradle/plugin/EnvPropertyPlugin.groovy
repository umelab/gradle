package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin

class EnvPropertyPlugin implements Plugin<Project> {
    def TASK_NAME_ENV = 'env'
    def TASK_NAME_PROP = 'prop'

    @Override
    void apply(Project project) {
        // print environment variables
        project.task(TASK_NAME_ENV) {
            def envVariable = System.getenv("GRADLE_HOME")
            doFirst {
                println envVariable
            }
        }
        // print property variables 
        project.task(TASK_NAME_PROP) {
            def propVariable = System.getProperty("Foo")
            doFirst {
                println propVariable
            }
        }
    }
}