package com.d4rk.android.apps.apptoolkit.core.data.favorites

import android.content.Context
import android.content.Intent
import android.util.Log
import com.d4rk.android.apps.apptoolkit.app.apps.favorites.domain.repository.FavoritesRepository
import com.d4rk.android.apps.apptoolkit.core.broadcast.FavoritesChangedReceiver
import com.d4rk.android.apps.apptoolkit.core.data.datastore.DataStore
import kotlinx.coroutines.flow.Flow

class FavoritesRepositoryImpl(
    private val context: Context,
    private val dataStore: DataStore
) : FavoritesRepository {
    override fun observeFavorites(): Flow<Set<String>> = dataStore.favoriteApps

    override suspend fun toggleFavorite(packageName: String) {
        dataStore.toggleFavoriteApp(packageName)
        val intent = Intent(FavoritesChangedReceiver.ACTION_FAVORITES_CHANGED).apply {
            putExtra(FavoritesChangedReceiver.EXTRA_PACKAGE_NAME, packageName)
        }
        try {
            context.sendBroadcast(intent)
        } catch (e: Exception) {
            if (e.javaClass.name == "android.app.RemoteServiceException") {
                Log.w("FavoritesRepositoryImpl", "Failed to send favorites broadcast", e)
            } else {
                throw e
            }
        }
    }
}

