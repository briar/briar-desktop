package androidx.compose.material

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.InitialFocusState.AFTER_FIRST_FOCUSSED
import androidx.compose.material.InitialFocusState.AFTER_FOCUS_LOST_ONCE
import androidx.compose.material.InitialFocusState.FROM_START
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Material Design outlined text field with extended support for error and helper messages,
 * as well as for handling the Enter key.
 * All parameters not specified here are the same as on the original [OutlinedTextField].
 *
 * @param onEnter Callback to be executed whenever the Enter key is pressed
 * @param errorIcon Icon to be shown instead of [trailingIcon] in case of error
 * @param helperMessage Message to be shown beneath the text field, should not exceed one line
 * @param errorMessage Message to be shown beneath the text field in case of error
 * @param showErrorWhen Show error only if the given focus state has been passed, even if [isError] is true
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun OutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    onEnter: () -> Unit = {},
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    errorIcon: @Composable (() -> Unit)? = null,
    helperMessage: String? = null,
    errorMessage: String? = null,
    isError: Boolean = false,
    showErrorWhen: InitialFocusState = FROM_START,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = MaterialTheme.shapes.small,
    colors: TextFieldColors = TextFieldDefaults.outlinedTextFieldColors()
) {
    var initialFocusState by remember { mutableStateOf(FROM_START) }
    val showError by derivedStateOf { isError && initialFocusState >= showErrorWhen }

    Column(
        modifier = modifier
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = modifier.onFocusEvent {
                when {
                    initialFocusState == FROM_START && it.isFocused -> initialFocusState = AFTER_FIRST_FOCUSSED
                    initialFocusState == AFTER_FIRST_FOCUSSED && !it.isFocused ->
                        initialFocusState =
                            AFTER_FOCUS_LOST_ONCE
                }
            }.onPreviewKeyEvent {
                if (it.type == KeyEventType.KeyUp && it.key == Key.Enter) {
                    onEnter()
                    return@onPreviewKeyEvent true
                }
                return@onPreviewKeyEvent false
            },
            enabled = enabled,
            readOnly = readOnly,
            singleLine = singleLine,
            textStyle = textStyle,
            label = label,
            placeholder = placeholder,
            leadingIcon = leadingIcon,
            trailingIcon = if (showError) errorIcon else trailingIcon,
            isError = showError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = KeyboardActions { onEnter() },
            maxLines = maxLines,
            interactionSource = interactionSource,
            shape = shape,
            colors = colors
        )
        Box(
            modifier = Modifier
                .requiredHeight(20.dp)
                .padding(start = 16.dp, end = 12.dp, top = 4.dp)
        ) {
            if (showError) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage,
                        style = LocalTextStyle.current.copy(
                            fontSize = 12.sp,
                            color = MaterialTheme.colors.error
                        )
                    )
                }
            } else {
                if (helperMessage != null) {
                    Text(
                        text = helperMessage,
                        style = LocalTextStyle.current.copy(
                            fontSize = 12.sp,
                            color = LocalTextStyle.current.color.copy(alpha = ContentAlpha.medium)
                        )
                    )
                }
            }
        }
    }
}

enum class InitialFocusState { FROM_START, AFTER_FIRST_FOCUSSED, AFTER_FOCUS_LOST_ONCE }
