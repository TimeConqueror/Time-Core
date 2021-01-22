package ru.timeconqueror.timecore.api.reflection.provider

import ru.timeconqueror.timecore.api.reflection.ReflectionHelper
import ru.timeconqueror.timecore.api.reflection.UnlockedMethod
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod

object KotlinClassHandler : ClassHandler {
    override fun canHandle(clazz: Class<*>) = isKotlinClass(clazz)

    override fun <R : Any?> isStatic(method: UnlockedMethod<R>): Boolean {
        val klass = method.method.declaringClass.kotlin
        return klass.objectInstance != null
    }

    override fun <R : Any?> requireStatic(method: UnlockedMethod<R>) {
        if (!isStatic(method)) throw IllegalArgumentException("Method $method is required to be only in object or companion!")
    }

    override fun <R : Any?> findMethod(clazz: Class<*>, signature: String): UnlockedMethod<R>? {
        val klass = clazz.kotlin

        for (method in klass.memberFunctions) {
            val javaMethod = method.javaMethod
            if (javaMethod != null && ReflectionHelper.getMethodSignature(javaMethod) == signature) {
                return UnlockedMethod(javaMethod)
            }
        }

        return null
    }

    override fun <R> invokeStaticMethod(method: UnlockedMethod<R>, vararg args: Any): Any? {
        val staticInstance = getStaticInstance(method.method.declaringClass.kotlin)
            ?: throw IllegalArgumentException("Method $method from ${method.method.declaringClass.name} is not in a companion or object.")

        return method.invoke(staticInstance, *args)
    }

    private fun getStaticInstance(klass: KClass<*>) = if (klass.objectInstance != null) klass.objectInstance else null

    private fun isKotlinClass(clazz: Class<*>): Boolean {
        return clazz.declaredAnnotations.any {
            it.annotationClass == Metadata::class
        }
    }
}