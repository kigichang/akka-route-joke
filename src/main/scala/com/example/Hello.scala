package com.example

import akka.actor.ActorSystem
import akka.actor.Props
import com.example.message._

object Hello extends App {
  
  val system = ActorSystem("Hello")
  
  val report = system.actorOf(Props[Reporter])
  
  for (i <- 1 to 100) {
    report ! HitDong
  }
  
  Thread.sleep(5000)
  
  report ! QueryAll
}
