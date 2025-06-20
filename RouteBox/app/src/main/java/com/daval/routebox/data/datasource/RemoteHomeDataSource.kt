package com.daval.routebox.data.datasource

import com.daval.routebox.data.remote.HomeApiService
import javax.inject.Inject

class RemoteHomeDataSource @Inject constructor(
    private val homeApiService: HomeApiService
) {

}