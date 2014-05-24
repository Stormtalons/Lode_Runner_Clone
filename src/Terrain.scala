import javafx.scene.image.Image

trait Terrain
{
	val zOrder = 0
	val isPassable = false
	val isCharacter = false
}

object NormalTerrain
{
	val img = new Image("res/terrain.png")
}
class NormalTerrain extends GameObject[NormalTerrain] with Terrain
{
	def getSprite: Image = NormalTerrain.img
	def make: NormalTerrain = new NormalTerrain
}