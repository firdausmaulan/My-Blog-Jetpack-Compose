package com.fd.myblog.ui.location.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.fd.myblog.R
import com.fd.myblog.data.model.LocationData
import com.fd.myblog.helper.UiHelper
import com.fd.myblog.ui.common.DebounceTextField
import com.fd.myblog.ui.common.EditableMapScreen
import com.fd.myblog.ui.common.ErrorBottomSheetDialog


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchLocationScreen(
    viewModel: SearchLocationViewModel,
    onClose: () -> Unit,
    onConfirm: (locationData: LocationData) -> Unit
) {

    Scaffold(topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black,
                actionIconContentColor = Color.Black,
            ),
            title = {
                Text(
                    "Edit Location",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.SemiBold
                )
            },
            actions = {
                IconButton(onClick = { onClose() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        tint = Color.Black,
                        contentDescription = "Close"
                    )
                }
            }
        )
    }) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            EditableMapScreen(
                viewModel.latitude,
                viewModel.longitude,
                onCenterChanged = { lat, lon ->
                    viewModel.onCenterChanged(lat, lon)
                }
            )

            Column(modifier = Modifier.fillMaxSize()) {
                Box(Modifier.padding(8.dp)) {
                    DebounceTextField(placeholderText = "Search location") {
                        viewModel.searchLocation(it)
                    }
                }

                when (val state = viewModel.state) {
                    is SearchLocationState.Idle -> {}

                    is SearchLocationState.Success -> {
                        LocationList(
                            state.results,
                            onSelect = {
                                viewModel.setLocation(it)
                                viewModel.state = SearchLocationState.Idle
                            }
                        )
                    }

                    is SearchLocationState.Error -> {
                        ErrorBottomSheetDialog(
                            subMessage = state.message,
                            onDismissRequest = { viewModel.state = SearchLocationState.Idle },
                        )
                    }
                }
            }

            if (viewModel.address.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 150.dp)
                        .align(Alignment.BottomCenter)
                        .background(color = Color.White),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text(
                        text = viewModel.address,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.selectedLocation?.let { onConfirm(it) } },
                        elevation = UiHelper.buttonElevation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(text = "CONFIRM")
                    }
                }
            }
        }
    }
}

@Composable
fun LocationList(locations: List<LocationData>, onSelect: (locationData: LocationData) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 8.dp)
    ) {
        locations.forEach { location ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White)
                    .clickable {
                        onSelect(location)
                    }) {
                Text(
                    text = location.displayName,
                    color = Color.Black,
                    minLines = 2,
                    modifier = Modifier.padding(8.dp),
                )
                HorizontalDivider(color = Color.LightGray, thickness = 1.dp)
            }
        }
    }
}