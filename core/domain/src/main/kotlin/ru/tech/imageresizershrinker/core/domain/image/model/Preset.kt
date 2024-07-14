/*
 * ImageToolbox is an image editor for android
 * Copyright (c) 2024 T8RIN (Malik Mukhametzyanov)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * You should have received a copy of the Apache License
 * along with this program.  If not, see <http://www.apache.org/licenses/LICENSE-2.0>.
 */

package ru.tech.imageresizershrinker.core.domain.image.model

sealed class Preset {

    data object Telegram : Preset()

    data object None : Preset()

    data class Percentage(val value: Int) : Preset()

    data class AspectRatio(val ratio: Float) : Preset()

    fun isTelegram(): Boolean = this is Telegram

    fun value(): Int? = (this as? Percentage)?.value

    fun ratio(): Float? = (this as? AspectRatio)?.ratio

    fun isEmpty(): Boolean = this is None

    fun isAspectRatio(): Boolean = this is AspectRatio

    companion object {
        val Original by lazy {
            Percentage(100)
        }

        fun createListFromInts(presets: String?): List<Preset> {
            return ((presets?.split("*")?.map {
                it.toInt()
            } ?: List(6) { 100 - it * 10 })).toSortedSet().reversed().toList()
                .map { Percentage(it) }
        }
    }
}