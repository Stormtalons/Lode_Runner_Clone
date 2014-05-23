package sw.lr

import javafx.scene.image.Image

abstract class Unit extends GameObject
{
	val isPassable = true
	val zOrder = Tile.maxItems
}

object Player {val img = new Image("sw/lr/res/player.png")}
class Player extends Unit
{
	def getSprite: Image = Player.img
}

class AI extends Unit
{

}