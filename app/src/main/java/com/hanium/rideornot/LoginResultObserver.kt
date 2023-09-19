package com.hanium.rideornot

class LoginResultObserver {
    private val listeners = mutableListOf<ILoginResultListener>()

    fun addListener(listener: ILoginResultListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ILoginResultListener) {
        listeners.remove(listener)
    }

    fun onLoginSuccess() {
        for (i in listeners) {
            i.onLoginSuccess()
        }
    }

    fun onLoginFailure() {
        for (i in listeners) {
            i.onLoginFailure()
        }
    }
}