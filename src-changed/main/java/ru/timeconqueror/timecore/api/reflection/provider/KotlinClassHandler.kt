package ru.timeconqueror.timecore.api.reflection.provider

import ru.timeconqueror.timecore.api.reflection.KReflectionHelper
import ru.timeconqueror.timecore.api.reflection.ReflectionHelper
import ru.timeconqueror.timecore.api.reflection.UnlockedMethod
import kotlin.reflect.KClass
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.jvm.javaMethod

object KotlinClassHandler : ClassHandler {
    override fun canHandle(clazz: Class<*>) = KReflectionHelper.isKotlinClass(clazz)

    override fun <O, R> isStatic(method: UnlockedMethod<O, R>): Boolean {
        val klass = method.method.declaringClass.kotlin
        return klass.objectInstance != null
    }

    override fun <O, R> requireStatic(method: UnlockedMethod<O, R>) {
        if (!isStatic(method)) throw IllegalArgumentException("Method $method is required to be only in object or companion!")
    }

    override fun <O : Any, R> findMethod(clazz: Class<O>, signature: String): UnlockedMethod<O, R>? {
        val klass = clazz.kotlin

        for (method in klass.memberFunctions) {
            val javaMethod = method.javaMethod
            if (javaMethod != null && ReflectionHelper.getMethodSignature(javaMethod) == signature) {
                return UnlockedMethod(javaMethod)
            }
        }

        return null
    }

    override fun <O, R> invokeStaticMethod(method: UnlockedMethod<O, R>, vararg args: Any): Any? {
        val staticInstance = getStaticInstance(method.method.declaringClass.kotlin) as O?
            ?: throw IllegalArgumentException("Method $method from ${method.method.declaringClass.name} is not in a companion or object.")

        return method.invoke(staticInstance, *args)
    }

    private fun <O : Any> getStaticInstance(klass: KClass<O>) = klass.objectInstance
}