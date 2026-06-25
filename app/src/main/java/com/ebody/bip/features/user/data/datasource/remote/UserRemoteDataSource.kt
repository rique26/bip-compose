package com.ebody.bip.features.user.data.datasource.remote

import com.ebody.bip.core.domain.util.Result
import com.ebody.bip.features.auth.domain.model.BipAuthException
import com.ebody.bip.features.user.data.entity.UserProfileEntity

interface UserRemoteDataSource {
    suspend fun saveProfile(userProfile: UserProfileEntity): Result<Unit, BipAuthException>
}