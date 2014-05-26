import javafx.scene.image.Image

object NormalTerrain
{
	final val img = new Image("res/terrain.png")
}
class NormalTerrain extends GameItem
{
	setImage(NormalTerrain.img)

	val isTraversable = false
	val blocksFalling = true
	val itemType = "NT"
}