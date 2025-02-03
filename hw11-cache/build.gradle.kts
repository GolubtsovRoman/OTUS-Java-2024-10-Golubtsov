dependencies {
    implementation("ch.qos.logback:logback-classic")

    implementation("org.postgresql:postgresql")
    implementation("com.zaxxer:HikariCP")

    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testImplementation("org.assertj:assertj-core")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
}
