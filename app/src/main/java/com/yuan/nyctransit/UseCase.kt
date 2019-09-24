package com.yuan.nyctransit

import com.yuan.nyctransit.core.exception.Failure
import com.yuan.nyctransit.core.functional.Either
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

abstract class UseCase<out Type, in Params> where Type: Any {

    abstract suspend fun run(params: Params): Either<Failure, Type>

    /** todo
     * origin code is below, my code doesn't have CommonPool and UI, is this ok
     * val job = async(CommonPool) { run(params) }
     * launch(UI) { onResult(job.await()) }
     */
    operator fun invoke(params: Params, onResult: (Either<Failure, Type>) -> Unit = {}) {
        val job = CoroutineScope(Dispatchers.Default).async { run(params) }
        CoroutineScope(Dispatchers.Main).launch { onResult(job.await()) }
    }


    class None
}