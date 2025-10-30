package com.example.firemarkers.viewmodel

import com.example.firemarkers.CustomTestRunner
import com.example.firemarkers.data.FirebaseConnection
import com.google.common.truth.Truth.assertThat
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.robolectric.annotation.Config
import app.cash.turbine.test

@RunWith(CustomTestRunner::class)
@Config(manifest=Config.NONE)
@ExperimentalCoroutinesApi
class MarkersViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var viewModel: MarkersViewModel
    private val mockFirebaseConnection: FirebaseConnection = mock()
    private val mockFirebaseDatabase: FirebaseDatabase = mock()
    private val mockMarkersRef: DatabaseReference = mock()
    private val mockAnimationRef: DatabaseReference = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        whenever(mockFirebaseConnection.database).thenReturn(mockFirebaseDatabase)
        whenever(mockFirebaseDatabase.getReference("markers")).thenReturn(mockMarkersRef)
        whenever(mockFirebaseDatabase.getReference("animation")).thenReturn(mockAnimationRef)
        whenever(mockMarkersRef.addValueEventListener(any())).thenReturn(mock())
        whenever(mockAnimationRef.addValueEventListener(any())).thenReturn(mock())

        viewModel = MarkersViewModel(mockFirebaseConnection)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `isController is true when controllerId matches viewModelId`() = runTest {
        val listenerCaptor = argumentCaptor<ValueEventListener>()
        verify(mockAnimationRef).addValueEventListener(listenerCaptor.capture())

        // Use Turbine to test the flow
        viewModel.isController.test {
            // The initial value is always false
            assertThat(awaitItem()).isFalse()

            // Simulate the Firebase update making this VM the controller
            val mockSnapshot = mockAnimationStateSnapshot(
                MarkersViewModel.AnimationState(controllerId = viewModel.viewModelId)
            )
            listenerCaptor.firstValue.onDataChange(mockSnapshot)

            // Assert that the flow now emits true
            assertThat(awaitItem()).isTrue()
        }
    }

    @Test
    fun `isController is false when controllerId does not match`() = runTest {
        val listenerCaptor = argumentCaptor<ValueEventListener>()
        verify(mockAnimationRef).addValueEventListener(listenerCaptor.capture())

        val mockSnapshot = mockAnimationStateSnapshot(
            MarkersViewModel.AnimationState(controllerId = "some-other-id")
        )
        listenerCaptor.firstValue.onDataChange(mockSnapshot)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(viewModel.isController.value).isFalse()
    }

    @Test
    fun `toggleAnimation does nothing if not controller`() = runTest {
        val listenerCaptor = argumentCaptor<ValueEventListener>()
        verify(mockAnimationRef).addValueEventListener(listenerCaptor.capture())
        val mockSnapshot = mockAnimationStateSnapshot(
            MarkersViewModel.AnimationState(controllerId = "not-this-vm")
        )
        listenerCaptor.firstValue.onDataChange(mockSnapshot)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.toggleAnimation()
        testDispatcher.scheduler.advanceUntilIdle()

        verify(mockAnimationRef, never()).setValue(any())
    }

    @Test
    fun `takeControl updates controllerId in Firebase`() = runTest {
        viewModel.takeControl()
        testDispatcher.scheduler.advanceUntilIdle()

        val stateMapCaptor = argumentCaptor<Map<String, Any>>()
        verify(mockAnimationRef).setValue(stateMapCaptor.capture())

        assertThat(stateMapCaptor.firstValue["controllerId"]).isEqualTo(viewModel.viewModelId)
        assertThat(stateMapCaptor.firstValue["running"]).isEqualTo(false)
    }

    @Test
    fun `errorEvents emits message on database error`() = runTest {
        val listenerCaptor = argumentCaptor<ValueEventListener>()
        verify(mockAnimationRef).addValueEventListener(listenerCaptor.capture())

        val error = mock<DatabaseError> {
            on { message }.thenReturn("Test Error")
        }

        var errorMessage: String? = null
        val job = launch(testDispatcher) {
            viewModel.errorEvents.first {
                errorMessage = it
                true
            }
        }

        listenerCaptor.firstValue.onCancelled(error)
        testDispatcher.scheduler.advanceUntilIdle()

        assertThat(errorMessage).isEqualTo("DB error on animation: Test Error")
        job.cancel()
    }
}

private fun mockAnimationStateSnapshot(state: MarkersViewModel.AnimationState): DataSnapshot {
    val mockSnapshot = mock<DataSnapshot>()
    whenever(mockSnapshot.getValue(eq(MarkersViewModel.AnimationState::class.java))).thenReturn(state)
    return mockSnapshot
}
