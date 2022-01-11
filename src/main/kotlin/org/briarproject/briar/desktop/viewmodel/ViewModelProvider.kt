/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    fun <VM : ViewModel> get(modelClass: KClass<VM>, key: String? = null): VM {
        val viewModelKey = "${modelClass.qualifiedName}:$key"
        val viewModel = viewModels[viewModelKey]

        if (modelClass.isInstance(viewModel)) {
            return viewModel as VM
        }

        try {
            val viewModel = viewModelFactory.create(modelClass)
            viewModels[viewModelKey] = viewModel
            return viewModel
        } catch (e: InstantiationException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Cannot create an instance of $modelClass", e)
        }
    }
}
