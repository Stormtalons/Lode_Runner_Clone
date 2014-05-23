import javafx.scene.image.{Image, ImageView}

object Env extends ImageView
{
	val images: Array[Image] = Array(
		new Image("sw/lr/res/terrain.png"),
		new Image("sw/lr/res/bomb.png"),
		new Image("sw/lr/res/player.png")
	)

	val z: Array[Int] = Array(
		0,
		1,
		2
	)

	val TERRAIN = 0
	val BOMB = 1

	def createItem: Env =
		if (Main.tool != -1) new Env(Main.tool)
		else null
}
class Env(t: Int) extends ImageView
{
	val itemType = t
	setImage(Env.images(itemType))

	def z: Int = GameObject.z(itemType)
}