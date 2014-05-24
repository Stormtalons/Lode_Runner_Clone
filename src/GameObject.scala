import javafx.scene.image.Image
import javafx.scene.control.Button
import javafx.event.{ActionEvent, EventHandler}

object GameObject
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

	val TERRAIN = NormalTerrain.getClass
	val BOMB = 1

//	def createToolButton(t: Int): Button =
//	{
//		val toReturn = new Button
//		t match
//		{
//			case TERRAIN =>
//				toReturn.setText("T")
//				toReturn.setOnAction(new EventHandler[ActionEvent]{def handle(evt: ActionEvent) = Main.tool = TERRAIN})
//			case BOMB =>
//				toReturn.setText("B")
//				toReturn.setOnAction(new EventHandler[ActionEvent]{def handle(evt: ActionEvent) = Main.tool = BOMB})
//		}
//		toReturn
//	}
}

trait GameObject[T <: GameObject[T]]
{
	def getSprite: Image
	def zOrder: Int
	def isPassable: Boolean
	def isCharacter: Boolean
	def isObj(obj: GameObject[_]): Boolean = obj.getClass.equals(this.getClass)
	def make: T
}