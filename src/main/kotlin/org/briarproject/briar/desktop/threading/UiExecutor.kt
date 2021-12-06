package org.briarproject.briar.desktop.threading

import javax.inject.Qualifier

/**
 * Annotation for injecting the executor for tasks that should be run on the UI thread.
 * Also used for annotating methods that should run on the UI executor.
 */
@Qualifier
@Target(
    AnnotationTarget.FIELD,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER,
    AnnotationTarget.VALUE_PARAMETER
)
@Retention(AnnotationRetention.RUNTIME)
annotation class UiExecutor
