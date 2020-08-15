package ru.skillbranch.skillarticles.data.remote.err

import java.io.IOException

class NoNetworkError(override val message: String = "Network not available") : IOException(message)