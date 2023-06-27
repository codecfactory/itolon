package com.example.myapplication.utils
import io.reactivex.rxjava3.annotations.NonNull
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.functions.Consumer
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject


object RxBus {
    //this how to create our bus
    private val publisher = PublishSubject.create<Any>()

    public val behaviorSubject = BehaviorSubject.create<Any>()
    fun subscribe(@NonNull action: Consumer<Any?>?): Disposable {
        return behaviorSubject.subscribe(action)
    }

    //use this method to send data
    fun publish(@NonNull message: Any) {
        behaviorSubject.onNext(message)
    }
}