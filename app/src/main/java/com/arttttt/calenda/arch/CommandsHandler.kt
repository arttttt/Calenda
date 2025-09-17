package com.arttttt.calenda.arch

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class CommandsHandler<Command : Any> private constructor(
    private val sharedFlow: MutableSharedFlow<Command> = MutableSharedFlow()
): Flow<Command> by sharedFlow {

    companion object {

        context(_: ViewModel)
        operator fun <Command : Any> invoke(): CommandsHandler<Command> {
            return CommandsHandler()
        }
    }

    context(_: ViewModel)
    suspend fun sendCommand(command: Command) {
        sharedFlow.emit(command)
    }
}