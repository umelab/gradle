package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification


class EnvPropertyPluginTest extends Specification {
    @TempDir File testProjectDir

    File settingsGradle
    File buildGradle
    File gradleProperty

    def envTask = 'env'
    def propTask = 'prop'

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
            |rootProject.name = 'env_prop-test'
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins{
            | id 'env_prop'    
            |} 
            |""".stripIndent().stripMargin()

        gradleProperty << """\
        	|# gradle.properties file
            |systemProp.Foo=bar
            |""".stripIndent().stripMargin()

    }

    /**
     * test task result
     */
    def "Test_Env_TaskResult"() {
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(envTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":env").outcome == SUCCESS
	}

    /**
     * test standardoutput message as 'c:\\tools\\gradle-7.2'
     * environment variable as GRADLE_HOME
     */
    def "Test_Env_OutputMessage"() {
        def expectedMsg = '/opt/homebrew/bin/gradle'
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(envTask)
            .withPluginClasspath()
            .build()

        then:
        def actualMsg = result.output
        assert(actualMsg.count(expectedMsg) == 1)
	}

    /**
     * How to use withEnvrionment method.
     * In this sample, environment variable GRADLE_HOME is
     * updated from 'c:\\tools\\gradle-7.2' to 'c:\\tools\\gradle-7.5'
     */
    def "Test_Env_OutputMessage_Update"() {
        def expectedMsg = '/opt/bin/gradle/'
        def map = ['GRADLE_HOME':expectedMsg]
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(envTask)
            .withEnvironment(map)
            .withPluginClasspath()
            .build()

        then:
        def actualMsg = result.output
        assert(actualMsg.count(expectedMsg) == 1)
	}

    /**
     * test standardoutput message as 'bar'
     * which is set in gradle.properties
     */
    def "Test_Output_Property_Message"(){
        def expectedMsg = 'bar'
        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(propTask)
            .withPluginClasspath()
            .build()

        then:
        // println result.output
        def actualMsg = result.output
        assert(actualMsg.count(expectedMsg) == 1)
    }

    /**
     * How to update property value
     * overwrite gradle.properties in the test method
     * as following:
     * systemProp.Foo=boo
     */
    def "Test_Output_UpdateProperty_Message"(){
        def expectedMsg = 'boo'
        // overwrite gradle.properties
        gradleProperty << """\
        	|# gradle.properties file
            |systemProp.Foo=boo
            |""".stripIndent().stripMargin()

        when:
        def result = GradleRunner.create()
            .withProjectDir(testProjectDir)
            .withArguments(propTask)
            .withPluginClasspath()
            .build()

        then:
        def actualMsg = result.output
        assert(actualMsg.count(expectedMsg) == 1)
    }
}

