package org.briarproject.briar.desktop.privategroups

import androidx.compose.runtime.mutableStateOf
import org.briarproject.briar.desktop.viewmodel.ViewModel
import javax.inject.Inject

class SingleThreadViewModel
@Inject
constructor() : ViewModel {
    private var _isOpen = mutableStateOf<Boolean>(false)
    val isOpen = _isOpen

    fun setViewState(isOpen: Boolean) {
        _isOpen.value = isOpen
    }
}