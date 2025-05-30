import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.gradle.plugins.ide.idea.model.IdeaLanguageLevel
import org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES

plugins {
    id("java")
    idea
    id("io.spring.dependency-management")
    id("org.springframework.boot") apply false
}

idea {
    project {
        languageLevel = IdeaLanguageLevel(21)
    }
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

allprojects {
    group = "ru.otus.java.dev.pro"
    version = "1.0-SNAPSHOT"

    repositories {
        mavenLocal()
        mavenCentral()
    }

    val testcontainersBom: String by project
    val guava: String by project
    val jacksonDatabind: String by project
    val reflections: String by project
    val grpc: String by project

    val sockjs: String by project
    val stomp: String by project
    val bootstrap: String by project

    val jsr305: String by project
    val r2dbcPostgresql: String by project

    apply(plugin = "io.spring.dependency-management")
    dependencyManagement {
        dependencies {
            dependencies {
                imports {
                    mavenBom(BOM_COORDINATES)
                    mavenBom("org.testcontainers:testcontainers-bom:$testcontainersBom")
                }

                dependency("com.google.guava:guava:$guava")
                dependency("com.fasterxml.jackson.core:jackson-core:$jacksonDatabind")
                dependency("com.fasterxml.jackson.core:jackson-databind:$jacksonDatabind")
                dependency("org.reflections:reflections:$reflections")

                dependency("io.grpc:grpc-netty:$grpc")
                dependency("io.grpc:grpc-protobuf:$grpc")
                dependency("io.grpc:grpc-stub:$grpc")

                dependency("org.webjars:sockjs-client:$sockjs")
                dependency("org.webjars:stomp-websocket:$stomp")
                dependency("org.webjars:bootstrap:$bootstrap")

                dependency("com.google.code.findbugs:jsr305:$jsr305")
                dependency("io.r2dbc:r2dbc-postgresql:$r2dbcPostgresql")
            }
        }
    }
}

subprojects {
    plugins.apply(JavaPlugin::class.java)
    extensions.configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.compilerArgs.addAll(listOf("-Xlint:all,-serial,-processing"))
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        testLogging.showExceptions = true
        reports {
            junitXml.required.set(true)
            html.required.set(true)
        }
    }

}


tasks {
    val managedVersions by registering {
        doLast {
            project.extensions.getByType<DependencyManagementExtension>()
                .managedVersions
                .toSortedMap()
                .map { "${it.key}:${it.value}" }
                .forEach(::println)
        }
    }
}
