package ru.skillbranch.skillarticles.data.remote.err

import java.io.IOException

class ApiError (override val message: String = "Network not available") : IOException(message)