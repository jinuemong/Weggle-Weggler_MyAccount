package com.puresoftware.bottomnavigationappbar.Server

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import com.facebook.stetho.BuildConfig
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.puresoftware.bottomnavigationappbar.Server.TokenManager.AuthInterceptor
import com.puresoftware.bottomnavigationappbar.Server.TokenManager.TokenAuthenticator
import com.puresoftware.bottomnavigationappbar.Server.TokenManager.TokenRefreshApi
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import kotlin.math.log

//http://dev-api.kooru.be/swagger-ui/index.html#/

//refrofit client
class MasterApplication : Application() {
    lateinit var service: RetrofitService
    private val baseUrl = "http://dev-api.kooru.be/api/v1"
    private lateinit var activity: Activity
    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }

    fun createRetrofit(currentActivity: Activity){
        activity = currentActivity

        val header = Interceptor{
            val original = it.request()
            if (checkIsLogin()){
                getUserToken().let { token->
                    Log.d("token test 1",token.toString())
                    val request = original.newBuilder()
                        .header("Authorization","Bearer $token")
                        .build()
                    it.proceed(request)
                }
            }else{
                it.proceed(original)
            }
        }

//        val normalClient = OkHttpClient.Builder()
//            .addInterceptor(header)
//            .addInterceptor(logInterceptor)
//            .addNetworkInterceptor(StethoInterceptor())
//            .build()
//
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor(header)
//            .addInterceptor(AuthInterceptor(activity,buildTokenApi(normalClient)))
//            .build()
//
        // ???????????? ?????? 1
        val retrofit = Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(getRetrofitClient(header))
            .build()

        service = retrofit.create(RetrofitService::class.java)

        //???????????? ?????? 2

        //????????? ?????? ??????
        //TokenAuthenticator?????? ??? ?????? ??????
//        val authenticator = TokenAuthenticator(activity,buildTokenApi())

//        val retrofit = Retrofit.Builder()
//            .baseUrl("$baseUrl/")
//            .client(getRetrofitClient(authenticator))
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(RetrofitService::class.java)
//        service = retrofit

    }

    //TokenRefreshApi??? ?????? 1
    private fun buildTokenApi() : TokenRefreshApi {
        //?????? ???????????????
        val client = OkHttpClient.Builder().build()
        return Retrofit.Builder()
            .baseUrl("$baseUrl/")
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TokenRefreshApi::class.java)
    }
    //TokenRefreshApi??? ?????? 2
//    private fun buildTokenApi() : TokenRefreshApi {
//
//        return Retrofit.Builder()
//            .baseUrl("$baseUrl/")
//            .client(getRetrofitClient())
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(TokenRefreshApi::class.java)
//    }

    //???????????? ??????????????? ?????? ?????? 1
    // ?????? :  okhttp client
    // ??????????????? ?????? request??? ??????

    private fun getRetrofitClient(header:Interceptor):OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor {chain->
                chain.proceed(chain.request().newBuilder().also {
                    it.addHeader("Accept", "application/json")
                }.build())
            }.also { client->
                client.addInterceptor(header)
                client.addInterceptor(AuthInterceptor(activity,buildTokenApi()))
                //?????? ?????? ??????????????? ?????? (?????? ?????? ?????? )
                val logInterceptor = HttpLoggingInterceptor()
                logInterceptor.level = HttpLoggingInterceptor.Level.BODY
                client.addInterceptor(logInterceptor)
            }.build()
    }

    // ???????????? ?????? 2
//    private fun getRetrofitClient(authenticator: Authenticator? = null): OkHttpClient{
//        return OkHttpClient.Builder()
//            .connectTimeout(1,TimeUnit.HOURS)
//            .readTimeout(1,TimeUnit.HOURS)
//            .writeTimeout(1,TimeUnit.HOURS)
//            .addInterceptor { chain->
//                chain.proceed(chain.request().newBuilder().also {
//                    it.addHeader("Accept", "application/json")
//                }.build())
//            }.also { clint->
//                // 401 ?????? ??? ?????? ?????? ??????
//                authenticator?.let { clint.authenticator(it) }
//                if (BuildConfig.DEBUG){
//                    //?????? ?????? ??????????????? ?????? (?????? ?????? ?????? )
//                    val logInterceptor  = HttpLoggingInterceptor()
//                    logInterceptor.level = HttpLoggingInterceptor.Level.BODY
//                    clint.addInterceptor(logInterceptor)
//                }
//            }.build()
//    }

    // 401 ?????? ?????? ??? ???????????? ????????? ?????? ??????????????? ??????
    // ?????? ????????? ???????????? ???????????? ???????????? ?????? ??????


//    //?????? ?????? ??????
//    private fun getUserToken(): String {
//        val sp = activity.getSharedPreferences("login_sp", Context.MODE_PRIVATE)
//        return sp.getString("accessToken", "no token").toString()
//    }

    private fun checkIsLogin() : Boolean{
        val sp = activity.getSharedPreferences("login_sp",Context.MODE_PRIVATE)
        val token = sp.getString("accessToken","null")
        return token!="null"
        //is not default
    }
    private fun getUserToken(): String?{
        val sp = activity.getSharedPreferences("login_sp",Context.MODE_PRIVATE)
        val token = sp.getString("accessToken","null")
        return if (token=="null") null
        else token
    }
}