package dk.itu.moapd.scootersharing.lufr.model

data class PreviousRide (
    var name: String,
    var location: String,
    var timestamp: String,
    var price: Double,
    var timer: String
        )
{
    constructor(): this("","","",0.0,"")
}