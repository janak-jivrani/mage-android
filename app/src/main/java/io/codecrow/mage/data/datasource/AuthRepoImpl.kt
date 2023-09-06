package io.codecrow.mage.data.datasource

import io.codecrow.mage.data.DataException
import io.codecrow.mage.data.Either
import io.codecrow.mage.data.service.ChannelApi
import io.codecrow.mage.data.handle
import io.codecrow.mage.data.service.AuthApi
import io.codecrow.mage.model.AuthUser
import io.codecrow.mage.model.Channel
import javax.inject.Inject

class AuthRepoImpl @Inject constructor(private val api: AuthApi): AuthRepo {
    override suspend fun getCurrentUser(headerMap : HashMap<String,String>): Either<DataException, AuthUser?> {
        return handle({
            api.getCurrentUser(headerMap)
        }) {
            Either.Right(it?:null)
        }
    }
}