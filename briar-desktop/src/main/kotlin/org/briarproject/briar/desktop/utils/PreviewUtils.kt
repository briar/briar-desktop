/*
 * Briar Desktop
 * Copyright (C) 2021-2023 The Briar Project
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

package org.briarproject.briar.desktop.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.singleWindowApplication
import org.briarproject.bramble.api.UniqueId
import org.briarproject.briar.desktop.settings.UnencryptedSettings
import org.briarproject.briar.desktop.theme.BriarTheme
import org.briarproject.briar.desktop.ui.LocalWindowFocusState
import org.briarproject.briar.desktop.ui.LocalWindowScope
import org.briarproject.briar.desktop.ui.WindowFocusState
import org.briarproject.briar.desktop.utils.UiUtils.DensityDimension
import org.briarproject.briar.desktop.viewmodel.SingleStateEvent
import org.jetbrains.annotations.NonNls
import java.util.prefs.Preferences
import kotlin.random.Random

object PreviewUtils {

    class PreviewScope {
        private val random = Random(0)

        val parameters = mutableMapOf<String, MutableState<Any>>()

        private inline fun <reified T> getDatatype(name: String): T {
            val state = parameters[name] ?: throw IllegalArgumentException("No parameter found with name '$name'")
            if (state.value !is T) throw IllegalArgumentException("Parameter '$name' is not of type ${T::class.simpleName}")
            return state.value as T
        }

        private inline fun <reified T> setDatatype(name: String, value: T) {
            val state = parameters[name] ?: throw IllegalArgumentException("No parameter found with name '$name'")
            if (state.value !is T) throw IllegalArgumentException("Parameter '$name' is not of type ${T::class.simpleName}")
            state.value = value!!
        }

        fun getStringParameter(@NonNls name: String) = getDatatype<String>(name)

        fun setStringParameter(@NonNls name: String, value: String) = setDatatype(name, value)

        fun getBooleanParameter(@NonNls name: String) = getDatatype<Boolean>(name)

        fun setBooleanParameter(@NonNls name: String, value: Boolean) = setDatatype(name, value)

        fun getIntParameter(@NonNls name: String) = getDatatype<Int>(name)

        fun setIntParameter(@NonNls name: String, value: Int) = setDatatype(name, value)

        fun getLongParameter(@NonNls name: String) = getDatatype<Long>(name)

        fun setLongParameter(@NonNls name: String, value: Long) = setDatatype(name, value)

        fun getFloatParameter(@NonNls name: String) = getDatatype<Float>(name)

        fun setFloatParameter(@NonNls name: String, value: Float) = setDatatype(name, value)

        fun getRandomId() = random.nextBytes(UniqueId.LENGTH)

        @Composable
        fun getRandomIdPersistent() =
            remember { getRandomId() }
    }

    @Composable
    private fun <T : Any> PreviewScope.addParameter(
        name: String,
        initial: T,
        editField: @Composable (MutableState<T>) -> Unit,
    ) {
        val value = remember { mutableStateOf(initial) }

        Row {
            Text("$name: ")
            editField(value)
        }

        parameters[name] = value as MutableState<Any>
    }

    @Composable
    private fun PreviewScope.addStringParameter(name: String, initial: String) = addParameter(name, initial) { value ->
        BasicTextField(value.value, { value.value = it })
    }

    @Composable
    private fun PreviewScope.addBooleanParameter(name: String, initial: Boolean) =
        addParameter(name, initial) { value ->
            Box(modifier = Modifier.size(15.dp).border(1.dp, Color.Black).clickable { value.value = !value.value }) {
                if (value.value) Icon(Icons.Filled.Done, "")
            }
        }

    @Composable
    private fun PreviewScope.addIntParameter(name: String, initial: Int) = addParameter(name, initial) { value ->
        BasicTextField(value.value.toString(), { value.value = it.toInt() })
    }

    @Composable
    private fun PreviewScope.addLongParameter(name: String, initial: Long) = addParameter(name, initial) { value ->
        BasicTextField(value.value.toString(), { value.value = it.toLong() })
    }

    @Composable
    private fun PreviewScope.addFloatParameter(name: String, initial: Float) = addParameter(name, initial) { value ->
        BasicTextField(value.value.toString(), { value.value = it.toFloat() })
    }

    @Composable
    private fun PreviewScope.addFloatSliderParameter(name: String, initial: FloatSlider) =
        addParameter(name, initial.initial) { value ->
            Slider(
                value.value,
                { value.value = it },
                valueRange = initial.min..initial.max,
                modifier = Modifier.width(400.dp)
            )
        }

    @Composable
    private fun PreviewScope.addDropDownParameter(
        name: String,
        initial: DropDownValues,
    ) {
        var expanded by remember { mutableStateOf(false) }
        val items = initial.values
        val initialValue = items[initial.initial]
        var selectedIndex by remember { mutableStateOf(initial.initial) }
        addParameter(name, initialValue) { value ->
            Box {
                Text(
                    items[selectedIndex],
                    modifier = Modifier.fillMaxWidth().clickable(onClick = { expanded = true })
                )
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items.forEachIndexed { index, s ->
                        DropdownMenuItem(
                            onClick = {
                                selectedIndex = index
                                expanded = false
                                value.value = items[index]
                            }
                        ) {
                            Text(text = s)
                        }
                    }
                }
            }
        }
    }

    /**
     * Open an interactive preview of the composable specified by [content].
     * All [parameters] passed to this function will be changeable on the fly.
     * They can be retrieved as [State] using [PreviewScope.getStringParameter] or similar functions
     * and used inside the composable [content].
     */
    @Suppress("HardCodedStringLiteral")
    fun preview(
        vararg parameters: Pair<String, Any>,
        content: @Composable PreviewScope.() -> Unit,
    ) {
        val scope = PreviewScope()

        val prefs = Preferences.userNodeForPackage(PreviewUtils::class.java)
        val settingsDensity: Float? = prefs.get("previewsUiScale", null)?.toFloat()

        singleWindowApplication(title = "Interactive Preview") {
            val focusState = remember { WindowFocusState() }
            CompositionLocalProvider(
                LocalWindowScope provides this,
                LocalWindowFocusState provides focusState
            ) {
                Column {
                    val density = settingsDensity ?: LocalDensity.current.density
                    CompositionLocalProvider(LocalDensity provides Density(density)) {
                        window.preferredSize = DensityDimension(800, 600)
                        Column(Modifier.padding(10.dp)) {
                            scope.addBooleanParameter("darkTheme", true)
                            scope.addDropDownParameter(
                                "language",
                                DropDownValues(0, UnencryptedSettings.Language.values().toList().map { it.name })
                            )
                            scope.addFloatParameter("density", density)
                            parameters.forEach { (name, initial) ->
                                when (initial) {
                                    is String -> scope.addStringParameter(name, initial)
                                    is Boolean -> scope.addBooleanParameter(name, initial)
                                    is Int -> scope.addIntParameter(name, initial)
                                    is Long -> scope.addLongParameter(name, initial)
                                    is Float -> scope.addFloatParameter(name, initial)
                                    is FloatSlider -> scope.addFloatSliderParameter(name, initial)
                                    is DropDownValues -> scope.addDropDownParameter(name, initial)
                                    else -> throw IllegalArgumentException("Type ${initial::class.simpleName} is not supported for previewing.")
                                }
                            }
                        }
                    }

                    val invalidate = remember { SingleStateEvent<Boolean>() }

                    LaunchedEffect(scope.getStringParameter("language")) {
                        InternationalizationUtils.locale =
                            UnencryptedSettings.Language.valueOf(scope.getStringParameter("language")).locale
                        invalidate.emit(true)
                    }

                    invalidate.react { return@Column }

                    BriarTheme(
                        isDarkTheme = scope.getBooleanParameter("darkTheme"),
                        density = scope.getFloatParameter("density"),
                    ) {
                        Box(Modifier.fillMaxSize(1f)) {
                            Column(Modifier.padding(10.dp)) {
                                content(scope)
                            }
                        }
                    }
                }
            }
        }
    }

    data class FloatSlider(
        val initial: Float,
        val min: Float,
        val max: Float,
    )

    data class DropDownValues(
        val initial: Int,
        val values: List<String>,
    )
}
