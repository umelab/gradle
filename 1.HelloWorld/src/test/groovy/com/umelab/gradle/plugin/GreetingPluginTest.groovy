package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification


class GreetingPluginTest extends Specification {
    @TempDir File testProjectDir

    File settingsGradle
    File buildGradle
    File gradleProperty

    def helloTask = 'hello'

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
            |rootProject.name = 'helloworld'
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins{
            | id 'helloworld'    
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
            .withProjectDir(testProjectDir)
            .withArguments(helloTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":hello").outcome == SUCCESS
	}

    /**
     * test standardoutput message 'Hello World!'
     */
    def "Test_OutputMessage"() {
        def expectedMsg = 'Hello World!'
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(helloTask)
            .withPluginClasspath()
            .build()

        then:
        def actualMsg = result.output
        assert(actualMsg.count(expectedMsg) == 1)
	}

}

