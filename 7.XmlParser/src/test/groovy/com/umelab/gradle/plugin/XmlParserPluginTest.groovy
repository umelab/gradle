package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification


class XmlParserPluginTest extends Specification {
    @TempDir File rootProjectDir

    File settingsGradle
    File buildGradle
    File gradleProperty

    def commonProjectDir
    def commonResrcDir
    File commonBuildGradle
    File commonTestXml

    def execTask = 'addNode'

    /**
    * 【Testkit Folder Structure】
    *
    *     rootProjectDir
    *          +-- AA/Common/Eclipse
    *          |               +-- src/main/resources
    *          |               |                +-- test.xml
    *          |               +-- build.gradle
    *          +-- build.gradle
    *          +-- gradle.properties
    *          +-- settings.gradle
    */
    def setup() {
        settingsGradle = new File(rootProjectDir, 'settings.gradle')
        buildGradle    = new File(rootProjectDir, 'build.gradle')
        gradleProperty = new File(rootProjectDir, 'gradle.properties')

        commonProjectDir = new File(rootProjectDir, 'AA/Common/Eclipse')
        commonProjectDir.mkdirs()                 
        commonResrcDir = new File(commonProjectDir, 'src/main/resources')
        commonResrcDir.mkdirs()

        commonBuildGradle = new File(commonProjectDir, 'build.gradle')
        commonTestXml = new File(commonResrcDir, 'test.xml')

        commonBuildGradle << """\
            |plugins {
            |    id 'xmlparser'
            |}
            |""".stripIndent().stripMargin()

        commonTestXml << """\
            |<?xml version="1.0" encoding="UTF-8"?>
            |<tomcatProjectProperties>
            |    <rootDir>/war/target/aipo</rootDir>
            |    <exportSource>false</exportSource>
            |    <reloadable>false</reloadable>
            |    <redirectLogger>false</redirectLogger>
            |    <updateXml>true</updateXml>
            |    <warLocation></warLocation>
            |    <extraInfo></extraInfo>
            |    <webPath>/</webPath>
            |</tomcatProjectProperties>        
            |""".stripIndent().stripMargin().normalize()
        
        settingsGradle << """\
            |rootProject.name = 'xmlparser-sample'
            |include ':aa-common'
            |project(':aa-common').projectDir = file('./AA/Common/Eclipse')
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins {
            |    id 'xmlparser'   
            |} 
            |""".stripIndent().stripMargin()

        gradleProperty << """\
        	|# gradle.properties file
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

    /**
     * test standardoutput message 'Hello World!'
     */
    def "Test_OutputMessage"() {

        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        def updateXmlFile = new File(commonResrcDir, 'test.xml')
        println updateXmlFile.text
        //assert(actualMsg.count(expectedMsg) == 1)
	}

}

