package plugin.api

interface RoofVisibilityHandler {
    fun getGroupLimit(): Int

    fun isActive(): Boolean

    fun disableIfExpired()

    fun ensureBuffers()

    fun applyRequests()

    fun isPicking(): Boolean

    fun getPickScreenX(): Int

    fun getPickScreenY(): Int

    fun reportTile(sceneX: Int, sceneZ: Int, plane: Int)

    fun isLocPickable(key: Long): Boolean

    fun reportLoc(key: Long, plane: Int)

    fun beginGroup(group: Int)

    fun addGroupTile(group: Int, plane: Int, sceneX: Int, sceneZ: Int)

    fun setDestinationTarget(sceneX: Int, sceneZ: Int)

    fun clearDestinationTarget()
}
