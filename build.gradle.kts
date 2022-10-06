
group = "sample"
version = "1.0"

val specialAttribute = Attribute.of("my-special-attribute", Named::class.java)

dependencies {
    attributesSchema {
        attribute(specialAttribute)
    }
}

val packing by tasks.registering(Zip::class) {
    from(layout.projectDirectory.file("gradle"))
    destinationDirectory.set(layout.buildDirectory.dir("archive"))
    archiveFileName.set("output.zip")
}

val outConf by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = true
    attributes {
        attribute(specialAttribute, objects.named("something"))
    }
    outgoing {
        artifact(packing.flatMap { it.archiveFile })
    }
}
