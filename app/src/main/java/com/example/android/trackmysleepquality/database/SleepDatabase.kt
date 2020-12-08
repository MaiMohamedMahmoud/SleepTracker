/*
 * Copyright 2018, The Android Open Source Project
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

package com.example.android.trackmysleepquality.database

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper

@Database(entities = [SleepNight::class], version = 1, exportSchema = false)
abstract class SleepDatabase : RoomDatabase() {

    abstract val sleepDatabaseDao: SleepDatabaseDao

    companion object CreateDataBase {

        /**
         *   Volatile,Value are always up to date, never be cashed and all
         *   reads and writes will be done from and to Main Memory
         *   Changed that made by a one thread to VARBIABLE (INSTANCE)  are immediately visible to all other threads
         *
         */

        @Volatile
        var INSTANCE: SleepDatabase? = null

        fun getInstance(context: Context): SleepDatabase {
            /**
             * here in this app is not complex ...for complex apps you can have multiple thread and two ore more thread can have more instance so
             * we use synchronized
             * Wrapping the code in synchronized(){}
             * Means, Only one thread can access the code at a time
             * So we make sure we have only one instace of DB
             */
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            SleepDatabase::class.java,
                            "sleep_database"
                    )
                            /**
                             * if we change the db scheme (Number or type of coulum)
                             * we need a way to convert all existing data and table to the new scheme
                             * ONLY SPECIFIC TO THIS APP BECAUSE WE DOESN't HAVE ANY Users
                             * WE will wipe and rebuild the scheme
                             */
                            .fallbackToDestructiveMigration()
                            .build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }
}
