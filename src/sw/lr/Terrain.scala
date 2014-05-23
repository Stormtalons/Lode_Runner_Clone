package sw.lr

import javafx.scene.image.Image

abstract class Terrain extends GameObject
{
	val isPassable: Boolean = false
	val zOrder = 0
}

object NormalTerrain {val img = new Image("sw/lr/res/terrain.png")}
class NormalTerrain extends Terrain
{
	def getSprite: Image = NormalTerrain.img
}