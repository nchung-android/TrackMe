package com.nchungdev.domain.provider

import java.util.concurrent.TimeUnit

interface TimerProvider {

    fun getFormattedTime(): CharSequence

    fun getTimeIn(timeUnit: TimeUnit): Long

    fun start()

    fun stop()

    fun reset()
}