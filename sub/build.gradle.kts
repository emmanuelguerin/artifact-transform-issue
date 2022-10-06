

abstract class ArchiveUnzipTransform @Inject constructor(private val archiveOperations: ArchiveOperations, private val fileSystemOperations: FileSystemOperations): TransformAction<TransformParameters.None> {
@get:InputArtifact
abstract val inputArtifact: Provider<FileSystemLocation>

    override
    fun transform(outputs: TransformOutputs) {
        val input = inputArtifact.get().asFile
        val unzipDir = outputs.dir(input.name)
        fileSystemOperations.sync {
            from(archiveOperations.zipTree(input))
            into(unzipDir)
        }
    }
}

val specialAttribute = Attribute.of("my-special-attribute", Named::class.java)

val resolvedTransform by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes {
        attribute(specialAttribute, objects.named("target"))
    }
}

val resolvedSimple by configurations.creating {
    isCanBeConsumed = false
    isCanBeResolved = true
    attributes {
        attribute(specialAttribute, objects.named("something"))
    }
}

dependencies {
    attributesSchema {
        attribute(specialAttribute)
    }
    registerTransform(ArchiveUnzipTransform::class.java) {
        from.attribute(specialAttribute, objects.named("something"))
        to.attribute(specialAttribute, objects.named("target"))
    }
    resolvedTransform(project(":"))
    resolvedSimple(project(":"))
}

val resolveTransform by tasks.registering {
    inputs.files(resolvedTransform)
    doLast {
        inputs.files.forEach {
            println(it)
        }
    }
}

val resolveSimple by tasks.registering {
    inputs.files(resolvedSimple)
    doLast {
        inputs.files.forEach {
            println(it)
        }
    }
}

val check by tasks.registering {
    dependsOn(resolveTransform, resolveSimple)
}
