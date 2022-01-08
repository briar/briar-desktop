package org.briarproject.briar.desktop.utils

/**
 * Add [element] to the list after the last element that matches the given [predicate].
 * If no element matches the [predicate], add [element] to the beginning of the list.
 * This method makes most sense for pre-sorted lists.
 */
fun <T> MutableList<T>.addAfterLast(element: T, predicate: (T) -> Boolean): Int {
    val idx = indexOfLast(predicate)
    if (idx == lastIndex || isEmpty()) {
        add(element)
    } else {
        add(idx + 1, element)
    }
    return idx + 1
}

fun <T> MutableList<T>.clearAndAddAll(elements: Collection<T>) {
    clear()
    addAll(elements)
}

fun <T> MutableList<T>.replaceIf(predicate: (T) -> Boolean, transformation: (T) -> T) {
    val li = listIterator()
    while (li.hasNext()) {
        val n = li.next()
        if (predicate(n)) {
            li.set(transformation(n))
        }
    }
}

fun <T> MutableList<T>.replaceIfIndexed(predicate: (Int, T) -> Boolean, transformation: (Int, T) -> T) {
    val li = listIterator()
    var index = 0
    while (li.hasNext()) {
        val n = li.next()
        if (predicate(index, n)) {
            li.set(transformation(index, n))
        }
        index++
    }
}

inline fun <T, reified U : T> MutableList<T>.replaceFirst(predicate: (U) -> Boolean, transformation: (U) -> U) {
    val li = listIterator()
    while (li.hasNext()) {
        val n = li.next()
        if (n is U && predicate(n)) {
            li.set(transformation(n))
            break
        }
    }
}

inline fun <T, reified U : T> MutableList<T>.removeFirst(predicate: (U) -> Boolean) {
    val li = listIterator()
    while (li.hasNext()) {
        val n = li.next()
        if (n is U && predicate(n)) {
            li.remove()
            break
        }
    }
}
