package club.crestmc.neptunecarbonbukkit.utils

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class Util {
    fun usernameExists(username: String): Boolean {
        return try {
            val url = URL("https://api.mojang.com/users/profiles/minecraft/$username")
            val reader = BufferedReader(InputStreamReader(url.openStream()))
            val response = reader.readLine()
            reader.close()
            response != null
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    val pID: Int
        get() {
            val random = Random()
            return random.nextInt(999999999 - 100000000 + 1) + 100000000
        }

    companion object {
        fun getHistorySlotData(id: Int): IntArray? {
            var data = intArrayOf(1, 0)
            if (id >= 0 && id < 9) data = intArrayOf(1, id)
            if (id >= 9 && id < 18) data = intArrayOf(2, id - 9)
            if (id >= 18 && id < 27) data = intArrayOf(3, id - 18)
            if (id >= 27 && id < 36) data = intArrayOf(4, id - 27)
            if (id >= 36 && id < 45) data = intArrayOf(5, id - 36)
            if (id >= 45 && id < 54) data = intArrayOf(6, id - 45)
            if (id >= 54 && id < 63) data = intArrayOf(7, id - 54)
            if (id >= 63 && id < 72) data = intArrayOf(8, id - 63)
            if (id >= 72 && id < 81) data = intArrayOf(9, id - 72)
            if (id >= 81) {
                println("Too many pages!")
                return null
            }
            return data
        }

        fun getExpirationDate(ms: Long): String {
            val SDF = SimpleDateFormat("MM/dd/yyyy, HH:mm:ss, z")
            return SDF.format(ms)
        }
    }
}