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
