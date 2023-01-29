package ru.timeconqueror.timecore.api.reflection

import kotlin.reflect.KProperty1

object KReflectionHelper {
    fun isKotlinClass(clazz: Class<*>): Boolean {
        return clazz.declaredAnnotations.any {
            it.annotationClass == Metadata::class
        }
    }

    val KProperty1<*, *>.qualifiedName: String
        get() = this.javaClass.name + "#" + this.name
}