package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification


class KeywordGrepPluginTest extends Specification {
    @TempDir File rootProject

    File settingsGradle
    File buildGradle
    File gradleProperty

    def commonProjectDir 
    def onlineProjectDir 
    def commonJavaSrcDir
    def onlineJavaSrcDir 
    File commonBuildGradle
    File common1Java
    File common2Java

    File onlineBuildGradle
    File online1Java
    File online2Java

    def execTask = 'replaceTag'

    /**
    * 【Testkit Folder Structure】
    *
    *     rootProject
    *          +-- AA/Common/Eclipse
    *          |         +-- build.gradle
    *          |         +-- src/main/java
    *          |                     +-- Common1.java
    *          |                     +-- Common2.java
    *          +-- AA/Online/Eclipse
    *          |         +-- build.gradle
    *          |         +-- src/main/java
    *          |                     +-- Online1.java
    *          |                     +-- Online2.java
    *          +-- build.gradle
    *          +-- gradle.properties
    *          +-- settings.gradle
    */
    def setup() {
        settingsGradle = new File(rootProject, 'settings.gradle')
        buildGradle    = new File(rootProject, 'build.gradle')
        gradleProperty = new File(rootProject, 'gradle.properties')

        commonProjectDir = new File(rootProject, 'AA/Common/Eclipse/')
        onlineProjectDir = new File(rootProject, 'AA/Online/Eclipse/')

        commonJavaSrcDir = new File(commonProjectDir, 'src/main/java/')
        onlineJavaSrcDir = new File(onlineProjectDir, 'src/main/java/')

        commonProjectDir.mkdirs()
        onlineProjectDir.mkdirs()
        commonJavaSrcDir.mkdirs()
        onlineJavaSrcDir.mkdirs()

        commonBuildGradle = new File(commonProjectDir, 'build.gradle')
        common1Java       = new File(commonProjectDir, 'src/main/java/Common1.java')
        common2Java       = new File(commonProjectDir, 'src/main/java/Common2.java')

        onlineBuildGradle = new File(onlineProjectDir, 'build.gradle')
        online1Java       = new File(onlineProjectDir, 'src/main/java/Online1.java')
        online2Java       = new File(onlineProjectDir, 'src/main/java/Online2.java')

        settingsGradle << """\
            |rootProject.name = 'keywordgrep-test'
            |include ":aa-common", ":aa-online"
            |project(":aa-common").projectDir = new File("AA/Common/Eclipse")
            |project(":aa-online").projectDir = new File("AA/Online/Eclipse")
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins{
            | id 'keywordgrep'
            |} 
            |""".stripIndent().stripMargin()

        gradleProperty << """\
        	|# gradle.properties file
            |""".stripIndent().stripMargin()

        commonBuildGradle << """\
            |plugins{
            | id 'keywordgrep'
            |}
            |""".stripIndent().stripMargin()

        common1Java << """\
            |package com.umelab.gradle.plugin;
            |/**
            | * @update
            | */
            |public class Common1 {
            |    public static void main(String[] args) {
            |        System.out.println("Hello World!");
            |    }
            |}
            |""".stripIndent().stripMargin()    

        common2Java << """\
            |package com.umelab.gradle.plugin;
            |public class Common2 {
            |    public static void main(String[] args) {
            |        System.out.println("Hello World!");
            |    }
            |}
            |""".stripIndent().stripMargin()    
        
        onlineBuildGradle << """\
            |plugins{
            | id 'keywordgrep'
            |}
            |""".stripIndent().stripMargin()
        
        online1Java << """\
            |package com.umelab.gradle.plugin;
            |/**
            | * @update
            | */
            |public class Online1 {
            |    public static void main(String[] args) {
            |        System.out.println("Hello World!");
            |    }
            |}
            |""".stripIndent().stripMargin()
        
        online2Java << """\
            |package com.umelab.gradle.plugin;
            |public class Online2 {
            |    public static void main(String[] args) {
            |        System.out.println("Hello World!");
            |    }
            |}
            |""".stripIndent().stripMargin()
    }

    /**
     * test task result
     */
    def "test task status with common project"() {
        when:
        def result = GradleRunner.create()
            .withProjectDir(commonProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":aa-common:${execTask}").outcome == SUCCESS
    }

    /**
     * test task result
     */
    def "test task status with online project"() {
        when:
        def result = GradleRunner.create()
            .withProjectDir(onlineProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":aa-online:${execTask}").outcome == SUCCESS
    }

    /**
     * test task result
     */
    def "test task status with common project and online project"() {
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProject)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        result.task(":${execTask}").outcome == SUCCESS
    }

    /**
     * test task result
     */
    def "test replaced tag is correct"() {
        def targetTag = '@newupdate'
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProject)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        def actualCommon1 = new File(commonProjectDir, 'src/main/java/Common1.java').text
        assert(actualCommon1.contains(targetTag))
        def actualCommon2 = new File(commonProjectDir, 'src/main/java/Common2.java').text
        assert(!actualCommon2.contains(targetTag)) 
        def actualOnline1 = new File(onlineProjectDir, 'src/main/java/Online1.java').text
        assert(actualOnline1.contains(targetTag))
        def actualOnline2 = new File(onlineProjectDir, 'src/main/java/Online2.java').text
        assert(!actualOnline2.contains(targetTag))
    }
}

