import javafx.scene.image.ImageView

object GameItem
{

}

trait GameItem extends ImageView
{
	setDisable(true)
	def isTraversable: Boolean
	def blocksFalling: Boolean
	def itemType: String
}