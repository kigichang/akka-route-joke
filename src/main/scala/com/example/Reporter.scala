package com.example

import com.example.message._
import akka.actor.Actor
import akka.actor.Identify
import scala.concurrent.duration._
import akka.actor.ActorIdentity
import akka.actor.Props
import akka.actor.ActorRef
import akka.actor.OneForOneStrategy
import akka.actor.AllForOneStrategy
import akka.actor.SupervisorStrategy._
import akka.routing.ActorRefRoutee
import akka.routing.SmallestMailboxRoutingLogic
import akka.routing.Router
import akka.actor.Terminated
import akka.routing.RoundRobinRoutingLogic

/**
 * 記者
 */
class Reporter() extends Actor {

  override val supervisorStrategy =
    OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      //AllForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 minute) {
      case _: DontBotherMeException => Resume
      case _: ExplodeException => Restart
      case _: IamGodException => Stop
      case _: Exception => Escalate
    }

  val penguins = new Array[ActorRefRoutee](10)

  for (i <- 0 to 8) {
    val actor = context.actorOf(Props(classOf[Penguin], s"Penguin-$i"))
    context watch actor
    penguins(i) = ActorRefRoutee(actor)
    actor ! Identify(actor.path.toString())
  }

  val dong = context.actorOf(Props[DongDong], "dongdong")
  context watch dong
  penguins(9) = ActorRefRoutee(dong)
  dong ! Identify(dong.path.toString())

  import context.dispatcher
  context.setReceiveTimeout(5 seconds) // 設定 timeout 5 seconds
  //var router = Router(SmallestMailboxRoutingLogic(), penguins.toIndexedSeq)
  var router = Router(RoundRobinRoutingLogic(), penguins.toIndexedSeq)
  
  var count = 0
  
  def receive = {
    case ActorIdentity(path, Some(actor)) =>
      count += 1
      if (count == penguins.length) {
        context.setReceiveTimeout(Duration.Undefined)
      }
      actor ! Interest

    /* 有三個興趣的回覆 */
    case Three(name, a, b, c) =>
      println(s"$name: $a, $b, $c")

    /* 只有二個興趣的回覆，反問 why */
    case Two(name, a, b) =>
      println(s"$name: $a, $b")
      sender() ! Why()

    /* 接到 why 的回覆 */
    case Because(name, msg) =>
      println(s"$name: $msg")

    case Iam(name) =>
      println(s"$name: I am $name")

    case DontHitMe(name, hit) =>
      println(s"$name: Don't Hit Me, I had been hit $hit times")

    case ResponseHit(name, hit) =>
      println(s"$name: I had been hit $hit times")

    case Terminated(child) =>
      router = router.removeRoutee(child)
      val actor = 
        if (child.path.toString().indexOf("dongdong") >= 0)
          context.actorOf(Props[DongDong], "dongdong")
        else
          context.actorOf(Props(classOf[Penguin], "Penguin" + (System.nanoTime())))

      context watch actor
      router = router.addRoutee(actor)
      
    case HitDong =>
      router.route(Hit, self)
    
    case QueryAll =>
      router.routees.foreach(_.send(QueryHit, self))
      
    case _ => println
    
  }

  override def preStart() = {
    println("Reporter pre-start")
  }

  override def postStop() = {
    println("Reporter post-stop")
  }
}