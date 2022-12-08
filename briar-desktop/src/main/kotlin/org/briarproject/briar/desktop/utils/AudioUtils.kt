/*
 * Briar Desktop
 * Copyright (C) 2021-2022 The Briar Project
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package org.briarproject.briar.desktop.utils

import org.briarproject.briar.desktop.utils.ResourceUtils.getResourceAsStream
import java.io.BufferedInputStream
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip

object AudioUtils {

    fun loadAudioFromResource(name: String): Clip? {
        val resourceStream = getResourceAsStream(name) ?: return null
        val bufferedStream = BufferedInputStream(resourceStream) // add buffer for mark/reset support
        val audioInputStream = AudioSystem.getAudioInputStream(bufferedStream)
        val f = audioInputStream.format
        val audioInputStream2 = AudioSystem.getAudioInputStream(
            AudioFormat(f.encoding, f.sampleRate, f.sampleSizeInBits, f.channels, f.frameSize, f.frameRate, true),
            audioInputStream
        )
        val sound = AudioSystem.getClip()
        sound.open(audioInputStream2)
        return sound
    }

    /**
     * Play audio from the beginning.
     * If it is currently played, it is stopped and restarted.
     */
    fun Clip.play() = this.apply {
        stop()
        flush()
        framePosition = 0
        start()
    }
}
