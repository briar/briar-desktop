/*
Code inspired by and adapted to our needs from
https://cs.android.com/androidx/architecture-components-samples/+/main:GithubBrowserSample/app/src/main/java/com/android/example/github/viewmodel/GithubViewModelFactory.kt;drc=3283b6bbc6c9d62a616f058cdf0225185a5a69d1
originally licensed under the Apache License, Version 2.0
 */
package org.briarproject.briar.desktop.viewmodel

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.reflect.KClass

@Singleton
class ViewModelFactory
@Inject
constructor(
    private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>
) {
    fun <VM : ViewModel> create(modelClass: KClass<VM>): VM {
        var creator = creators[modelClass.java]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.java.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }
        requireNotNull(creator) { "unknown model class $modelClass" }
        return creator.get() as VM
    }
}
