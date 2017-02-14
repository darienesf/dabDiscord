package ovh.not.dabbot

import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class Requester(val baseUrl: String, token: String) {
    val json: MediaType = MediaType.parse("application/json; charset=utf-8")
    val client: OkHttpClient

    init {
        this.client = OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3, TimeUnit.SECONDS)
                .writeTimeout(3, TimeUnit.SECONDS)
                .addInterceptor{ chain ->
                    val request = chain.request().newBuilder()
                            .header("Authorization", token)
                            .header("User-Agent", "dabBot")
                            .header("Accept", "application/json")
                            .build()
                    chain.proceed(request)
                }.addInterceptor{ chain ->
                    val method = chain.request().method()
                    val path = chain.request().url().uri().path
                    println("Started   $method $path")
                    chain.proceed(chain.request())
                }.addInterceptor{ chain ->
                    val response = chain.proceed(chain.request())
                    val method = chain.request().method()
                    val path = chain.request().url().uri().path
                    val code = response.code()
                    val message = response.message()
                    println("Completed $method $path $code $message")
                    response
                }.build()
    }

    fun execute(method: Method, url: String, json: JSONObject?): Response {
        var body: RequestBody? = null
        if (json != null) {
            body = RequestBody.create(this.json, json.toString())
        }
        val r = Request.Builder()
                .url(baseUrl + url)
                .method(method.name, body)
                .build()
        return client.newCall(r).execute()
    }

    fun execute(method: Method, url: String): Response {
        return execute(method, url, null)
    }
}