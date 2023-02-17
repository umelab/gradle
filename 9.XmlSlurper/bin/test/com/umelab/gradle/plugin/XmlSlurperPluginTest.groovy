package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification


class XmlSlurperPluginTest extends Specification {
    @TempDir File rootProjectDir

    File settingsGradle
    File buildGradle
    File gradleProperty

    File log4jXml

    def execTask = 'parse'

    /**
    * 【Testkit Folder Structure】
    *
    *     rootProjectDir
    *          +-- log4j.xml
    *          +-- build.gradle
    *          +-- gradle.properties
    *          +-- settings.gradle
    */
    def setup() {
        settingsGradle = new File(rootProjectDir, 'settings.gradle')
        buildGradle    = new File(rootProjectDir, 'build.gradle')
        gradleProperty = new File(rootProjectDir, 'gradle.properties')

        log4jXml = new File(rootProjectDir, 'log4j.xml')

        settingsGradle << """\
            |rootProject.name = 'helloworld'
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins{
            | id 'xmlslurper'    
            |} 
            |""".stripIndent().stripMargin()

        gradleProperty << """\
        	|# gradle.properties file
            |""".stripIndent().stripMargin()
        
        log4jXml << """<?xml version="1.0" encoding="UTF-8" ?>
            |<!DOCTYPE log4j:configuration SYSTEM "log4j.dtd">
            |<log4j:configuration xmlns:log4j='http://jakarta.apache.org/log4j/'>
            |    <appender name="STDOUT" class="org.apache.log4j.ConsoleAppender">
            |    <layout class="org.apache.log4j.PatternLayout">
            |        <param name="ConversionPattern"
            |                value="%d %-5p [%t] %c - %m (%F:%L)%n"/>
            |    </layout>
            |    </appender>
            |    <appender name="STDOUT-SIMPLE" class="org.apache.log4j.ConsoleAppender">
            |    <layout class="org.apache.log4j.PatternLayout">
            |        <param name="ConversionPattern"
            |                value="%d [%t] %c - %m%n"/>
            |    </layout>
            |    </appender>
            |    <appender name="rolling" class="org.apache.log4j.DailyRollingFileAppender">
            |    <param name="File"   value="log/mkbatch.log" />
            |    <param name="Append" value="true" />
            |    <layout class="org.apache.log4j.PatternLayout">
            |        <param name="ConversionPattern" value="%d %5p %c{1} - %m%n" />
            |    </layout>
            |    </appender>
            |    <root>
            |    <priority value ="debug" />
            |    <appender-ref ref="rolling" />  
            |    </root>
            |</log4j:configuration>
            |""".stripIndent().stripMargin()
    }

    /**
     * test task result
     */
    def "Test_TaskResult"() {
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":${execTask}").outcome == SUCCESS
	}

    def "validate STD-SIMPLE appender element is removed"() {
        given:

        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        def node = findXmlNode(log4jXml.text, 'STDOUT-SIMPLE')
        assert (node.size() == 0)
    }

    def "validate STDOUT appender element is not removed"() {
        given:

        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        def node = findXmlNode(log4jXml.text, 'STDOUT')
        assert (node.size() > 0)
    }
    
    def findXmlNode(fileContent, nodeName) {
        def docTypeIgnoreUrl = 'http://apache.org/xml/features/disallow-doctype-decl'
        def loadExtIgnoreUrl = 'http://apache.org/xml/features/nonvalidating/load-external-dtd'

        def xmlNode = new XmlSlurper()
        // ignore !DOCTYPE
        xmlNode.setFeature(docTypeIgnoreUrl, false)
        // ignore external DTD
        xmlNode.setFeature(loadExtIgnoreUrl, false)
        def xmlResult = xmlNode.parseText(fileContent)
        def delNode = xmlResult.children().find { node ->
            node.getProperty('@name') == nodeName
        }
        return delNode
    }

}

