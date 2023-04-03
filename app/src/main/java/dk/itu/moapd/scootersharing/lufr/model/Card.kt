package dk.itu.moapd.scootersharing.lufr.model

data class Card(
    val title: String,
    val secondaryText: String,
    val supportingText: String,
    val mediaResId: Int,
    val actionText: String
)