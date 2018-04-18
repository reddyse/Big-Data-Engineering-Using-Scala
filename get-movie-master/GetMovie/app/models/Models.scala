package models



case class Flight(source: String, destination: String, carrier: String, monthOfTravel: String, dayOfTravel: String, actualPrice: String, predictedPrice: String, id:Option[Int]=None)


