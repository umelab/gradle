package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification

import groovy.json.JsonSlurper
import groovy.json.JsonBuilder

class JsonParserPluginTest extends Specification {
    @TempDir File rootProjectDir

    File settingsGradle
    File buildGradle
    File gradleProperty

    def commonProjectDir
    def commonResrcDir
    File commonBuildGradle
    File commonTestJson

    def execTask = 'addNode'

    /**
    * 【Testkit Folder Structure】
    *
    *     rootProjectDir
    *          +-- AA/Common/Eclipse
    *          |               +-- src/main/resources
    *          |               |                +-- test.json
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
        commonTestJson = new File(commonResrcDir, 'test.json')

        commonTestJson << """\
            |{
            |"name": "Tom",
            |"age": 40,
            |"address": {
            |    "country": "England",
            |    "city": "London"
            |},
            |"phones": [
            |    {
            |        "id": 1,
            |        "number": "09011112222",
            |    },
            |    {
            |        "id": 2,
            |        "number": "08011112222"
            |    }
            |]
            |}
            |""".stripIndent().stripMargin()


        commonBuildGradle << """\
            |plugins {
            |    id 'jsonparser'
            |}
            |""".stripIndent().stripMargin()

        settingsGradle << """\
            |rootProject.name = 'jsonparser-test'
            |include ':aa-common'
            |project(':aa-common').projectDir = file('./AA/Common/Eclipse')
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins{
            | id 'jsonparser'    
            |} 
            |""".stripIndent().stripMargin()

        gradleProperty << """\
        	|# gradle.properties file
            |""".stripIndent().stripMargin()

    }

    /**
     * test addNode task
     */
    def "Test add task"() {
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":${execTask}").outcome == SUCCESS

        def content = commonTestJson.text
        def builder = new JsonBuilder(new JsonSlurper().parseText(content))
        // confirm new property is added
        assert(builder.content.address.street == 'Broadway Avenue')
        assert(builder.content.cars.toyota)
        assert(builder.content.cars.toyota[0] == 'black')
        assert(builder.content.cars.toyota[1] == 'white')
        assert(builder.content.cars.toyota[2] == 'blue')
	}

    /**
     * test updateNode task
     */
    def "test update phone no and country element"() {
        given:
            def updateTask = 'updateNode'
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(updateTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":${updateTask}").outcome == SUCCESS

        def content = commonTestJson.text
        def builder = new JsonBuilder(new JsonSlurper().parseText(content))
        // confirm phone no and country element is updated
        assert(builder.content.address.country == 'Japan')
        assert(builder.content.phones[0].number == '09011119999')
	}

    /**
     * test removeNode task
     */
    def "test remove age property"() {
        given:
            def deleteTask = 'removeNode'
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(deleteTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":${deleteTask}").outcome == SUCCESS

        def content = commonTestJson.text
        def builder = new JsonBuilder(new JsonSlurper().parseText(content))
        // confirm age property is removed
        assert(builder.content.age == null)
    }
}

