package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification


class CreateDeleteFilePluginTest extends Specification {
    @TempDir File rootProjectDir

    File settingsGradle
    File buildGradle
    File gradleProperty

    def execTask = 'createFile'

    /**
    * 【Testkit Folder Structure】
    *
    *     rootProjectDir
    *          +-- build.gradle
    *          +-- gradle.properties
    *          +-- settings.gradle
    */
    def setup() {
        settingsGradle = new File(rootProjectDir, 'settings.gradle')
        buildGradle    = new File(rootProjectDir, 'build.gradle')
        gradleProperty = new File(rootProjectDir, 'gradle.properties')

        settingsGradle << """\
            |rootProject.name = 'fileoperator-test'
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins{
            | id 'fileoperator'    
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
     * check file created
     */
    def "Test_FileCreated"() {
        def filePath = 'tmp/test.txt'
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        def actualFile = new File(rootProjectDir, filePath)
        assert(actualFile.exists())
	}

    /**
     * check file deleted
     */
    def "Test_FileDeleted"() {
        def filePath = 'tmp/test.txt'
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments('deleteFile')
            .withPluginClasspath()
            .build()

        then:
        def actualFile = new File(rootProjectDir, filePath)
        assert(!actualFile.exists())
    }
}

