package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.GradleException

class ArgsPlugin implements Plugin<Project> {
    def TASK_NAME_ARGS = 'args'
    def TASK_ARGUMENT_KEY = 'param'
    def ERROR_MSG = 'An argument format error'

    @Override
    void apply(Project project) {
        // print arguments
        project.task(TASK_NAME_ARGS) {
            def args = 'no args'
            doFirst {
				if (project.hasProperty(TASK_ARGUMENT_KEY)){
					args = project.property(TASK_ARGUMENT_KEY)
                    if (args ==~ /^[0-9].+/) {
                        throw new GradleException(ERROR_MSG)
                    }
                } 
                println args
            }
        }
    }
}