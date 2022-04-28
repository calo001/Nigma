package com.github.calo001.nigma.interactor

import android.graphics.Bitmap
import com.github.calo001.nigma.repository.RemoteUserRepository
import com.github.calo001.nigma.repository.model.UserInfo
import javax.inject.Inject

class UpdateUserProfileInteractor @Inject constructor(private val repository: RemoteUserRepository) {
    suspend fun updateProfileInfo(userInfo: UserInfo) = repository.updateProfileInfo(userInfo)
    suspend fun uploadImageProfile(bitmap: Bitmap, userInfo: UserInfo) = repository.uploadImageProfile(bitmap, userInfo)
}