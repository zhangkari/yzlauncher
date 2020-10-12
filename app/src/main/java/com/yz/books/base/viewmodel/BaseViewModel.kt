package com.yz.books.base.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * @author lilin
 * @time on 2019-11-02 18:41
 */
abstract class BaseViewModel : ViewModel(), LifecycleObserver {

    val mGlobalState = MutableLiveData<State>()
    protected val _mGlobalState: MutableLiveData<State>
        get() = mGlobalState

    lateinit var mLifecycleOwner: LifecycleOwner

    fun initLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        mLifecycleOwner = lifecycleOwner
    }

    /*@InjectOwner
    var model: BaseModel? = null

    fun initLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        val fields = this::class.java.declaredFields
        for(field in fields){
            if(field.isAnnotationPresent(InjectOwner::class.java)){
                field.isAccessible = true
                try {
                    val member = field.get(this)
                    if(member is SuperBaseModel){
                        member.init(lifecycleOwner)
                    }
                } catch (e: Exception){
                    continue
                }
            }
        }
    }*/

}