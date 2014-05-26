import java.lang.Boolean
import java.nio.file.{StandardOpenOption, OpenOption, Paths, Files}
import javafx.application.Platform
import javafx.beans.value.{ObservableValue, ChangeListener}
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.Insets
import javafx.scene.control.{CheckBox, Button}
import javafx.scene.input.{KeyCode, KeyEvent, MouseEvent}
import javafx.scene.layout.{BorderPane, HBox, AnchorPane}
import javafx.scene.Scene
import javafx.stage.{WindowEvent, Stage}

object Main extends App
{
	final val STILL	= -1
	final val LEFT	= 0
	final val RIGHT	= 1
	final val UP	= 2
	final val DOWN	= 3
	final val movements = Array((-1, 0), (1, 0), (0, -1), (0, 1))

	final val rows	= 20
	final val cols	= 30
	final val scale	= 32

	new Main().launch

	def toItemCoords(_x: Double, _y: Double): (Int, Int) = (((_x - (_x % scale)) / scale).toInt, ((_y - (_y % scale)) / scale).toInt)
	def toSceneCoords(_x: Int, _y: Int): (Double, Double) = (_x * scale, _y * scale)

	def run(code: => Unit) = new Thread(new Runnable {def run = code}).start
	def fx(code: => Unit) = if (Platform.isFxApplicationThread) code else Platform.runLater(new Runnable{def run = code})
}

class Main extends javafx.application.Application
{
	def launch = javafx.application.Application.launch()

	var player: Player = null

	var contentPane: BorderPane = null

	var isEditing: CheckBox = null
	var map: Map = null
	val itemPlacementHandler: EventHandler[MouseEvent] = new EventHandler[MouseEvent]
	{
		def handle(evt: MouseEvent) =
		{
			map.addItem(evt.getX, evt.getY, toolBox.createItem)
		}
	}

	var toolBox: ToolSelector = null

	def start(stg: Stage)
	{
		player = new Player
		contentPane = new BorderPane

		toolBox = new ToolSelector

		isEditing = new CheckBox("Edit Mode:")
		isEditing.selectedProperty().addListener(new ChangeListener[Boolean] {def changed(prop: ObservableValue[_ <: Boolean], from: Boolean, to: Boolean) = toggleEditMode})
		BorderPane.setMargin(isEditing, new Insets(5))
		contentPane.setTop(isEditing)

		map = new Map
		BorderPane.setMargin(map, new Insets(5))
		contentPane.setCenter(map)

		val runGame = new Button("Run Game Loop")
		runGame.setOnAction(new EventHandler[ActionEvent]{def handle(evt: ActionEvent) = gameLoop})
		val hb = new HBox
		hb.setSpacing(30)
		hb.getChildren.addAll(toolBox, runGame)
		BorderPane.setMargin(hb, new Insets(5))
		contentPane.setBottom(hb)

		stg.setOnHiding(new EventHandler[WindowEvent]{def handle(evt: WindowEvent) = quit})
		val sce = new Scene(contentPane)
		sce.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler[KeyEvent]
		{
			def handle(evt: KeyEvent) =
			{
				player.moving(evt.getCode match
				{
					case KeyCode.LEFT => Main.LEFT
					case KeyCode.RIGHT => Main.RIGHT
					case KeyCode.UP => Main.UP
					case KeyCode.DOWN => Main.DOWN
					case _ => Main.STILL
				}) = true
			}
		})
		sce.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler[KeyEvent]
		{
			def handle(evt: KeyEvent) =
			{
				player.moving(evt.getCode match
				{
					case KeyCode.LEFT => Main.LEFT
					case KeyCode.RIGHT => Main.RIGHT
					case KeyCode.UP => Main.UP
					case KeyCode.DOWN => Main.DOWN
					case _ => Main.STILL
				}) = false
			}
		})
		stg.setScene(sce)
		stg.show

		isEditing.setSelected(true)
		load
	}

	def gameLoop =
	{
		Main.run(
		{
			while (true)
			{
				if (map.shouldFall(AnchorPane.getLeftAnchor(player), AnchorPane.getTopAnchor(player), player))
					AnchorPane.setTopAnchor(player, AnchorPane.getTopAnchor(player) + 1)
				else
				{
					for (i <- 0 to player.moving.length - 1)
					{
						if (player.moving(i) && map.canGo(AnchorPane.getLeftAnchor(player), AnchorPane.getTopAnchor(player), player,  i))
						{
							AnchorPane.setLeftAnchor(player, AnchorPane.getLeftAnchor(player) + Main.movements(i)._1)
							AnchorPane.setTopAnchor(player, AnchorPane.getTopAnchor(player) + Main.movements(i)._2)
						}
					}
				}

				Thread.sleep(5)
			}
		})
	}

	def toggleEditMode =
	{
		if (isEditing.isSelected)
		{
			toolBox.setDisable(false)
			map.addEventHandler(MouseEvent.MOUSE_DRAGGED, itemPlacementHandler)
			map.addEventHandler(MouseEvent.MOUSE_PRESSED, itemPlacementHandler)
		}
		else
		{
			toolBox.setDisable(true)
			map.removeEventHandler(MouseEvent.MOUSE_DRAGGED, itemPlacementHandler)
			map.removeEventHandler(MouseEvent.MOUSE_PRESSED, itemPlacementHandler)
		}
	}

	def load =
	{
		val lines = Files.readAllLines(Paths.get("config.ini"))
		for (i <- 0 to lines.size - 1)
		{
			var line = lines.get(i)
			if (line.length > 0)
			{
				line = line.substring(line.indexOf("x=") + 2, line.length)
				val x = line.substring(0, line.indexOf(" ")).toInt
				line = line.substring(line.indexOf("y=") + 2, line.length)
				val y = line.substring(0, line.indexOf(" ")).toInt
				line = line.substring(line.indexOf("items={") + 7, line.indexOf("}"))
				if (line.length > 0)
				{
					line.split(",").foreach(i =>
					{
						map.addItem(x, y, i match
						{
							case "P" => player = new Player;player
							case "NT" => new NormalTerrain
							case "B" => new Bomb
							case "L" => new Ladder
						})
					})
				}
			}
		}
	}

	def quit =
	{
		Files.write(Paths.get("config.ini"), map.toXML.getBytes, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
		System.exit(0)
	}
}