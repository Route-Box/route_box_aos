package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteHomeDataSource
import com.daval.routebox.domain.repositories.HomeRepository
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val remoteHomeDataSource: RemoteHomeDataSource
) : HomeRepository {

}