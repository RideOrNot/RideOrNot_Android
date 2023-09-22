package com.hanium.rideornot.utils.observers

class LoginResultObserver {
    private val listeners = mutableListOf<ILoginResultListener>()
    fun addListener(listener: ILoginResultListener) {
        listeners.add(listener)
    }
    fun removeListener(listener: ILoginResultListener) {
        listeners.remove(listener)
    }
    fun notifyLoginSuccess() {
        for (i in listeners) {
            i.onLoginSuccess()
        }
    }
    fun notifyLoginFailure() {
        for (i in listeners) {
            i.onLoginFailure()
        }
    }
}