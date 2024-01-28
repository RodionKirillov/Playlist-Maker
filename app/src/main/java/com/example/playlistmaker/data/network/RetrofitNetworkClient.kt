package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackSearchRequest
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitNetworkClient : NetworkClient {

    private val baseURL = "https://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseURL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesSearchAPI = retrofit.create(ITunesSearchAPI::class.java)
    override fun doRequest(dto: Any): Response {
        return try {
            if (dto is TrackSearchRequest) {

                val resp = iTunesSearchAPI.search(dto.expression).execute()  // тут проверка

                val body = resp.body() ?: Response()

                body.apply { resultCode = resp.code() }

            } else {
                Response().apply { resultCode = 400 }
            }
        } catch (e: Exception) {
            Response().apply { resultCode == 0 }
        }
    }
}