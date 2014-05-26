import javafx.scene.image.Image

object Bomb
{
	final val img = new Image("res/bomb.png")
}

class Bomb extends GameItem
{
	setImage(Bomb.img)

	val isTraversable = true
	val blocksFalling = false
	val itemType = "B"
}