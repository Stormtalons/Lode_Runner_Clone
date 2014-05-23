import javafx.event.EventHandler
import javafx.scene.input.{MouseDragEvent, MouseEvent}
import javafx.scene.layout.StackPane

object Tile
{
	val maxItems = 10
}
class Tile extends StackPane
{
	val objects: Array[GameObject] = new Array(Tile.maxItems)
	var objectCount = 0

	setPrefSize(32, 32)
	getStyleClass.add("tile")
	getStylesheets.add("sw/lr/dft.css")

//	val stuff = new Label
	setOnMousePressed(new EventHandler[MouseEvent]{def handle(evt: MouseEvent) = addItem_o(Env.createItem)})
	setOnDragDetected(new EventHandler[MouseEvent]{def handle(evt: MouseEvent) = startFullDrag})
	setOnMouseDragOver(new EventHandler[MouseDragEvent]{def handle(evt: MouseDragEvent) = addItem_o(Env.createItem)})
//	getChildren.add(stuff)
	
//	def canAddObj(obj: GameObject): Boolean =
//	{
//		if (obj == null)
//			return false
//
//		if (obj.isUnit)
//		{
//			objects.foreach(o => if (!o.isPassable) return false)
//			return true
//		}
//
//		objects.foreach(o => if (o.isInstanceOf[obj.type]) return false)
//		true
//	}
//
//	def addObj(obj: GameObject) =
//	{
//		if (canAddObj(obj))
//		{
//			for (i <- 0 to objectCount)
//			{
//				if (obj.zOrder < objects(i).zOrder)
//				{
//					val (l, r) = objects.splitAt()
//				}
//			}
//		}
//	}

	def addItem_o(item: Env): Unit =
	{
		if (item == null || hasItem(item)) return
		val items = new Array[Env](getChildren.size + 1)
		items(math.min(item.z, getChildren.size)) = item
		for (i <- 0 to getChildren.size - 1)
			items(i + (if (i < math.min(item.z, getChildren.size)) 0 else 1)) = getChildren.get(i).asInstanceOf[Env]
		getChildren.clear
		items.foreach(i => getChildren.add(i))
		if (item.itemType == 2)
		{
			Main.playerPlaced = true
			Main.tool = -1
		}
	}

	def hasItem(item: Env): Boolean =
	{
		for (i <- 0 to getChildren.size - 1)
			if (getChildren.get(i).asInstanceOf[Env].itemType == item.itemType) return true
		false
	}
}