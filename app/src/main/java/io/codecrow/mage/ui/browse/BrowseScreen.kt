/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.codecrow.mage.ui.browse

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.snapping.SnapLayoutInfoProvider
import androidx.compose.foundation.gestures.snapping.SnapPositionInLayout
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import io.codecrow.mage.model.Channel
import io.codecrow.mage.model.UserDetails
import io.codecrow.mage.ui.components.TitleTextStyle
import io.codecrow.mage.ui.theme.*


@Composable
fun BrowseScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: BrowseViewModel = hiltViewModel()
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val context = LocalContext.current
    val items by produceState<BrowseUiState>(
        initialValue = BrowseUiState.Loading, key1 = lifecycle, key2 = viewModel
    ) {
        lifecycle.repeatOnLifecycle(state = STARTED) {
            viewModel.uiState.collect {
                value = it
            }
        }
        lifecycle.repeatOnLifecycle(state = STARTED) {
            viewModel.subscribeResponse.collect {
                when(it){
                    is BrowseUiState.SuccessSubscribe -> {
                        Toast.makeText(context,"Subscribed To Channel",Toast.LENGTH_SHORT).show()
                    }
                    is BrowseUiState.Error-> {
                        Toast.makeText(context,"Failed to subscribe",Toast.LENGTH_SHORT).show()
                    }
                    is BrowseUiState.Loading-> {

                    }
                    else -> {

                    }
                }
            }
        }
    }
    if (items is BrowseUiState.Success) {
        BrowseScreen(items = (items as BrowseUiState.Success).data, modifier = modifier, onClick = {
            navController.navigate("channel/$it")
        },{ channelId ->
            Log.d("BrowseScreen", "BrowseScreen: ChannelId=$channelId")
            //TODO: subscribe from here
            viewModel.subscribeToChannel(channelId)
        })
    } else if (items is BrowseUiState.Loading) {
        LoadingView()
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
internal fun BrowseScreen(
    items: List<Channel>, modifier: Modifier = Modifier, onClick: (String) -> Unit, onSubscribeClick : (channelId: String) -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val state = rememberLazyListState()
    val snappingLayout =
        remember(state) { SnapLayoutInfoProvider(state) { _: Int, _: Int, _: Int -> 0 } }
    val flingBehavior = rememberSnapFlingBehavior(snappingLayout)

    Scaffold(

        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection), topBar = {
            CenterAlignedTopAppBar(
                title = {
                    TitleTextStyle()
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                scrollBehavior = scrollBehavior
            )
        }, content = {
//            var nameBrowse by remember { mutableStateOf("Compose") }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(it),
                state = state,
                flingBehavior = flingBehavior,

                ) {
                items(items) { it: Channel ->
                    ChannelItem(it,{ channelId -> onClick(channelId) },{ channelId -> onSubscribeClick(channelId)})
                }
            }
        })
}

// Previews

@Preview(showBackground = true)
@Composable
private fun PortraitPreview() {
    val channels = listOf(
        Channel(
            "",
            "VideoTitle",
            "des",
            "",
            listOf(""),
            listOf(""),
            "",
            0,
            "channel",
            UserDetails("", "", "")
        )
    )
    MyApplicationTheme {
        BrowseScreen(channels, onClick = {}, onSubscribeClick = {})
    }
}

@Preview(showBackground = true, widthDp = 480)
@Composable
private fun LandscapePreview() {
    val channels = listOf(
        Channel(
            "",
            "VideoTitle",
            "des",
            "",
            listOf(""),
            listOf(""),
            "",
            0,
            "channel",
            UserDetails("", "", "")
        )
    )
    MyApplicationTheme {
        BrowseScreen(channels, onClick = {}, onSubscribeClick = {})
    }
}

@Composable
fun dpToSp(dp: Dp) = with(LocalDensity.current) { dp.toSp() }

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        repeat(5) {
            LoadingChannelItem()
        }
    }
}
