package com.wyl.componentization

import com.componentization.annotation.ServiceAnnotation

interface UserService {
    fun getUserName(): String
}

@ServiceAnnotation
class UserServiceImpl : UserService {
    override fun getUserName(): String = "wyl"
}