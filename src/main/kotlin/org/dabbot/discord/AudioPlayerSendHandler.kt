package org.dabbot.discord

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame
import net.dv8tion.jda.core.audio.AudioSendHandler

class AudioPlayerSendHandler(val audioPlayer: com.sedmelluq.discord.lavaplayer.player.AudioPlayer): net.dv8tion.jda.core.audio.AudioSendHandler {
    private var lastFrame: com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame? = null

    override fun provide20MsAudio(): ByteArray {
        return lastFrame!!.data
    }

    override fun canProvide(): Boolean {
        lastFrame = audioPlayer.provide()
        return lastFrame != null
    }

    override fun isOpus(): Boolean {
        return true
    }
}