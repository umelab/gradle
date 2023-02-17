package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.api.Plugin

class XmlSlurperPlugin implements Plugin<Project> {
    def TASK_NAME_PARSE = 'parse'
    def oldXmlHeader = '<?xml version="1.0" encoding="UTF-8"?>'
    def newXmlHeader = '<?xml version="1.0" encoding="UTF-8"?>\n<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">\n'

    @Override
    void apply(Project project) {
        project.task(TASK_NAME_PARSE) {
            def docTypeIgnoreUrl = 'http://apache.org/xml/features/disallow-doctype-decl'
            def loadExtIgnoreUrl = 'http://apache.org/xml/features/nonvalidating/load-external-dtd'
            def targetElementName = 'STDOUT-SIMPLE'
            doFirst {
                def log4jXml = project.file('log4j.xml')
                def xmlNode = new XmlSlurper()
                // ignore !DOCTYPE
                xmlNode.setFeature(docTypeIgnoreUrl, false)
                // ignore external DTD
                xmlNode.setFeature(loadExtIgnoreUrl, false)
                def result = xmlNode.parseText(log4jXml.text)
                // search element
                def delNode = result.children().find { node ->
                    node.getProperty('@name') == targetElementName
                }
                // remove and update xml
                if (delNode.size() > 0) {
                    delNode.replaceNode{}
                    def updateLog4jContent = groovy.xml.XmlUtil.serialize( result )
                    updateLog4jContent = updateLog4jContent.replace(oldXmlHeader, newXmlHeader)
                    log4jXml.text = updateLog4jContent
                }
            }
        }
    }
}