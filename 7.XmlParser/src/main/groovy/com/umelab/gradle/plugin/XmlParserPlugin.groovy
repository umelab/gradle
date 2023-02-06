package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin
import org.gradle.api.GradleException

class XmlParserPlugin implements Plugin<Project> {
    def TASK_NAME_ADDNODE = 'addNode'
    def xmlPath = 'src/main/resources/test.xml'
    def xmlNodeName = 'workDir'
    def xmlNodeText = '/work'

    @Override
    void apply(Project project) {
        project.task(TASK_NAME_ADDNODE) {
            doFirst {
                project.subprojects { subproject ->
                    def testXmlFile = subproject.file(xmlPath)
                    if (testXmlFile.exists()) {
                        def xmlNode = new XmlParser().parse(testXmlFile)
                        def tomcatProjProp = xmlNode.children()
                        // find workDir node
                        def workDirNode = tomcatProjProp.find { node ->
                            node.name() == xmlNodeName
                        }
                        if (!workDirNode) {
                            // create node
                            workDirNode = new Node(null, xmlNodeName, xmlNodeText)
                            //tomcatProjProp.appendNode(workDirNode)
                            tomcatProjProp.add(workDirNode)
                        } else {
                            // replace node
                            xmlNode.workDir.replaceNode{ node ->
                                workDir(xmlNodeText)
                            }
                        }
                        def output = groovy.xml.XmlUtil.serialize(xmlNode)
                        testXmlFile.write(output)
                    } else {
                        throw new GradleException("test.xml is not found.")
                    }
                }
            }
        }
    }
}