package io.rot.labs.tunnel_compiler

import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import io.rot.labs.tunnel_common.SubscriberDetail
import io.rot.labs.tunnel_common.annotation.Subscribe
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.SourceVersion
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic


class TunnelProcessor : AbstractProcessor() {


    lateinit var messager: Messager
    lateinit var elementUtils: Elements
    override fun init(p0: ProcessingEnvironment) {
        super.init(p0)
        messager = p0.messager
        elementUtils = p0.elementUtils
    }

    override fun process(
        annotations: MutableSet<out TypeElement>,
        roundEnv: RoundEnvironment
    ): Boolean {
        val tunnelMap = ConcurrentHashMap<String, ArrayList<SubscriberDetail>>()

        var rootPackage = ""
        for (rootElement in roundEnv.rootElements) {
            rootPackage = elementUtils.getPackageOf(rootElement).qualifiedName.toString()
            break
        }


        if (rootPackage.isNotEmpty()) {
            val generatedClassName = ClassName(rootPackage, "TunnelMap")

            val arrayListTypeName =
                ArrayList::class.asClassName().parameterizedBy(SubscriberDetail::class.asTypeName())
            val hashMapTypeName =
                ConcurrentHashMap::class.asClassName().parameterizedBy(STRING, arrayListTypeName)
            val mapProperty = PropertySpec.builder("map", hashMapTypeName, KModifier.PRIVATE)
                .initializer(
                    "%T<%T,%T<%T>>()",
                    ConcurrentHashMap::class,
                    String::class,
                    ArrayList::class,
                    SubscriberDetail::class
                )
                .build()

            val classBuilder = TypeSpec.objectBuilder(generatedClassName)
                .addModifiers(KModifier.PUBLIC)
                .addProperty(mapProperty)
                .addFunction(
                    FunSpec.builder("getMap")
                        .addModifiers(KModifier.PUBLIC)
                        .returns(hashMapTypeName)
                        .addAnnotation(JvmStatic::class)
                        .addStatement("return map")
                        .build()
                )

            val codeBlockBuilder = CodeBlock.builder()

            for (element in roundEnv.getElementsAnnotatedWith(Subscribe::class.java)) {
                val method = element as ExecutableElement
                val msgObjectType = method.parameters[0].asType()
                val subscribe = element.getAnnotation(Subscribe::class.java)
                if (subscribe != null) {
                    for (channelId in subscribe.channelIds) {
                        val key = "${msgObjectType}_$channelId"
                        messager.printMessage(Diagnostic.Kind.WARNING, "Produced Key : $key")
                        var subscribeDetailList = tunnelMap[key]
                        if (subscribeDetailList == null) {
                            subscribeDetailList = ArrayList()
                        }
                        val invokerClassName = element.enclosingElement.asType().toString()
                        val methodName = element.simpleName.toString()
                        subscribeDetailList.add(
                            SubscriberDetail(
                                invokerClassName,
                                methodName,
                                subscribe.dispatcherType
                            )
                        )
                        tunnelMap[key] = subscribeDetailList
                    }
                }
            }

            for (entry in tunnelMap.entries) {
                val sb = StringBuilder("arrayListOf(")
                for (subscribeObj in entry.value) {
                    with(subscribeObj) {
                        sb.append("SubscriberDetail(\"${invokerClassName}\",\"${methodName}\",${dispatcherType::class.simpleName}.${dispatcherType.name}),")
                    }
                }
                val arrayListStr = sb.toString()
                val modArrayListStr = arrayListStr.substring(0, arrayListStr.length - 1) + ")"

                codeBlockBuilder.addStatement("map.put(\"${entry.key}\",${modArrayListStr})")
            }

            classBuilder.addInitializerBlock(codeBlockBuilder.build())

            val fileBuilder = FileSpec.builder(rootPackage, "TunnelMap")
                .addImport("io.rot.labs.tunnel_common.utils", "DispatcherType")
                .addType(classBuilder.build())

            fileBuilder.build().writeTo(processingEnv.filer)
            messager.printMessage(Diagnostic.Kind.WARNING, "SIZE ${tunnelMap.size}")
        }
        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(Subscribe::class.java.canonicalName)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.RELEASE_8
    }
}