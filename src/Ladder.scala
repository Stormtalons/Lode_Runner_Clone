import javafx.scene.image.Image

object Ladder
{
	final val img = new Image("res/ladder.png")
}

class Ladder extends GameItem
{
	setImage(Ladder.img)

	val isTraversable = true
	val blocksFalling = true
	val itemType = "L"
}
