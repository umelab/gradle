package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification


class ArgsPluginTest extends Specification {
    @TempDir File testProjectDir

    File settingsGradle
    File buildGradle
    File gradleProperty

    def argsTask = 'args'

    /**
    * 【Testkit Folder Structure】
    *
    *     testProjectDir
    *          +-- build.gradle
    *          +-- gradle.properties
    *          +-- settings.gradle
    */
    def setup() {
        settingsGradle = new File(testProjectDir, 'settings.gradle')
        buildGradle    = new File(testProjectDir, 'build.gradle')
        gradleProperty = new File(testProjectDir, 'gradle.properties')

        settingsGradle << """\
            |rootProject.name = 'args-test'
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins{
            | id 'args'    
            |} 
            |""".stripIndent().stripMargin()

        gradleProperty << """\
        	|# gradle.properties file
            |""".stripIndent().stripMargin()

    }

    /**
     * test task result
     */
    def "Test_Args_TaskResult"() {
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(argsTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":args").outcome == SUCCESS
	}

    /**
     * How to test task with arguments
     * test output message
     */
    def "Test_Args_Output"() {
        def taskArgs = ['args', '-Pparam=foo']
        def expectedOutput = 'foo'
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(taskArgs)
            .withPluginClasspath()
            .build()

        then:
        // task status
        result.task(":args").outcome == SUCCESS
        // output mssage
        def actualOutput = result.output
        assert(actualOutput.contains(expectedOutput))
	}

    /**
     * How to test with failed case
     * test if argument of the task is number -> Fail
     */
    def "Test_Args_Error_Format"() {
        def taskArgs = ['args', '-Pparam=123']
        def expectedOutput = 'An argument format error'
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(taskArgs)
            .withPluginClasspath()
            .buildAndFail()

        then:
        // task status
        result.task(":args").outcome == FAILED
        // output message
        def actualOutput = result.output
        assert(actualOutput.contains(expectedOutput))
	}

}

