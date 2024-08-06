package com.example.playlistmaker.search.data.source

import com.example.playlistmaker.search.data.dto.Response

interface NetworkClient {
    suspend fun doRequest(dto: Any): Response
}