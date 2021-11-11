/*
Code inspired by and adapted to our needs from
https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:lifecycle/lifecycle-viewmodel/src/main/java/androidx/lifecycle/ViewModelProvider.kt;drc=ca3a75330b198ea068bc25d965597f72e719d8f5
originally licensed under the Apache License, Version 2.0
 */
package org.briarproject.briar.desktop.viewmodel

import javax.inject.Inject
import kotlin.reflect.KClass

class ViewModelProvider
@Inject
constructor(
    private val viewModelFactory: ViewModelFactory
) {

    private val viewModels = HashMap<String, ViewModel>()

    fun <VM : ViewModel> get(modelClass: KClass<VM>): VM =
        get(modelClass.qualifiedName!!, modelClass)

    fun <VM : ViewModel> get(key: String, modelClass: KClass<VM>): VM {
        val viewModel = viewModels[key]

        if (modelClass.isInstance(viewModel)) {
            return viewModel as VM
        }

        try {
            val viewModel = viewModelFactory.create(modelClass)
            viewModels[key] = viewModel
            return viewModel
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }
}
