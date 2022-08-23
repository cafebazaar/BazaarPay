package ir.cafebazaar.bazaarpay.widget.button

internal enum class ButtonStyle(
    val value: Int,
    val contentColor: ButtonContentColorType = ButtonContentColorType.BUTTON_TYPE_COLOR
) {

    OUTLINE(0),
    CONTAINED(1, ButtonContentColorType.GREY),
    CONTAINED_GREY(2),
    TRANSPARENT(3)
}