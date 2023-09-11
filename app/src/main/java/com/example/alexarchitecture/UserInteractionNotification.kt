package com.example.alexarchitecture

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.isActive

/**
 * A helper modifier that tracks any interaction happening on the element.
 *
 * This is usually helpful to understand which side of the [TwoPane] was interacted last so when the
 * layout is switch to a single pane the most appropriate UI is shown.
 *
 * @param onInteracted a callback to be invoked when the modifier element is interacted
 */
fun Modifier.userInteractionNotification(onInteracted: () -> Unit): Modifier {
    return pointerInput(onInteracted) {
        val currentContext = currentCoroutineContext()
        awaitPointerEventScope {
            while (currentContext.isActive) {
                val event = awaitPointerEvent(PointerEventPass.Initial)
                // if user taps (down) or scrolls - consider it an interaction signal
                if (
                    event.type == PointerEventType.Press || event.type == PointerEventType.Scroll
                ) {
                    onInteracted.invoke()
                }
            }
        }
    }
}
