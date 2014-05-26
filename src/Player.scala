import javafx.scene.image.Image

class Player extends GameItem
{
	setImage(new Image("res/player.png"))

	val isTraversable = true
	val blocksFalling = true
	val itemType = "P"

	var moving = Array(false, false, false, false)
}