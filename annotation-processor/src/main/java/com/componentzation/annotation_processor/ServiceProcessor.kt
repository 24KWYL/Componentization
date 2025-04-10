package com.componentzation.annotation_processor

import com.componentization.annotation.ServiceAnnotation
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ksp.writeTo

class ServiceProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
) :
    SymbolProcessor {

    private var isGenerated = false // 确保只生成一次
    private val implMap = mutableMapOf<String, String>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        if (isGenerated) return emptyList()
        resolver.getSymbolsWithAnnotation(ServiceAnnotation::class.qualifiedName ?: "")
            .filterIsInstance<KSClassDeclaration>()
            .forEach { implClass ->
                val implName = implClass.qualifiedName?.asString() ?: ""
                implClass.superTypes
                    .mapNotNull { it.resolve().declaration as? KSClassDeclaration }
                    .filter { it.classKind == ClassKind.INTERFACE }
                    .forEach { interfaceDecl ->
                        val interfaceName = interfaceDecl.qualifiedName?.asString() ?: ""
                        logger.info("ServiceProcessor   interfaceName : $interfaceName  implName : $implName")
                        implMap[interfaceName] = implName
                    }
            }
        generateRegistryClass()
        isGenerated = true
        return emptyList()
    }

    private fun generateRegistryClass() {
        val packageName = "com.example.generated"
        val fileName = "ServiceRegistry.kt"
        FileSpec.builder(packageName, fileName)
            .addType(
                TypeSpec.classBuilder("ServiceRegistry")
                    .addFunction(createMapperFunction())
                    .build()
            )
            .build()
            .writeTo(codeGenerator, Dependencies(aggregating = false))
    }

    private fun createMapperFunction(): FunSpec {
        return FunSpec.builder("getMapper")
            .addModifiers(KModifier.PUBLIC)
            .returns(MAP_TYPE)
            .addCode(buildMapperCode()) // 使用 addCode 插入代码块
            .build()
    }

    private fun buildMapperCode(): CodeBlock {
        val code = CodeBlock.builder()
            .add("return mutableMapOf(\n")
            .indent()
        implMap.forEach { (interfaceName, implName) ->
            code.add("%S to %S,\n", interfaceName, implName)
        }
        return code
            .unindent()
            .add(")\n")
            .build()
    }

    companion object {
        private val MAP_TYPE = Map::class.asClassName()
            .parameterizedBy(String::class.asClassName(), String::class.asClassName())
    }
}