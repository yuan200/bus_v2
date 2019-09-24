package com.yuan.nyctransit.features.lirr

import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LirrService
@Inject constructor(retrofit: Retrofit) : LirrFeedApi {
    private val lirrApi by lazy {retrofit.create(LirrFeedApi::class.java)}

    override fun lirrFeed()= lirrApi.lirrFeed()
}