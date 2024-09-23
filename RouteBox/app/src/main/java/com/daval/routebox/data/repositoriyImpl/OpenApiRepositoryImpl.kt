package com.daval.routebox.data.repositoriyImpl

import com.daval.routebox.data.datasource.RemoteOpenApiDataSource
import com.daval.routebox.domain.model.TourApiResult
import com.daval.routebox.domain.model.WeatherApiResult
import com.daval.routebox.domain.repositories.OpenApiRepository
import javax.inject.Inject

class OpenApiRepositoryImpl @Inject constructor(
    private val remoteOpenApiDataSource: RemoteOpenApiDataSource
) : OpenApiRepository {
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
        return remoteOpenApiDataSource.getTourList(mobileOs, mobileApp, serviceKey, mapX, mapY, radius, contentTypeId, _type)
    }

    override suspend fun getWeatherList(
        ServiceKey: String,
        pageNo: Int,
        numOfRows: Int,
        dataType: String,
        base_date: String,
        base_time: String,
        nx: Int,
        ny: Int
    ): Result<WeatherApiResult> {
        return remoteOpenApiDataSource.getWeatherList(ServiceKey, pageNo, numOfRows, dataType, base_date, base_time, nx, ny)
    }
}