package com.example.playlistmaker.player.ui.model

sealed class PlayerState {

    object Prepared : PlayerState()

    object Paused : PlayerState()

    object Completion : PlayerState()

    object Default : PlayerState()

    data class Playing(
        val currentPosition: String
    ) : PlayerState()
}