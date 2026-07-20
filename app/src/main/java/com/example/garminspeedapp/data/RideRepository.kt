package com.example.garminspeedapp.data

import kotlinx.coroutines.flow.Flow

class RideRepository(private val rideDao: RideDao) {
    val allRides: Flow<List<RideEntity>> = rideDao.getAllRides()

    suspend fun insertRide(ride: RideEntity) {
        rideDao.insertRide(ride)
    }

    suspend fun getRideById(id: Long): RideEntity? {
        return rideDao.getRideById(id)
    }

    suspend fun deleteRide(ride:RideEntity) {
        rideDao.deleteRide(ride)
    }
}
