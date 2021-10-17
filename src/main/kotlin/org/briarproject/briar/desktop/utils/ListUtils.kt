package org.briarproject.briar.desktop.utils

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
