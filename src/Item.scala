import javafx.scene.image.Image

trait Item
{
	val zOrder = 1
	val isPassable = true
	val isCharacter = false
}

object Bomb
{
	val img = new Image("res/bomb.png")
}
class Bomb extends GameObject[Bomb] with Item
{
	def getSprite: Image = Bomb.img
	def make: Bomb = new Bomb
}