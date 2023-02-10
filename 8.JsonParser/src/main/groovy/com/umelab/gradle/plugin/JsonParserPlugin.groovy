package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.GradleException

import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

class JsonParserPlugin implements Plugin<Project> {
    def TASK_NAME_ADDNODE = 'addNode'
    def TASK_NAME_UPDATENODE = 'updateNode'
    def TASK_NAME_REMOVENODE = 'removeNode'

    @Override
    void apply(Project project) {
        project.task(TASK_NAME_ADDNODE) {
            doFirst {
                project.subprojects { subproject ->
                    handleJsonNode(subproject, 'add')
                }
            }
        }
        project.task(TASK_NAME_UPDATENODE) {
            doFirst {
                project.subprojects { subproject ->
                    handleJsonNode(subproject, 'update')
                }
            }
        }
        project.task(TASK_NAME_REMOVENODE) {
            doFirst {
                project.subprojects { subproject ->
                    handleJsonNode(subproject, 'remove')
                }
            }
        }
    }

    def handleJsonNode(subproject, action) {
        def testJsonFile = subproject.file('src/main/resources/test.json')
        if (testJsonFile.exists()) {
            def jsonNode = new JsonSlurper().parse(testJsonFile)
            if (action == 'add') {
                def carArray = ['black', 'white', 'blue']
                def builder = new JsonBuilder()
                def detail = "Broadway Avenue"
                def element = builder.event {
                    "toyota" carArray
                }
                // add element
                jsonNode.address.street = detail
                // add element as array
                jsonNode.cars = element.event
            } else if (action == 'update') {
                // update element
                jsonNode.address.country = 'Japan'
                // find id = 1 and update number
                jsonNode.phones.find { 
                    it.id == 1 
                }.number = '09011119999'
            } else if (action == 'remove') {
                // remove element
                jsonNode.remove('age')
            }
            def updateContent = new JsonBuilder(jsonNode).toPrettyString()
            testJsonFile.text = updateContent
        } else {
            throw new GradleException('test.json is not found.')
        }
    }
}