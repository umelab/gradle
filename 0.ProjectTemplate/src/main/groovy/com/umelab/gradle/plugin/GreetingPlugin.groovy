package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin

class GreetingPlugin implements Plugin<Project> {
    def TASK_NAME_HELLO = 'hello'
    def OUTPUT_MESSAGE  = 'Hello World!'

    @Override
    void apply(Project project) {
        project.task(TASK_NAME_HELLO) {
            doFirst {
                println OUTPUT_MESSAGE 
            }
        }
    }
}