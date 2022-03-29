package com.github.xuankaicat.ksp

import com.github.xuankaicat.annotation.MyAspect
import com.github.xuankaicat.annotation.Processor
import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.validate
import com.squareup.kotlinpoet.*

@AutoService(SymbolProcessorProvider::class)
class MyAspectProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment) = MyAspectProcessor(
        options = environment.options,
        codeGenerator = environment.codeGenerator,
        logger = environment.logger
    )
}

class MyAspectProcessor(
    val options: Map<String, String>,
    val codeGenerator: CodeGenerator,
    val logger: KSPLogger,
) : SymbolProcessor {

    var invoked = false

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (invoked) return emptyList()

        val symbols = resolver.getSymbolsWithAnnotation(MyAspect::class.qualifiedName!!)
        val ret = symbols.filter { !it.validate() }

        symbols.filter { it is KSClassDeclaration && it.validate() }
            .forEach {
                it.accept(MyAspectVisitor(), Unit)
            }

        invoked = true

        return ret.toList()
    }

    inner class MyAspectVisitor : KSVisitorVoid() {
        private lateinit var className: String
        private lateinit var packageName: String

        private lateinit var fileSpecBuilder: FileSpec.Builder
        private lateinit var typeSpecBuilder: TypeSpec.Builder

        override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
            val qualifiedName = classDeclaration.qualifiedName?.asString()

            if(qualifiedName == null) {
                logger.error("MyAspect must target classes with qualified names", classDeclaration)
                return
            }

            /**
             * 解析Class信息
             */
            className = qualifiedName
            packageName = classDeclaration.packageName.asString()
            val fileName = "${classDeclaration.simpleName.asString()}Proxy"

            fileSpecBuilder = FileSpec.builder(
                packageName =  packageName,
                fileName = fileName
            )

            typeSpecBuilder = TypeSpec.classBuilder(fileName)
                .addModifiers(KModifier.PUBLIC)
                .superclass(ClassName(packageName, classDeclaration.simpleName.asString()))
                .primaryConstructor(
                    FunSpec.constructorBuilder()
                        .addParameter(
                            ParameterSpec.builder("instance", ClassName(packageName, classDeclaration.simpleName.asString()))
                                .defaultValue("${ClassName(packageName, classDeclaration.simpleName.asString())}()")
                                .build()
                        )
                        .build()
                )
                .addProperty(PropertySpec.builder("instance", ClassName(packageName, classDeclaration.simpleName.asString()))
                    .initializer("instance")
                    .addModifiers(KModifier.PRIVATE)
                    .build())
            classDeclaration.getAllFunctions()
                .forEach {
                    it.accept(this, Unit)
                }

            fileSpecBuilder.addType(typeSpecBuilder.build())

            /**
             * 代码生成
             */
            codeGenerator.createNewFile(
                dependencies = Dependencies(aggregating = true),
                packageName = packageName,
                fileName = fileName
            ).use { outputStream ->
                outputStream.writer()
                    .use {
                        fileSpecBuilder.build().writeTo(it)
                    }
            }
        }

        override fun visitFunctionDeclaration(function: KSFunctionDeclaration, data: Unit) {
            var processor: KSAnnotation? = null
            function.annotations.filter {
                val annotations = it.annotationType.resolve().declaration.annotations
                processor = annotations.find { it.shortName.asString() == Processor::class.simpleName }
                processor != null
            }.forEach { annotation ->
                // found MyAspect annotation
                val declaration = (processor!!.arguments.first().value as KSType).declaration

                val funSpec = FunSpec.builder(function.simpleName.asString()).apply {
                    addModifiers(KModifier.OVERRIDE)
                    function.parameters.forEach {
                        addParameter(it.name!!.asString(), ClassName(packageName, it.type.toString()))
                    }
                    addStatement(
                        declaration.simpleName.asString() +
                            "(instance::" +
                            function.simpleName.asString() +
                            ", ${function.parameters.joinToString(",") { it.name!!.asString() }}" +
                            ")()"
                    )
                }.build()
                //throw Exception(declaration.packageName.asString())
                fileSpecBuilder.addImport(declaration.packageName.asString(), declaration.simpleName.asString())
                typeSpecBuilder.addFunction(funSpec)
            }
        }
    }
}