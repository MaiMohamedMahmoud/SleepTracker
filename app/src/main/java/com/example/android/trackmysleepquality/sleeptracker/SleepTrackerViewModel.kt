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

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import androidx.lifecycle.*
import androidx.navigation.NavOptions
import com.example.android.trackmysleepquality.R
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.launch

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
        val database: SleepDatabaseDao,
        application: Application) : AndroidViewModel(application) {

    val tonight = MutableLiveData<SleepNight>()

    val nights = database.getAllNights()

    /**
     * create _navigateToSleepQuality to make it listen to if you want now to navigate or not
     */
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()

    val navigateToSleepQuality: LiveData<SleepNight>
        get() = _navigateToSleepQuality

    init {
        initializeTonight()
    }

    private fun initializeTonight() {
        viewModelScope.launch {
            tonight.value = getTonightFromDatabase()
        }
    }

    private suspend fun getTonightFromDatabase(): SleepNight? {
        var night = database.getTonight()
        if (night?.endTime != night?.startTime) {
            night = null
        }
        return night
    }


    //handle start button
    fun onStartBtnTracking() {
        viewModelScope.launch {
            insertNight(SleepNight())
        }
    }

    //handle stop button
    fun onStopTracking() {
        viewModelScope.launch {
            val oldNight = tonight.value
            oldNight?.endTime = System.currentTimeMillis()
            if (oldNight != null) {
                _navigateToSleepQuality.value = oldNight
                updateNight(oldNight)
            }
        }
    }

    private fun doneNavigate() {
        _navigateToSleepQuality.value = null
    }

    //handle clear button
    fun onClearTracking() {
        viewModelScope.launch {
            clear()
            tonight.value = null
        }
    }

    val nightsString = Transformations.map(nights) { nights ->
        formatNights(nights, application.resources)
    }

    private suspend fun insertNight(night: SleepNight) {
        database.insertSleep(night)
    }

    private suspend fun updateNight(night: SleepNight) {
        database.updateSleep(night)
    }

    private suspend fun clear() {
        database.clear()
    }

}

