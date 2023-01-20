package ru.timeconqueror.timecore.api.util

@JvmName("StringUtils")

fun String.capitalizeFirstLetter(): String {
    return replaceFirstChar { it.uppercaseChar() }
}

/**
 * Transforms the string if it's not null,
 * otherwise method won't call [transformNotNull] and will return an empty string.
 */
fun String?.transformIfNotNull(transformNotNull: (String) -> String): String {
    if (this != null) {
        return transformNotNull(this)
    }

    return ""
}