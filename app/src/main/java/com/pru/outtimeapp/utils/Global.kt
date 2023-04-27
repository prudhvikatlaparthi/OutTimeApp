package com.pru.outtimeapp.utils

import java.lang.ref.WeakReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object Global {
    fun <T> weakReference(tIn: T? = null): ReadWriteProperty<Any?, T?> {
        return object : ReadWriteProperty<Any?, T?> {
            var t = WeakReference<T?>(tIn)
            override fun getValue(thisRef: Any?, property: KProperty<*>): T? {
                return t.get()
            }

            override fun setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
                t = WeakReference(value)
            }
        }
    }
}