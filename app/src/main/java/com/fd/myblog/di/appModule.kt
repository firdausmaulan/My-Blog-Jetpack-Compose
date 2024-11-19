package com.fd.myblog.di

import com.fd.myblog.data.local.preference.AppPreference
import com.fd.myblog.data.local.preference.CorePreference
import com.fd.myblog.data.local.preference.ICorePreference
import com.fd.myblog.data.remote.api.core.HttpClientImpl
import com.fd.myblog.data.remote.api.core.IHttpClient
import com.fd.myblog.data.remote.api.core.createHttpClient
import com.fd.myblog.data.remote.api.service.BlogPostApiService
import com.fd.myblog.data.remote.api.service.LocationApiService
import com.fd.myblog.data.remote.api.service.UserApiService
import com.fd.myblog.helper.location.ILocationHelper
import com.fd.myblog.helper.location.LocationHelper
import com.fd.myblog.ui.blog.detail.BlogDetailViewModel
import com.fd.myblog.ui.blog.form.BlogFormViewModel
import com.fd.myblog.ui.blog.list.BlogListViewModel
import com.fd.myblog.ui.location.search.SearchLocationViewModel
import com.fd.myblog.ui.user.changepassword.UserChangePasswordViewModel
import com.fd.myblog.ui.user.detail.UserDetailViewModel
import com.fd.myblog.ui.user.login.UserLoginViewModel
import com.fd.myblog.ui.user.register.UserRegisterViewModel
import io.ktor.client.engine.okhttp.OkHttp
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Define the Koin module
val appModule = module {
    // Define preferences
    single<ICorePreference> { CorePreference(androidContext()) }
    single { AppPreference(get()) }

    // Define HttpClient and API service
    single { createHttpClient(OkHttp.create()) }
    single<IHttpClient> { HttpClientImpl(get(), get()) }
    single { BlogPostApiService(get()) }
    single { UserApiService(get()) }
    single { LocationApiService(get()) }

    // Location Helper
    single<ILocationHelper> { LocationHelper(androidContext()) }

    viewModel { BlogListViewModel(get(), get()) }
    viewModel { BlogDetailViewModel(get(), get()) }
    viewModel { BlogFormViewModel(get()) }

    viewModel { UserRegisterViewModel(get(), get()) }
    viewModel { UserLoginViewModel(get(), get()) }
    viewModel { UserDetailViewModel(get(), get(), get()) }
    viewModel { UserChangePasswordViewModel(get()) }

    viewModel { SearchLocationViewModel(get(), get()) }
}
