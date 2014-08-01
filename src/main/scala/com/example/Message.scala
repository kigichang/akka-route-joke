package com.example

package message {
  trait Question
  
  case object Interest extends Question
  case class Why() extends Question
  
  trait Answer
  
  case class Three(name: String, a: String, b: String, c: String) extends Answer
  case class Two(name: String, a: String, b: String) extends Answer
  case class Because(name: String, reason: String) extends Answer
  case class Iam(name: String) extends Answer
  case class DontHitMe(name: String, hit: Int) extends Answer
  case class ResponseHit(name: String, hit: Int) extends Answer
  
  case object Hit
  case object QueryHit
  case object WhoAreYou
  case object HitDong
  case object QueryAll
}