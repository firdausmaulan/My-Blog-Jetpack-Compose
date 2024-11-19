package com.fd.myblog.ui.blog.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.fd.myblog.R
import com.fd.myblog.data.model.BlogPost
import com.fd.myblog.helper.UiHelper
import com.fd.myblog.ui.common.DebounceTextField
import com.fd.myblog.ui.common.EmptyScreen
import com.fd.myblog.ui.common.ErrorScreen
import com.fd.myblog.ui.common.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlogListScreen(
    viewModel: BlogListViewModel,
    onProfileClick: () -> Unit,
    onCreateNewPostClick: () -> Unit,
    onItemClick: (blog: BlogPost) -> Unit
) {

    val pullRefreshState = rememberPullToRefreshState()

    Scaffold(
        topBar = {
            TopAppBar(
                colors = UiHelper.topAppBarColors(),
                title = {
                    Text(
                        "My Blog",
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                actions = {
                    AsyncImage(
                        model = viewModel.getUser()?.imageUrl,
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(id = R.drawable.ic_user_circle),
                        error = painterResource(id = R.drawable.ic_user_circle),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable {
                                onProfileClick()
                            },
                    )
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onCreateNewPostClick() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create New Post")
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(
                    state = pullRefreshState,
                    isRefreshing = viewModel.isRefreshing,
                    onRefresh = { viewModel.reloadBlogs() }
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(color = MaterialTheme.colorScheme.background)
            ) {
                Box(Modifier.padding(8.dp)) {
                    DebounceTextField(placeholderText = "Search here") {
                        viewModel.search(it)
                    }
                }
                when (val state = viewModel.state) {
                    is BlogListState.Loading -> LoadingScreen()
                    is BlogListState.Empty -> EmptyScreen()
                    is BlogListState.Error -> ErrorScreen(subMessage = state.message)
                    is BlogListState.Success -> Box(modifier = Modifier.fillMaxSize()) {
                        BlogListView(
                            blogs = state.blogs,
                            isLoadMore = state.isLoadMore,
                            onLoadMore = { viewModel.loadBlogs(true) },
                            onItemClick = onItemClick
                        )
                        PullToRefreshBox(
                            isRefreshing = viewModel.isRefreshing,
                            onRefresh = {},
                            state = pullRefreshState,
                            modifier = Modifier.align(Alignment.TopCenter),
                            content = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BlogListView(
    blogs: List<BlogPost>,
    isLoadMore: Boolean,
    onLoadMore: () -> Unit,
    onItemClick: (blog: BlogPost) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(blogs) { blog ->
            BlogListItem(blog, onItemClick)
        }
        if (isLoadMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            onLoadMore()
        }
    }
}