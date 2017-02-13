package ovh.not.dabbot

import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import java.util.function.BiConsumer
import java.util.function.Consumer

class Requester {
    val json: MediaType = MediaType.parse("application/json; charset=utf-8")
    val client: OkHttpClient
    val baseUrl: String

    constructor(baseUrl: String, token: String) {
        this.baseUrl = baseUrl
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

    fun execute(method: Method, url: String, json: JSONObject?, callback: BiConsumer<Response, JSONObject>, errorCallback: Consumer<IOException>) {
        var body: RequestBody? = null
        if (json != null) {
            body = RequestBody.create(this.json, json.toString())
        }
        val r = Request.Builder()
                .url(baseUrl + url)
                .method(method.name, body)
                .build()
        client.newCall(r).enqueue(object : Callback {
            override fun onFailure(c: Call, e: IOException) {
                val m = c.request().method()
                val p = c.request().url().uri().path
                println("Failed    $m $p")
                errorCallback.accept(e)
            }

            override fun onResponse(c: Call, r: Response) {
                val o: JSONObject
                try {
                    o = JSONObject(r.body().string())
                } catch (e: JSONException) {
                    e.printStackTrace()
                    return
                }
                callback.accept(r, o)
            }
        })
    }

    fun execute(method: Method, url: String, callback: BiConsumer<Response, JSONObject>, errorCallback: Consumer<IOException>) {
        execute(method, url, null, callback, errorCallback)
    }
}