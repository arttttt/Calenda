package com.arttttt.calenda.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlin.coroutines.cancellation.CancellationException

suspend fun <A, B> CoroutineScope.zip(
    value1: suspend () -> Result<A>,
    value2: suspend () -> Result<B>,
): Result<Pair<A, B>> {
    val d1 = async { value1.safeCall() }
    val d2 = async { value2.safeCall() }

    val r1 = d1.await()
    val r2 = d2.await()

    return when {
        r1.isSuccess && r2.isSuccess -> Result.success(r1.getOrThrow() to r2.getOrThrow())
        r1.isFailure && r2.isFailure -> {
            val e1 = r1.exceptionOrNull()!!
            val e2 = r2.exceptionOrNull()!!
            if (e1 !== e2) e1.addSuppressed(e2)
            Result.failure(e1)
        }

        r1.isFailure -> Result.failure(r1.exceptionOrNull()!!)
        else -> Result.failure(r2.exceptionOrNull()!!)
    }
}

private suspend fun <T> (suspend () -> Result<T>).safeCall(): Result<T> {
    return try {
        invoke()
    } catch (e: CancellationException) {
        throw e
    } catch (e: Throwable) {
        Result.failure(e)
    }
}
