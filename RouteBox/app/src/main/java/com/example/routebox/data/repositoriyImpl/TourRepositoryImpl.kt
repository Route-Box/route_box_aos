package com.example.routebox.data.repositoriyImpl

import com.example.routebox.data.datasource.RemoteTourDataSource
import com.example.routebox.domain.model.TourApiResult
import com.example.routebox.domain.repositories.TourRepository
import javax.inject.Inject

class TourRepositoryImpl @Inject constructor(
    private val remoteTourDataSource: RemoteTourDataSource
) : TourRepository {
    override suspend fun getTourList(
        mobileOs: String,
        mobileApp: String,
        serviceKey: String,
        mapX: String,
        mapY: String,
        radius: String,
        contentTypeId: String,
        _type: String
    ): TourApiResult {
        return remoteTourDataSource.getTourList(mobileOs, mobileApp, serviceKey, mapX, mapY, radius, contentTypeId, _type)
    }
}