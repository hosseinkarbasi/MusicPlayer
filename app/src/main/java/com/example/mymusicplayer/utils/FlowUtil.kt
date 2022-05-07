package com.example.mymusicplayer.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collectWithRepeatOnLifecycle(
    lifecycleOwner: LifecycleOwner,
    crossinline block: suspend (T) -> Unit
) {
    lifecycleOwner.lifecycleScope.launch {
        lifecycleOwner.lifecycle.repeatOnLifecycle(state = Lifecycle.State.STARTED) {
            collect {
                block(
                    it
                )
            }
        }
    }
}
