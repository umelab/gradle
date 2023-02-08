package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.GradleException

class XmlParserPlugin implements Plugin<Project> {
    def TASK_NAME_ADDNODE = 'addNode'
    def TASK_NAME_UPDATENODE = 'updateNode'
    def TASK_NAME_REMOVENODE = 'removeNode'
    
    def ERROR_MSG = 'test.xml is not found.'

    def xmlPath = 'src/main/resources/test.xml'
    def xmlNodeName = 'workDir'
    def xmlNodeText = '/work'

    @Override
    void apply(Project project) {
        project.task(TASK_NAME_ADDNODE) {
            doFirst {
                project.subprojects { subproject ->
                    handleXmlNode(subproject, 'add')
                }
            }
        }
        project.task(TASK_NAME_UPDATENODE) {
            doFirst {
                project.subprojects { subproject ->
                    handleXmlNode(subproject, 'update')
                }
            }
        }
        project.task(TASK_NAME_REMOVENODE) {
            doFirst {
                project.subprojects { subproject ->
                    handleXmlNode(subproject, 'remove')
                }
            }
        }

    }

    def handleXmlNode(subproject, action) {
        def testXmlFile = subproject.file(xmlPath)
        if (testXmlFile.exists()) {
            def xmlNode = new XmlParser().parse(testXmlFile)
            def tomcatProjProp = xmlNode.children()
            // find workDir node
            def workDirNode = tomcatProjProp.find { node ->
                node.name() == xmlNodeName
            }
            // add xml node
            if (!workDirNode && action == 'add') {
                // create node
                workDirNode = new Node(null, xmlNodeName, xmlNodeText)
                tomcatProjProp.add(workDirNode)
            // update xml node
            } else if (action == 'update') {
                // replace node
                xmlNode.workDir.replaceNode{ node ->
                    workDir(xmlNodeText)
                }
            } else if (action == 'remove') {
                // remove node
                xmlNode.workDir.replaceNode{}
            }
            def output = groovy.xml.XmlUtil.serialize(xmlNode)
            testXmlFile.write(output)
        } else {
            throw new GradleException(ERROR_MSG)
        }
    }
}