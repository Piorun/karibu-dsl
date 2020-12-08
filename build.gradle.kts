import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.*

plugins {
    kotlin("jvm") version "1.4.21"
    id("org.gretty") version "3.0.3"
    id("com.jfrog.bintray") version "1.8.3"
    `maven-publish`
    id("org.jetbrains.dokka") version "1.4.0"
    id("com.vaadin") version "0.14.3.7" apply(false)
}

defaultTasks("clean", "build")

allprojects {
    group = "com.github.mvysny.karibudsl"
    version = "1.0.4-SNAPSHOT"

    repositories {
        jcenter()
        mavenCentral()
        maven { setUrl("https://maven.vaadin.com/vaadin-prereleases/") }
    }

    tasks {
        // Heroku
        val stage by registering {
            // see example-v8/build.gradle.kts - the 'stage' task is properly configured there
        }
    }
}

subprojects {

    apply {
        plugin("maven-publish")
        plugin("kotlin")
        plugin("com.jfrog.bintray")
        plugin("org.jetbrains.dokka")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging {
            // to see the exceptions of failed tests in Travis-CI console.
            exceptionFormat = TestExceptionFormat.FULL
        }
    }

    // creates a reusable function which configures proper deployment to Bintray
    ext["configureBintray"] = { artifactId: String ->

        val local = Properties()
        val localProperties: File = rootProject.file("local.properties")
        if (localProperties.exists()) {
            localProperties.inputStream().use { local.load(it) }
        }

        val sourceJar = task("sourceJar", Jar::class) {
            dependsOn(tasks["classes"])
            archiveClassifier.set("sources")
            from(sourceSets.main.get().allSource)
        }

        val javadocJar = task("javadocJar", Jar::class) {
            from(tasks["dokkaJavadoc"])
            archiveClassifier.set("javadoc")
        }

        publishing {
            publications {
                create("mavenJava", MavenPublication::class.java).apply {
                    groupId = project.group.toString()
                    this.artifactId = artifactId
                    version = project.version.toString()
                    pom {
                        description.set("Karibu-DSL, Kotlin extensions/DSL for Vaadin")
                        name.set(artifactId)
                        url.set("https://github.com/mvysny/karibu-dsl")
                        licenses {
                            license {
                                name.set("The MIT License (MIT)")
                                url.set("https://opensource.org/licenses/MIT")
                                distribution.set("repo")
                            }
                        }
                        developers {
                            developer {
                                id.set("mavi")
                                name.set("Martin Vysny")
                                email.set("martin@vysny.me")
                            }
                        }
                        scm {
                            url.set("https://github.com/mvysny/karibu-dsl")
                        }
                    }

                    from(components["java"])
                    artifact(sourceJar)
                    artifact(javadocJar)
                }
            }
        }

        bintray {
            user = local.getProperty("bintray.user")
            key = local.getProperty("bintray.key")
            pkg(closureOf<BintrayExtension.PackageConfig> {
                repo = "github"
                name = "com.github.mvysny.karibudsl"
                setLicenses("MIT")
                vcsUrl = "https://github.com/mvysny/karibu-dsl"
                publish = true
                setPublications("mavenJava")
                version(closureOf<BintrayExtension.VersionConfig> {
                    this.name = project.version.toString()
                    released = Date().toString()
                })
            })
        }
    }
}
