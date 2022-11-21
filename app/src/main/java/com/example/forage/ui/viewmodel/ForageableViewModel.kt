/*
 * Copyright (C) 2021 The Android Open Source Project.
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
package com.example.forage.ui.viewmodel

import android.content.ClipData
import android.provider.SyncStateContract.Helpers.insert
import androidx.lifecycle.*
import androidx.room.Dao
import com.example.forage.data.ForageDatabase
import com.example.forage.data.ForageableDao
import com.example.forage.model.Forageable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Shared [ViewModel] to provide data to the [ForageableListFragment], [ForageableDetailFragment],
 * and [AddForageableFragment] and allow for interaction the the [ForageableDao]
 *
 * Author: Kemmy MO Jones.
 * Date: Nov 21th, 2022.
 * Topic: Data Persistence (PathWay 2 - Use Room For Data Persistence,Ex 6 => Forage App).
 */

// TODO: pass a ForageableDao value as a parameter to the view model constructor
class ForageableViewModel (
    // Pass dao here
    private val forageableDao: ForageableDao

): ViewModel() {

    // TODO: create a property to set to a list of all forageables from the DAO
    val allForageable: LiveData<List<Forageable>> = forageableDao.getForageables().asLiveData()

    fun isSeasonAvailable(forageable: Forageable): Boolean {
        return (forageable.inSeason)
    } //end: isSeasonAvailable

    // TODO : create method that takes id: Long as a parameter and retrieve a Forageable from the
    //  database by id via the DAO.
    fun retrieveForage(id: Long): LiveData<Forageable> {
        return forageableDao.getForageable(id).asLiveData()
    }//end: retrieveItem

    fun addForageable(
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String
    ) {
        val forageable = Forageable(
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )

    // TODO: launch a coroutine and call the DAO method to add a Forageable to the database within it

        insertForageable(forageable)

    } //end: addForageable

    private fun insertForageable(forageable: Forageable) {
        viewModelScope.launch {
            forageableDao.insert(forageable)
        }
    }//end: insertForageable

    fun updateForageable(
        id: Long,
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String
    ) {
        val forageable = Forageable(
            id = id,
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: call the DAO method to update a forageable to the database here
            updateForageable(forageable)
        }
    }//end: updateForageable

    private fun updateForageable(forageable: Forageable) {
        viewModelScope.launch {
            forageableDao.update(forageable)
        }
    }//end: updateForageable


    fun deleteForageable(forageable: Forageable) {
        viewModelScope.launch(Dispatchers.IO) {
            // TODO: call the DAO method to delete a forageable to the database here
            forageableDao.delete(forageable)
        }
    } //end: deleteForageable

    fun isValidEntry(name: String, address: String): Boolean {
        return name.isNotBlank() && address.isNotBlank()
    }//end:isValidEntry

    private fun getNewForageableEntry(name: String, address: String, inSeason: Boolean, notes: String?): Forageable {
        return Forageable(
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )
    }//end: getNewForageableEntry

    private fun getUpdatedForageableEntry(
        id: Long,
        name: String,
        address: String,
        inSeason: Boolean,
        notes: String
    ): Forageable {
        return Forageable(
            id = id,
            name = name,
            address = address,
            inSeason = inSeason,
            notes = notes
        )
    } //end: getUpdatedForageableEntry


}//end: ForageViewModel

// TODO: create a view model factory that takes a ForageableDao as a property and
//  creates a ForageableViewModel

class ForageableViewModelFactory(private val forageableDao: ForageableDao) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForageableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ForageableViewModel(forageableDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")

    } //end: override
}//end: viewModelFactor