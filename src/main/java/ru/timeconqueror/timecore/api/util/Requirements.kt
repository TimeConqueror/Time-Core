package ru.timeconqueror.timecore.api.util

import net.minecraft.world.level.Level
import ru.timeconqueror.timecore.api.exception.IllegalSideException

object Requirements {
    /**
     * Requires the provided number to be in inclusive range from [min] to [max]
     * Throws exception if it isn't in this range.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is not in inclusive range.
     */
    @JvmOverloads
    @JvmStatic
    fun inRangeInclusive(number: Int, min: Int, max: Int, valueName: String = NUMBER) {
        require(number in min..max) { "Provided $valueName (=$number) should be in range [$min, $max] (inclusive)" }
    }

    /**
     * Requires the provided number to be in inclusive range from [min] to [max]
     * Throws exception if it isn't in this range.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is not in inclusive range.
     */
    @JvmOverloads
    @JvmStatic
    fun inRangeInclusive(number: Long, min: Long, max: Long, valueName: String = NUMBER) {
        require(number in min..max) { "Provided $valueName (=$number) should be in range [$min, $max] (inclusive)" }
    }

    /**
     * Requires the provided number to be in inclusive range from [min] to [max]
     * Throws exception if it isn't in this range.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is not in inclusive range.
     */
    @JvmOverloads
    @JvmStatic
    fun inRangeInclusive(number: Float, min: Float, max: Float, valueName: String = NUMBER) {
        require(number in min..max) { "Provided $valueName (=$number) should be in range [$min, $max] (inclusive)" }
    }

    /**
     * Requires the provided number to be in inclusive range from [min] to [max]
     * Throws exception if it isn't in this range.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is not in inclusive range.
     */
    @JvmOverloads
    @JvmStatic
    fun inRangeInclusive(number: Double, min: Double, max: Double, valueName: String = NUMBER) {
        require(number in min..max) { "Provided $valueName (=$number) should be in range [$min, $max] (inclusive)" }
    }

    /**
     * Requires the provided number to be in exclusive range from [min] to [max]
     * Throws exception if it isn't in this range.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is not in exclusive range.
     */
    @JvmOverloads
    @JvmStatic
    fun inRangeExclusive(number: Int, min: Int, max: Int, valueName: String = NUMBER) {
        require(number > min && number < max) { "Provided $valueName (=$number) should be in range ($min, $max) (exclusive)" }
    }

    /**
     * Requires the provided number to be in exclusive range from [min] to [max]
     * Throws exception if it isn't in this range.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is not in exclusive range.
     */
    @JvmOverloads
    @JvmStatic
    fun inRangeExclusive(number: Long, min: Long, max: Long, valueName: String = NUMBER) {
        require(number > min && number < max) { "Provided $valueName (=$number) should be in range ($min, $max) (exclusive)" }
    }

    /**
     * Requires the provided number to be in exclusive range from [min] to [max]
     * Throws exception if it isn't in this range.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is not in exclusive range.
     */
    @JvmOverloads
    @JvmStatic
    fun inRangeExclusive(number: Float, min: Float, max: Float, valueName: String = NUMBER) {
        require(number > min && number < max) { "Provided $valueName (=$number) should be in range ($min, $max) (exclusive)" }
    }

    /**
     * Requires the provided number to be in exclusive range from [min] to [max]
     * Throws exception if it isn't in this range.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is not in exclusive range.
     */
    @JvmOverloads
    @JvmStatic
    fun inRangeExclusive(number: Double, min: Double, max: Double, valueName: String = NUMBER) {
        require(number > min && number < max) { "Provided $valueName (=$number) should be in range ($min, $max) (exclusive)" }
    }

    /**
     * Requires the provided number to be greater or equal [min]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is less then [min].
     */
    @JvmOverloads
    @JvmStatic
    fun greaterOrEquals(number: Int, min: Int, valueName: String = NUMBER) {
        require(number >= min) { "Provided $valueName (=$number) should be greater or equal to $min" }
    }

