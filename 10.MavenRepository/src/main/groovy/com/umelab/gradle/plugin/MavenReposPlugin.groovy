package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.artifacts.dsl.RepositoryHandler

class MavenReposPlugin implements Plugin<Project> {
    def list = []

    @Override
    void apply(Project project) {
        init()
        configureRepos(project)
        // this is a dummy task for unit test
        project.task("dummy") {
            doFirst {
                println "dummy task completed"
            }
        }
    }

    /**
     * put url and token in map obj
     * insert the map object into list obj
     */
    def init() {
        String artifact_urls = System.getProperty("ARTIFACTS_REPOSITORY_URL")
        String artifact_tokens = System.getProperty("ARTIFACTS_TOKEN") 
        String url[] = artifact_urls.split(",")
        String token[] = artifact_tokens.split(",")
        for (i = 0; i < url.length; ++) {
            def map = [:]
            map.put('URL', url[i])
            map.put('TOKEN', token[i])
            list.add(map)
        }
    }

    /**
     * construct mutiple maven repositories
     * @param project
     *
     */
    def configureRepos(project) {
        RepositoryHandler handler = project.getRepositories()
        list.each { item ->
            handler.maven {
                url(item.get('URL'))
                credentials {
                    username ('')
                    password (item.get('TOKEN'))
                }
            }
        }
    }
}