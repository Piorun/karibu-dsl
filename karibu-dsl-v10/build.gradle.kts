dependencies {
    compile(kotlin("stdlib-jdk8"))

    testCompile("com.github.mvysny.dynatest:dynatest-engine:${properties["dynatest_version"]}")
    testCompile("com.github.mvysny.kaributesting:karibu-testing-v10:${properties["kaributesting_version"]}")
    testCompile("org.slf4j:slf4j-simple:${properties["slf4j_version"]}")

    // Vaadin
    // don't compile-depend on vaadin-core anymore: the app itself should manage Vaadin dependencies, for example
    // using the gradle-flow-plugin or direct dependency on vaadin-core. The reason is that the app may wish to use the
    // npm mode and exclude all webjars.
    compileOnly(platform("com.vaadin:vaadin-bom:${properties["vaadin10_version"]}"))
    compileOnly("com.vaadin:vaadin-core:${properties["vaadin10_version"]}")
    testCompile("com.vaadin:vaadin-core:${properties["vaadin10_version"]}")
    compileOnly("javax.servlet:javax.servlet-api:3.1.0")

    // IDEA language injections
    compile("com.intellij:annotations:12.0")

    // always include support for bean validation
    compile("javax.validation:validation-api:2.0.1.Final")  // so that the BeanFieldGroup will perform JSR303 validations
    compile("org.hibernate.validator:hibernate-validator:${properties["hibernate_validator_version"]}") {
        exclude(module = "jakarta.validation-api")
    }
    // EL is required: http://hibernate.org/validator/documentation/getting-started/
    compile("org.glassfish:javax.el:3.0.1-b08")
}

val configureBintray = ext["configureBintray"] as (artifactId: String) -> Unit
configureBintray("karibu-dsl-v10")
