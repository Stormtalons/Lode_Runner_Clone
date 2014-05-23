import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.control.Button
import javafx.scene.layout._
import javafx.scene.Scene
import javafx.stage.Stage
import scala.util.control.Breaks._

object Main extends App
{
	val temp = Array(1, 2, 3, 4)
	val ta = 0
	breakable
	{
		for (i <- 0 to temp.length - 1)
			if (ta < temp(i))
			{
				val (l, r) = temp.splitAt(i - 1)
				l.foreach(i => print(i + ", "))
				println
				r.foreach(i => print(i + ", "))
				println
				val n = l :+ ta union r
				n.foreach(i => print(i + ", "))
				break
			}
	}
//	new Main().launch

	var tool = -1
	var playerPlaced = false
}
class Main extends javafx.application.Application
{
	def launch = javafx.application.Application.launch()

	val cols = 30
	val rows = 20

	var contentPane: BorderPane = null
	var map: GridPane = null
	var grid: Array[Array[Tile]] = null

	var toolBox: HBox = null

	def start(stg: Stage) =
	{
		contentPane = new BorderPane

		map = new GridPane
		grid = new Array(cols)
		for (i <- 0 to grid.length - 1)
		{
			val cc = new ColumnConstraints
			cc.setMaxWidth(32)
			cc.setMinWidth(32)
			map.getColumnConstraints.add(cc)
			grid(i) = new Array(rows)
			for (j <- 0 to grid(i).length - 1)
			{
				if (i == 0)
				{
					val rc = new RowConstraints
					rc.setMaxHeight(32)
					rc.setMinHeight(32)
					map.getRowConstraints.add(rc)
				}
				grid(i)(j) = new Tile
				map.add(grid(i)(j), i, j)
			}
		}
		contentPane.setCenter(map)

		toolBox = new HBox
		val pp = new Button("Place Player")
		pp.setOnAction(new EventHandler[ActionEvent]{def handle(evt: ActionEvent) = Main.tool = 2})
		toolBox.getChildren.addAll(GameObject.createToolButton(GameObject.TERRAIN), GameObject.createToolButton(GameObject.BOMB), pp)
		contentPane.setBottom(toolBox)

		stg.setScene(new Scene(contentPane))
		stg.show
	}

//	def quit =
//	{
//		val sb = new StringBuilder
//		for (i <- 0 to grid.length - 1)
//		{
//			for (j <- 0 to grid(i).length - 1)
//				sb.append(grid(i)(j).)
//		}
//	}
}
