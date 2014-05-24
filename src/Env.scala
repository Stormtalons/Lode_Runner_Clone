import javafx.scene.image.{Image, ImageView}

object Env extends ImageView
{
	val images: Array[Image] = Array(
		new Image("res/terrain.png"),
		new Image("res/bomb.png"),
		new Image("res/player.png")
	)

	val z: Array[Int] = Array(
		0,
		1,
		2
	)

	val TERRAIN = 0
	val BOMB = 1
}
class Env(t: Int) extends ImageView
{
	val itemType = t
	setImage(Env.images(itemType))

	def z: Int = GameObject.z(itemType)
}