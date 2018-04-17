package com.auth0.samples.kotlinapp

import android.app.Activity
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import org.json.JSONArray

/**
 * Created by Roxel on 17/04/2018.
 */

val ENDPOINT = "http://10.0.2.2:8080/"

fun getItems(activity: Activity, queue: RequestQueue, listView: ListView) {
    val jsonArrayRequest = JsonArrayRequest(Request.Method.GET, ENDPOINT, null,
            Response.Listener<JSONArray> { response ->
                val list = ArrayList<String>()

                (0 until response.length()).mapTo(list) {
                    response[it].toString()
                }

                val adapter = ArrayAdapter(activity, android.R.layout.simple_list_item_1, list)
                listView.adapter = adapter
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                        activity.applicationContext,
                        error.toString(),
                        Toast.LENGTH_SHORT).show()
            }
    )
    // add getItems to queue
    queue.add(jsonArrayRequest)
}

fun addItem(queue: RequestQueue, item: String, accessToken: String, done: () -> Unit) {
    val postRequest = object : StringRequest(Request.Method.POST, ENDPOINT,
            Response.Listener<String> {
                done()
            },
            Response.ErrorListener {
                error -> Log.w("APIRequest", error.toString())
            }
    ) {
        @Throws(AuthFailureError::class)
        override fun getBody(): ByteArray {
            return item.toByteArray()
        }

        @Throws(AuthFailureError::class)
        override fun getHeaders(): MutableMap<String, String> {
            val headers: MutableMap<String, String> = hashMapOf(
                    "Authorization" to "Bearer $accessToken",
                    "Content-Type" to "text/plain"
            )

            return headers
        }
    }
    queue.add(postRequest)
}