package org.briarproject.briar.desktop.utils

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

fun <T> MutableList<T>.replaceFirst(predicate: (T) -> Boolean, transformation: (T) -> T) {
    val li = listIterator()
    while (li.hasNext()) {
        val n = li.next()
        if (predicate(n)) {
            li.set(transformation(n))
            break
        }
    }
}

fun <T> MutableList<T>.removeFirst(predicate: (T) -> Boolean) {
    val li = listIterator()
    while (li.hasNext()) {
        val n = li.next()
        if (predicate(n)) {
            li.remove()
            break
        }
    }
}
