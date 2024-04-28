plugins {
    id("java")
    `maven-publish`
    signing
}

group = "net.eewbot.base65536j"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}

tasks {
    create<Copy>("includeReadmeAndLicense") {
        destinationDir = project.layout.buildDirectory.file("resources/main").get().asFile

        from(rootProject.file("LICENSE")) {
            rename { "LICENSE_${rootProject.name}" }
        }

        from(rootProject.file("README.md")) {
            rename { "README_${rootProject.name}.md" }
        }
    }

    create<Jar>("sourcesJar") {
        destinationDirectory.set(rootProject.layout.buildDirectory.file("libs").get().asFile)

        archiveClassifier.set("sources")

        from(sourceSets.main.get().allSource)
        from(processResources.get().outputs)
        from(getByName("includeReadmeAndLicense").outputs)
    }

    jar {
        dependsOn("includeReadmeAndLicense")
    }

    compileTestJava {
        dependsOn("includeReadmeAndLicense")
    }

    assemble {
        dependsOn("sourcesJar")
    }

    test {
        useJUnitPlatform()
    }
}

publishing {
    publications {
        create<MavenPublication>("publication") {
            groupId = rootProject.group.toString()
            artifactId = rootProject.name
            version = rootProject.version.toString()

            from(components.getByName("java"))
            artifact(tasks.getByName("sourcesJar"))

            pom {
                name.set(artifactId)
                description.set(rootProject.description)
                url.set("https://github.com/EEWBot/Base65536J")

                licenses {}

                developers {
                    developer {
                        id.set("Siro256")
                        name.set("Siro_256")
                        email.set("siro@siro256.dev")
                        url.set("https://github.com/Siro256")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/EEWBot/Base65536J.git")
                    developerConnection.set("scm:git:ssh://github.com/EEWBot/Base65536J.git")
                    url.set("https://github.com/EEWBot/Base65536J")
                }
            }
        }
    }

    repositories {
        maven {
            url = uri("https://maven.pkg.github.com/EEWBot/Base65536J")
            credentials {
                username = "EEWBot"
                password = System.getenv("GPR_KEY")
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        System.getenv("SIGNING_KEY_ID"),
        System.getenv("SIGNING_KEY"),
        System.getenv("SIGNING_KEY_PASSWORD")
    )
    sign(publishing.publications.getByName("publication"))
}
