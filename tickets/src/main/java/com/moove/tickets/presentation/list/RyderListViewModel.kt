package com.moove.tickets.presentation.list

import androidx.lifecycle.ViewModel
import com.moove.core.exception.ExceptionHandler
import com.moove.core.exception.asCoroutineExceptionHandler
import com.moove.shared.presentation.viewmodel.executeUseCase
import com.moove.tickets.domain.use_cases.GetRydersUseCase
import com.moove.tickets.presentation.list.model.RyderModel
import com.moove.tickets.presentation.list.model.asPresentation
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

class RyderListViewModel(
    private val exceptionHandler: ExceptionHandler,
    private val getRydersUseCase: GetRydersUseCase,
) : ViewModel(), ContainerHost<RyderListState, RyderListEffect> {

    override val container: Container<RyderListState, RyderListEffect> = container(
        initialState = RyderListState(),
        buildSettings = {
            this.exceptionHandler =
                this@RyderListViewModel.exceptionHandler.asCoroutineExceptionHandler()
        },
    ) {
        fetchRyders()
    }

    fun onRyderClick(ruder: RyderModel) = intent {
        postSideEffect(RyderListEffect.GoToFares(ruder.id))
    }

    private fun fetchRyders() = intent {
        executeUseCase { getRydersUseCase() }
            .onSuccess { reduce { state.copy(ryders = it.asPresentation()) } }
            .onFailure { postSideEffect(RyderListEffect.ShowGenericError) }
    }
}
