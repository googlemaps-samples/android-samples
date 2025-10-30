package com.example.firemarkers.data

import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * A singleton provider for the [FirebaseDatabase] instance.
 *
 * This class is responsible for creating and providing a single instance of the [FirebaseDatabase]
 * throughout the application. It is injected into the [com.example.firemarkers.viewmodel.MarkersViewModel] to provide access to the
 * database.
 */
@Singleton
class FirebaseConnection @Inject constructor() {
    /**
     * The singleton instance of the [FirebaseDatabase].
     */
    val database: FirebaseDatabase = FirebaseDatabase.getInstance()
}
