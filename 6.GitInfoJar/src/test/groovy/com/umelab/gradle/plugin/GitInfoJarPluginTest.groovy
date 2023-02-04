package com.umelab.gradle.plugin

import org.gradle.api.Project
import org.gradle.testkit.runner.GradleRunner
import static org.gradle.testkit.runner.TaskOutcome.*
import spock.lang.TempDir
import spock.lang.Specification

import java.util.jar.JarFile
/**
* 【Testkit Folder Structure】
*
*     rootProject
*          +-- .git
*          |     +-- objects/info
*          |     +-- objects/pack
*          |     +-- refs/heads
*          |     |           +-- master
*          |     +-- HEAD
*          |        
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

class GitInfoJarPluginTest extends Specification {
    @TempDir File rootProjectDir

    File settingsGradle
    File buildGradle
    File gradleProperty

    def gitBaseDir
    def gitRefDir
    def gitObjectDir

    def gitHeadFile         // HEAD
    def gitInfoDir         // objects/info
    def gitPackDir         // objects/pack
    def gitMasterFile       // refs/heads/master

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

    def execTask = 'jar'

    def setup() {
        settingsGradle = new File(rootProjectDir, 'settings.gradle')
        buildGradle    = new File(rootProjectDir, 'build.gradle')
        gradleProperty = new File(rootProjectDir, 'gradle.properties')

        // git
        gitBaseDir = new File(rootProjectDir, '.git')
        gitRefDir = new File(gitBaseDir, 'refs/heads')
        gitObjectDir = new File(gitBaseDir, 'objects')
        gitInfoDir = new File(gitObjectDir, 'info')
        gitPackDir = new File(gitObjectDir, 'pack')

        gitBaseDir.mkdirs()
        gitRefDir.mkdirs()
        gitObjectDir.mkdirs()
        gitInfoDir.mkdirs()
        gitPackDir.mkdirs()

        gitHeadFile = new File(gitBaseDir, 'HEAD')
        gitMasterFile = new File(gitRefDir, 'master')

        commonProjectDir = new File(rootProjectDir, 'AA/Common/Eclipse/')
        onlineProjectDir = new File(rootProjectDir, 'AA/Online/Eclipse/')

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

        gitHeadFile << """\
            |ref: refs/heads/master
            |""".stripIndent().stripMargin()

        gitMasterFile << """\
            |e8d3ffab552895c19b9fcf7aa264d277cde33881
            |""".stripIndent().stripMargin()

        settingsGradle << """\
            |rootProject.name = 'gitinfojar-test'
            |include ":aa-common", ":aa-online"
            |project(":aa-common").projectDir = new File("AA/Common/Eclipse")
            |project(":aa-online").projectDir = new File("AA/Online/Eclipse")
            |""".stripIndent().stripMargin()

        buildGradle << """\
            |plugins{
            | id 'gitinfojar'    
            |} 
            |""".stripIndent().stripMargin()

        gradleProperty << """\
        	|# gradle.properties file
            |""".stripIndent().stripMargin()

        commonBuildGradle << """\
            |plugins{
            | id 'gitinfojar'
            |}
            |""".stripIndent().stripMargin()

        common1Java << """\
            |/**
            | * @update
            | */
            |public class Common1 {
            |}
            |""".stripIndent().stripMargin()    

        common2Java << """\
            |public class Common2 {
            |
            |}
            |""".stripIndent().stripMargin()    
        
        onlineBuildGradle << """\
            |plugins{
            | id 'gitinfojar'
            |}
            |""".stripIndent().stripMargin()
        
        online1Java << """\
            |/**
            | * @update
            | */
            |public class Online1 {
            |}
            |""".stripIndent().stripMargin()
        
        online2Java << """\
            |public class Online2 {
            |}
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
        def expectedMsg = 'Hello World!'
        when:
        def result = GradleRunner.create()
            .withProjectDir(rootProjectDir)
            .withArguments(execTask)
            .withPluginClasspath()
            .build()

        then:
        def actualMsg = result.output
        def onlineJarDir = new File(onlineProjectDir, 'build/libs/')
        def files = onlineJarDir.listFiles()
        def onlineJarFile = files[0]
        println '----------'
        println "${onlineProjectDir}/build/libs/aa-online.jar"
        println '----------'
        println onlineJarFile
        def actualBranchVal = getManifestKeyValue("${onlineProjectDir}/build/libs/aa-online.jar", "Git-Branch")
        println actualBranchVal
	}

    def getManifestKeyValue(jarFilePath, key) {
        def jarFile = new JarFile(new File(jarFilePath))
        def manifest = jarFile.getManifest()
        def attributes = manifest.getMainAttributes()
        return attributes.getValue(key)
    }
}

