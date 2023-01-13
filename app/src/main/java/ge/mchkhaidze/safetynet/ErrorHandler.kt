package ge.mchkhaidze.safetynet

interface ErrorHandler {
    fun handleError(err: String): Boolean
}