    /**
     * Requires the provided number to be greater or equal [min]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is less then [min].
     */
    @JvmOverloads
    @JvmStatic
    fun greaterOrEquals(number: Long, min: Long, valueName: String = NUMBER) {
        require(number >= min) { "Provided $valueName (=$number) should be greater or equal to $min" }
    }

    /**
     * Requires the provided number to be greater or equal [min]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is less then [min].
     */
    @JvmOverloads
    @JvmStatic
    fun greaterOrEquals(number: Float, min: Float, valueName: String = NUMBER) {
        require(number >= min) { "Provided $valueName (=$number) should be greater or equal to $min" }
    }

    /**
     * Requires the provided number to be greater or equal [min]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is less then [min].
     */
    @JvmOverloads
    @JvmStatic
    fun greaterOrEquals(number: Double, min: Double, valueName: String = NUMBER) {
        require(number >= min) { "Provided $valueName (=$number) should be greater or equal to $min" }
    }

    /**
     * Requires the provided number to be greater than [min]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is less or equal [min].
     */
    @JvmOverloads
    @JvmStatic
    fun greaterThan(number: Int, min: Int, valueName: String = NUMBER) {
        require(number > min) { "Provided $valueName (=$number) should be greater than $min" }
    }

    /**
     * Requires the provided number to be greater than [min]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is less or equal [min].
     */
    @JvmOverloads
    @JvmStatic
    fun greaterThan(number: Long, min: Long, valueName: String = NUMBER) {
        require(number > min) { "Provided $valueName (=$number) should be greater than $min" }
    }

    /**
     * Requires the provided number to be greater than [min]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is less or equal [min].
     */
    @JvmOverloads
    @JvmStatic
    fun greaterThan(number: Float, min: Float, valueName: String = NUMBER) {
        require(number > min) { "Provided $valueName (=$number) should be greater than $min" }
    }

    /**
     * Requires the provided number to be greater than [min]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param min       minimum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is less or equal [min].
     */
    @JvmOverloads
    @JvmStatic
    fun greaterThan(number: Double, min: Double, valueName: String = NUMBER) {
        require(number > min) { "Provided $valueName (=$number) should be greater than $min" }
    }

    /**
     * Requires the provided number to be less than [max]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is greater or equal [max].
     */
    @JvmOverloads
    @JvmStatic
    fun lessThan(number: Int, max: Int, valueName: String = NUMBER) {
        require(number < max) { "Provided $valueName (=$number) should be less than $max" }
    }

    /**
     * Requires the provided number to be less than [max]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is greater or equal [max].
     */
    @JvmOverloads
    @JvmStatic
    fun lessThan(number: Long, max: Long, valueName: String = NUMBER) {
        require(number < max) { "Provided $valueName (=$number) should be less than $max" }
    }

    /**
     * Requires the provided number to be less than [max]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is greater or equal [max].
     */
    @JvmOverloads
    @JvmStatic
    fun lessThan(number: Float, max: Float, valueName: String = NUMBER) {
        require(number < max) { "Provided $valueName (=$number) should be less than $max" }
    }

    /**
     * Requires the provided number to be less than [max]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is greater or equal [max].
     */
    @JvmOverloads
    @JvmStatic
    fun lessThan(number: Double, max: Double, valueName: String = NUMBER) {
        require(number < max) { "Provided $valueName (=$number) should be less than $max" }
    }

    /**
     * Requires the provided number to be less or equal to [max]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is greater than [max].
     */
    @JvmOverloads
    @JvmStatic
    fun lessOrEquals(number: Int, max: Int, valueName: String = NUMBER) {
        require(number <= max) { "Provided $valueName (=$number) should be less or equal to $max" }
    }

