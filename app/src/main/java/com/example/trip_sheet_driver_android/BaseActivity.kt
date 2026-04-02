package com.example.trip_sheet_driver_android

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class BaseActivity : AppCompatActivity() {

    private var isFabOpen = false

    protected fun setupFabMenu(root: View) {

        val fabMain = root.findViewById<FloatingActionButton>(R.id.fabMain)
        val overlay = root.findViewById<View>(R.id.fabOverlay)

        val callLayout = root.findViewById<View>(R.id.layoutCall)
        val smsLayout = root.findViewById<View>(R.id.layoutSms)
        val chatLayout = root.findViewById<View>(R.id.layoutChat)

        fabMain.setOnClickListener {
            if (isFabOpen) closeFab(callLayout, smsLayout, chatLayout, overlay, fabMain)
            else openFab(callLayout, smsLayout, chatLayout, overlay, fabMain)
        }

        overlay.setOnClickListener {
            closeFab(callLayout, smsLayout, chatLayout, overlay, fabMain)
        }
    }

    private fun openFab(call: View, sms: View, chat: View, overlay: View, fabMain: View) {
        isFabOpen = true

        overlay.visibility = View.VISIBLE
        overlay.alpha = 0f
        overlay.animate().alpha(1f).setDuration(200).start()

        call.visibility = View.VISIBLE
        sms.visibility = View.VISIBLE
        chat.visibility = View.VISIBLE

        call.translationY = 100f
        sms.translationY = 100f
        chat.translationY = 100f

        call.alpha = 0f
        sms.alpha = 0f
        chat.alpha = 0f

        call.animate().translationY(-5f).alpha(1f).setDuration(250).start()
        sms.animate().translationY(-5f).alpha(1f).setDuration(250).start()
        chat.animate().translationY(-5f).alpha(1f).setDuration(250).start()

        // 🔥 ROTATION
        fabMain.animate().rotation(45f).setDuration(200).start()
    }

    private fun closeFab(call: View, sms: View, chat: View, overlay: View, fabMain: View) {
        isFabOpen = false

        overlay.animate().alpha(0f).setDuration(200).withEndAction {
            overlay.visibility = View.GONE
        }.start()

        call.animate().translationY(0f).alpha(0f).setDuration(200).withEndAction {
            call.visibility = View.GONE
        }.start()

        sms.animate().translationY(0f).alpha(0f).setDuration(200).withEndAction {
            sms.visibility = View.GONE
        }.start()

        chat.animate().translationY(0f).alpha(0f).setDuration(200).withEndAction {
            chat.visibility = View.GONE
        }.start()

        fabMain.animate().rotation(0f).setDuration(200).start()
    }
}