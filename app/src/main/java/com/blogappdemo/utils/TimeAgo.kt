package com.blogappdemo.utils

import java.util.concurrent.TimeUnit

private const val SECOND_MILLIS = 1
private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
private const val DAY_MILLIS = 24 * HOUR_MILLIS


object TimeUtils {

    //obtener el tiempo/hora de creacion del post
    fun getTimeAgo(time: Int): String {

        //obtener el tiempo actual del telefono
        val now = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())
        if (time > now || time <= 0) {
            return "in the future"
        }

        //diferencia del tiempo actual y el que le enviamos
        val diff = now - time
        return when {
            diff < MINUTE_MILLIS ->  "en este momento"
            diff < 2 * MINUTE_MILLIS -> "hace 1 minuto"
            diff < 60 * MINUTE_MILLIS -> "hace ${diff / MINUTE_MILLIS} minutos"
            diff < 2 * HOUR_MILLIS -> "hace 1 hora"
            diff < 24 * HOUR_MILLIS -> "hace ${diff / HOUR_MILLIS} horas"
            diff < 48 * HOUR_MILLIS -> "ayer"
            else -> "hace ${diff / DAY_MILLIS} días"
        }
    }

}

