package ru.tech.imageresizershrinker.media_picker.presentation.components

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.TaskAlt
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.widget.other.Loading
import ru.tech.imageresizershrinker.media_picker.domain.AllowedMedia
import ru.tech.imageresizershrinker.media_picker.presentation.viewModel.PickerViewModel

@Composable
fun PickerScreen(
    allowedMedia: AllowedMedia,
    allowSelection: Boolean,
    viewModel: PickerViewModel,
    sendMediaAsResult: (List<Uri>) -> Unit
) {
    val scope = rememberCoroutineScope()
    var selectedAlbumIndex by rememberSaveable { mutableLongStateOf(-1) }
    val selectedMedia = viewModel.selectedMedia

    val albumsState by viewModel.albumsState.collectAsState()
    val mediaState by viewModel.mediaState.collectAsState()
    val chipColors = InputChipDefaults.inputChipColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column {
        AnimatedVisibility(visible = albumsState.albums.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(
                    8.dp,
                    Alignment.CenterHorizontally
                ),
                contentPadding = PaddingValues(start = 32.dp)
            ) {
                items(
                    items = albumsState.albums,
                    key = { it.toString() }
                ) {
                    val selected = selectedAlbumIndex == it.id
                    InputChip(
                        onClick = {
                            selectedAlbumIndex = it.id
                            viewModel.getAlbum(selectedAlbumIndex)
                        },
                        colors = chipColors,
                        shape = RoundedCornerShape(16.dp),
                        label = {
                            val title =
                                if (it.id == -1L) stringResource(R.string.all) else it.label
                            Text(text = title)
                        },
                        selected = selected,
                        border = null
                    )
                }
            }
        }
        AnimatedContent(
            targetState = mediaState.media.isNotEmpty(),
            modifier = Modifier.weight(1f)
        ) { haveData ->
            if (haveData) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PickerMediaScreen(
                        state = mediaState,
                        selectedMedia = selectedMedia,
                        allowSelection = allowSelection
                    )
                    androidx.compose.animation.AnimatedVisibility(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(32.dp),
                        visible = !allowSelection || selectedMedia.isNotEmpty(),
                        enter = slideInVertically { it * 2 },
                        exit = slideOutVertically { it * 2 }
                    ) {
                        val enabled = selectedMedia.isNotEmpty()
                        val containerColor by animateColorAsState(
                            targetValue = if (enabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                            label = "containerColor"
                        )
                        val contentColor by animateColorAsState(
                            targetValue = if (enabled) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            label = "contentColor"
                        )
                        ExtendedFloatingActionButton(
                            text = {
                                if (allowSelection)
                                    Text(text = "Add (${selectedMedia.size})")
                                else
                                    Text(text = "Add")
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.TaskAlt,
                                    contentDescription = null
                                )
                            },
                            containerColor = containerColor,
                            contentColor = contentColor,
                            expanded = allowSelection,
                            onClick = {
                                if (enabled) {
                                    scope.launch {
                                        sendMediaAsResult(selectedMedia.map { it.uri })
                                    }
                                }
                            },
                            modifier = Modifier
                                .semantics {
                                    contentDescription = "Add media"
                                }
                        )
                        BackHandler(selectedMedia.isNotEmpty()) {
                            selectedMedia.clear()
                        }
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Loading()
                }
            }
        }
    }
    BackHandler(selectedAlbumIndex != -1L) {
        selectedAlbumIndex = -1L
        viewModel.getAlbum(selectedAlbumIndex)
    }
}