plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("com.sleepycat:je:18.3.12")
    implementation("edu.uci.ics:crawler4j:4.4.0")
    implementation("org.mongodb:mongodb-driver-sync:5.2.0")
}

tasks.test {
    useJUnitPlatform()
}