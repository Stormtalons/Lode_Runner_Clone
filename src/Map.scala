import javafx.scene.layout.AnchorPane
import scala.collection.immutable.HashMap
import scala.collection.mutable.ArrayBuffer

class Map extends AnchorPane
{
	setPrefSize(Main.cols * Main.scale, Main.rows * Main.scale)
	getStyleClass.add("map")
	getStylesheets.add("res/dft.css")

	var items: HashMap[(Int, Int), ArrayBuffer[GameItem]] = new HashMap
	for (i <- 0 to Main.cols - 1)
		for (j <- 0 to Main.rows - 1)
			items += (i, j) -> new ArrayBuffer[GameItem]

	def addItem(_x: Double, _y: Double, _gi: GameItem): Boolean =
	{
		val (itemX, itemY) = Main.toItemCoords(_x, _y)
		val (sceneX, sceneY) = Main.toSceneCoords(itemX, itemY)
		items.get(itemX, itemY).get.foreach(i => if (i.getClass.equals(_gi.getClass)) return false)
		items.get(itemX, itemY).get += _gi
		AnchorPane.setLeftAnchor(_gi, sceneX)
		AnchorPane.setTopAnchor(_gi, sceneY)
		getChildren.add(_gi)
		true
	}

	def addItem(_x: Int, _y: Int, _gi: GameItem): Boolean =
	{
		val (sceneX, sceneY) = Main.toSceneCoords(_x, _y)
		items.get(_x, _y).get.foreach(i => if (i.getClass.equals(_gi.getClass)) return false)
		items.get(_x, _y).get += _gi
		AnchorPane.setLeftAnchor(_gi, sceneX)
		AnchorPane.setTopAnchor(_gi, sceneY)
		getChildren.add(_gi)
		true
	}
	
	def shouldFall(_x: Double, _y: Double, _gi: GameItem): Boolean =
	{
		val (itemX, itemY) = Main.toItemCoords(_x, _y)
		items.get(itemX, itemY).get.foreach(i => if (i.isTraversable && !i.equals(_gi)) return false)
		if (itemY + 1 >= Main.rows)
			false
		else
		{
			items.get(itemX, itemY + 1).get.foreach(i =>
			{
				if (i.blocksFalling) return false
			})
			if (itemX + 1 < Main.cols)
			{
				items.get(itemX + 1, itemY + 1).get.foreach(i =>
				{
					if (i.blocksFalling) return false
				})
			}
			true
		}
	}

	def canGo(_x: Double, _y: Double, _gi: GameItem, _dir: Int): Boolean =
	{
		val (itemX, itemY) = Main.toItemCoords(_x, _y)
		val newX = itemX + (if (_x % Main.scale == 0) Main.movements(_dir)._1 else 0)
		val newY = itemY + (if (_y % Main.scale == 0) Main.movements(_dir)._2 else 0)

		if (newX < 0 || newX >= Main.cols || newY < 0 || newY >= Main.rows)
			return false
		items.get(itemX, itemY).get.foreach(i => if (!i.isTraversable) return false)
		items.get(newX, newY).get.foreach(i => if (!i.isTraversable) return false)
		true
	}

	def toXML: String =
	{
		val sb = new StringBuffer

		items.foreach
		{
			case (k, v) =>
				sb.append("<Cell x=" + k._1 + " y=" + k._2 + " items={")
				v.foreach(gi => sb.append(gi.itemType + ","))
				if (v.length != 0)
					sb.deleteCharAt(sb.length - 1)
				sb.append("} />\r\n")
		}
		sb.toString
	}
}