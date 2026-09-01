/*
 * Copyright 2026 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.common_ui.catalog.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database instance storing all local sample evaluations and reviewer notes.
 */
@Database(
    entities = [SampleEvaluationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SampleCatalogDatabase : RoomDatabase() {

    abstract fun sampleEvaluationDao(): SampleEvaluationDao

    companion object {
        @Volatile
        private var INSTANCE: SampleCatalogDatabase? = null

        fun getInstance(context: Context): SampleCatalogDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    SampleCatalogDatabase::class.java,
                    "gmp_samples_catalog.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
