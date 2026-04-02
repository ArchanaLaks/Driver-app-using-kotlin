package com.example.trip_sheet_driver_android.pages.trip

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.trip_sheet_driver_android.R


class DriverChatActivity : AppCompatActivity() {

    private lateinit var input: EditText
    private lateinit var chatBox: LinearLayout
    private lateinit var quickReplies: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_chat)

        input = findViewById(R.id.etMessage)
        chatBox = findViewById(R.id.chatContainer)
        quickReplies = findViewById(R.id.quickReplies)

        setupQuickReplies()

        findViewById<View>(R.id.btnSend).setOnClickListener {
            val msg = input.text.toString()

            if (msg.isNotEmpty()) {
                sendMessage(msg)
                input.text.clear()
            }
        }

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbarChat)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val root = findViewById<View>(android.R.id.content)

        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->

            val navBarHeight = insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom

            // push input above nav bar
            findViewById<View>(R.id.btnSend).setPadding(0, 0, 0, navBarHeight)
            findViewById<View>(R.id.etMessage).setPadding(0, 0, 0, navBarHeight)

            insets
        }

    }

    private fun setupQuickReplies() {
        val replies = listOf(
            "I have arrived",
            "I am nearby",
            "Please come outside",
            "Stuck in traffic"
        )

        replies.forEach { text ->
            val btn = TextView(this)
            btn.text = text
            btn.setPadding(30, 15, 30, 15)
            btn.setBackgroundColor(Color.LTGRAY)

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginEnd = 20
            btn.layoutParams = params

            btn.setOnClickListener {
                sendMessage(text)
            }

            quickReplies.addView(btn)
        }
    }

    private fun sendMessage(message: String) {
        addMessage(message, true)

        // 🔥 TODO: Replace with API / socket
        simulateCustomerReply(message)
    }

    private fun simulateCustomerReply(msg: String) {
        addMessage("Customer: Got it 👍", false)
    }

    private fun addMessage(text: String, isDriver: Boolean) {

        val container = LinearLayout(this)
        container.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        container.orientation = LinearLayout.HORIZONTAL
        container.setPadding(8, 4, 8, 4)

        val tv = TextView(this)
        tv.text = text
        tv.setPadding(25, 15, 25, 15)
        tv.setTextColor(Color.BLACK)

        if (isDriver) {
            container.gravity = android.view.Gravity.END
            tv.setBackgroundResource(R.drawable.bg_driver_bubble)
        } else {
            container.gravity = android.view.Gravity.START
            tv.setBackgroundResource(R.drawable.bg_customer_bubble)
        }

        container.addView(tv)
        chatBox.addView(container)
    }
}