    /**
     * Requires the provided number to be less or equal to [max]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is greater than [max].
     */
    @JvmOverloads
    @JvmStatic
    fun lessOrEquals(number: Long, max: Long, valueName: String = NUMBER) {
        require(number <= max) { "Provided $valueName (=$number) should be less or equal to $max" }
    }

    /**
     * Requires the provided number to be less or equal to [max]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is greater than [max].
     */
    @JvmOverloads
    @JvmStatic
    fun lessOrEquals(number: Float, max: Float, valueName: String = NUMBER) {
        require(number <= max) { "Provided $valueName (=$number) should be less or equal to $max" }
    }

    /**
     * Requires the provided number to be less or equal to [max]
     * Throws exception if this condition is not met.
     *
     * @param number    number to check
     * @param max       maximum value
     * @param valueName name of the number being checked. Used for more readable exception.
     * @throws IllegalArgumentException if number is greater than [max].
     */
    @JvmOverloads
    @JvmStatic
    fun lessOrEquals(number: Double, max: Double, valueName: String = NUMBER) {
        require(number <= max) { "Provided $valueName (=$number) should be less or equal to $max" }
    }

    /**
     * Requires the provided [array] to have the required [length]
     * Throws exception if this condition is not met.
     *
     * @param array     array to check
     * @param length    required length
     * @param arrayName name of the array being checked. Used for more readable exception.
     * @throws IllegalArgumentException if [array] doesn't have the required [length].
     */
    @JvmOverloads
    @JvmStatic
    fun <T> arrayWithLength(array: Array<T>, length: Int, arrayName: String? = null) {
        require(array.size == length) { "Provided array ${arrayName.transformIfNotNull { "'$it'" }} (with length = ${array.size}) should have length $length" }
    }

    /**
     * Requires the provided [object][obj] to extend provided [class][clazz]
     * Throws exception if this condition is not met.
     *
     * @param obj     object to check
     * @param clazz   class, which the the provided object should extend
     * @param objName name of the object being checked. Used for more readable exception.
     * @throws IllegalArgumentException if the object doesn't extend the provided class.
     */
    @JvmOverloads
    @JvmStatic
    fun instanceOf(obj: Any, clazz: Class<*>, objName: String? = null) {
        require(clazz.isInstance(obj)) { "Provided object ${objName.transformIfNotNull { "'$it'" }} (class = ${clazz.name}) should extend ${clazz.name}" }
    }

    /**
     * Requires the provided array to be not empty.
     *
     * @param arr       array to check
     * @param arrayName name of the array being checked. Used for more readable exception.
     * @throws IllegalArgumentException if empty array was provided.
     */
    @JvmOverloads
    @JvmStatic
    fun <T> notEmpty(arr: Array<T>, arrayName: String? = null) {
        require(arr.isNotEmpty()) { "Provided array ${arrayName.transformIfNotNull { "'$it'" }} shouldn't be empty" }
    }

    /**
     * Requires the provided collection to be not empty.
     *
     * @param collection     collection to check
     * @param collectionName name of the collection being checked. Used for more readable exception.
     * @throws IllegalArgumentException if empty collection was provided.
     */
    @JvmOverloads
    @JvmStatic
    fun <T> notEmpty(collection: Collection<T>, collectionName: String? = null) {
        require(collection.isNotEmpty()) { "Provided collection ${collectionName.transformIfNotNull { "'$it'" }} shouldn't be empty" }
    }

    /**
     * Requires code to be called only on server side.
     *
     * @throws IllegalSideException if is called on client side.
     */
    @JvmStatic
    fun onServer(level: Level) {
        if (level.isClientSide()) {
            IllegalSideException.notOnServer()
        }
    }

    /**
     * Requires code to be called only on client side.
     *
     * @throws IllegalSideException if is called on server side.
     */
    @JvmStatic
    fun onClient(level: Level) {
        if (!level.isClientSide()) {
            IllegalSideException.notOnClient()
        }
    }

    private const val NUMBER = "number"
}