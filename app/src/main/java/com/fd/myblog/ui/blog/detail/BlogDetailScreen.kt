package com.fd.myblog.ui.blog.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fd.myblog.R
import com.fd.myblog.data.model.BlogPost
import com.fd.myblog.helper.UiHelper
import com.fd.myblog.ui.common.ErrorScreen
import com.fd.myblog.ui.common.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogDetailScreen(
    viewModel: BlogDetailViewModel,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                colors = UiHelper.topAppBarColors(),
                title = { Text(viewModel.blogTitle, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = { onBackClick() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    if (viewModel.canEdit) {
                        IconButton(onClick = { onEditClick()}) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_edit),
                                contentDescription = "Edit"
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(color = MaterialTheme.colorScheme.background),
        ) {
            when (val state = viewModel.state) {
                is BlogDetailState.Idle -> Unit
                is BlogDetailState.Loading -> LoadingScreen()
                is BlogDetailState.Error -> ErrorScreen(subMessage = state.message)
                is BlogDetailState.Success -> BlogDetailView(state.blogPost)
            }
        }
    }
}

@Composable
fun BlogDetailView(blogPost: BlogPost) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState()),
    ) {
        AsyncImage(
            model = blogPost.imageUrl,
            contentDescription = "Content Picture",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Text(
            text = blogPost.formattedUpdatedAt,
            color = Color.Gray,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(8.dp)
        )

        Text(
            text = blogPost.content,
            color = Color.Black,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp, end = 8.dp)
        )
    }
}