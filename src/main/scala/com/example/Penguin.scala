package com.example

import com.example.message._
import akka.actor.Actor

class DontBotherMeException extends Exception

class ExplodeException extends Exception

class IamGodException extends Exception

/**
 * 企鵝
 */
class Penguin(val name: String) extends Actor {
  protected var hit = 0
  
  def rInterest() = {
    sender() ! Three(name, "吃飯", "睡覺", "打東東")
  }
  
  def rHit() = {
    hit += 1
    sender() ! DontHitMe(name, hit)
    
    
  }
  
  def rQueryHit() = {
    sender() ! ResponseHit(name, hit)
  }
  
  def rWhy() = {
    
  }
  
  def rWhoAreYou() = {
    sender() ! Iam(name)
  }
  
  def receive = {
    case Interest => rInterest()
    
    case Hit => rHit()
    
    case QueryHit => rQueryHit()
    
    case Why() => rWhy()
    
    case WhoAreYou => rWhoAreYou()
  }
  
  override def preStart() = {
    println(s"$name pre-start")
  }
  
  override def postStop() = {
    println(s"$name post-stop")
  }
}

/**
 * 叫東東的企鵝
 */
class DongDong extends Penguin("東東") {
  
  override def rInterest() = {
    sender() ! Two(name, "吃飯", "睡覺")
  }
  
  override def rWhy() = {
    sender() ! Because(name, "我就是" + name)
  }
  
  override def rHit() = {
    hit += 1
    if (hit == 4)
      throw new DontBotherMeException
    else if (hit == 6)
      throw new ExplodeException
    else if (hit > 6)
      throw new IamGodException
    else {
      sender() ! DontHitMe(name, hit)
      Thread.sleep(10)
    }
  }
}


