plugins {
    id("java")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.0.0.202111291000-r")
}
