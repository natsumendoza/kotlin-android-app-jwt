package com.auth0.samples.kotlinapp

import android.app.Dialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import com.android.volley.toolbox.Volley
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.provider.AuthCallback
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import com.auth0.samples.kotlinapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // setting up a Volley RequestQueue
        val queue = Volley.newRequestQueue(this)

        // referencing the binding object of the view
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // loggedIn should be false by default to show the button
        binding?.loggedIn = false

        // getting a reference for the listView
        val listTodo = findViewById<ListView>(R.id.list_todo)

        // passing the activity, the queue and the listView to the function
        // that consumes the RESTful endpoint
        getItems(this, queue, listTodo)

        // triggering the login method when the button is clicked
        val loginButton = findViewById<Button>(R.id.login_button)
        loginButton.setOnClickListener { login() }

        val addItemButton = findViewById<Button>(R.id.add_item)
        val itemEditText = findViewById<EditText>(R.id.item)

        addItemButton.setOnClickListener {
            val item = itemEditText.text.toString()
            addItem(queue, item, CredentialsManager.getAccessToken(this)) {
                itemEditText.text.clear()
                Toast.makeText(this, "Item added", Toast.LENGTH_SHORT).show()
                getItems(this, queue, listTodo)
            }
        }

    }

    override fun onNewIntent(intent: Intent?) {

        if (WebAuthProvider.resume(intent)) {
            return
        }

        super.onNewIntent(intent)
    }

    private fun login() {
        val account = Auth0(getString(R.string.auth0_client_id), getString(R.string.auth0_domain))
        account.isOIDCConformant = true

        WebAuthProvider.init(account)
                .withScheme("demo")
                .withAudience("kotlin-todo-app")
                .start(this, object : AuthCallback {
                    override fun onFailure(dialog: Dialog) {
                        runOnUiThread { dialog.show() }
                    }

                    override fun onFailure(exception: AuthenticationException?) {
                        runOnUiThread {
                            Toast.makeText(
                                    this@MainActivity, "Oops, something went wrong!",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onSuccess(credentials: Credentials) {
                        CredentialsManager.saveCredentials(this@MainActivity, credentials)
                        binding?.loggedIn = true
                    }

                })
    }

}
