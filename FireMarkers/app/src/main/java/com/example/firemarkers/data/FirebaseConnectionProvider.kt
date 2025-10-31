// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
