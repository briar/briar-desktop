package org.briarproject.briar.desktop.utils

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
