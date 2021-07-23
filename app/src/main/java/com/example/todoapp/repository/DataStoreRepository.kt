package com.example.todoapp.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.todoapp.enums.SortMode
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton


const val DATA_STORE_NAME = "DATA_STORE"
private val Context.dataStore by preferencesDataStore(name = DATA_STORE_NAME)

@Singleton
class DataStoreRepository @Inject constructor(
    @ApplicationContext val context: Context) { //

    private val localDataSore = context.dataStore

    object PreferenceKey{
        val sortType = stringPreferencesKey("SortType")
        val layoutType = stringPreferencesKey("LayoutType")
    }


    suspend fun saveSortMode(value: String){
        localDataSore.edit { preference ->
            preference[PreferenceKey.sortType] = value

        }
    }

    fun readSortMode(): Flow<String> = localDataSore.data
        .catch { exception ->
            if (exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else{
                throw exception
            }
        }
        .map { preference ->
            val sortType = preference[PreferenceKey.sortType] ?: ""
            sortType
        }


    suspend fun saveLayoutType(value: String){
        context.dataStore.edit { preference ->
            preference[PreferenceKey.layoutType] = value
        }
    }

    fun readLayoutType(): Flow<String> = context.dataStore.data
        .catch { exception ->
            if (exception is IOException){
                Log.d("DataStore", exception.message.toString())
                emit(emptyPreferences())
            } else{
                throw exception
            }
        }
        .map { preference ->
            val layoutType = preference[PreferenceKey.layoutType] ?: ""
            layoutType
        }
}