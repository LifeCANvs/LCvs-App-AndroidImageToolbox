package ru.tech.imageresizershrinker.core.filters.presentation.model

import android.graphics.Bitmap
import ru.tech.imageresizershrinker.core.domain.image.filters.Filter
import ru.tech.imageresizershrinker.core.domain.image.filters.FilterParam
import ru.tech.imageresizershrinker.core.resources.R


class UiStackBlurFilter(
    override val value: Pair<Float, Int> = 0.5f to 25,
) : UiFilter<Pair<Float, Int>>(
    title = R.string.stack_blur,
    value = value,
    paramsInfo = listOf(
        FilterParam(R.string.scale, 0.1f..1f, 2),
        FilterParam(R.string.radius, 0f..100f, 0)
    )
), Filter.StackBlur<Bitmap>