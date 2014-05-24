import javafx.animation.{Interpolator, TranslateTransition, SequentialTransition}
import javafx.application.Platform
import javafx.event.{EventHandler, ActionEvent}
import javafx.scene.control.Button
import javafx.scene.image.ImageView
import javafx.scene.input.{KeyCode, KeyEvent}
import javafx.scene.layout._
import javafx.scene.Scene
import javafx.stage.{WindowEvent, Stage}
import javafx.util.Duration
import scala.collection.mutable.ArrayBuffer

object Main extends App
{
	var tool: GameObject[_ <: GameObject[_]] = null

	val scale = 32
	val cols = 30
	val rows = 20
	var grid: Array[Array[Tile]] = null

	val maxPlayers = 2
	val players = new ArrayBuffer[Player]

	val LEFT = 0
	val RIGHT = 1
	val UP = 2
	val DOWN = 3
	val movements = Array((-Main.scale, 0), (Main.scale, 0), (0, -Main.scale), (0, Main.scale))

	new Main().launch

	def addPlayer(player: Player, t: Tile): Unit =
	{
		for (i <- 0 to grid.length - 1)
			for (j <- 0 to grid(i).length - 1)
				if (grid(i)(j).equals(t))
				{
					player.init(players.length, i, j)
					players += player
				}
	}

	def getPlayerNode(p: Player): ImageView = grid(p.x)(p.y).getPlayerNode(p)

	def movePlayer(p: Player, dir: Int) =
	{
		val xMod = if (movements(dir)._1 > 0) 1 else if (movements(dir)._1 < 0) -1 else 0
		val yMod = if (movements(dir)._2 > 0) 1 else if (movements(dir)._2 < 0) -1 else 0
		grid(p.x + xMod)(p.y + yMod).takeObj(p, grid(p.x)(p.y))
		p.x += xMod
		p.y -= yMod
	}

	def gameLoop =
	{
		Main.run(
			while (true)
			{
				players.foreach(p =>
				{
					if (p.y < rows - 1 && grid(p.x)(p.y + 1).isPassable)
					{
						grid(p.x)(p.y + 1).takeObj(p, grid(p.x)(p.y))
						p.y += 1
					}
					else
					{
						val pn: ImageView = getPlayerNode(p)

						if (p.currentAction.length == 0)
						{
							if (p.test != null)
								p.cancelMoving(pn.getTranslateX, pn.getTranslateY, p.test.getCurrentTime)
						}
						else
						{
							if (!p.currentAction.equals(p.lastAction) && p.test != null)
							{
								println(p.currentAction)
								println(p.lastAction)
								println
								p.cancelMoving(pn.getTranslateX, pn.getTranslateY, p.test.getCurrentTime)
							}

							if (p.currentAction == "P")
							{

							}
							else if (p.test == null)
							{
								p.test = new TranslateTransition
								p.test.setNode(pn)
								p.test.setRate(50)
								p.test.setInterpolator(Interpolator.LINEAR)
								p.lastAction = p.currentAction
								p.currentAction match
								{
									case "L" =>
										if (p.x > 0 && grid(p.x - 1)(p.y).isPassable)
										{
											p.test.setToX(-Main.scale - p.xMod)
//											if (math.abs(p.test.getByX) > Main.scale)
//												p.test.setDuration(p.test.getDuration.add(p.durMod))
//											else
//												p.test.setDuration(p.test.getDuration.subtract(p.durMod))
											p.test.setOnFinished(new EventHandler[ActionEvent]
											{
												def handle(evt: ActionEvent) =
												{
													grid(p.x - 1)(p.y).takeObj(p, grid(p.x)(p.y))
													p.x -= 1
													p.cancelMoving(0, 0, Duration.millis(0))
												}
											})
										}
									case "R" =>
										if (p.x < Main.cols - 1 && grid(p.x + 1)(p.y).isPassable)
										{
											p.test.setToX(Main.scale - p.xMod)
//											if (math.abs(p.test.getByX) > Main.scale)
//												p.test.setDuration(p.test.getDuration.add(p.durMod))
//											else
//												p.test.setDuration(p.test.getDuration.subtract(p.durMod))
											p.test.setOnFinished(new EventHandler[ActionEvent]
											{
												def handle(evt: ActionEvent) =
												{
													grid(p.x + 1)(p.y).takeObj(p, grid(p.x)(p.y))
													p.x += 1
													p.cancelMoving(0, 0, Duration.millis(0))
												}
											})
										}
									case "U" =>
										if (p.y > 0 && grid(p.x)(p.y - 1).isPassable)
										{
											val tt = grid(p.x)(p.y).moveObj(p, -Main.scale, 0, 500)
											tt.setOnFinished(new EventHandler[ActionEvent]
											{
												def handle(evt: ActionEvent) =
												{
													grid(p.x)(p.y - 1).takeObj(p, grid(p.x)(p.y))
													p.y -= 1
												}
											})
											p.move(tt)
										}
									case "D" =>
										if (p.y < rows - 1 && grid(p.x)(p.y + 1).isPassable)
										{
											val tt = grid(p.x)(p.y).moveObj(p, -Main.scale, 0, 500)
											tt.setOnFinished(new EventHandler[ActionEvent]
											{
												def handle(evt: ActionEvent) =
												{
													grid(p.x)(p.y + 1).takeObj(p, grid(p.x)(p.y))
													p.y += 1
												}
											})
											p.move(tt)
										}
									case _ => println("Unknown command received")
								}

								Main.fx(p.test.play)
							}
						}
					}
				})

				Thread.sleep(10)
			})
	}

