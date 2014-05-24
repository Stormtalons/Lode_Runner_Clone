import javafx.animation.TranslateTransition
import javafx.event.EventHandler
import javafx.scene.image.ImageView
import javafx.scene.input.{MouseDragEvent, MouseEvent}
import javafx.scene.layout.StackPane
import javafx.scene.Node
import javafx.util.Duration
import scala.util.control.Breaks._

class Tile extends StackPane
{
	var objects: Array[GameObject[_]] = Array()

	setPrefSize(32, 32)
	getStyleClass.add("tile")
	getStylesheets.add("dft.css")

	setOnMousePressed(new EventHandler[MouseEvent]{def handle(evt: MouseEvent) = addNewObj(Main.tool.make)})
	setOnDragDetected(new EventHandler[MouseEvent]{def handle(evt: MouseEvent) = startFullDrag})
	setOnMouseDragOver(new EventHandler[MouseDragEvent]{def handle(evt: MouseDragEvent) = addNewObj(Main.tool.make)})

	def refreshObjs =
		Main.fx(
		{
			getChildren.clear
			objects.foreach(o =>
			{
				val iv = new ImageView(o.getSprite)
				iv.setDisable(true)
				getChildren.add(iv)
			})
		})
	
	def canAddObj(obj: GameObject[_]): Boolean =
	{
		if (obj == null)
			return false

		if (obj.isCharacter)
		{
			objects.foreach(o => if (!o.isPassable) return false)
			return true
		}

		objects.foreach(o => if (o.getClass.equals(obj.getClass)) return false)
		true
	}

	def addNewObj(obj: GameObject[_]): Unit =
	{
		if (!canAddObj(obj))
			return

		addObj(obj)

		if (obj.isInstanceOf[Player])
			Main.addPlayer(obj.asInstanceOf[Player], this)
	}

	def addObj(obj: GameObject[_]): Unit =
	{
		if (obj.isInstanceOf[Player])
		{
			Main.fx(
			{
				getStyleClass.remove("tile")
				getStyleClass.add("playerTile")
			})
		}

		breakable
		{
			for (i <- 0 to objects.length - 1)
				if (obj.zOrder < objects(i).zOrder)
				{
					val (l, r) = objects.splitAt(i - 1)
					objects = l :+ obj union r
					break
				}
			objects = objects :+ obj
		}
		refreshObjs
	}

	def removeObj(obj: GameObject[_]): Unit =
	{
		for (i <- 0 to objects.length - 1)
			if (objects(i).isObj(obj))
			{
				if (obj.isInstanceOf[Player])
				{
					Main.fx(
					{
						getStyleClass.remove("playerTile")
						getStyleClass.add("tile")
					})
				}
				val (l, r) = objects.splitAt(i - 1)
				objects = l union r drop 1
				refreshObjs
				return
			}
	}

	def takeObj(obj: GameObject[_], tile: Tile): Unit =
	{
		if (!tile.equals(this))
		{
			tile.removeObj(obj)
			addObj(obj)
		}
	}

	def moveObj(obj: GameObject[_], x: Double, y: Double, sp: Double): TranslateTransition =
	{
		for (i <- 0 to objects.length - 1)
			if (objects(i).isObj(obj))
			{
				val tt = new TranslateTransition(Duration.millis(sp), getChildren.get(i))
				tt.setByX(x)
				tt.setByY(y)
				return tt
			}
		null
	}

	def getPlayerNode(p: Player): ImageView =
	{
		for (i <- 0 to objects.length - 1)
			if (objects(i).isObj(p))
				return getChildren.get(i).asInstanceOf[ImageView]
		null
	}

	def isPassable: Boolean =
	{
		objects.foreach(o => if (!o.isPassable) return false)
		true
	}
}