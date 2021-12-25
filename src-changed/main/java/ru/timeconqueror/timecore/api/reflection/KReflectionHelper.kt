package ru.timeconqueror.timecore.api.reflection

object KReflectionHelper {
    fun isKotlinClass(clazz: Class<*>): Boolean {
        return clazz.declaredAnnotations.any {
            it.annotationClass == Metadata::class
        }
    }
}