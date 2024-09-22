package com.kbds.unit.project.service

import retrofit2.create

class Network() {
    private var service: Service? = null
    companion object {
        @Volatile
        private var instance: Network? = null
        fun getInstance(): Network {
            return instance ?: synchronized(this){
                instance ?: Network().also { instance = it }
            }
        }
    }

    init {
        service = ApiClient.retrofit.create(Service::class.java)
    }

    fun getService(): Service {
        return service!!
    }
}