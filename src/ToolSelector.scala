import javafx.scene.control.{ToggleGroup, RadioButton}
import javafx.scene.layout.HBox

class ToolSelector extends HBox
{
	setDisable(true)

	val group = new ToggleGroup
	val ter = new RadioButton("Terrain")
	ter.setToggleGroup(group)
	val b = new RadioButton("Bomb")
	b.setToggleGroup(group)
	val l = new RadioButton("Ladder")
	l.setToggleGroup(group)
	ter.setSelected(true)

	getChildren.addAll(ter, b, l)

	def createItem: GameItem =
	{
		group.getSelectedToggle match
		{
			case `ter` => new NormalTerrain
			case `b` => new Bomb
			case `l` => new Ladder
			case _ => null
		}
	}
}