	def run(code: => Unit) = new Thread(new Runnable {def run = code}).start
	def fx(code: => Unit) = if (Platform.isFxApplicationThread) code else Platform.runLater(new Runnable{def run = code})
}
class Main extends javafx.application.Application
{
	def launch = javafx.application.Application.launch()

	var contentPane: BorderPane = null
	var map: GridPane = null

	var toolBox: HBox = null

	def start(stg: Stage) =
	{
		contentPane = new BorderPane

		map = new GridPane
		Main.grid = new Array(Main.cols)
		for (i <- 0 to Main.grid.length - 1)
		{
			val cc = new ColumnConstraints
			cc.setMaxWidth(Main.scale)
			cc.setMinWidth(Main.scale)
			map.getColumnConstraints.add(cc)
			Main.grid(i) = new Array(Main.rows)
			for (j <- 0 to Main.grid(i).length - 1)
			{
				if (i == 0)
				{
					val rc = new RowConstraints
					rc.setMaxHeight(Main.scale)
					rc.setMinHeight(Main.scale)
					map.getRowConstraints.add(rc)
				}
				Main.grid(i)(j) = new Tile
				map.add(Main.grid(i)(j), i, j)
			}
		}
		contentPane.setCenter(map)

		toolBox = new HBox
		val ter = new Button("Terrain")
		ter.setOnAction(new EventHandler[ActionEvent]{def handle(evt: ActionEvent) = Main.tool = new NormalTerrain})
		val b = new Button("Bomb")
		b.setOnAction(new EventHandler[ActionEvent]{def handle(evt: ActionEvent) = Main.tool = new Bomb})
		val pp = new Button("Place Player")
		pp.setOnAction(new EventHandler[ActionEvent]{def handle(evt: ActionEvent) = Main.tool = new Player})
		val runGame = new Button("Run Game Loop")
		runGame.setOnAction(new EventHandler[ActionEvent]{def handle(evt: ActionEvent) = Main.gameLoop})
		toolBox.getChildren.addAll(ter, b, pp, runGame)
		contentPane.setBottom(toolBox)

		stg.addEventHandler(WindowEvent.WINDOW_HIDING, new EventHandler[WindowEvent]{def handle(evt: WindowEvent) = System.exit(0)})
		val sce = new Scene(contentPane)
		sce.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler[KeyEvent]{def handle(evt: KeyEvent) = Main.players.foreach(p => p.toggleMoving(evt.getCode, true))})
		sce.addEventFilter(KeyEvent.KEY_RELEASED, new EventHandler[KeyEvent]{def handle(evt: KeyEvent) = Main.players.foreach(p => p.toggleMoving(evt.getCode, false))})
		stg.setScene(sce)
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
