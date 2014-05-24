import javafx.animation.{Interpolator, SequentialTransition, TranslateTransition}
import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.image.Image
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.util.Duration
import scala.collection.mutable

trait Character
{
	val zOrder = 10
	val isPassable = true
	val isCharacter = true

	var id: Int = -1
}

object Player
{
	val img = new Image("res/player.png")
}
class Player extends GameObject[Player] with Character
{
	def getSprite: Image = Player.img
	def make: Player = new Player
	override def isObj(obj: GameObject[_]): Boolean = obj.isInstanceOf[Player] && obj.asInstanceOf[Player].id == id

	var lastAction = ""
	
	// (left, right, up, down)
	var currentAction = ""

	var x: Int = -1
	var y: Int = -1
	def init(_id: Int, i: Int, j: Int) =
	{
		id = _id
		x = i
		y = j
	}

	def getAnimation(dir: Int): TranslateTransition =
	{
		val tr = new TranslateTransition
		tr.setRate(1)
		tr.setInterpolator(Interpolator.LINEAR)
		tr.setByX(Main.movements(dir)._1)
		tr.setByY(Main.movements(dir)._2)
		tr.setNode(Main.getPlayerNode(this))
		tr.setOnFinished(new EventHandler[ActionEvent]
		{
			def handle(evt: ActionEvent) =
			{
				Main.movePlayer(Player.this, dir)
				tr.setNode(Main.getPlayerNode(Player.this))
				tr.playFromStart
			}
		})
		tr
	}

	val anims = new Array[TranslateTransition](4)

	def toggleMoving(key: KeyCode, b: Boolean): Unit =
	{
		val dir = key match
		{
			case KeyCode.LEFT => Main.LEFT
			case KeyCode.RIGHT => Main.RIGHT
			case KeyCode.UP => Main.UP
			case KeyCode.DOWN => Main.DOWN
		}

		if (!b && anims(dir) != null)
		{
			anims(dir).stop
			anims(dir) = null
		}
		else
		{
			for (i <- 0 to anims.length - 1)
				if (i != dir && anims(i) != null)
				{
					anims(i).stop
					anims(i) = null
				}
			if (anims(dir) == null)
			{
				anims(dir) = getAnimation(dir)
				anims(dir).setByX(anims(dir).getByX - Main.getPlayerNode(this).getTranslateX)
				anims(dir).setByY(anims(dir).getByY - Main.getPlayerNode(this).getTranslateY)
				anims(dir).play
			}
		}
	}

	var test: TranslateTransition = null
	var currentMovement: SequentialTransition = null
	var xMod = 0.0
	var yMod = 0.0
	var durMod: Duration = Duration.millis(0)
	def move(tt: TranslateTransition) =
	{
//		currentMovement.setByX(currentMovement.getByX - xMod)
//		currentMovement.setByY(currentMovement.getByY - yMod)
//		currentMovement.play
	}
	def cancelMoving(_xMod: Double, _yMod: Double, _durMod: Duration) =
	{
		if (currentMovement != null)
		{
			currentMovement.stop
			currentMovement = null
		}
		if (test != null)
		{
			xMod = _xMod
			yMod = _yMod
			durMod = _durMod
			test.stop
			test = null
		}
	}
}

class AI extends GameObject[AI] with Character
{
	def getSprite: Image = null
	def make: AI = new AI
	override def isObj(obj: GameObject[_]): Boolean = obj.isInstanceOf[AI] && obj.asInstanceOf[AI].id == id
}